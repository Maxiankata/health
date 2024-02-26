package com.example.healthtracker.ui.login.resetpswd

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.healthtracker.databinding.FragmentForgotPasswordBinding
import kotlinx.coroutines.launch


class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private val forgotPasswordViewModel: ForgotPasswordViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            sendPassword.apply {
                setOnClickListener {
                    lifecycleScope.launch {
                        if (forgotPasswordViewModel.resetPassword(emailInput.text.toString())) Toast.makeText(
                            context,
                            "Email sent!",
                            Toast.LENGTH_SHORT
                        ).show()
                        else {
                            Toast.makeText(
                                context,
                                "There was an issue with sending the email",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
}