package com.localclasstech.layanandesa.feature.beranda.view.helper

import android.content.Context
import android.widget.Toast
import com.localclasstech.layanandesa.databinding.FragmentBerandaSimpleBinding

class BerandaSimpleHelper(private val binding: FragmentBerandaSimpleBinding, private val context: Context) {
    fun setupLogic(){
        binding.button.setOnClickListener {
            Toast.makeText(context, "Simple Button Clicked", Toast.LENGTH_SHORT).show()
        }
    }
}