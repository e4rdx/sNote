package com.e4rdx.snote.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.Editable
import android.text.InputType
import android.text.SpannableStringBuilder
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


class TextInputDialog(context: Context, title: String, message: String) : AlertDialog.Builder(context) {
    private var input: EditText? = null

    init {
        this.setTitle(title)
        this.setMessage(message)

        // Set up the input
        input = EditText(context)
        input!!.inputType = InputType.TYPE_CLASS_TEXT

        this.setView(input)
    }

    fun setupButtons(text_btn_positive: String, text_btn_negative: String,
                     dialogClickListener: DialogInterface.OnClickListener){
        this.setPositiveButton(text_btn_positive, dialogClickListener)
        this.setNegativeButton(text_btn_negative, dialogClickListener)
    }

    fun getText(): String{
        return input!!.text.toString()
    }

    fun setText(text: String){
        val editable: Editable = SpannableStringBuilder(text)
        input!!.text = editable
    }

    fun selectAllOnFocus(){
        input!!.setSelectAllOnFocus(true)
    }

    fun openKeyboard(context: Context){
        input!!.requestFocus()
        input!!.postDelayed({
            val keyboard: InputMethodManager? = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            keyboard!!.showSoftInput(input, 0)
        }, 200)
    }
}