package com.example.healthtracker.ui.friends

import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
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
import com.example.healthtracker.user.UserInfo
import com.example.healthtracker.user.UserMegaInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class FriendListAdapter : RecyclerView.Adapter<FriendListAdapter.FriendListViewHolder>() {
    var items = ArrayList<UserInfo>()
    var itemClickListener: ItemClickListener<FriendFront>? = null
    inner class FriendListViewHolder(view: View):RecyclerView.ViewHolder(view){
        val name:TextView= view.findViewById(R.id.friend_name)
        val email:TextView=view.findViewById(R.id.friend_mail)
        val image:ImageView=view.findViewById(R.id.friend_photo)
        val challenge:ImageView=view.findViewById(R.id.challenge)
        val message:ImageView=view.findViewById(R.id.chat)

        fun bind(friendFront: UserInfo){
            if (friendFront.image!=null && friendFront.image!=""){
                Glide.with(image).load(Base64.decode(friendFront.image, Base64.DEFAULT))
                    .into(image)

            }
            image.setRoundedCorners(360F)
            name.text=friendFront.username
            email.text=friendFront.mail
            challenge.setOnClickListener{
                //todo put this in a fragment maybe, navigate with bundle
            }
            message.setOnClickListener {
                //todo put this in a fragment maybe, navigate with bundle
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<UserInfo>){
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

    override fun onBindViewHolder(holder: FriendListViewHolder, position: Int) {

        return holder.bind(items[position])
    }


    override fun getItemCount(): Int =items.size
    interface ItemClickListener<T> {
        fun onItemClicked(item: T, itemPosition: Int)
    }



}