package com.example.healthtracker.ui.login

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
import androidx.lifecycle.map
import androidx.navigation.findNavController
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentLoginBinding
import com.example.healthtracker.ui.hideLoading
import com.example.healthtracker.ui.isInternetAvailable
import com.example.healthtracker.ui.showLoading
import kotlinx.coroutines.launch
import com.example.healthtracker.data.user.LoginUiMapper
import com.example.healthtracker.ui.setLoadingVisibility


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
            loginFragmentViewModel.state.map { LoginUiMapper.map(it) }.observe(viewLifecycleOwner) {
                Log.d("state", if(it.loadingVisibility == View.VISIBLE) "visibile" else "gone")
                showLog(it.message)
                requireActivity().setLoadingVisibility(it.loadingVisibility)
                if(it.shouldNavigate) {
                    val intent =
                        Intent(context, MainActivity::class.java)
                    startActivity(intent)

                    activity?.finish()
                }
            }
            loginFragmentViewModel.requestPermissionsUntilGranted(this@LoginFragment)
            signInButton.apply {
                setOnClickListener {
                    requireActivity().showLoading()
                    loginFragmentViewModel.logIn(email = usernameInput.text.toString(), passwordInput.text.toString(), isInternetAvailable(context))
                }
            }
            forgotPassword.apply {
                setOnClickListener {
                    findNavController().navigate(R.id.action_login_to_forgotPassword)

                }
            }
        }
    }
    private fun showLog(message: String?) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}