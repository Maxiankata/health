package com.example.healthtracker.ui.account.friends.challenges

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.healthtracker.MyApplication
import com.example.healthtracker.R
import com.example.healthtracker.databinding.ChallengeBuilderDialogBinding
import com.example.healthtracker.ui.account.friends.profile.ChallengeSpinnerAdapter
import com.example.healthtracker.ui.durationToString
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.format.DateTimeParseException

class ChallengeBuilderDialogFragment : DialogFragment() {
    private var _binding: ChallengeBuilderDialogBinding? = null
    private val binding get() = _binding!!
    private val challengeBuilderDialogViewModel = ChallengeBuilderDialogViewModel(MyApplication())
    private lateinit var userId: String
    private var assigner: String? = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        userId = arguments?.getString("uid")!!
        Log.d("bundle recieved by dialog", userId.toString())

        _binding = ChallengeBuilderDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.custom_rounded_background)
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            assigner = challengeBuilderDialogViewModel.getAssigner()
        }
        binding.apply {
            val items = listOf(
                SpinnerItem(getString(R.string.running), R.drawable.running_icon, 1),
                SpinnerItem(getString(R.string.cycling), R.drawable.cycling_icon, 2),
                SpinnerItem(getString(R.string.jogging), R.drawable.jogging_icon, 3),
                SpinnerItem(getString(R.string.power_walking), R.drawable.power_walking_icon, 4),

                )
            val adapter = ChallengeSpinnerAdapter(MyApplication.getContext(), items)
            challengeTypeSpinner.apply {
                background = ColorDrawable(Color.TRANSPARENT)
                this.adapter = adapter
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?, view: View?, position: Int, id: Long
                    ) {
                        challengeTypeSpinner.setSelection(position)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                }
            }
            submitButton.setOnClickListener {
                val hours = editTextHours.text.toString().toIntOrNull() ?: 0
                val minutes = editTextMinutes.text.toString().toIntOrNull() ?: 0
                val seconds = editTextSeconds.text.toString().toIntOrNull() ?: 0
                val duration = Duration.ofHours(hours.toLong()).plusMinutes(minutes.toLong())
                    .plusSeconds(seconds.toLong())
                val stringDuration = durationToString(duration)
                if (challengeTypeSpinner.selectedItem == null || (editTextHours.text.toString()
                        .isBlank() && editTextMinutes.text.toString()
                        .isEmpty() && editTextSeconds.text.toString().isEmpty())
                ) {
                    Log.d("DurationText is null", duration.toString())
                    Toast.makeText(
                        MyApplication.getContext(), R.string.empty_field, Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.d("DurationText isnt null", stringDuration)
                    Toast.makeText(MyApplication.getContext(), "both are fine", Toast.LENGTH_SHORT)
                        .show()
                    val selectedItem = challengeTypeSpinner.selectedItem as SpinnerItem
                    val challengeTypeId = selectedItem.itemId
                    try {
                        val activityType: ChallengeType = when (challengeTypeId) {
                            1 -> ChallengeType.RUNNING
                            2 -> ChallengeType.CYCLING
                            3 -> ChallengeType.JOGGING
                            4 -> ChallengeType.POWER_WALKING
                            else -> {
                                ChallengeType.WALKING
                            }
                        }
                        val challenge = Challenge(
                            challengeCompletion = false,
                            challengeDuration = stringDuration,
                            challengeType = activityType,
                            assigner = assigner!!
                        )
                        lifecycleScope.launch {
                            challengeBuilderDialogViewModel.sendChallenge(userId, challenge)
                        }
                    } catch (e: DateTimeParseException) {
                        Toast.makeText(
                            MyApplication.getContext(),
                            R.string.invalid_duration_format,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            cancelButton.setOnClickListener {
                editTextHours.text = null
                editTextMinutes.text = null
                editTextSeconds.text = null
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}