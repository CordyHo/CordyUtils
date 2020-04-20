package com.cordyho.widget

import android.content.Context
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog

class MProgressDialog(context: Context) : AlertDialog(context) {

    init {
        val progressBar = ProgressBar(context)
        progressBar.setPadding(0, 0, 0, 50)
        setView(progressBar)
    }
}