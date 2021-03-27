package com.e4rdx.snote.ui.popups

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

class YesNoPopup(context: Context, title: String, message: String,
                 dialogClickListener: DialogInterface.OnClickListener) : AlertDialog.Builder(context) {
    init {
        this.setTitle(title)
        this.setMessage(message)
        this.setPositiveButton("Ja", dialogClickListener)
        this.setNegativeButton("Nein", dialogClickListener)
        this.show()
    }
}