package com.example.healthtracker.ui.login.register

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentLoginBinding
import com.example.healthtracker.databinding.FragmentRegisterBinding
import com.example.healthtracker.ui.hideLoading
import com.example.healthtracker.ui.showLoading
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val registerViewModel: RegisterViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            signInButton.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
            registerButton.setOnClickListener {
                requireActivity().showLoading()
                if (passwordInput.text.toString()
                        .isNotEmpty() && confirmPasswordInput.text.toString() == passwordInput.text.toString() && emailInput.text.toString()
                        .isNotEmpty()
                ) {
                    lifecycleScope.launch {
                        registerViewModel.register(
                            emailInput.text.toString(),
                            passwordInput.text.toString(),
                            usernameInput.text.toString()
                        )
                        registerViewModel.getUser()
                    }
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent).also {
                        requireActivity().hideLoading()
                    }
                }
            }
        }
    }

}




