package com.example.healthtracker.ui.settings

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.databinding.FragmentSettingsBinding
import com.example.healthtracker.databinding.RgbPickerDialogBinding
import com.example.healthtracker.ui.hideBottomNav
import kotlinx.coroutines.launch


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val rgbPickerDialogBinding: RgbPickerDialogBinding by lazy {
        RgbPickerDialogBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        requireActivity().hideBottomNav()
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            val rgbDialog = Dialog(requireContext()).apply{
                setContentView(rgbPickerDialogBinding.root)
                window!!.setLayout(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setCancelable(true)
            }
            backButton.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
            rgbPickerDialogBinding.pick.setOnClickListener {
                val user = UserMegaInfo.currentUser.value
                user?.userInfo = UserInfo(theme = setRgbColor())
                Log.d("User Background Color", user?.userInfo?.theme.toString())
                if (user != null) {
                    UserMegaInfo.setCurrentUser(user)
                }
                rgbDialog.dismiss()
            }
            setOnSeekBar(
                "R",
                rgbPickerDialogBinding.redLayout.typeTxt,
                rgbPickerDialogBinding.redLayout.seekBar,
                rgbPickerDialogBinding.redLayout.colorValueText,

                )
            setOnSeekBar(
                "G",
                rgbPickerDialogBinding.greenLayout.typeTxt,
                rgbPickerDialogBinding.greenLayout.seekBar,
                rgbPickerDialogBinding.greenLayout.colorValueText,

                )
            setOnSeekBar(
                "B",
                rgbPickerDialogBinding.blueLayout.typeTxt,
                rgbPickerDialogBinding.blueLayout.seekBar,
                rgbPickerDialogBinding.blueLayout.colorValueText,

                )
            rgbPickerDialogBinding.cancel.setOnClickListener {
                rgbDialog.dismiss()
            }
            changeBackground.setOnClickListener {
                rgbDialog.show()
            }
            deleteAccount.setOnClickListener {
                deletionConfirmation()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setOnSeekBar(type:String, typeTxt: TextView, seekBar: SeekBar, colorTxt: TextView){
        typeTxt.text=type
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                colorTxt.text=seekBar.progress.toString()
                setRgbColor()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        colorTxt.text = seekBar.progress.toString()
    }
    private fun setRgbColor():String{
        val hex = String.format(
            "#%02x%02x%02x",
            rgbPickerDialogBinding.redLayout.seekBar.progress,
            rgbPickerDialogBinding.greenLayout.seekBar.progress,
            rgbPickerDialogBinding.blueLayout.seekBar.progress
        )
        rgbPickerDialogBinding.colorView.setBackgroundColor(Color.parseColor(hex))
        return hex
    }
    private fun deletionConfirmation(){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Confirmation Dialog")
            .setMessage("Do you want to proceed with the action?")
        builder.setPositiveButton("Confirm") { dialogInterface: DialogInterface, i: Int ->
            lifecycleScope.launch {
                settingsViewModel.deleteUser()
            }
            dialogInterface.dismiss()

        }
        builder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}