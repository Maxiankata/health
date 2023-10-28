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
    override fun onBackPressed() {
        super.onBackPressed()
        finish() // Finish the activity (close the app)
    }
//    override fun onBackPressed() {
//        val fragmentManager = supportFragmentManager
//        if (fragmentManager.backStackEntryCount > 0) {
//            fragmentManager.popBackStack()
//        } else {
//            super.onBackPressed()
//        }
//    }

}