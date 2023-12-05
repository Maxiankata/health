package com.example.healthtracker.ui.home

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.AnimationDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.lang.UCharacter.VerticalOrientation
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.healthtracker.databinding.FragmentHomeBinding
import com.example.healthtracker.ui.setRoundedCorners
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlinx.coroutines.delay

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel:HomeViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel.walkingStart(requireContext())

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val weightRecyclerVal = WeightRecyclerAdapter()

        binding.apply {
        homeViewModel.walkService.currentSteps.observe(viewLifecycleOwner) {
                weight.setRoundedCorners(30F)
                water.setRoundedCorners(30F)
                sleep.setRoundedCorners(30F)
                stepcount.apply {
                    text = "Steps: $it"
                    setOnLongClickListener {
                        homeViewModel.walkService.resetSteps()
                        true
                    }
                }
                stepsCircularProgressBar.apply {
                    setProgressWithAnimation(it.toFloat())
                    progressMax = 6000f
                }



            homeViewModel.walkService.caloriesBurned.observe(viewLifecycleOwner) {
                binding.apply {
                    calorieCount.text = "Calories: $it"
                    caloriesProgressBar.apply {
                        setProgressWithAnimation(it.toFloat())
                        progressMax = 240f

                    }

                }
            }
        }
            weightRecycler.apply {
                adapter = weightRecyclerVal

                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
                val fakelayoutmanager = this.layoutManager as LinearLayoutManager
                val fakeadapter = adapter

                if (fakeadapter is WeightRecyclerAdapter) {
                    scaleImage.setOnClickListener { var middleItem =
                        fakeadapter.getItem((fakelayoutmanager.findFirstVisibleItemPosition() + fakelayoutmanager.findLastVisibleItemPosition()))
                    }
                    }
            }
//            secondWeightRecycler.apply{
//                adapter = weightRecyclerVal
//                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
////                    addItemDecoration(DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation ))
//                val fakelayoutmanager = this.layoutManager as LinearLayoutManager
//                val fakeadapter = adapter
//
//                if (fakeadapter is WeightRecyclerAdapter) {
//                    var middleItem =
//                        fakeadapter.getItem((fakelayoutmanager.findFirstVisibleItemPosition() + fakelayoutmanager.findLastVisibleItemPosition()))
//                }
//            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun findMid(){

    }

}