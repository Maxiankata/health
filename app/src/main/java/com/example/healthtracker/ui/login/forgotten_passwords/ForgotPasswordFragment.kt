package com.example.healthtracker.ui.login.forgotten_passwords

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentForgotPasswordBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
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
            var email = emailInput.editText.toString()
            sendPassword.apply {
                var renewalCode :Int = Random.nextInt(minrandom, maxrandom)


                setOnClickListener {
                    resetPassword(email)
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
    private fun resetPassword(email:String){
        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                }
            }
    }

}