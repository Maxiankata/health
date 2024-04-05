package com.example.healthtracker.ui.home.running

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.healthtracker.MyApplication
import com.example.healthtracker.R
import com.example.healthtracker.databinding.RunningDialogBinding
import com.example.healthtracker.ui.durationToString
import com.example.healthtracker.ui.home.speeder.ActivityEnum
import com.example.healthtracker.ui.home.speeder.SpeederService
import com.example.healthtracker.ui.parseDurationToLong
import com.example.healthtracker.ui.startSpeeder
import java.time.Duration
import java.util.Timer

class RunningDialogFragment : DialogFragment() {
    private var _binding: RunningDialogBinding? = null
    private val binding get() = _binding!!
    private val timer = Timer()
    private lateinit var dialogTag: ActivityEnum

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = RunningDialogBinding.inflate(inflater, container, false)
        Log.d("tag is", tag.toString())
        dialogTag = when (tag) {
            "running" -> ActivityEnum.RUNNING
            "walking" -> ActivityEnum.WALKING
            "jogging" -> ActivityEnum.JOGGING
            "cycling" -> ActivityEnum.CYCLING
            else -> {
                ActivityEnum.WALKING
            }
        }
        Log.d("dialog tag is", dialogTag.name)
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

        binding.apply {
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
                    Log.d("DurationText is null", duration.toString())
                    Toast.makeText(
                        MyApplication.getContext(), R.string.empty_field, Toast.LENGTH_SHORT
                    ).show()
                } else {
                    timePicker.visibility = View.VISIBLE
                    editTextHours.apply {
                        visibility = View.GONE
                        text.clear()
                    }
                    editTextMinutes.apply {
                        visibility = View.GONE
                        text.clear()
                    }
                    editTextHours.apply {
                        visibility = View.GONE
                        text.clear()
                    }
                    val tag: ActivityEnum = when (tag) {
                        "running" -> ActivityEnum.RUNNING
                        "walking" -> ActivityEnum.WALKING
                        "jogging" -> ActivityEnum.JOGGING
                        "cycling" -> ActivityEnum.CYCLING
                        else -> {
                            ActivityEnum.WALKING
                        }
                    }
                    startSpeeder(stringDuration, dialogTag)

                }
            }
            timePicker.text = "00:00:00"
            SpeederService.time.observe(viewLifecycleOwner) {
                timePicker.text = it
                timer.progressMax = parseDurationToLong(SpeederService.timey).toFloat()
                timer.setProgressWithAnimation(parseDurationToLong(it).toFloat())
                if (it.equals("00:00:00")) {
                    timePicker.text = "completed"
                    editTextHours.visibility = View.VISIBLE
                    editTextMinutes.visibility = View.VISIBLE
                    editTextSeconds.visibility = View.VISIBLE
                    timePicker.visibility = View.GONE
                } else {
                    timePicker.visibility = View.VISIBLE
                    editTextHours.visibility = View.GONE
                    editTextMinutes.visibility = View.GONE
                    editTextSeconds.visibility = View.GONE
                }
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


}