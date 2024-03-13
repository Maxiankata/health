package com.example.healthtracker.ui.home.running

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.healthtracker.R
import com.example.healthtracker.databinding.RunningDialogBinding
import java.util.Date
import java.util.Timer
import java.util.TimerTask

class RunningDialogFragment: DialogFragment() {
    private var _binding: RunningDialogBinding? = null
    private val binding get() = _binding!!
    lateinit var timeHelper : TimeHelper
    private val timer = Timer()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        timeHelper= TimeHelper(requireContext())
        _binding = RunningDialogBinding.inflate(inflater, container, false)
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

            timer.apply {
//                setProgressWithAnimation(it.toFloat())
//                progressMax = 6000f
            }
            start.setOnClickListener {
                startStopAction()
            }
            cancel.setOnClickListener {
                dismiss()
            }
            timerInput.apply {
                visibility = GONE
            }
            if (timeHelper.timerCounting()){
                startTimer()
            }else{
                stopTimer()
                if(timeHelper.startTime()!=null && timeHelper.stopTime()!=null){
                    val time = Date().time - restartTime().time
                    timePicker.text= timeStringFromLong(time)
                }
            }
        }
        timer.scheduleAtFixedRate(TimeTask(), 0, 500)
    }

    private inner class TimeTask:TimerTask(){
        override fun run() {
            if (timeHelper.timerCounting()){
                val time = Date().time - timeHelper.startTime()!!.time
                activity?.runOnUiThread {
                    binding.timePicker.text = timeStringFromLong(time)
                }
            }
        }

    }
    private fun startStopAction() {
        if (timeHelper.timerCounting()){
            timeHelper.setStopTime(Date())
            stopTimer()
        }else{
            if (timeHelper.stopTime()!=null){
                timeHelper.setStartTime(restartTime())
                timeHelper.setStopTime(null)
            }else{
                timeHelper.setStartTime(Date())
            }
            startTimer()
        }
    }

    private fun restartTime(): Date {
        val diff = timeHelper.startTime()!!.time - timeHelper.stopTime()!!.time
        return Date(System.currentTimeMillis()+diff)
    }

    private fun resetTimer(){
        timeHelper.setStartTime(null)
        timeHelper.setStopTime(null)
        stopTimer()
        binding.timePicker.text = timeStringFromLong(0)
    }

    private fun timeStringFromLong(time:Long): String{
            val seconds = (time/1000)%60
        val minutes = ((time / (1000 * 60)) % 60)
        val hours = ((time / (1000 * 60 * 60)) % 24)
        return makeTimeString(seconds,minutes,hours)

    }

    private fun makeTimeString(seconds: Long, minutes: Long, hours: Long): String {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun startTimer() {
        timeHelper.setTimerCounting(true)
        binding.start.text = getString(R.string.stop)
    }
    private fun stopTimer() {
        timeHelper.setTimerCounting(false)
        binding.start.text = getString(R.string.start)
    }
}