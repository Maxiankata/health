package com.example.healthtracker.ui.login.resetpswd

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.healthtracker.databinding.FragmentForgotPasswordBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


class ForgotPasswordFragment : Fragment() {

    var _binding: FragmentForgotPasswordBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
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