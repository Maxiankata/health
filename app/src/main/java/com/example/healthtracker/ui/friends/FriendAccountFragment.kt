package com.example.healthtracker.ui.friends

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.healthtracker.FirebaseViewModel
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentFriendAccountBinding

import com.example.healthtracker.ui.base64ToBitmap
import com.example.healthtracker.ui.hideBottomNav
import com.example.healthtracker.ui.setRoundedCorners
import com.google.firebase.auth.FirebaseAuth
import java.time.Duration
import kotlin.coroutines.coroutineContext

//FIXME check comment in FirebaseViewModel
class FriendAccountFragment : Fragment() {
    private var _binding: FragmentFriendAccountBinding? = null
    private lateinit var auth: FirebaseAuth
    val firebaseViewModel: FirebaseViewModel by activityViewModels()
    val friendListViewModel: FriendListViewModel by activityViewModels()
    private var notificationId = 1

    private val binding get() = _binding!!
    lateinit var user: com.example.healthtracker.user.UserInfo

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().hideBottomNav()
        user = friendListViewModel.getUserInfoByUid(arguments?.getString("uid")!!)!!
        _binding = FragmentFriendAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            profilePhoto.apply {
                setRoundedCorners(360F)
                if (user.image != null && user.image!="") {
                    //FIXME there is probably a better way to handle images, check out Glide library
                    // and its local image loading capabilities https://bumptech.github.io/glide/
                    setImageBitmap(user.image?.let { base64ToBitmap(it) })
                }
            }
            userName.apply {
                text = user.username
            }
            friendListViewModel.user.observe(viewLifecycleOwner){
                if (it != null) {
                    if(it.contains(user)){
                        relationshipStatus.apply {
                            setImageResource(R.drawable.friend_remove)
                            setOnClickListener {
                                user.uid?.let {
                                    friendListViewModel.removeFriend(it)
                                    val message = "Friend ${user.username} removed"
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }else{
                        relationshipStatus.apply {
                            setImageResource(R.drawable.friend_add)
                            setOnClickListener {
                                user.uid?.let { it1 -> friendListViewModel.addFriend(it1) }
                            }
                        }
                    }
                }
            }
            challengeFriend.apply {
                setOnClickListener {
                    sendNotification(getString(R.string.close),getString(R.string.new_friend))
                }
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun sendNotification(title: String, content: String) {
        val channelID = "friend_channel"
        val builder = NotificationCompat.Builder(requireContext(), channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(requireContext())) {
            notify(notificationId, builder.build())
            notificationId++
        }
    }
}