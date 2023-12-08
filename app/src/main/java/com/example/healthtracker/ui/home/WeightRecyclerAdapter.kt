package com.example.healthtracker.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtracker.R

class WeightRecyclerAdapter:RecyclerView.Adapter<WeightRecyclerAdapter.WeightViewHolder>() {
    private val weight = Weight.weight


    inner class WeightViewHolder(view: View):RecyclerView.ViewHolder(view){
        private val text = view.findViewById<TextView>(R.id.weight)
        fun bind(double:Any){
            text.text = double.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.weigh_card,parent,false)
        return WeightViewHolder(view)
    }

    override fun getItemCount() = weight.size

    public fun getItem(position:Int):String{
        return weight[position].toString()
    }
    override fun onBindViewHolder(holder: WeightViewHolder, position: Int) {
        holder.bind(weight[position])
    }

}