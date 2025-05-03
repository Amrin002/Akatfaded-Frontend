// Create a new file: DialogHelper.kt

package com.localclasstech.layanandesa.settings.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.localclasstech.layanandesa.databinding.CostumPopUpDeleteSuratBinding


object DialogHelper {

    /**
     * Shows a generic confirmation dialog
     *
     * @param context Context to create the dialog
     * @param message Message to show in the dialog
     * @param onConfirm Action to perform when user confirms
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
}