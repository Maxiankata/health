package com.example.healthtracker.ui.account

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.healthtracker.databinding.FragmentAccountBinding
import com.example.healthtracker.ui.LoginActivity
import com.example.healthtracker.ui.navigateToActivity
import com.example.healthtracker.ui.setRoundedCorners

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
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

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            signOutButton.apply {
                setRoundedCorners(25F)
                setOnClickListener {
                    navigateToActivity(requireActivity(), LoginActivity::class.java)
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