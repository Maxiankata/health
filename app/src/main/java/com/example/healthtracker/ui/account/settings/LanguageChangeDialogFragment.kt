package com.example.healthtracker.ui.account.settings

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.healthtracker.MyApplication
import com.example.healthtracker.databinding.ChangeLanguageDialogBinding

class LanguageChangeDialogFragment : DialogFragment() {
    private var _binding: ChangeLanguageDialogBinding? = null
    private val binding get() = _binding!!
    private val languageChangeDialogViewModel = LanguageChangeDialogViewModel(MyApplication())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = ChangeLanguageDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            val languages = arrayOf("english", "bulgarian")
            unitsSpinner.apply {
                val adapter =
                    ArrayAdapter(requireContext(), R.layout.simple_spinner_item, languages)
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                this.adapter = adapter
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedLanguage= languages[position]
                        languageChangeDialogViewModel.updateLanguage(selectedLanguage)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }
            }
            val weightUnits = arrayOf("kg", "lbs")
            unitsSpinner.apply {
                val adapter =
                    ArrayAdapter(requireContext(), R.layout.simple_spinner_item, weightUnits)
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                this.adapter = adapter
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedWeightUnit = weightUnits[position]
                        languageChangeDialogViewModel.updateUnits(selectedWeightUnit)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
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