package com.localclasstech.layanandesa.settings.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.localclasstech.layanandesa.R

class CustomSpinnerAdapter(context: Context, private val items: List<String>) :
    ArrayAdapter<String>(context, R.layout.spinner_item, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view as TextView
        textView.setTextColor(ContextCompat.getColor(context, R.color.black)) // Set your desired color
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val textView = view as TextView
        textView.setTextColor(ContextCompat.getColor(context, R.color.black)) // Set your desired color
        return view
    }
}