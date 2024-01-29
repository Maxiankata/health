package com.example.healthtracker.ui.login.register

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.AuthInterface
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentRegisterBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    val registerViewModel:RegisterViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
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
                if (passwordInput.text.toString()
                        .isNotEmpty() && confirmPasswordInput.text.toString() == passwordInput.text.toString() && emailInput.text.toString()
                        .isNotEmpty()
                ) {
                    runBlocking {
                        launch {
                            registerViewModel.register(
                                emailInput.text.toString(),
                                passwordInput.text.toString(),
                                usernameInput.text.toString()
                            )
                        }
                    }
                    Firebase.auth.signInWithEmailAndPassword(
                        usernameInput.text.toString(), passwordInput.text.toString()
                    )
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

}




