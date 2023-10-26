package com.example.healthtracker.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import com.example.healthtracker.MainActivity
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
            signInButton.setOnClickListener {
                navigateToActivity(this@RegisterActivity, LoginActivity::class.java)
            }
            registerButton.setOnClickListener {
                navigateToActivity(this@RegisterActivity, MainActivity::class.java)
            }
        }

    }


    fun navigateToActivity(currentActivity: Activity, targetActivityClass: Class<*>) {
        val intent = Intent(currentActivity, targetActivityClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        currentActivity.startActivity(intent)
        currentActivity.finish()
    }

}