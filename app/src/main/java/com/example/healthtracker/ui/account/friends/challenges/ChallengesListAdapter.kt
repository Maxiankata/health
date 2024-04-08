package com.example.healthtracker.ui.account.friends.challenges

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.MyApplication
import com.example.healthtracker.R
import com.example.healthtracker.ui.base64ToBitmap
import com.example.healthtracker.ui.home.speeder.ActivityEnum
import com.example.healthtracker.ui.home.speeder.SpeederService
import com.example.healthtracker.ui.home.speeder.SpeederServiceBoolean
import com.example.healthtracker.ui.isServiceRunning
import com.example.healthtracker.ui.startSpeeder
import com.example.healthtracker.ui.stopSpeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChallengesListAdapter :
    RecyclerView.Adapter<ChallengesListAdapter.ChallengeListViewHolder>() {

    val items = ArrayList<Challenge>()
    val itemClickListener: ItemClickListener<Challenge>? = null
    val context = MyApplication.getContext()
    val userDao = MainActivity.getDatabaseInstance().dao()
    val authImpl = AuthImpl.getInstance()
    private val customCoroutineScope = CoroutineScope(Dispatchers.IO)

    inner class ChallengeListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val challengeType: TextView = view.findViewById(R.id.challenge_type)
        private val challengeDuration: TextView = view.findViewById(R.id.challenge_duration)
        private val challenger: TextView = view.findViewById(R.id.challenger_name)
        private val challengerIcon: ImageView = view.findViewById(R.id.challenger_icon)
        private val status: TextView = view.findViewById(R.id.status)
        private val challengeImage: ImageView = view.findViewById(R.id.challenge_image)
        val accept: Button = view.findViewById(R.id.accept_challenge)
        val decline: Button = view.findViewById(R.id.decline_challenge)
        fun bind(challenge: Challenge) {
            when (challenge.challengeType) {
                ActivityEnum.WALKING -> {
                    challengeImage.setImageResource(R.drawable.power_walking_icon)
                    challengeType.text = buildString {
                        append(
                            "${getString(context, R.string.type)} : ${
                                getString(
                                    context, R.string.power_walking
                                )
                            }"
                        )
                    }
                }

                ActivityEnum.CYCLING -> {
                    challengeImage.setImageResource(R.drawable.cycling_icon)
                    challengeType.text = buildString {
                        append(
                            "${getString(context, R.string.type)} : ${
                                getString(
                                    context, R.string.cycling
                                )
                            }"
                        )
                    }
                }

                ActivityEnum.RUNNING -> {
                    challengeImage.setImageResource(R.drawable.running_icon)
                    challengeType.text = buildString {
                        append(
                            "${getString(context, R.string.type)} : ${
                                getString(
                                    context, R.string.running
                                )
                            }"
                        )
                    }
                }

                ActivityEnum.JOGGING -> {
                    challengeImage.setImageResource(R.drawable.jogging_icon)
                    challengeType.text = buildString {
                        append(
                            "${getString(context, R.string.type)} : ${
                                getString(
                                    context, R.string.jogging
                                )
                            }"
                        )
                    }
                }
            }
            challengeDuration.text = buildString {
                append("${getString(context, R.string.duration)}: ${challenge.challengeDuration}")
            }
            challenger.text = buildString {
                append("${getString(context, R.string.by)}: ${challenge.assigner}")
            }
            challengerIcon.setImageBitmap(base64ToBitmap(challenge.image))
            SpeederServiceBoolean.isMyServiceRunningLive.observeForever {
                if (it) {
                    val id = SpeederService.speedIntent.getStringExtra("challenge_id")
                    if (id == challenge.id.toString()) {
                        status.text = buildString {
                            append(
                                "${getString(context, R.string.status)}: ${
                                    getString(
                                        context, R.string.active
                                    )
                                }"
                            )
                        }
                        val parseColor = Color.parseColor("#FFFF00")
                        status.setTextColor(parseColor)
                        accept.visibility = View.GONE
                        decline.apply {
                            text = getString(context, R.string.cancel)
                            setOnClickListener {
                                stopSpeeder()
                            } }
                        }
                    } else {
                    decline.visibility = View.VISIBLE
                    accept.visibility = View.VISIBLE
                    decline.text = getString(context, R.string.decline)
                        if (challenge.challengeCompletion) {
                            status.text = buildString {
                                append(
                                    "${getString(context, R.string.status)}: ${
                                        getString(
                                            context, R.string.complete
                                        )
                                    }"
                                )
                            }
                            decline.visibility = View.GONE
                            accept.visibility = View.GONE
                            val parseColor = Color.parseColor("#00FF00")
                            status.setTextColor(parseColor)
                        } else {
                            Log.d("status is complete", "hehe")
                            status.text = buildString {
                                append(
                                    "${getString(context, R.string.status)}: ${
                                        getString(
                                            context, R.string.incomplete
                                        )
                                    }"
                                )
                            }
                            val parseColor = Color.parseColor("#FF0000")
                            status.setTextColor(parseColor)
                            accept.setOnClickListener {
                                if (!isServiceRunning()) {
                                    startSpeeder(
                                        challenge.challengeDuration,
                                        challenge.challengeType,
                                        challenge
                                    )
                                } else {
                                    Toast.makeText(
                                        MyApplication.getContext(),
                                        R.string.active_activity,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Toast.makeText(
                                        MyApplication.getContext(),
                                        R.string.active_activity,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            decline.setOnClickListener {
                                customCoroutineScope.launch {
                                    val challenges = getChallenges().toMutableList()
                                    Log.d("challenges", challenges.toString())
                                    for (item in challenges) {
                                        if (item.id == challenge.id) {
                                            Log.d("removing item at", item.id.toString())
                                            Log.d(
                                                "Removed item",
                                                "${item.id} , ${item.assigner}, ${item.challengeType}"
                                            )
                                            challenges.remove(item)
                                            updateChallenges(challenges)
                                            break
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int
        ): ChallengeListViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.challenge_recycler_card, parent, false)
            return ChallengeListViewHolder(view)

        }

        override fun getItemCount(): Int = items.size
        interface ItemClickListener<T> {
            fun onItemClicked(item: T, itemPosition: Int)
        }

        suspend fun getChallenges(): List<Challenge> {
            return withContext(Dispatchers.IO) {
                userDao.getEntireUser().challenges ?: emptyList()
            }
        }

        fun updateChallenges(challenges: List<Challenge>) {
            ChallengesDisplayDialogViewModel._challenges.postValue(challenges)
            customCoroutineScope.launch {
                withContext(Dispatchers.IO) {
                    userDao.updateChallenges(challenges)
                    userDao.getUserInfo()?.uid?.let { authImpl.setChallenges(challenges, it) }
                }
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        fun updateItems(newItems: List<Challenge>) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }

        override fun onBindViewHolder(holder: ChallengeListViewHolder, position: Int) {
            return holder.bind(items[position])
        }
    }