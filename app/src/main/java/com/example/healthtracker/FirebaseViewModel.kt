package com.example.healthtracker

import android.content.ContentValues
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.healthtracker.ui.login.LoginActivity
import com.example.healthtracker.user.UserInfo
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseViewModel : ViewModel() {

    fun SetUser(email: String,username: String,uid:String){
        Firebase.database.getReference("user/${Firebase.auth.uid}").setValue(UserInfo(username,
            uid,"",email))
            .addOnCompleteListener{
                Log.d("BINDED TO BASE", "BASE BINDED BINGO BINGO")
            }
    }
    fun signout() {
        Firebase.auth.signOut()
    }
    fun createAcc(email: String , password: String, username:String){
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    val user = LoginActivity.auth.currentUser
                    SetUser(email, username, user!!.uid)
                } else {
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                }
            }
    }
}