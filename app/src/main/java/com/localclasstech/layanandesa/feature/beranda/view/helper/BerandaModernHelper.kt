package com.localclasstech.layanandesa.feature.beranda.view.helper

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.localclasstech.layanandesa.databinding.FragmentBerandaModernBinding

class BerandaModernHelper(private val binding: FragmentBerandaModernBinding, private val context: Context) {

    fun setupLogic(){
        binding.button.setOnClickListener {
            Toast.makeText(context, "Modern Button Clicked", Toast.LENGTH_SHORT).show()
        }
    }

}