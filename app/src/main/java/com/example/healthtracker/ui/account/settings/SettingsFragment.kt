package com.example.healthtracker.ui.account.settings

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.healthtracker.MyApplication
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentSettingsBinding
import com.example.healthtracker.ui.account.settings.colorchange.ColorChangerDialog
import com.example.healthtracker.ui.account.settings.goals.GoalChangeDialogFragment
import com.example.healthtracker.ui.hideBottomNav
import com.example.healthtracker.ui.isInternetAvailable
import com.example.healthtracker.ui.login.LoginActivity
import com.example.healthtracker.ui.navigateToActivity
import kotlinx.coroutines.launch


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val settingsViewModel: SettingsViewModel by viewModels()

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
            backButton.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
            changeBackground.setOnClickListener {
                val rgbDialog = ColorChangerDialog()
                rgbDialog.show(requireActivity().supportFragmentManager, "change_goals_dialog")
            }
            deleteAccount.setOnClickListener {
                deletionConfirmation()
            }
            changeGoals.setOnClickListener {
                val dialog = GoalChangeDialogFragment()
                dialog.show(requireActivity().supportFragmentManager, "change_goals_dialog")
            }
            changeLanguage.setOnClickListener {
                val dialog = LanguageChangeDialogFragment()
                dialog.show(requireActivity().supportFragmentManager, "change_language_dialog")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun deletionConfirmation(){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.are_you_sure)
            .setMessage(R.string.deletion_confirmation)
        builder.setPositiveButton(R.string.confirm) { dialogInterface: DialogInterface, i: Int ->
            if (isInternetAvailable(MyApplication.getContext())){
                lifecycleScope.launch {
                    settingsViewModel.deleteUser()
                    navigateToActivity(requireActivity(), LoginActivity::class.java)
                }
                dialogInterface.dismiss()
            }else{
                Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show()
            }

        }
        builder.setNegativeButton(R.string.cancel) { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}