package com.example.healthtracker.ui.account.friends.challenges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthtracker.MyApplication
import com.example.healthtracker.R
import com.example.healthtracker.databinding.ChallengesDialogBinding
import kotlinx.coroutines.launch

class ChallengeDisplayDialog : DialogFragment() {
    private var _binding: ChallengesDialogBinding? = null
    private val binding get() = _binding!!
    val recyclerAdapter = ChallengesListAdapter()
    private val challengesDisplayDialogViewModel = ChallengesDisplayDialogViewModel(MyApplication())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = ChallengesDialogBinding.inflate(inflater, container, false)
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
        lifecycleScope.launch {
            challengesDisplayDialogViewModel.feedChallenges()
        }
        challengesDisplayDialogViewModel.challenges.observe(viewLifecycleOwner){
            if (it != null && it!= listOf<Challenge>()) {
                binding.emptyChallenges.visibility = GONE
                recyclerAdapter.updateItems(it)
            }else{
                binding.emptyChallenges.visibility= VISIBLE
            }
        }

        binding.apply {
            challengeRecycler.apply {
                adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(requireContext())

            }
            refresh.setOnClickListener {
                challengesDisplayDialogViewModel.refreshChallenges()
            }
            closeButton.setOnClickListener {
                dismiss()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

