package com.example.healthtracker.ui.friends

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.healthtracker.R
import com.example.healthtracker.ui.setRoundedCorners
import com.example.healthtracker.user.FriendFront

class FriendListAdapter: RecyclerView.Adapter<FriendListAdapter.FriendListViewHolder>() {

    val items = ArrayList<FriendFront>()
    var itemClickListener: ItemClickListener<FriendFront>? = null

    inner class FriendListViewHolder(view: View):RecyclerView.ViewHolder(view){
        val name:TextView= view.findViewById(R.id.friend_name)
        val email:TextView=view.findViewById(R.id.friend_mail)
        val image:ImageView=view.findViewById(R.id.friend_photo)
        val challenge:TextView=view.findViewById(R.id.challenge)
        val message:ImageView=view.findViewById(R.id.chat)

        fun bind(friendFront: FriendFront){
            Glide.with(image).load(friendFront.photo).into(image)

            image.setRoundedCorners(360F)
            name.text=friendFront.name
            email.text=friendFront.email

            challenge.setOnClickListener{
                //todo put this in a fragment maybe, navigate with bundle
            }
            message.setOnClickListener {
                //todo put this in a fragment maybe, navigate with bundle
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<FriendFront>){
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friend_card, parent, false)
        return FriendListViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendListViewHolder, position: Int) =
        holder.bind(items[position])


    override fun getItemCount(): Int =items.size
    interface ItemClickListener<T> {
        fun onItemClicked(item: T, itemPosition: Int)
    }
}