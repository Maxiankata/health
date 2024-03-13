package com.example.healthtracker.ui.home

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtracker.R
import com.example.healthtracker.data.user.UserInfo

class WeightRecyclerAdapter:RecyclerView.Adapter<WeightRecyclerAdapter.WeightViewHolder>() {
    private val items = mutableListOf<Int>()

    inner class WeightViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val text = view.findViewById<TextView>(R.id.weight)

//        init {
//            // Attach a focus change listener to the root view
//            view.setOnFocusChangeListener { _, hasFocus ->
//                if (hasFocus) {
//                    animateScale(view, 1.2f) // Scale up when gaining focus
//                    Log.d("Has focus", text.toString())
//                } else {
//                    Log.d("NO HAS FOCUS", text.toString())
//                    animateScale(view, 1.0f) // Revert to normal size when losing focus
//                }
//            }
//        }

        fun bind(double: Any) {
            text.text = double.toString()
        }
    }

    // ... (unchanged code)

    private fun animateScale(view: View, scale: Float) {
        val scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.SCALE_X, scale),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, scale)
        )
        scaleAnimator.duration = 300 // Adjust the duration as needed
        scaleAnimator.interpolator = AccelerateDecelerateInterpolator()
        scaleAnimator.start()
    }
//    what the hell have i written here

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.weigh_card,parent,false)
        return WeightViewHolder(view)
    }

    override fun getItemCount() = items.size
    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<Int>) {
        items.clear()
        items.addAll(newItems)
        Log.d("items", items.toString())
        notifyDataSetChanged()
    }
    public fun getItem(position:Int):String{
        return items[position].toString()
    }
    override fun onBindViewHolder(holder: WeightViewHolder, position: Int) {
        holder.bind(items[position])
//        val recyclerView = holder.itemView as? RecyclerView
//        val layoutManager = recyclerView?.layoutManager
//        //val recyclerView is null, fix
//        if (layoutManager is LinearLayoutManager) {
//            val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
//            val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
//
//            if (position in firstVisiblePosition..lastVisiblePosition) {
//                val middlePosition = (lastVisiblePosition - firstVisiblePosition) / 2 + firstVisiblePosition
//
//                if (position == middlePosition) {
//                    Log.d("middle position", getItem(position))
//                    holder.itemView.requestFocus()
//                }else{
//                    Log.d("not middle position", getItem(position))
//                }
//            }
//        }else{
//            Log.d("Layout manager is not linear layout manager", layoutManager.toString())
//        }

    }

}