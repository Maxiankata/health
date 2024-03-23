package com.example.healthtracker.ui.account.friends.profile

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.unit.dp
import androidx.core.view.marginBottom
import com.example.healthtracker.MyApplication
import com.example.healthtracker.R
import com.example.healthtracker.data.SpinnerItem

class ChallengeSpinnerAdapter(context: Context, private val items: List<SpinnerItem>) :
    ArrayAdapter<SpinnerItem>(context, R.layout.custom_spinner_item, items) {

    private val textColor: Int = Color.BLACK

    @SuppressLint("ResourceAsColor", "UseCompatLoadingForDrawables")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.custom_spinner_item, parent, false)
        val textView = view.findViewById<TextView>(R.id.spinner_text)
        val imageView = view.findViewById<ImageView>(R.id.spinner_image)
        view.marginBottom.plus(10)
        view.background = MyApplication.getContext().getDrawable(R.drawable.rounded_background)
        val item = items[position]
        textView.text = item.text
        imageView.setImageResource(item.imageId)

        textView.setTextColor(textColor)

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {

        return getView(position, convertView, parent)
    }
}