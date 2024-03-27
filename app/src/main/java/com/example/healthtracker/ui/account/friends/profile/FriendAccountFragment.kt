package com.example.healthtracker.ui.account.friends.profile

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
import androidx.lifecycle.lifecycleScope
import com.example.healthtracker.R
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.databinding.FragmentFriendAccountBinding
import com.example.healthtracker.ui.account.friends.challenges.ChallengeBuilderDialogFragment
import com.example.healthtracker.ui.base64ToBitmap
import com.example.healthtracker.ui.account.friends.popup.FriendListViewModel
import com.example.healthtracker.ui.hideBottomNav
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FriendAccountFragment : Fragment() {
    private var _binding: FragmentFriendAccountBinding? = null
    private lateinit var auth: FirebaseAuth
    private val friendListViewModel: FriendListViewModel by activityViewModels()
    private var notificationId = 1

    private val binding get() = _binding!!
    lateinit var user: UserInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        requireActivity().hideBottomNav()
        runBlocking {
            launch {
                user = friendListViewModel.getUser(arguments?.getString("uid")!!)!!
            }
        }
        _binding = FragmentFriendAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            profilePhoto.apply {
                setBackgroundResource(R.drawable.circle_background)
                if (user.image != null && user.image != "") {
                    setImageBitmap(user.image?.let { base64ToBitmap(it) })
                }
            }
            backButton.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
            userName.apply {
                text = user.username
            }

            friendListViewModel.friendsList.observe(viewLifecycleOwner) {
                if (it != null) {
                    if (it.contains(user)) {
                        relationshipStatus.apply {
                            setImageResource(R.drawable.friend_remove)
                            setOnClickListener {
                                user.uid?.let {
                                    lifecycleScope.launch {
                                            friendListViewModel.removeFriend(it)
                                    }
                                    val message = "${getString(R.string.friend)} ${user.username} ${
                                        getString(R.string.removed)
                                    }"
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    } else {
                        relationshipStatus.apply {
                            setImageResource(R.drawable.friend_add)
                            setOnClickListener {
                                lifecycleScope.launch {

                                    user.uid?.let { it1 -> friendListViewModel.addFriend(it1) }
                                    val message = "${getString(R.string.friend)} ${user.username} ${
                                        getString(R.string.added)
                                    }"
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    }
                }
            }
            challengeFriend.apply {
                setOnClickListener {
                    val bundle = Bundle().apply {
                        putString("uid", arguments?.getString("uid"))
                    }
                    val dialogFragment = ChallengeBuilderDialogFragment()
                    Log.d("bundle sent to dialog", bundle.toString())
                    dialogFragment.arguments = bundle
                    dialogFragment.show(parentFragmentManager, "MyDialogFragment")
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification(title: String, content: String) {
        val channelID = "friend_channel"
        val builder = NotificationCompat.Builder(requireContext(), channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle(title)
            .setContentText(content).setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(requireContext())) {
            notify(notificationId, builder.build())
            notificationId++
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}