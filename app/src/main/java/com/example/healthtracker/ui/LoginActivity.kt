package com.example.healthtracker.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            registerButton.setOnClickListener{
                navigateToActivity(this@LoginActivity,RegisterActivity::class.java)
            }
            signInButton.setOnClickListener {
                navigateToActivity(this@LoginActivity,MainActivity::class.java)
            }
        }

    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish() // Finish the activity (close the app)
    }
    fun navigateToActivity(currentActivity: Activity, targetActivityClass: Class<*>) {
        val intent = Intent(currentActivity, targetActivityClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        currentActivity.startActivity(intent)
        currentActivity.finish()
    }
}