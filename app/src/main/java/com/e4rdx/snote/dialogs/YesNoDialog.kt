package com.e4rdx.snote.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.e4rdx.snote.R

class YesNoDialog(context: Context, title: String, message: String,
                  dialogClickListener: DialogInterface.OnClickListener) : AlertDialog.Builder(context) {
    init {
        this.setTitle(title)
        this.setMessage(message)
        this.setPositiveButton(context.getText(R.string.yes), dialogClickListener)
        this.setNegativeButton(context.getString(R.string.no), dialogClickListener)
        this.show()
    }
}