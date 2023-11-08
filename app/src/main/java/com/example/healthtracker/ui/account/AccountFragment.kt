package com.example.healthtracker.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.healthtracker.FirebaseViewModel
import com.example.healthtracker.databinding.FragmentAccountBinding
import com.example.healthtracker.ui.login.LoginActivity
import com.example.healthtracker.ui.navigateToActivity
import com.example.healthtracker.ui.setRoundedCorners
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private lateinit var auth : FirebaseAuth
    val firebaseViewModel:FirebaseViewModel by activityViewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(AccountViewModel::class.java)

        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root
        auth = FirebaseAuth.getInstance()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lateinit var user:String
            Firebase.database.getReference("user/${auth.currentUser!!.uid}/username").get().addOnCompleteListener {
            user = it.result.getValue(String::class.java).toString()
                binding.username.text = user
        }
        binding.apply {

            signOutButton.apply {
                setRoundedCorners(25F)
                setOnClickListener {
                    navigateToActivity(requireActivity(), LoginActivity::class.java)
                    firebaseViewModel.signout()
                }
            }
            achievements.apply {
                setRoundedCorners(25F)
            }
            friends.apply {
                setRoundedCorners(25F)
            }
            profilePhoto.apply {
                setRoundedCorners(360F)
            }
            statistics.apply{
                setRoundedCorners(25F)

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}