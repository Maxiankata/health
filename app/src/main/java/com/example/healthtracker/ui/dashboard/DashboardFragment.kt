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

                Log.d("Calendar clicked", "Lul"


                )
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year,month,dayOfMonth)
                Log.d("Selected date", selectedDate.toString())
                val daate = java.time.LocalDateTime.now()
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                Toast.makeText(requireContext(), "Selected date: $formattedDate", Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    dashboardViewModel.feedDay(selectedDate.time)
                }
                dashboardViewModel.userDay.observe(viewLifecycleOwner){
                    date.apply {
                        text = buildString {
                            append(R.string.date)
//                            append(it?.dateTime)
                        }
                    }
                    stepsTaken.apply {
                        text = buildString {
                            append(getString(R.string.steps))
                            append(it?.automaticInfo?.steps?:0)
                        }
                    }
                    caloriesBurned.apply {
                        text = buildString {
                            append(getString(R.string.calories))
//                            append(it?.automaticInfo??:0)
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