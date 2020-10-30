package com.zoop.zoopandroidsample

import android.content.Context
import android.widget.Toast

class ToastHelper (val context: Context) {

    fun showToast(sMessage: String) {
            Toast.makeText(
                context,
                sMessage,
                Toast.LENGTH_SHORT
            ).show()
    }

}