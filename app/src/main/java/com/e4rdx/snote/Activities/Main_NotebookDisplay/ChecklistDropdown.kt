package com.e4rdx.snote.Activities.Main_NotebookDisplay

import android.content.Context
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView

class ChecklistDropdown(context: Context, text: String, state: Boolean) : LinearLayout(context) {
    init {
        this.orientation = HORIZONTAL

        val checkbox = CheckBox(context)
        checkbox.isChecked = state
        checkbox.isClickable = false

        val t = TextView(context)
        t.text = text

        this.addView(checkbox)
        this.addView(t)
    }
}