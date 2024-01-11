package com.example.healthtracker.ui.friends

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.healthtracker.FirebaseViewModel
import com.example.healthtracker.databinding.FragmentFriendAccountBinding

import com.example.healthtracker.ui.base64ToBitmap
import com.example.healthtracker.ui.hideBottomNav
import com.example.healthtracker.ui.setRoundedCorners
import com.google.firebase.auth.FirebaseAuth

class FriendAccountFragment : Fragment() {
    private var _binding: FragmentFriendAccountBinding? = null
    private lateinit var auth: FirebaseAuth
    val firebaseViewModel: FirebaseViewModel by activityViewModels()
    val friendListViewModel: FriendListViewModel by activityViewModels()

    private val binding get() = _binding!!
    lateinit var user: com.example.healthtracker.user.UserInfo

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().hideBottomNav()
        user = friendListViewModel.getUserInfoByUid(arguments?.getString("uid")!!)!!
        Log.d("user", user.toString())
        _binding = FragmentFriendAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            profilePhoto.apply {
                setRoundedCorners(360F)
                if (user.image != null && user.image!="") {
                    setImageBitmap(user.image?.let { base64ToBitmap(it) })
                }
            }
            userName.apply {
                text = user.username
            }
        }
    }

}