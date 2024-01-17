package com.example.healthtracker.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.healthtracker.FirebaseViewModel
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentRegisterBinding
import com.example.healthtracker.ui.login.LoginActivity.Companion.auth

//FIXME _binding should be cleared in onDestroyView()
// Check comment in FirebaseViewModel
class RegisterFragment : Fragment() {
    private val firebaseViewModel: FirebaseViewModel by activityViewModels()
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
                    firebaseViewModel.createAcc(
                        emailInput.text.toString(),
                        passwordInput.text.toString(),
                        usernameInput.text.toString()
                    )
                    auth.signInWithEmailAndPassword(
                        usernameInput.text.toString(),
                        passwordInput.text.toString()
                    )
                    val user = auth.currentUser
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}




