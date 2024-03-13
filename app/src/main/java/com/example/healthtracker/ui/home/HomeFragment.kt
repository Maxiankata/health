package com.example.healthtracker.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthtracker.R
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.databinding.FragmentHomeBinding
import com.example.healthtracker.ui.account.friends.popup.FriendsDialogFragment
import com.example.healthtracker.ui.home.running.RunningDialogFragment
import com.example.healthtracker.ui.home.running.RunningService
import com.example.healthtracker.ui.home.walking.WalkService
import com.example.healthtracker.ui.home.walking.WalkViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val walkViewModel: WalkViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val weightRecyclerAdapter : WeightRecyclerAdapter = WeightRecyclerAdapter()
    private lateinit var speedTracker: RunningService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            speedTracker = RunningService(requireContext())
            walkViewModel.walkStart()
            homeViewModel.feedUser()

//            if (UserMegaInfo.currentUser.value?.userSettingsInfo?.units == "kg"){
//            }
        }
        lateinit var dialog:Fragment
        val weightRecyclerVal = WeightRecyclerAdapter()
        weightRecyclerAdapter.updateItems(Weight.weight.toList())
        binding.apply {
            walkViewModel.walkService.currentSteps.observe(viewLifecycleOwner) {
                lifecycleScope.launch {
                    UserMegaInfo.currentUser.value?.let {
                        val user = homeViewModel.getUser()
                    }
//                    walkViewModel.walkService.writeSteps()
                }

                stepcount.apply {
                    text = buildString {
                        append(getString(R.string.steps))
                        append(it)
                    }
                    setOnLongClickListener {
                        lifecycleScope.launch {
                            walkViewModel.walkService.nullifySteps()
                        }
                        true
                    }
                }
                stepsCircularProgressBar.apply {
                    setProgressWithAnimation(it.toFloat())
                    progressMax = 6000f
                }
                walkViewModel.walkService.caloriesBurned.observe(viewLifecycleOwner) {
                    binding.apply {
                        calorieCount.text = buildString {
                            append(getString(R.string.calories))
                            append(it)
                        }
                        caloriesProgressBar.apply {
                            setProgressWithAnimation(it.toFloat())
                            progressMax = 240f
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
                        } else {
                        }
                        append("/")
                        append(it?.waterGoal ?: 6)
                    }
                }
                plus.setOnClickListener {
                    viewLifecycleOwner.lifecycleScope.launch {
                        homeViewModel.waterIncrement(1)
                        Log.d("INCREMENTED", "INCREMENTED")
                    }
                }
                minus.setOnClickListener {
                    lifecycleScope.launch {
                        homeViewModel.waterIncrement(-1)
                        Log.d("DECREMENTED", "DECREMENTED")
                    }

                }
                homeViewModel.currentService.observe(viewLifecycleOwner) {

                runLayout.setOnClickListener {
                        lifecycleScope.launch {
                            homeViewModel.switchActivity(UserActivityStates.RUNNING)
                        }
                        val runDialog = RunningDialogFragment()
                        runDialog.show(requireActivity().supportFragmentManager, "running dialog")
                }
                cyclingLayout.setOnClickListener {
                    if (it.equals(WalkService(requireContext()))) {
                        lifecycleScope.launch {
                            homeViewModel.switchActivity(UserActivityStates.CYCLING)
                        }
                        val runDialog = RunningDialogFragment()
                        runDialog.show(requireActivity().supportFragmentManager, "cycling dialog")
                    }
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
                    runDialog.show(requireActivity().supportFragmentManager, "power walking dialog")
                }
            }
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
            secondWeightRecycler.apply{
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
            UserMegaInfo.getCurrentUser()?.let { homeViewModel.syncToFireBase(it) }
        }
        super.onDestroyView()
        _binding = null
    }

    fun findMid() {

    }

}