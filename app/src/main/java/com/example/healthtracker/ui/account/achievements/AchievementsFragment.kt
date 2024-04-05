package com.example.healthtracker.ui.account.achievements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthtracker.data.user.Achievement
import com.example.healthtracker.databinding.FragmentAchievementsBinding
import com.example.healthtracker.ui.hideBottomNav

class AchievementsFragment : Fragment() {
    private var _binding: FragmentAchievementsBinding? = null
    private val achievementsViewModel: AchievementsViewModel by viewModels()
    private val binding get() = _binding!!
    private lateinit var achievementsRecyclerAdapter: AchievementsRecyclerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        achievementsViewModel.feedUser()
        requireActivity().hideBottomNav()
        _binding = FragmentAchievementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            backButton.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
            achievementsViewModel.user.observe(viewLifecycleOwner) {
                val achievements = Achievements.achievements
                val achieved = mutableListOf<Achievement>()
                val unachieved = mutableListOf<Achievement>()
                for (achievement in achievements) {
                    if (achievement.goal <= it.userInfo.totalSteps!!) {
                        achieved.add(achievement)
                    } else {
                        unachieved.add(achievement)
                    }
                }
                achievedRecycler.apply {
                    achievementsRecyclerAdapter =
                        AchievementsRecyclerAdapter(it.userInfo.totalSteps!!, achieved)
                    layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    adapter = achievementsRecyclerAdapter
                }
                unachievedRecycler.apply {
                    achievementsRecyclerAdapter =
                        AchievementsRecyclerAdapter(it.userInfo.totalSteps!!, unachieved)
                    layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    adapter = achievementsRecyclerAdapter
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}