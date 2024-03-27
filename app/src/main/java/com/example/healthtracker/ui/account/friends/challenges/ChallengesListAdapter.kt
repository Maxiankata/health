package com.example.healthtracker.ui.account.friends.challenges

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtracker.MyApplication
import com.example.healthtracker.R

class ChallengesListAdapter :
    RecyclerView.Adapter<ChallengesListAdapter.ChallengeListViewHolder>() {

    val items = ArrayList<Challenge>()
    val itemClickListener : ItemClickListener<Challenge>? = null
    val context = MyApplication.getContext()
    inner class ChallengeListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val challengeType: TextView = view.findViewById(R.id.challenge_type)
        private val challengeDuration: TextView = view.findViewById(R.id.challenge_duration)
        private val challenger: TextView = view.findViewById(R.id.challenger)
        private val status: TextView = view.findViewById(R.id.status)
        private val challengeImage: ImageView = view.findViewById(R.id.challenge_image)
        val accept: Button = view.findViewById(R.id.accept_challenge)
        val decline: Button = view.findViewById(R.id.decline_challenge)
        fun bind(challenge: Challenge) {
            when(challenge.challengeType){
                ChallengeType.WALKING -> {
                    challengeImage.setImageResource(R.drawable.power_walking_icon)
                    challengeType.text = buildString {
                        append("${getString(context, R.string.type)} : ${getString(context,R.string.power_walking)}")
                    }
                    accept.setOnClickListener {

                    }
                }
                ChallengeType.CYCLING -> {
                    challengeImage.setImageResource(R.drawable.cycling_icon)
                    challengeType.text = buildString {
                        append("${getString(context, R.string.type)} : ${getString(context,R.string.cycling)}")
                    }
                    accept.setOnClickListener {

                    }
                }
                ChallengeType.RUNNING -> {
                    challengeImage.setImageResource(R.drawable.running_icon)
                    challengeType.text = buildString {
                        append("${getString(context, R.string.type)} : ${getString(context,R.string.running)}")
                    }
                    accept.setOnClickListener {

                    }
                }
                ChallengeType.JOGGING -> {
                    challengeImage.setImageResource(R.drawable.jogging_icon)
                    challengeType.text = buildString {
                        append("${getString(context, R.string.type)} : ${getString(context,R.string.jogging)}")
                    }
                    accept.setOnClickListener {
//todo() start activity with challenge
                    }
                }
                ChallengeType.POWER_WALKING -> {
                    challengeImage.setImageResource(R.drawable.power_walking_icon)
                    challengeType.text = buildString {
                        append("${getString(context, R.string.type)} : ${getString(context,R.string.power_walking)}")
                    }
                    accept.setOnClickListener {

                    }
                }
            }

            challengeDuration.text = buildString {
                append("${getString(context, R.string.duration)}: ${challenge.challengeDuration}")
            }
            challenger.text = buildString {
                append("${getString(context,R.string.by)}: ${challenge.assigner}")
            }
            if (challenge.challengeCompletion) {
                status.text.apply {
                    buildString {
                        append(
                            "${getString(context, R.string.status)}: ${
                                getString(
                                    context,
                                    R.string.complete
                                )
                            }"
                        )
                    }
                }
                val parseColor = Color.parseColor("#00FF00");
                status.setTextColor(parseColor)
            }else{
                status.text = buildString {
                    append("${getString(context,R.string.status)}: ${getString(context, R.string.incomplete)}")
                }
                val parseColor = Color.parseColor("#FF0000");
                status.setTextColor(parseColor)
            }

        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ChallengeListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.challenge_recycler_card, parent, false)
        return ChallengeListViewHolder(view)

    }

    override fun getItemCount(): Int = items.size
    interface ItemClickListener<T> {
        fun onItemClicked(item: T, itemPosition: Int)
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