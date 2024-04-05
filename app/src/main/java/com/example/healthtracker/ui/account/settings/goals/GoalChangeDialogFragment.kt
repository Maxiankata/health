package com.example.healthtracker.ui.account.settings.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.example.healthtracker.MyApplication
import com.example.healthtracker.R
import com.example.healthtracker.data.user.UserGoals
import com.example.healthtracker.databinding.GoalChangeDialogBinding
import kotlinx.coroutines.launch

class GoalChangeDialogFragment:DialogFragment() {
    private var _binding: GoalChangeDialogBinding? = null
    private val binding get() = _binding!!
    private val goalChangeDialogViewModel = GoalChangeDialogViewModel(MyApplication())
    var userGoals: LiveData<UserGoals?> = GoalChangeDialogViewModel.userGoals
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GoalChangeDialogBinding.inflate(inflater, container, false)
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
        lifecycleScope.launch {
            goalChangeDialogViewModel.getUserGoals()
        }
        binding.apply {
            userGoals.observe(viewLifecycleOwner) {userGoals->
                if (userGoals != null) {
                    stepGoalEdit.hint = userGoals.stepGoal.toString()
                    calorieGoalEdit.hint = userGoals.calorieGoal.toString()
                    waterGoalEdit.hint = userGoals.waterGoal.toString()
                    sleepGoal.hint = userGoals.sleepGoal.toString()
                    stepSubmit.setOnClickListener {
                        if (stepGoalEdit.text.isNullOrEmpty()) {  //put an empty string check ""
                            Toast.makeText(MyApplication.getContext(),getString(R.string.empty_field), Toast.LENGTH_SHORT).show()
                        }else{
                            lifecycleScope.launch {
                                val stepGoal = stepGoalEdit.text.toString()
                                val usererGoals = userGoals
                                usererGoals.stepGoal = stepGoal.toInt()
                                goalChangeDialogViewModel.updateGoal(usererGoals)
                            }
                        }

                    }
                    calorieSubmit.setOnClickListener{
                        if (calorieGoalEdit.text.isNullOrEmpty()) {
                            Toast.makeText(MyApplication.getContext(),getString(R.string.empty_field), Toast.LENGTH_SHORT).show()
                        }else{
                            lifecycleScope.launch {
                                val calorieGoal = calorieGoalEdit.text.toString()
                                val usererGoals = userGoals
                                usererGoals.calorieGoal = calorieGoal.toInt()
                                goalChangeDialogViewModel.updateGoal(usererGoals)
                            }
                        }
                    }
                    waterSubmit.setOnClickListener{
                        if (waterGoalEdit.text.isNullOrEmpty()) {
                            Toast.makeText(MyApplication.getContext(),getString(R.string.empty_field), Toast.LENGTH_SHORT).show()
                        }else{
                            lifecycleScope.launch {
                            val waterGoal= waterGoalEdit.text.toString()
                            val usererGoals = userGoals
                            usererGoals.waterGoal = waterGoal.toInt()
                                goalChangeDialogViewModel.updateGoal(usererGoals)
                            }
                        }
                    }
                    sleepSubmit.setOnClickListener{
                        if (sleepGoalEdit.text.isNullOrEmpty()) {
                            Toast.makeText(MyApplication.getContext(),getString(R.string.empty_field), Toast.LENGTH_SHORT).show()

                        }else{
                            lifecycleScope.launch {
                                val stepGoal = sleepGoalEdit.text.toString()
                                val usererGoals = userGoals
                                usererGoals.sleepGoal= stepGoal.toDouble()
                                goalChangeDialogViewModel.updateGoal(usererGoals)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding=null
        super.onDestroyView()
    }
}