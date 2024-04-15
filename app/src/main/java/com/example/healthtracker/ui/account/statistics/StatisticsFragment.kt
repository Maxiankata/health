package com.example.healthtracker.ui.account.statistics

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentStatisticsBinding
import com.example.healthtracker.ui.parseDurationToLong
import com.example.healthtracker.ui.setRoundedCorners
import com.example.healthtracker.ui.showBottomNav
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import kotlin.math.round

class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val statisticsViewModel: StatisticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        requireActivity().showBottomNav()
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        statisticsViewModel.getUserDays()
        statisticsViewModel.getUserUnits()
        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            statisticsViewModel.userDays.observe(viewLifecycleOwner) {
                val datetimeList = mutableListOf<String>()
                val stepsList = mutableListOf<Int>()
                val caloriesList = mutableListOf<Int>()
                val watersList = mutableListOf<Int>()
                val weightList = mutableListOf<Double>()
                val sleepList = mutableListOf<Long>()
                val activeList = mutableListOf<Long>()

                if (it != null) {
                    for (userday in it) {
                        datetimeList.add(userday!!.dateTime)
                        val steps = userday.automaticInfo?.steps?.currentSteps ?: 0
                        stepsList.add(steps)
                        val calories = userday.automaticInfo?.steps?.currentCalories ?: 0
                        caloriesList.add(calories)
                        val water = userday.putInInfo?.waterInfo?.currentWater ?: 0
                        watersList.add(water)
                        val weight = userday.putInInfo?.weight ?: 0.0
                        if (statisticsViewModel.units.value == "kg") {
                            if (userday.putInInfo?.units == "kg") {
                                weightList.add(weight)
                            }else if (userday.putInInfo?.units == "lbs") {
                                weightList.add(round((weight*0.45)*10)/10)
                            }
                        } else if (statisticsViewModel.units.value == "lbs") {
                            if (userday.putInInfo?.units == "lbs") {
                                weightList.add(weight)
                            }else if (userday.putInInfo?.units == "kg") {
                                weightList.add(round((weight*2.54)*10)/10)
                            }
                        }
                        val sleep = (userday.putInInfo?.sleepDuration ?: "00:00:00")
                        if (sleep.isNotEmpty()) {
                            sleepList.add(parseDurationToLong(sleep))
                        } else {
                            sleepList.add(parseDurationToLong("00:00:00"))
                        }
                        val activeTime = (userday.automaticInfo?.activeTime ?: 0)
                        activeList.add(activeTime)
                    }
                }
                val intChartModel =
                    AAChartModel().chartType(AAChartType.Column).title(getString(R.string.progress))
                        .titleStyle(AAStyle().color("#FFFFFF"))
                        .categories(datetimeList.toTypedArray()).backgroundColor("#000000").series(
                            arrayOf(
                                AASeriesElement().name(getString(R.string.steps))
                                    .color("#1CFEBA") //swap with R.color.lightgreen
                                    .data(stepsList.toTypedArray()),
                                AASeriesElement().name(getString(R.string.calories))
                                    .color("#FF9237") //swap with R.color.orange
                                    .data(caloriesList.toTypedArray()),
                                AASeriesElement().name(getString(R.string.water))
                                    .color("#5EABC6") // swap with R.color.blue
                                    .data(watersList.toTypedArray())
                            )
                        )
                intChart.aa_drawChartWithChartModel(intChartModel)
                intChart.setRoundedCorners(30F)
                val weightChartModel =
                    AAChartModel().chartType(AAChartType.Line).title(getString(R.string.progress))
                        .titleStyle(AAStyle().color("#FFFFFF"))
                        .categories(datetimeList.toTypedArray()).backgroundColor("#000000").series(
                            arrayOf(
                                AASeriesElement().name(getString(R.string.daily_weight))
                                    .color("#FF9237") // swap with R.color.blue
                                    .data(weightList.toTypedArray())
                            )
                        )

                weightChart.aa_drawChartWithChartModel(weightChartModel)
                weightChart.setRoundedCorners(30F)
                val timeChartModel =
                    AAChartModel().chartType(AAChartType.Area).title(getString(R.string.progress))
                        .subtitleStyle(AAStyle().color("#FFFFFF"))
                        .titleStyle(AAStyle().color("#FFFFFF"))
                        .categories(datetimeList.toTypedArray()).backgroundColor("#000000").series(
                            arrayOf(
                                AASeriesElement().name(getString(R.string.sleep))
                                    .color("#84419C") //swap with R.color.lightgreen
                                    .data(sleepList.toTypedArray()),
                                AASeriesElement().name(getString(R.string.active))
                                    .color("#FFFF00") //swap with R.color.lightgreen
                                    .data(activeList.toTypedArray()),
                            )

                        )
                timeChart.aa_drawChartWithChartModel(timeChartModel)
                timeChart.setRoundedCorners(30F)
            }
            backButton.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }

}