package com.example.healthtracker.ui.login.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import com.example.healthtracker.MyApplication
import com.example.healthtracker.R
import com.example.healthtracker.data.user.LoginUiMapper
import com.example.healthtracker.databinding.FragmentRegisterBinding
import com.example.healthtracker.ui.isInternetAvailable
import com.example.healthtracker.ui.setLoadingVisibility
import com.example.healthtracker.ui.showLoading
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
            registerViewModel.state.map { LoginUiMapper.maps(it) }.observe(viewLifecycleOwner) {
                showLog(it.message)
                requireActivity().setLoadingVisibility(it.loadingVisibility)
                if (it.shouldNavigate) {
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
            }
            registerViewModel.clickableButtons.observe(viewLifecycleOwner) { state ->
                signInButton.isEnabled = state
                usernameInput.isEnabled = state
                passwordInput.isEnabled = state
                registerButton.isEnabled = state
                confirmPasswordInput.isEnabled = state
                emailInput.isEnabled = state
            }
            registerButton.setOnClickListener {
                requireActivity().showLoading()
                lifecycleScope.launch {
                    registerViewModel.register(
                        emailInput.text.toString(),
                        passwordInput.text.toString(),
                        confirmPasswordInput.text.toString(),
                        usernameInput.text.toString(),
                        isInternetAvailable(MyApplication.getContext())
                    )
                    registerViewModel.getUser()
                }
            }
        }
    }

    private fun showLog(message: String?) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
}




