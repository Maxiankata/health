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
import com.example.healthtracker.databinding.FragmentHomeBinding
import com.mikhaellopez.circularprogressbar.CircularProgressBar

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
        homeViewModel.walkService.currentSteps.observe(viewLifecycleOwner) {
            binding.apply {
                stepcount.apply {
                    text = "Steps: $it"
                    setOnLongClickListener {
                        homeViewModel.walkService.resetSteps()
                        true
                    }
                }
                stepsCircularProgressBar.apply{
                    setProgressWithAnimation(it.toFloat())
                    progressMax = 6000f
                }
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}