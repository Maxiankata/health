package com.example.healthtracker.ui.login

import android.content.Intent
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            intent = Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        val imageView: ImageView = findViewById(R.id.animation)
        val navHostFragment: View = findViewById(R.id.nav_host_fragment)
        navHostFragment.visibility = View.GONE
        navHostFragment.alpha = 0f


        val animatedVectorDrawable: AnimatedVectorDrawable = imageView.drawable as AnimatedVectorDrawable
        animatedVectorDrawable.registerAnimationCallback(object : Animatable2.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                navHostFragment.visibility = View.VISIBLE
                navHostFragment.animate().alpha(1f)
                    .setDuration(500)
                    .setListener(null)
            }
        })
        animatedVectorDrawable.start()
    }
    companion object{
        var auth: FirebaseAuth = Firebase.auth
    }
}