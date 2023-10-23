package com.example.healthtracker.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import com.example.healthtracker.R
import com.example.healthtracker.databinding.ActivityMainBinding
import com.example.healthtracker.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            signInButton.setOnClickListener{

            }
            registerButton.setOnClickListener{

            }
        }
    }




}