package com.example.healthtracker.ui.account.friends.popup

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.data.user.UserFriends
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RequestRecyclerAdapter :
    RecyclerView.Adapter<RequestRecyclerAdapter.RecyclerAdapterViewHolder>() {
    private var items = ArrayList<UserFriends>()
    private val auth = AuthImpl.getInstance()
    private val customCoroutineScope = CoroutineScope(Dispatchers.IO)
    private val mainCoroutineScope = CoroutineScope(Dispatchers.Main)
    private val userDao = MainActivity.getDatabaseInstance().dao()
    var currentUserList = MutableLiveData<List<UserFriends>>()
    var currentUserId = MutableLiveData<String>()
    inner class RecyclerAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.friend_request_name)
        private val email: TextView = view.findViewById(R.id.friend_request_mail)
        private val image: ImageView = view.findViewById(R.id.friend_request_photo)
        private val decline: ImageView = view.findViewById(R.id.decline_request)
        private val accept: ImageView = view.findViewById(R.id.accept_request)
        private val status: TextView = view.findViewById(R.id.request_status)
        private val friendCardContainer: View =
            view.findViewById(R.id.friend_request_card_container)
        fun feedNeededInformation(){
            customCoroutineScope.launch {
                currentUserId.postValue(userDao.getUserInfo()?.uid!!)
                currentUserList.postValue(auth.fetchUserFriends())
            }
        }
        init {
            feedNeededInformation()
        }
        fun bind(userFriends: UserFriends) {
            status.visibility = GONE
            customCoroutineScope.launch {
                val userInfo = auth.getUserInfo(userFriends.uid)
                if (userInfo != null) {
                    mainCoroutineScope.launch {
                        if (userInfo.image.isNullOrEmpty()) {
                            Glide.with(image).load(R.drawable.profile_icon).into(image)
                        } else {
                            Glide.with(image).load(Base64.decode(userInfo.image, Base64.DEFAULT))
                                .into(image)
                        }
                    }
                    image.setBackgroundResource(R.drawable.circle_background)
                    name.text = userInfo.username
                    email.text = userInfo.mail
                    friendCardContainer.apply {
                        if (!userInfo.theme.isNullOrEmpty()) {
                            val backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor(userInfo.theme))
                            ViewCompat.setBackgroundTintList(this, backgroundTintList)
                        } else {
                            val backgroundTintList = ColorStateList.valueOf(
                                resources.getColor(
                                    R.color.light_green,
                                    resources.newTheme()
                                )
                            )
                            ViewCompat.setBackgroundTintList(this, backgroundTintList)
                        }
                    }
                }
            }

            accept.setOnClickListener {
                customCoroutineScope.launch {
                    if (!currentUserId.value.isNullOrBlank()){
                        auth.addFriend(currentUserId.value!!, userFriends.uid)
                    }
                }
                decline.visibility = GONE
                accept.visibility = GONE
                status.apply {
                    visibility = VISIBLE
                    text = "accepted"
                }
            }
            decline.setOnClickListener {
                customCoroutineScope.launch {
                    currentUserList.value?.let { it1 -> auth.removeFriend(userFriends.uid, it1) }
                    Log.d("removing friend at",userFriends.uid )
                }
                decline.visibility = GONE
                accept.visibility = GONE
                status.apply {
                    visibility = VISIBLE
                    text = "declined"
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<UserFriends>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): RecyclerAdapterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.friend_request_card, parent, false)
        return RecyclerAdapterViewHolder(view)

    }

    override fun onBindViewHolder(holder: RecyclerAdapterViewHolder, position: Int) {
        return holder.bind(items[position])
    }


    override fun getItemCount(): Int = items.size
    interface ItemClickListener<T> {
        fun onItemClicked(item: T, itemPosition: Int)
    }


}