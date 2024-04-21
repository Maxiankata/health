package com.example.healthtracker.ui.account.achievements

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtracker.MyApplication
import com.example.healthtracker.R
import com.example.healthtracker.data.user.Achievement

class AchievementsRecyclerAdapter(val steps:Int, val items:List<Achievement>) :
    RecyclerView.Adapter<AchievementsRecyclerAdapter.AchievementsViewHolder>() {
    val context = MyApplication.getContext()
    inner class AchievementsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.achievement_name)
        val image: ImageView = view.findViewById(R.id.achievement_image)
        val description: TextView = view.findViewById(R.id.achievement_goal)
        fun bind(achievement: Achievement) {
            image.setImageResource(achievement.image)
            name.text = getString(context,achievement.name)
            description.text = buildString {
                append("${getString(context, R.string.reach)} ${achievement.goal} ${getString(context, R.string.stepss)}")
            }
            if (steps>=achievement.goal){
                achievement.unlocked = true
                itemView.setBackgroundResource(R.drawable.achieved)
            }else{
                itemView.setBackgroundResource(R.drawable.unachieved)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): AchievementsRecyclerAdapter.AchievementsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.achievements_recycler_card, parent, false)
        return AchievementsViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: AchievementsRecyclerAdapter.AchievementsViewHolder, position: Int
    ) {
        return holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}