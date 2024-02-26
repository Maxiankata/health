package com.example.healthtracker.ui.login

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentLoginBinding
import com.example.healthtracker.ui.hideLoading
import com.example.healthtracker.ui.isInternetAvailable
import com.example.healthtracker.ui.showLoading
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginFragmentViewModel: LoginFragmentViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().hideLoading()
        binding.apply {

            registerButton.apply {
                setOnClickListener {
                    findNavController().navigate(R.id.action_login_to_register)
                }
            }
            signInButton.apply {
                setOnClickListener {
                    requireActivity().showLoading()
                    lifecycleScope.launch {
                        try {
                            if (loginFragmentViewModel.logIn(
                                    usernameInput.text.toString(), passwordInput.text.toString()
                                )
                            ) {
                                val intent = Intent(context, MainActivity::class.java)
                                startActivity(intent)
                                loginFragmentViewModel.getUser().also {
                                    requireActivity().hideLoading()
                                }
                                activity?.finish()
                            } else if (isInternetAvailable(context)) {
                                Toast.makeText(
                                    context,
                                    R.string.login_issue,
                                    Toast.LENGTH_SHORT,
                                ).show().also {
                                    requireActivity().hideLoading()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    R.string.no_internet,
                                    Toast.LENGTH_SHORT
                                ).show().also {
                                    requireActivity().hideLoading()
                                }
                            }
                        } catch (e: Exception) {
                            Log.d("empty", "$e")
                            Toast.makeText(
                                context,
                                getString(R.string.empty_field),
                                Toast.LENGTH_SHORT,
                            ).show().also {
                                findViewById<RelativeLayout>(R.id.loadingPanel).visibility = View.GONE
                            }
                        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}