package com.example.healthtracker.ui.account.friends.popup

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.healthtracker.R
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.ui.account.friends.challenges.ChallengeBuilderDialogFragment
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class FriendListAdapter : RecyclerView.Adapter<FriendListAdapter.FriendListViewHolder>() {
    private var items = ArrayList<UserInfo>()
    var itemClickListener: ItemClickListener<UserInfo>? = null
    private val friendListViewModel = FriendListViewModel()

    inner class FriendListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.friend_name)
        private val email: TextView = view.findViewById(R.id.friend_mail)
        private val image: ImageView = view.findViewById(R.id.friend_photo)
        private val cardButton: ImageView = view.findViewById(R.id.card_button)
        private val friendCardContainer: View = view.findViewById(R.id.friend_card_container)

        @SuppressLint("ResourceAsColor")
        fun bind(userInfo: UserInfo) {
            if (userInfo.image.isNullOrEmpty()) {
                Glide.with(image).load(R.drawable.profile_icon).into(image)
            } else {
                Glide.with(image).load(Base64.decode(userInfo.image, Base64.DEFAULT)).into(image)
            }
            image.setBackgroundResource(R.drawable.circle_background)
            name.text = userInfo.username
            email.text = userInfo.mail
            friendCardContainer.apply {
                if (!userInfo.theme.isNullOrEmpty()) {
                    val backgroundTintList = ColorStateList.valueOf(Color.parseColor(userInfo.theme))
                    ViewCompat.setBackgroundTintList(this, backgroundTintList)
                }else{
                    val backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light_green,resources.newTheme()))
                    ViewCompat.setBackgroundTintList(this, backgroundTintList)
                }
                setOnClickListener {
                    itemClickListener?.onItemClicked(userInfo, adapterPosition)

                }
            }
            FriendListViewModel.searchState.observeForever {
                if (it) {
                    cardButton.apply {
                        setImageResource(R.drawable.friend_add)
                        Log.d("FRIEND", "ICON SHOULD BE HERE")
                        setOnClickListener {
                                    userInfo.uid?.let { it1 -> friendListViewModel.addFriend(it1) }
                        }
                    }
                } else cardButton.visibility = View.GONE
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<UserInfo>) {
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