package com.example.healthtracker.ui.login.forgotten_passwords

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentForgotPasswordBinding
import kotlin.random.Random


class ForgotPasswordFragment : Fragment() {

    lateinit var binding: FragmentForgotPasswordBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }
    val minrandom = 100
    val maxrandom = 10000
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            sendPassword.apply {
                var renewalCode :Int = Random.nextInt(minrandom, maxrandom)

                sendEmail(renewalCode)
                setOnClickListener {
                    findNavController().navigate(
                    R.id.action_forgotPasswordFragment_to_codeInputFragment,
                    bundleOf("renewalCode" to renewalCode)
                )
                }

            }
        }
    }

    private fun sendEmail(code:Int){

    }

}