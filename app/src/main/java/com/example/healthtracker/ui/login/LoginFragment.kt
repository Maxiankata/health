package com.example.healthtracker.ui.login

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentLoginBinding
import com.example.healthtracker.ui.login.LoginActivity.Companion.auth
import com.example.healthtracker.ui.navigateToActivity


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
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
                    try {
                        auth.signInWithEmailAndPassword(
                            usernameInput.text.toString(), passwordInput.text.toString()
                        ).addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "signInWithEmail:success")
                                    val user = auth.currentUser
                                    val intent = Intent(context, MainActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Incorrect e-mail or password",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                            }
                    } catch (e: Exception) {
                        Log.d("empty", "$e")
                        Toast.makeText(
                            context,
                            "Empty field",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }

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