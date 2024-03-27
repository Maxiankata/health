package com.example.healthtracker.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.healthtracker.MyApplication
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentDashboardBinding
import com.example.healthtracker.ui.calendarToString
import com.example.healthtracker.ui.showBottomNav
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.LocalDateTime
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DashboardFragment : Fragment() {
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        requireActivity().showBottomNav()
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year,month,dayOfMonth)
                val daySelection = calendarToString(selectedDate)
                Log.d("Selected date", daySelection)
                Toast.makeText(requireContext(), "Selected date: $daySelection", Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    dashboardViewModel.feedDay(daySelection)
                }
                dashboardViewModel.userDay.observe(viewLifecycleOwner){
                    Log.d("Userday", it.toString())
                    date.apply {
                        text = buildString {
                            append(it?.dateTime)
                        }
                    }
                    stepsTaken.apply {
                        text = buildString {
                            append(getString(R.string.steps))
                            append(it?.automaticInfo?.steps?.currentSteps?:0)
                        }
                    }
                    caloriesBurned.apply {
                        text = buildString {
                            append(getString(R.string.calories))
                            append(it?.automaticInfo?.steps?.currentCalories?:0)
                        }
                    }
                    challengesPassed.apply {
                        text = buildString {
                            append(getString(R.string.challenges_passed))
                            append(it?.challenges?.size?:0)
                        }
                    }
                    recordedWeight.apply {
                        text = buildString {
                            append(getString(R.string.daily_weight))
                            append(it?.putInInfo?.weight)
                        }
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