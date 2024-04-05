package com.example.healthtracker.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentDashboardBinding
import com.example.healthtracker.ui.calendarToString
import com.example.healthtracker.ui.showBottomNav
import java.util.Calendar

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
                                append(it.automaticInfo?.steps?.currentSteps ?: 0)
                                append("/")
                                append(it.automaticInfo?.steps?.stepsGoal)
                            }
                        }
                        caloriesBurned.apply {
                            text = buildString {
                                append(getString(R.string.calories))
                                append(it.automaticInfo?.steps?.currentCalories ?: 0)
                                append("/")
                                append(it.automaticInfo?.steps?.caloriesGoal)
                            }
                        }
                        waterGoal.apply {
                            text = buildString {
                                append(getString(R.string.water))
                                append(it.putInInfo?.waterInfo?.currentWater ?: 0)
                                append("/")
                                append(it.putInInfo?.waterInfo?.waterGoal)
                            }
                        }
                        challengesPassed.apply {
                            var challenges = 0
                            if (it.challenges != null) {
                                for (challenge in it.challenges) {
                                    if (challenge.challengeCompletion) {
                                        ++challenges
                                    }
                                }

                            }
                            text = buildString {
                                append(getString(R.string.challenges_passed))
                                append(challenges)
                            }
                        }
                        recordedWeight.apply {
                            text = buildString {
                                append(getString(R.string.daily_weight))
                                append(it.putInInfo?.weight)
                            }
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