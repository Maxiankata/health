package com.example.healthtracker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.example.healthtracker.databinding.ActivityLoginBinding
import androidx.navigation.ui.AppBarConfiguration.Builder;
import com.example.healthtracker.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
//    override fun onBackPressed() {
//        val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
//        if (fragment is RegisterFragment) {
//            finish()
//        } else {
//            super.onBackPressed()
//        }
//    }
}