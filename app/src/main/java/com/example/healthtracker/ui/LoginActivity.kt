package com.example.healthtracker.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.healthtracker.R
import com.example.healthtracker.databinding.ActivityLoginBinding
import com.example.healthtracker.databinding.ActivityRegisterBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}