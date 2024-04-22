package com.example.healthtracker.ui.home.running

import android.graphics.Color.YELLOW
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.text.toLowerCase
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.healthtracker.MyApplication
import com.example.healthtracker.R
import com.example.healthtracker.databinding.RunningDialogBinding
import com.example.healthtracker.ui.durationToString
import com.example.healthtracker.ui.home.speeder.ActivityEnum
import com.example.healthtracker.ui.home.speeder.SpeederService
import com.example.healthtracker.ui.home.speeder.SpeederServiceBoolean
import com.example.healthtracker.ui.parseDurationToLong
import com.example.healthtracker.ui.startSpeeder
import com.example.healthtracker.ui.stopSpeeder
import java.time.Duration
import java.util.Locale
import java.util.Timer
import kotlin.math.round

class RunningDialogFragment : DialogFragment() {
    private var _binding: RunningDialogBinding? = null
    private val binding get() = _binding!!
    private val timer = Timer()
    private lateinit var dialogTag: ActivityEnum
    private val runningDialogViewModel : RunningDialogViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = RunningDialogBinding.inflate(inflater, container, false)
        Log.d("tag is", tag?.lowercase().toString())
        dialogTag = when (tag?.lowercase(Locale.ROOT)) {
            "running" -> ActivityEnum.RUNNING
            "walking" -> ActivityEnum.WALKING
            "jogging" -> ActivityEnum.JOGGING
            "cycling" -> ActivityEnum.CYCLING
            else -> {
                ActivityEnum.WALKING
            }
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.custom_rounded_background)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runningDialogViewModel.getMetric()
        binding.apply {
            cancel.setBackgroundColor(YELLOW)
            SpeederServiceBoolean.isMyServiceRunning.observe(viewLifecycleOwner){
                if (it){
                    start.visibility=GONE
                    cancel.setOnClickListener {
                        stopSpeeder()
                        timer.setProgressWithAnimation(0F)
                    }
                }else{
                    start.visibility= VISIBLE
                    cancel.setOnClickListener {
                        dismiss()
                    }
                }
            }
            timePicker.visibility = GONE
            start.setBackgroundColor(YELLOW)
            start.setOnClickListener {
                val hours = editTextHours.text.toString().toIntOrNull() ?: 0
                val minutes = editTextMinutes.text.toString().toIntOrNull() ?: 0
                val seconds = editTextSeconds.text.toString().toIntOrNull() ?: 0
                val duration = Duration.ofHours(hours.toLong()).plusMinutes(minutes.toLong())
                    .plusSeconds(seconds.toLong())
                val stringDuration = durationToString(duration)
                if (editTextHours.text.toString().isBlank() && editTextMinutes.text.toString()
                        .isEmpty() && editTextSeconds.text.toString().isEmpty()
                ) {
                    Toast.makeText(
                        MyApplication.getContext(), R.string.empty_field, Toast.LENGTH_SHORT
                    ).show()
                } else {
                    timePicker.visibility = View.VISIBLE
                    editTextHours.apply {
                        clearFocus()
                        text.clear()
                    }
                    editTextMinutes.apply {
                        clearFocus()
                        text.clear()
                    }
                    editTextHours.apply {
                        clearFocus()
                        text.clear()
                    }
                    startSpeeder(stringDuration, dialogTag, challengeId = null)

                }
            }
            timePicker.text = getString(R.string.empty_timer)
            SpeederServiceBoolean.isMyServiceRunning.observe(viewLifecycleOwner
            ){
                if (!it) {
                    speed.visibility = View.GONE
                    timePicker.text = getString(R.string.complete)
                    editTextHours.visibility = View.VISIBLE
                    editTextMinutes.visibility = View.VISIBLE
                    editTextSeconds.visibility = View.VISIBLE
                    timePicker.visibility = View.GONE
                } else {
                    speed.visibility = View.VISIBLE
                    timePicker.visibility = View.VISIBLE
                    editTextHours.visibility = View.GONE
                    editTextMinutes.visibility = View.GONE
                    editTextSeconds.visibility = View.GONE
                }
            }

            SpeederService.speed.observe(viewLifecycleOwner){ speeder->
                runningDialogViewModel.userMetric.observe(viewLifecycleOwner) {
                    speed.text = buildString {
                        append("${round(speeder * 10) / 10}")
                        if (it == "kg") {
                            append("kph")
                        } else {
                            append("mph")
                        }
                    }
                }
            }
            SpeederService.time.observe(viewLifecycleOwner) {
                timePicker.text = it
                timer.progressMax = parseDurationToLong(SpeederService.timey).toFloat()
                timer.setProgressWithAnimation(parseDurationToLong(it).toFloat())
                timer.progressBarColor = YELLOW
            }
            when(dialogTag){
                ActivityEnum.RUNNING -> {
                    topIcon.setImageResource(R.drawable.running_icon)
                    topText.setText(R.string.running)
                }
                ActivityEnum.JOGGING -> {
                    topIcon.setImageResource(R.drawable.jogging_icon)
                    topText.setText(R.string.jogging)
                }
                ActivityEnum.WALKING -> {
                    topIcon.setImageResource(R.drawable.power_walking_icon)
                    topText.setText(R.string.power_walking)
                }
                ActivityEnum.CYCLING -> {
                    topIcon.setImageResource(R.drawable.cycling_icon)
                    topText.setText(R.string.cycling)
                }
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
