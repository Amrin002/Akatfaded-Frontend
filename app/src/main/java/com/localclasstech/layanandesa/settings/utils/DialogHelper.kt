package com.localclasstech.layanandesa.settings.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.localclasstech.layanandesa.databinding.CostumPopUpDeleteSuratBinding

object DialogHelper {

    /**
     * Shows confirmation dialog untuk delete/hapus (existing method)
     */
    fun showConfirmationDialog(
        context: Context,
        message: String,
        onConfirm: () -> Unit
    ) {
        val dialogBinding = CostumPopUpDeleteSuratBinding.inflate(LayoutInflater.from(context))

        val dialog = Dialog(context)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Set the message for the dialog
        dialogBinding.textMessage.text = message

        // Set up the button click listeners
        dialogBinding.btnYes.setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }

        dialogBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * Shows confirmation dialog dengan custom title dan pesan
     * Untuk kasus seperti "Belum Ada UMKM" dialog
     */
    fun showConfirmationDialog(
        context: Context,
        title: String,
        message: String,
        onPositive: () -> Unit,
        onNegative: (() -> Unit)? = null
    ) {
        val dialogBinding = CostumPopUpDeleteSuratBinding.inflate(LayoutInflater.from(context))

        val dialog = Dialog(context)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Set title jika ada (bisa tambahkan TextView untuk title di layout jika perlu)
        dialogBinding.textMessage.text = "$title\n\n$message"

        dialogBinding.btnYes.setOnClickListener {
            onPositive()
            dialog.dismiss()
        }

        dialogBinding.btnNo.setOnClickListener {
            onNegative?.invoke()
            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * Shows confirmation dialog khusus untuk UMKM dengan custom button text
     */
    fun showUmkmConfirmationDialog(
        context: Context,
        title: String,
        message: String,
        positiveText: String = "Ya",
        negativeText: String = "Tidak",
        onPositive: () -> Unit,
        onNegative: (() -> Unit)? = null
    ) {
        val dialogBinding = CostumPopUpDeleteSuratBinding.inflate(LayoutInflater.from(context))

        val dialog = Dialog(context)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Set content
        dialogBinding.textMessage.text = "$title\n\n$message"

        // Custom button text jika layout mendukung
        // dialogBinding.btnYes.text = positiveText
        // dialogBinding.btnNo.text = negativeText

        dialogBinding.btnYes.setOnClickListener {
            onPositive()
            dialog.dismiss()
        }

        dialogBinding.btnNo.setOnClickListener {
            onNegative?.invoke()
            dialog.dismiss()
        }

        dialog.show()
    }
}