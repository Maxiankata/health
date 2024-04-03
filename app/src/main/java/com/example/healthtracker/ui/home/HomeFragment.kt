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
    var weightRecyclerAdapterer= WeightRecyclerAdapter(Weight.weight.toMutableList())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            speedTracker = RunningSensorListener(requireContext())
            homeViewModel.feedUser()
            homeViewModel.checkForChallenges()
        }
//        homeViewModel.syncToFireBase()
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
            sleepDuration.observe(viewLifecycleOwner) {
                sleepLogger.text = buildString {
                    append("You have slept for ${formatDurationFromLong(it)} hours :)")
                }
            }
            homeViewModel.water.observe(viewLifecycleOwner) {
                homeViewModel.user.observe(viewLifecycleOwner) { user ->
                    textView2.text = buildString {
                        if (it != null) {
                            if (it.currentWater != null && it.currentWater != 0) {
                                append(it.currentWater)
                            } else {
                                append(0)
                            }
                        }
                        append("/")
                        append(user?.userSettingsInfo?.userGoals?.waterGoal ?: 6)
                    }
//                    val weight: Double = user?.userPutInInfo?.weight!!
//                    val integer: Int = weight.toInt()
//                    val decimal: Int = ((weight- integer)*10).toInt()
//                    if (integer<weightRecyclerAdapterer.itemCount) {
//                        weightRecycler.scrollToPosition(integer)
//                    }else{
//                        weightRecycler.scrollToPosition(0)
//                    }
//                    secondWeightRecycler.scrollToPosition(decimal)
                    if (user != null) {
                        if (user.userSettingsInfo?.units.toString()==getString(R.string.kg)){
                            weightRecyclerAdapterer= WeightRecyclerAdapter(Weight.weight.toMutableList())
                            units.text = getString(R.string.kg)
                        }else if (user.userSettingsInfo?.units.toString()==getString(R.string.lbs)){
                            weightRecyclerAdapterer= WeightRecyclerAdapter(Weight.eagleBeerWeight.toMutableList())
                            units.text = getString(R.string.lbs)
                        }
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
                RunningDialogFragment().show(requireActivity().supportFragmentManager, "running dialog")
            }
            cyclingLayout.setOnClickListener {
                RunningDialogFragment().show(
                    requireActivity().supportFragmentManager, "cycling dialog"
                )
            }
            joggingLayout.setOnClickListener {
                RunningDialogFragment().show(requireActivity().supportFragmentManager, "hiking dialog")
            }
            powerWalkingLayout.setOnClickListener {
                RunningDialogFragment().show(requireActivity().supportFragmentManager, "hiking dialog")

            }
            var mainUnits: Int = 0

            weightRecycler.apply {
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


                                        if (middleItem == -1) {
                                            delay(100)
                                            middleItem =
                                                (layoutManager.findFirstVisibleItemPosition() + layoutManager.findLastVisibleItemPosition()) / 2
                                            weightRecyclerAdapterer.updateMiddleItemSize(middleItem)
                                            mainUnits = middleItem
                                        }
                                        weightRecyclerAdapterer.updateMiddleItemSize(middleItem)
                                        mainUnits = middleItem
                                    }
                                } catch (e: Exception) {
                                    lifecycleScope.launch {
                                        var middlePosition =
                                            (layoutManager.findFirstVisibleItemPosition() + layoutManager.findLastVisibleItemPosition()) / 2
                                        if (middlePosition == -1) {
                                            delay(100)
                                            middlePosition =
                                                (layoutManager.findFirstVisibleItemPosition() + layoutManager.findLastVisibleItemPosition()) / 2
                                            mainUnits = middlePosition

                                        }
                                        weightRecyclerAdapterer.updateMiddleItemSize(middlePosition)
                                        mainUnits = middlePosition
                                    }
                                }
                                isScrolling = false
                            }
                        }
                    }


                })
            }
            var subUnits: Int = 0
            secondWeightRecycler.apply {
                val secondWeightRecyclerAdapter =
                    WeightRecyclerAdapter(Weight.subWeight.toMutableList())
                adapter = secondWeightRecyclerAdapter
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
                                    secondWeightRecyclerAdapter.updateMiddleItemSize(middleItem)
                                    subUnits = middleItem
                                } catch (e: Exception) {
                                    val middlePosition =
                                        (layoutManager.findFirstVisibleItemPosition() + layoutManager.findLastVisibleItemPosition()) / 2
                                    secondWeightRecyclerAdapter.updateMiddleItemSize(middlePosition)
                                    subUnits = middlePosition
                                }
                                isScrolling = false
                            }
                        }
                    }


                })
            }
            applyChanges.setOnClickListener {
                if (mainUnits != 0 || subUnits != 0) {
                    val weight: Double = mainUnits + (subUnits.toDouble() / 10)
                    Log.d("current selected weight is", weight.toString())
                    homeViewModel.updatePutInInfo(weight)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}