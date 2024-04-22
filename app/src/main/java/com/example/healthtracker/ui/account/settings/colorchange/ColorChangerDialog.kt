package com.example.healthtracker.ui.account.settings.colorchange

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.healthtracker.R
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.databinding.RgbPickerDialogBinding

class ColorChangerDialog : DialogFragment() {
    private var _binding: RgbPickerDialogBinding? = null
    private val binding get() = _binding!!
    private val colorChangerViewModel: ColorChangerViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = RgbPickerDialogBinding.inflate(inflater, container, false)
        colorChangerViewModel.getUser()
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
            pick.setOnClickListener {
                colorChangerViewModel.user.observe(viewLifecycleOwner) { user ->
                    if (user != null) {
                        val usurper = UserInfo(
                            username = user.username,
                            uid = user.uid,
                            image = user.image,
                            mail = user.mail,
                            bgImage = user.bgImage,
                            theme = setRgbColor(),
                            totalSteps = user.totalSteps
                        )
                        colorChangerViewModel.updateUser(usurper)
                    }
                }
                dismiss()
            }

            setOnSeekBar(
                "R",
                redLayout.typeTxt,
                redLayout.seekBar,
                redLayout.colorValueText,

                )
            setOnSeekBar(
                "G",
                greenLayout.typeTxt,
                greenLayout.seekBar,
                greenLayout.colorValueText,

                )
            setOnSeekBar(
                "B",
                blueLayout.typeTxt,
                blueLayout.seekBar,
                blueLayout.colorValueText,
            )
        }
    }

    private fun setOnSeekBar(
        type: String,
        typeTxt: TextView,
        seekBar: SeekBar,
        colorTxt: TextView
    ) {
        typeTxt.text = type
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                colorTxt.text = seekBar.progress.toString()
                setRgbColor()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        colorTxt.text = seekBar.progress.toString()
    }

    private fun setRgbColor(): String {
        val hex = String.format(
            "#%02x%02x%02x",
            binding.redLayout.seekBar.progress,
            binding.greenLayout.seekBar.progress,
            binding.blueLayout.seekBar.progress
        )
        binding.colorView.setBackgroundColor(Color.parseColor(hex))
        return hex
    }

}