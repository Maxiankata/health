package com.example.healthtracker.ui.account.friends.popup

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthtracker.R
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.databinding.PopupFriendsBinding
import com.example.healthtracker.ui.rotateView
import kotlinx.coroutines.launch

class FriendsDialogFragment : DialogFragment() {
    private var _binding: PopupFriendsBinding? = null
    private val binding get() = _binding


//    val friendListViewModel:FriendListViewModel by viewModels()
//    val friendListAdapter = FriendListAdapter(friendListViewModel)

    private val friendListViewModel: FriendListViewModel by activityViewModels()
    private lateinit var friendListAdapter: FriendListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        friendListAdapter = FriendListAdapter()
        _binding = PopupFriendsBinding.inflate(inflater, container, false)


        return binding?.root
    }

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.custom_rounded_background)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            friendListViewModel.usersList.observe(viewLifecycleOwner) {
                if (it != null) {
                    friendListAdapter.updateItems(it)
                }
                if (it != null && it != listOf<UserInfo>()) {
                    noFriends.visibility = GONE
                    noFriendsText.visibility = GONE
                } else {
                    noFriends.visibility = VISIBLE
                    noFriendsText.visibility = VISIBLE
                }
            }
            usernameInput.setOnFocusChangeListener { _, hasFocus ->
                val color = if (hasFocus) (ContextCompat.getColor(
                    requireContext(), R.color.light_green
                )) else (ContextCompat.getColor(requireContext(), R.color.input_grey))
                textInputLayout.setEndIconTintList(ColorStateList.valueOf(color))

                searchSwitch.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            }
            friendRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = friendListAdapter

            }

            searchSwitch.apply {
                friendListViewModel.searchState.observe(viewLifecycleOwner) { it1 ->
                    if (it1 == true) {
                        setOnClickListener {
                            friendListViewModel.switchSearchState()
                        }
                        viewLifecycleOwner.lifecycleScope.launch {
                            try {
                                friendListViewModel.fetchAllUsersInfo()
                                rotateView(searchSwitch, 45F)
                                textInputLayout.helperText = "new friend mode on"

                            } catch (e: Exception) {
                                Log.e("FetchUsersError", "Error fetching users", e)
                            }
                        }

                    } else {
                        setOnClickListener {
                            friendListViewModel.switchSearchState()
                        }
                        viewLifecycleOwner.lifecycleScope.launch {
                            try {
                                friendListViewModel.fetchUserFriends()
                                rotateView(searchSwitch, 0F)
                                textInputLayout.helperText = "current friend mode on"

                            } catch (e: Exception) {
                                Log.e("FetchFriendsError", "Error fetching friends", e)
                            }
                        }

                    }
                }
            }
            close.setOnClickListener {
                dismiss()
            }

        }
        friendListAdapter.apply {
            itemClickListener = object : FriendListAdapter.ItemClickListener<UserInfo> {
                override fun onItemClicked(item: UserInfo, itemPosition: Int) {
                    item.uid?.let { Log.d("uid", it) }
                    findNavController().navigate(
                        R.id.action_navigation_notifications_to_friendAccountFragment,
                        bundleOf("uid" to item.uid)
                    )
                    dismiss()
                }
            }
        }
    }


}