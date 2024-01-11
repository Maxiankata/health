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
import com.example.healthtracker.ui.login.LoginActivity.Companion.auth
import com.example.healthtracker.ui.setRoundedCorners
import com.example.healthtracker.user.UserInfo
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class FriendListAdapter(private val friendListViewModel: FriendListViewModel) :
    RecyclerView.Adapter<FriendListAdapter.FriendListViewHolder>() {
    var items = ArrayList<UserInfo>()
    var itemClickListener: ItemClickListener<UserInfo>? = null

//    init {
//        updateItems(items)
//    }

    inner class FriendListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.friend_name)
        val email: TextView = view.findViewById(R.id.friend_mail)
        val image: ImageView = view.findViewById(R.id.friend_photo)
        val cardButton: ImageView = view.findViewById(R.id.card_button)
        val friendCardContainer: View = view.findViewById(R.id.friend_card_container)


        fun bind(userInfo: UserInfo) {
            if (userInfo.image != null && userInfo.image != "") {
                Glide.with(image).load(Base64.decode(userInfo.image, Base64.DEFAULT)).into(image)
            }
            image.setRoundedCorners(360F)
            name.text = userInfo.username
            email.text = userInfo.mail
            image.setOnClickListener {
                Log.d("clciked itm", "clicked challenge")
                itemClickListener?.onItemClicked(
                    UserInfo(
                        userInfo.username, userInfo.image, userInfo.mail, userInfo.uid
                    ), adapterPosition
                )

            }
            friendListViewModel.searchState.observeForever() {
                if (it) {
                    cardButton.apply {
                        setImageResource(R.drawable.friend_add)
                        setOnClickListener {
                            friendListViewModel.addFriend()
                            Log.d("added friend", "added friend")
                        }
                    }
                } else {
                    cardButton.apply {
                        setImageResource(R.drawable.flag)
                        setOnClickListener {
                            Log.d("clicked challenge", "clicked challenge")

                        }
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<UserInfo>) {

        //when image is changed, notifyDataSetChanged() activates and doubles the list, think of fix pls

        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): FriendListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friend_card, parent, false)
        return FriendListViewHolder(view)

    }

    override fun onBindViewHolder(holder: FriendListViewHolder, position: Int) {
        return holder.bind(items[position])
    }


    override fun getItemCount(): Int = items.size
    interface ItemClickListener<T> {
        fun onItemClicked(item: T, itemPosition: Int)
    }


}