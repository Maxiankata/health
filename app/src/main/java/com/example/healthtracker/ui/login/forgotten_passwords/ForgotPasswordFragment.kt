package com.example.healthtracker.ui.login.forgotten_passwords

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentForgotPasswordBinding
import com.google.android.material.snackbar.Snackbar
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
//    val minrandom = 100
//    val maxrandom = 10000
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.apply {

            sendPassword.apply {
                setOnClickListener {
                    var email = emailInput.text.toString()
                    resetPassword(email)
//                    findNavController().navigate(R.id.action_forgotPasswordFragment_to_codeInputFragment)
                }

            }
        }
    }

//    private fun sendEmail(emailLink:String){
//        Firebase.auth.checkActionCode(emailLink)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val email = task.result?.data?.get("email")
//                } else {
//                    Log.e("LINK_HANDLING_FAIL", "Failed to handle email link: ${task.exception?.message}")
//                }
//            }
//    }
    private fun resetPassword(email:String){
        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Email sent!", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(context, "There was an issue with sending the email", Toast.LENGTH_SHORT).show()
                }
            }

    }

}