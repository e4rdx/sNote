package com.e4rdx.snote.activities.notebookDisplayer

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.e4rdx.snote.R

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
        t.text = text

        this.addView(checkBox)
        this.addView(t)
    }
}