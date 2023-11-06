package com.example.healthtracker.ui.login.forgotten_passwords

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentResetPasswordBinding

class ResetPasswordFragment : Fragment() {

    lateinit var binding : FragmentResetPasswordBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            signInButton.apply {
                setOnClickListener {
                    findNavController().navigate(R.id.action_resetPasswordFragment_to_loginFragment)
                }
            }
        }
    }


}