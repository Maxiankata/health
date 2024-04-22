package com.example.healthtracker.ui.home

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtracker.R

class WeightRecyclerAdapter(private val numbers: MutableList<Int>) :
    RecyclerView.Adapter<WeightRecyclerAdapter.WeightViewHolder>() {
    private var middleItemPosition = -1

    inner class WeightViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val text = view.findViewById<TextView>(R.id.weight_text)
        fun bind(item: Int, isMiddleItem: Boolean, position: Int) {
            text.text = item.toString()
            text.textSize = if (isMiddleItem) 25f else 20f
            var recyclerView: RecyclerView? = null
            var parentView: View? = itemView
            while (parentView != null) {
                if (parentView is RecyclerView) {
                    recyclerView = parentView
                    break
                }
                parentView = if (parentView.parent is View) {
                    parentView.parent as View
                } else {
                    null
                }
            }
            val translationY = if (isMiddleItem && recyclerView != null) {
                recyclerView.height / 2 - text.height / 2
            } else {
                0
            }
            val animator = ObjectAnimator.ofPropertyValuesHolder(
                text,
                PropertyValuesHolder.ofFloat("textSize", if (isMiddleItem) 30f else 25f),
                PropertyValuesHolder.ofFloat("translationY", translationY.toFloat())
            )
            animator.duration = 500
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.start()
        }


    }

    fun updateMiddleItemSize(middlePosition: Int) {
        if (middlePosition != middleItemPosition) {
            if (middleItemPosition != -1) {
                val previousMiddleItem = numbers[middleItemPosition]
                notifyItemChanged(middleItemPosition, previousMiddleItem)
            }
            middleItemPosition = middlePosition
            notifyItemChanged(middleItemPosition, numbers[middleItemPosition])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.weigh_card, parent, false)
        return WeightViewHolder(view)
    }

    override fun getItemCount() = numbers.size
    fun getItem(position: Int): String {
        return numbers[position].toString()
    }

    override fun onBindViewHolder(holder: WeightViewHolder, position: Int) {
        val item = numbers[position]
        holder.bind(item, position == middleItemPosition, position)
    }

}