package com.e4rdx.snote.activities.notebookDisplayer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView

@SuppressLint("ViewConstructor")
class ChecklistDropdown(context: Context, text: String, state: Boolean, index: Int) : LinearLayout(context) {
    var checkBox: CheckBox? = null
    var index: Int = 0
    init {
        this.index = index

        this.orientation = HORIZONTAL

        checkBox = CheckBox(context)
        checkBox!!.isChecked = state

        val t = TextView(context)
        t.setTextColor(Color.parseColor("#000000"))
        t.textSize = 20F
        //t.text = text
        t.setText(markLinks(text), TextView.BufferType.SPANNABLE)
        t.setOnLongClickListener{
            val parts: Array<String> = text.split(" ").toTypedArray()
            if (parts.isNotEmpty()) {
                for (part in parts) {
                    if (part.contains("https://") || part.contains("http://")) {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(part))
                        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(browserIntent)
                    }
                }
            }
            false
        }

        this.addView(checkBox)
        this.addView(t)
    }

    private fun markLinks(s: String): SpannableStringBuilder? {
        val builder = SpannableStringBuilder()
        val parts = s.split(" ").toTypedArray()
        if (parts.isNotEmpty()) {
            for (part in parts) {
                val str = SpannableString("$part ")
                if (part.contains("https://") || part.contains("http://")) {
                    str.setSpan(ForegroundColorSpan(Color.BLUE), 0, str.length - 1, 0)
                    str.setSpan(UnderlineSpan(), 0, str.length - 1, 0)
                } else {
                    str.setSpan(ForegroundColorSpan(Color.BLACK), 0, str.length, 0)
                }
                builder.append(str)
            }
        }
        return builder
    }
}