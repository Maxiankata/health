package com.example.healthtracker.ui.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentDashboardBinding
import com.example.healthtracker.ui.calendarToString
import com.example.healthtracker.ui.formatDurationFromLong
import com.example.healthtracker.ui.showBottomNav
import java.util.Calendar
import kotlin.math.round

class DashboardFragment : Fragment() {
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        requireActivity().showBottomNav()
        dashboardViewModel.getUserUnits()
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val daySelection = calendarToString(selectedDate)
                dashboardViewModel.feedDays(daySelection)

                dashboardViewModel.userDay.observe(viewLifecycleOwner) {
                    if (it != null) {
                        date.apply {
                            text = buildString {
                                append(it.dateTime)
                            }
                        }
                        stepsTaken.apply {
                            text = buildString {
                                append(getString(R.string.steps))
                                append(" ")
                                append(it.automaticInfo?.steps?.currentSteps ?: 0)
                                append("/")
                                append(it.automaticInfo?.steps?.stepsGoal)
                            }
                        }
                        caloriesBurned.apply {
                            text = buildString {
                                append("${getString(R.string.calories)} ")
                                append(it.automaticInfo?.steps?.currentCalories ?: 0)
                                append("/")
                                append(it.automaticInfo?.steps?.caloriesGoal)
                            }
                        }
                        waterGoal.apply {
                            text = buildString {
                                append(getString(R.string.water))
                                append(" ")
                                append(it.putInInfo?.waterInfo?.currentWater ?: 0)
                                append("/")
                                append(it.putInInfo?.waterInfo?.waterGoal)
                            }
                        }
                        challengesPassed.apply {
                            val challenges = it.automaticInfo?.challengesPassed
                            text = "${getString(R.string.challenges_passed)} $challenges"
                        }
                        activeTime.apply {
                            text = buildString {
                                append(getString(R.string.active_time))
                                append(it.automaticInfo?.activeTime?.let { it1 ->
                                    formatDurationFromLong(
                                        it1
                                    )
                                })
                            }
                        }
                        sleepyTime.apply {
                            text = buildString {
                                append(getString(R.string.sleep))
                                append(" ")
                                append(
                                    if (it.putInInfo?.sleepDuration.isNullOrEmpty()) {
                                        getString(R.string.time_placeholder)
                                    } else {
                                        it.putInInfo?.sleepDuration
                                    }
                                )
                            }
                        }
                        recordedWeight.apply {
                            var weight = it.putInInfo?.weight ?: 0.0
                            if (dashboardViewModel.units == "kg") {
                                if (it.putInInfo?.units == "kg") {
                                    weight = round(weight * 10) / 10
                                } else if (it.putInInfo?.units == "lbs") {
                                    weight = round((weight * 0.45) * 10) / 10
                                }
                            } else if (dashboardViewModel.units == "lbs") {
                                if (it.putInInfo?.units == "lbs") {
                                    weight = round(weight * 10) / 10
                                } else if (it.putInInfo?.units == "kg") {
                                    weight = round((weight * 2.54) * 10) / 10
                                }

                            }
                            text =  "${getString(R.string.daily_weight)} $weight ${dashboardViewModel.units}"
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