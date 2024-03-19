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
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.data.room.RoomToUserMegaInfoAdapter
import com.example.healthtracker.databinding.FragmentHomeBinding
import com.example.healthtracker.ui.home.running.RunningDialogFragment
import com.example.healthtracker.ui.home.running.RunningSensorListener
import com.example.healthtracker.ui.home.walking.StepCounterService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by activityViewModels()
    private val weightRecyclerAdapter: WeightRecyclerAdapter = WeightRecyclerAdapter()
    private lateinit var speedTracker: RunningSensorListener
    private var stepCount: LiveData<Int> = StepCounterService.steps

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
            homeViewModel.syncMetrics()

        }

        val weightRecyclerVal = WeightRecyclerAdapter()
        weightRecyclerAdapter.updateItems(Weight.weight.toList())
        binding.apply {
            stepCount.observe(viewLifecycleOwner) { steps ->
                stepsCircularProgressBar.apply {
//                    setProgressWithAnimation(steps.toFloat())
                    progressMax = 6000f
                }
                calorieCount.text = buildString {
                    append(getString(R.string.calories))
                    append(steps)
                }
                caloriesProgressBar.apply {
//                    setProgressWithAnimation(steps.toFloat())
                    progressMax = 240f
                }
                stepcount.apply {
                    text = buildString {
                        append(getString(R.string.steps))
                        append(steps)
                    }
                }
            }
            homeViewModel.water.observe(viewLifecycleOwner) {
                textView2.text = buildString {
                    if (it != null) {
                        if (it.currentWater != null && it.currentWater != 0) {
                            append(it.currentWater)
                        } else {
                            append(0)
                        }
                    }
                    append("/")
                    append(it?.waterGoal ?: 6)
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
                lifecycleScope.launch {
                    homeViewModel.switchActivity(UserActivityStates.CYCLING)
                }
                val runDialog = RunningDialogFragment()
                runDialog.show(
                    requireActivity().supportFragmentManager,
                    "cycling dialog"
                )
            }
            hikingLayout.setOnClickListener {
                lifecycleScope.launch {
                    homeViewModel.switchActivity(UserActivityStates.HIKING)
                }
                val runDialog = RunningDialogFragment()
                runDialog.show(requireActivity().supportFragmentManager, "hiking dialog")
            }
            powerWalkingLayout.setOnClickListener {
                lifecycleScope.launch {
                    homeViewModel.switchActivity(UserActivityStates.POWER_WALKING)
                }
                val runDialog = RunningDialogFragment()
                runDialog.show(
                    requireActivity().supportFragmentManager,
                    "power walking dialog"
                )
            }

            weightRecycler.apply {

                layoutManager = LinearLayoutManager(context)
                val fakeLayoutManager = this.layoutManager as LinearLayoutManager
                scaleImage.setOnClickListener {
                    val middleItem =
                        weightRecyclerAdapter.getItem((fakeLayoutManager.findFirstVisibleItemPosition() + fakeLayoutManager.findLastVisibleItemPosition()) / 2)
                    Log.d("Middle Item", middleItem)
                }
            }
            secondWeightRecycler.apply {
                adapter = weightRecyclerVal
                layoutManager = LinearLayoutManager(context)
                val fakeLayoutManager = this.layoutManager as LinearLayoutManager
                scaleImage.setOnClickListener {
                    val middleItem =
                        weightRecyclerAdapter.getItem((fakeLayoutManager.findFirstVisibleItemPosition() + fakeLayoutManager.findLastVisibleItemPosition()) / 2)
                    Log.d("Middle Item", middleItem)
                }
            }
        }
    }

    override fun onDestroyView() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val dao = MainActivity.getDatabaseInstance(requireContext()).dao().getEntireUser()
                val converter = RoomToUserMegaInfoAdapter()
                if (dao != null) {
                    homeViewModel.syncToFireBase(converter.adapt(dao))
                }
            }
        }
        super.onDestroyView()
        _binding = null
    }

    fun findMid() {

    }

}