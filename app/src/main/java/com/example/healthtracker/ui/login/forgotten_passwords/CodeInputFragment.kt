package com.example.healthtracker.ui.login.forgotten_passwords

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentCodeInputBinding


class CodeInputFragment : Fragment() {

    lateinit var binding: FragmentCodeInputBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCodeInputBinding.inflate(inflater, container,false)
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
}