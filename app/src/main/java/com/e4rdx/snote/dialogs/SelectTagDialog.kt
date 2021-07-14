package com.e4rdx.snote.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnMultiChoiceClickListener
import java.util.*


class SelectTagDialog(context: Context, title: String, tags: Array<String>) : AlertDialog.Builder(context) {
    private var choices: LinkedList<String>

    init {
        this.setTitle(title)

        choices = LinkedList()

        this.setMultiChoiceItems(tags, null) { dialog, index, isChecked ->
            // user checked or unchecked a box
            if(isChecked) {
                choices.add(tags[index])
            }
            else{
                choices.remove(tags[index])
            }
        }
    }

    fun setupButtons(text_btn_positive: String, text_btn_negative: String,
                     dialogClickListener: DialogInterface.OnClickListener){
        this.setPositiveButton(text_btn_positive, dialogClickListener)
        this.setNegativeButton(text_btn_negative, dialogClickListener)
    }

    fun getChoices(): LinkedList<String>{
        return choices
    }
}