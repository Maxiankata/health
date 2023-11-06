package com.example.healthtracker

import android.content.ContentValues
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.healthtracker.ui.login.LoginActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FirebaseViewModel : ViewModel() {

    fun signout() {
        Firebase.auth.signOut()
    }
    fun createAcc(email: String , password: String, view : View){
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    val user = LoginActivity.auth.currentUser
                } else {
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)

                }
            }
    }
}