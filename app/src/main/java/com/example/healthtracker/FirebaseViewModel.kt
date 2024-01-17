package com.example.healthtracker

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.healthtracker.ui.login.LoginActivity
import com.example.healthtracker.user.UserInfo
import com.example.healthtracker.user.UserMegaInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

//FIXME Looks like this class is used to manage both authentication and persistence
// That's not a good approach - you need to separate your concerns.
// Usually there is an authentication service interface that defines the API related to
// sign-in/login/logout/account deletion/etc. Since you are using Firebase Auth, your implementation
// will use that and will be the only place that Firebase.auth will be accessed.
// Then you should have a UserRepository interface that provides the current user. The usual practice is
// to have a current user observable field with a non-null value (you can use a sealed class with NoUser subclass)
// Other application components that need to know if there is a user present will rely only on UserRepository
// Then you can have other repository classes for the entities in your application domain, e.g. contacts (friends),
// vitals, challenges, settings (if backed by a server), etc. You might group similar entities in a common repository.
// It might look tempting to use the same objects that end up in the actual persistence implementation as entity
// objects but doing so has a big downside that you might introduce a dependency to the concrete implementation
// data types or make you change your entities in a way that does not make sense, like in the case of Firebase Database
// if you want to serialize your objects directly as POJOs, you must make all fields nullable - this does not make
// sense for domain entities - do you really care about sleep records with null values?
// So your repositories should operate on well-thought out domain objects that serve your application domain well
// and then mapped in the data layer to objects suitable for serialization
class FirebaseViewModel : ViewModel() {

    fun SetUser(email: String,username: String,uid:String){
        Firebase.database.getReference("user/${Firebase.auth.uid}").setValue(UserMegaInfo(UserInfo(username,
            uid,"",email)))
            .addOnCompleteListener{
                // FIXME Checkout Timber library for logging: https://github.com/JakeWharton/timber
                //  The idea of this library is that logging is a big no-no for published applications -
                //  it affects performance, battery life and can possibly leak sensitive information.
                //  The library makes it easy to disable logging for release builds.
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