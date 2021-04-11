@file:Suppress("PackageName")

package com.e4rdx.snote.Activities.Main_NotebookDisplay.fragments.tags

import android.content.Context
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.e4rdx.snote.FlowLayout
import com.e4rdx.snote.R

class Tag(context: Context, name: String, ref: TagFragment): LinearLayout(context) {
    var name: String? = null
    var fragmentRef: TagFragment? = null

    init {
        this.orientation = HORIZONTAL
        this.setBackgroundResource(R.drawable.background_tag)
        //this.setBackgroundColor(Color.parseColor("#FF0000"))

        fragmentRef = ref

        var params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        this.layoutParams = params

        this.name = name

        val txt = TextView(context)
        txt.text = name
        txt.textSize = 20F
        txt.setTextColor(ContextCompat.getColor(context, R.color.black))
        params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.leftMargin = 20
        txt.layoutParams = params


        val img_btn = ImageButton(context)
        img_btn.setBackgroundResource(R.drawable.ic_close)
        params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        img_btn.layoutParams = params
        img_btn.setOnClickListener{
            val parentLinearLayout = this.parent as FlowLayout
            parentLinearLayout.removeView(this)
            fragmentRef!!.saveTags()
        }

        this.addView(txt)
        this.addView(img_btn)
    }
}