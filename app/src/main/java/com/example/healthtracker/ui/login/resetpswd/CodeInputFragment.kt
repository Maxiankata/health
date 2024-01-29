package com.example.healthtracker.ui.login.resetpswd

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentCodeInputBinding


class CodeInputFragment : Fragment() {

    private var _binding: FragmentCodeInputBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCodeInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            sendPassword.apply {
                setOnClickListener {
                    findNavController().navigate(R.id.action_codeInputFragment_to_resetPasswordFragment)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}