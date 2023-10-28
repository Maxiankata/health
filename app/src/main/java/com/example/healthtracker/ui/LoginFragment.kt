package com.example.healthtracker.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentLoginBinding
import com.example.healthtracker.databinding.FragmentRegisterBinding


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            registerButton.apply {
                setOnClickListener {
                    findNavController().navigate(R.id.action_login_to_register)
                }
            }
            signInButton.apply {
                setOnClickListener {
                    navigateToActivity(requireActivity(), MainActivity::class.java)
                }
            }
            forgotPassword.apply {
                setOnClickListener {
                    findNavController().navigate(R.id.action_login_to_forgotPassword)

                }
            }
        }
    }
}