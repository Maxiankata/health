package com.example.healthtracker.ui.account.friends.profile

import android.annotation.SuppressLint
import android.graphics.Color
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthtracker.R
import com.example.healthtracker.data.user.Achievement
import com.example.healthtracker.data.user.UserDays
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.databinding.FragmentFriendAccountBinding
import com.example.healthtracker.ui.account.achievements.Achievements.achievements
import com.example.healthtracker.ui.account.achievements.AchievementsRecyclerAdapter
import com.example.healthtracker.ui.account.friends.challenges.ChallengeBuilderDialogFragment
import com.example.healthtracker.ui.base64ToBitmap
import com.example.healthtracker.ui.account.friends.popup.FriendListViewModel
import com.example.healthtracker.ui.hideBottomNav
import com.example.healthtracker.ui.setRoundedCorners
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FriendAccountFragment : Fragment() {
    private var _binding: FragmentFriendAccountBinding? = null
    private lateinit var auth: FirebaseAuth
    private val friendListViewModel: FriendListViewModel by activityViewModels()
    private val friendAccountViewModel:FriendAccountViewModel by viewModels()
    private var notificationId = 1

    private val binding get() = _binding!!
    lateinit var achievementsRecyclerAdapter :AchievementsRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        requireActivity().hideBottomNav()
        friendAccountViewModel.feedUser(arguments?.getString("uid")!!)
        _binding = FragmentFriendAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            friendAccountViewModel.user.observe(viewLifecycleOwner) {
                val achieved = mutableListOf<Achievement>()
                for (achievement in achievements) {
                    if (achievement.goal <= it.first.totalSteps!!) {
                        achieved.add(achievement)
                    }
                }
                achievementRecycler.apply {
                    achievementsRecyclerAdapter =
                        AchievementsRecyclerAdapter(it.first.totalSteps!!, achieved)
                    layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    adapter = achievementsRecyclerAdapter
                }
                val user = it.first
                val days = it.second
                profilePhoto.apply {
                    setBackgroundResource(R.drawable.circle_background)
                    if (user.image != null && user.image != "") {
                        setImageBitmap(base64ToBitmap(user.image))
                    }
                }
                backButton.setOnClickListener {
                    requireActivity().supportFragmentManager.popBackStack()
                }
                userName.apply {
                    text = user.username
                }
                if (!it.first.theme.isNullOrEmpty()) {
                    backgroundColor.setBackgroundColor(Color.parseColor(it.first.theme))
                }
                friendListViewModel.friendsList.observe(viewLifecycleOwner) {friendlist->
                    if (friendlist != null) {
                        if (friendlist.contains(user)) {
                            challengeFriend.apply {
                                setOnClickListener {
                                    val bundle = Bundle().apply {
                                        putString("uid", arguments?.getString("uid"))
                                    }
                                    val dialogFragment = ChallengeBuilderDialogFragment()
                                    dialogFragment.arguments = bundle
                                    dialogFragment.show(parentFragmentManager, "MyDialogFragment")
                                }
                            }
                            relationshipStatus.apply {
                                setImageResource(R.drawable.friend_remove)
                                setOnClickListener {
                                    user.uid?.let {
                                        lifecycleScope.launch {
                                            friendListViewModel.removeFriend(it)
                                        }
                                        val message =
                                            "${getString(R.string.friend)} ${user.username} ${
                                                getString(R.string.removed)
                                            }"
                                        Toast.makeText(
                                            requireContext(),
                                            message,
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                }
                            }
                        } else {
                            challengeFriend.setOnClickListener {
                                showToast(getString(R.string.not_a_friend))
                            }
                            relationshipStatus.apply {
                                setImageResource(R.drawable.friend_add)
                                setOnClickListener {
                                        user.uid?.let { it1 -> friendListViewModel.addFriend(it1) }
                                        val message =
                                            "${getString(R.string.friend)} ${user.username} ${
                                                getString(R.string.added)
                                            }"
                                        showToast(message)
                                }
                            }
                        }
                    }
                }

                    val datetimeList = mutableListOf<String>()
                    val stepsList = mutableListOf<Int>()
                    val caloriesList = mutableListOf<Int>()
                    val watersList = mutableListOf<Int>()
                    if (days != null) {
                        Log.d("second isnt null", days.toString())
                        for (userday in days) {
                            datetimeList.add(userday.dateTime)
                            val steps = userday.automaticInfo?.steps?.currentSteps ?: 0
                            stepsList.add(steps)
                            val calories = userday.automaticInfo?.steps?.currentCalories ?: 0
                            caloriesList.add(calories)
                            val water = userday.putInInfo?.waterInfo?.currentWater ?: 0
                            watersList.add(water)
                        }
                    }
                    val chartModel = AAChartModel()
                        .chartType(AAChartType.Column)
                        .title(getString(R.string.progress))
                        .titleStyle(AAStyle().color("#FFFFFF"))
                        .categories(datetimeList.toTypedArray())
                        .backgroundColor("#000000")
                        .series(
                            arrayOf(
                                AASeriesElement()
                                    .name(getString(R.string.steps))
                                    .color("#1CFEBA")
                                    .data(stepsList.toTypedArray()),
                                AASeriesElement()
                                    .name(getString(R.string.calories))
                                    .color("#FF9237")
                                    .data(caloriesList.toTypedArray()),
                                AASeriesElement()
                                    .name(getString(R.string.water))
                                    .color("#5EABC6")
                                    .data(watersList.toTypedArray())
                            )
                        )
                    friendChart.aa_drawChartWithChartModel(chartModel)
                    friendChart.setRoundedCorners(30F)
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
    private fun showToast(message:String){
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        )
            .show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}