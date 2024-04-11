package com.example.healthtracker.ui.account.statistics

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentStatisticsBinding
import com.example.healthtracker.ui.setRoundedCorners
import com.example.healthtracker.ui.showBottomNav
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAOptions
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.github.aachartmodel.aainfographics.aachartcreator.aa_toAAOptions
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAItemStyle
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AALabels
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AALegend
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AATitle
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAXAxis
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAYAxis

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
                if (it != null) {
                    for (userday in it) {
                        datetimeList.add(userday!!.dateTime)
                        val steps = userday.automaticInfo?.steps?.currentSteps ?: 0
                        stepsList.add(steps)
                        val calories = userday.automaticInfo?.steps?.currentCalories ?: 0
                        caloriesList.add(calories)
                        val water = userday.putInInfo?.waterInfo?.currentWater ?: 0
                        watersList.add(water)
                    }
                }
                val chartModel = AAChartModel()
                    .chartType(AAChartType.Column)
                    .title(getString(R.string.progress))
                    .titleStyle(AAStyle().color("#FFFFFF"))
                    .categories(datetimeList.toTypedArray())
                    .backgroundColor("#000000")
                    .series(
                        arrayOf(
                            AASeriesElement()
                                .name(getString(R.string.steps))
                                .color("#1CFEBA") //swap with R.color.lightgreen
                                .data(stepsList.toTypedArray()),
                            AASeriesElement()
                                .name(getString(R.string.calories))
                                .color("#FF9237") //swap with R.color.orange
                                .data(caloriesList.toTypedArray()),
                            AASeriesElement()
                                .name(getString(R.string.water))
                                .color("#5EABC6") // swap with R.color.blue
                                .data(watersList.toTypedArray())
                        )
                    )
                intChart.aa_drawChartWithChartModel(chartModel)
                intChart.setRoundedCorners(30F)

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