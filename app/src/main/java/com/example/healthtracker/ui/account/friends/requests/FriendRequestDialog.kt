package com.example.healthtracker.ui.account.friends.requests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthtracker.MainActivity
import com.example.healthtracker.MyApplication
import com.example.healthtracker.R
import com.example.healthtracker.databinding.RequestDialogBinding
import com.example.healthtracker.ui.account.friends.popup.FriendListAdapter
import com.example.healthtracker.ui.account.friends.popup.FriendListViewModel
import com.example.healthtracker.ui.account.friends.popup.RequestRecyclerAdapter

class FriendRequestDialog : DialogFragment() {
    private var _binding: RequestDialogBinding? = null
    private val binding get() = _binding!!
    private val friendRequestViewModel: FriendRequestViewModel by viewModels()
    private lateinit var friendRequestRecyclerAdapter: RequestRecyclerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        friendRequestRecyclerAdapter = RequestRecyclerAdapter()
        friendRequestViewModel.feedRequests()
        _binding = RequestDialogBinding.inflate(inflater, container, false)
        return binding.root
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
        binding.apply {
            requestRecycler.adapter = friendRequestRecyclerAdapter
            requestRecycler.layoutManager = LinearLayoutManager(MyApplication.getContext())
            friendRequestViewModel.requestList.observe(viewLifecycleOwner) {
                if (!it.isNullOrEmpty()) {
                    friendRequestRecyclerAdapter.updateItems(it)
                    emptyList.visibility=GONE
                }else{
                    emptyList.visibility=VISIBLE
                }
            }

        }
    }

}