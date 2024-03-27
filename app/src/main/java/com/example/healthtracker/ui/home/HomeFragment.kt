package com.example.healthtracker.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentHomeBinding
import com.example.healthtracker.ui.formatDurationFromLong
import com.example.healthtracker.ui.home.running.RunningDialogFragment
import com.example.healthtracker.ui.home.running.RunningSensorListener
import com.example.healthtracker.ui.home.walking.StepCounterService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var speedTracker: RunningSensorListener
    private var stepCount: LiveData<Int> = StepCounterService.steps
    private var sleepDuration: LiveData<Long> = StepCounterService.sleepDuration
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            speedTracker = RunningSensorListener(requireContext())
            homeViewModel.feedUser()
            homeViewModel.checkForChallenges()
        }
        homeViewModel.syncToFireBase()
        binding.apply {
            stepCount.observe(viewLifecycleOwner) { steps ->
                if (steps == null) {
                    Log.d("wait for it :)", "i swear")
                } else {
                    lifecycleScope.launch {
                        homeViewModel.user.observe(viewLifecycleOwner) {
                            val user = it
                            stepsCircularProgressBar.apply {
                                val steppi = user?.userSettingsInfo?.userGoals?.stepGoal?.toFloat()
                                setProgressWithAnimation(steps.toFloat())
                                steppi?.let {
                                    progressMax = steppi
                                }
                            }
                            stepcount.apply {
                                text = buildString {
                                    append(getString(R.string.steps))
                                    append(steps)
                                }
                            }

                            binding.calorieCount.text = buildString {
                                append(getString(R.string.calories))
                                append(steps / 25)
                            }
                            binding.caloriesProgressBar.apply {
                                val calori =
                                    user?.userSettingsInfo?.userGoals?.calorieGoal?.toFloat()
                                setProgressWithAnimation((steps / 25).toFloat())
                                calori?.let {
                                    progressMax = calori
                                }
                            }
                        }
                    }
                }
            }
            sleepDuration.observe(viewLifecycleOwner){
                Log.d("sleep has changed", it.toString())
                sleepLogger.text= buildString {
                    append("You have slept for ${formatDurationFromLong(it)} hours :)")
                }
            }
            homeViewModel.water.observe(viewLifecycleOwner) {
                homeViewModel.user.observe(viewLifecycleOwner) { user ->
                    textView2.text = buildString {
                        if (it != null) {
                            if (it.currentWater != null && it.currentWater != 0) {
                                Log.d("water isnt null and isnt 0", it.currentWater.toString())
                                append(it.currentWater)
                            } else {
                                Log.d("water is null", it.currentWater.toString())
                                append(0)
                            }
                        } else {
                            Log.d("water is null and isnt 0", it.toString())
                        }
                        append("/")
                        append(user?.userSettingsInfo?.userGoals?.waterGoal ?: 6)
                    }
                }
            }
            plus.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    homeViewModel.waterIncrement(1)
                }
            }
            minus.setOnClickListener {
                lifecycleScope.launch {
                    homeViewModel.waterIncrement(-1)
                }
            }

            runLayout.setOnClickListener {
                val runDialog = RunningDialogFragment()
                runDialog.show(requireActivity().supportFragmentManager, "running dialog")
            }
            cyclingLayout.setOnClickListener {
                val runDialog = RunningDialogFragment()
                runDialog.show(
                    requireActivity().supportFragmentManager, "cycling dialog"
                )
            }
            joggingLayout.setOnClickListener {
                val runDialog = RunningDialogFragment()
                runDialog.show(requireActivity().supportFragmentManager, "hiking dialog")
            }
            powerWalkingLayout.setOnClickListener {
                val runDialog = RunningDialogFragment()
                runDialog.show(
                    requireActivity().supportFragmentManager, "power walking dialog"
                )
            }

            weightRecycler.apply {
                val weightRecyclerAdapterer = WeightRecyclerAdapter(Weight.weight.toMutableList())
                adapter = weightRecyclerAdapterer
                layoutManager = LinearLayoutManager(context)
                val layoutManager = this.layoutManager as LinearLayoutManager
                var isScrolling = false
                weightRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                        super.onScrolled(recyclerView, dx, dy)
                        isScrolling = true
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            if (isScrolling) {
                                try {
                                    lifecycleScope.launch {
                                        var middleItem =
                                            (layoutManager.findFirstCompletelyVisibleItemPosition() + layoutManager.findLastCompletelyVisibleItemPosition()) / 2
                                        Log.d("middle item lookout", middleItem.toString())
                                        if (middleItem == -1) {
                                            delay(100)
                                            middleItem =
                                                (layoutManager.findFirstVisibleItemPosition() + layoutManager.findLastVisibleItemPosition()) / 2
                                            weightRecyclerAdapterer.updateMiddleItemSize(middleItem)

                                        } else {
                                            weightRecyclerAdapterer.updateMiddleItemSize(middleItem)
                                        }
                                    }
                                } catch (e: Exception) {
                                    lifecycleScope.launch {
                                        var middlePosition =
                                            (layoutManager.findFirstVisibleItemPosition() + layoutManager.findLastVisibleItemPosition()) / 2
                                        Log.d("middle item lookout", middlePosition.toString())
                                        if (middlePosition == -1) {
                                            delay(100)
                                            middlePosition =
                                                (layoutManager.findFirstVisibleItemPosition() + layoutManager.findLastVisibleItemPosition()) / 2

                                        }
                                        weightRecyclerAdapterer.updateMiddleItemSize(middlePosition)
                                    }
                                }
                                isScrolling = false
                            }
                        }
                    }


                })
            }
            secondWeightRecycler.apply {
                val weightRecyclerAdapterer =
                    WeightRecyclerAdapter(Weight.subWeight.toMutableList())
                adapter = weightRecyclerAdapterer
                layoutManager = LinearLayoutManager(context)
                val layoutManager = this.layoutManager as LinearLayoutManager
                var isScrolling = false
                secondWeightRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                        super.onScrolled(recyclerView, dx, dy)
                        isScrolling = true
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            if (isScrolling) {
                                try {
                                    val middleItem =
                                        (layoutManager.findFirstCompletelyVisibleItemPosition() + layoutManager.findLastCompletelyVisibleItemPosition()) / 2
                                    Log.d("middle item lookout", middleItem.toString())
                                    weightRecyclerAdapterer.updateMiddleItemSize(middleItem)
                                } catch (e: Exception) {
                                    val middlePosition =
                                        (layoutManager.findFirstVisibleItemPosition() + layoutManager.findLastVisibleItemPosition()) / 2
                                    weightRecyclerAdapterer.updateMiddleItemSize(middlePosition)
                                }
                                isScrolling = false
                            }
                        }
                    }


                })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}