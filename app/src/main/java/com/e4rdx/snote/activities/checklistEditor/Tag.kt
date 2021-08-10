@file:Suppress("PackageName")

package com.e4rdx.snote.activities.checklistEditor

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.e4rdx.snote.R
import com.e4rdx.snote.activities.notebookDisplayer.fragments.tags.FlowLayout
import com.e4rdx.snote.dialogs.YesNoDialog

@SuppressLint("ViewConstructor")
class Tag(context: Context, name: String): LinearLayout(context) {
    var name: String = ""
    var selectedByUser: Boolean = false

    init {
        this.orientation = HORIZONTAL
        this.setBackgroundResource(R.drawable.background_tag)

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
        txt.setOnClickListener{
            /*if(selectedByUser){
                this.setBackgroundResource(R.drawable.background_tag)
            } else{
                this.setBackgroundResource(R.drawable.background_tag_selected)
            }
            selectedByUser = !selectedByUser*/
        }


        val img_btn = ImageButton(context)
        img_btn.setBackgroundResource(R.drawable.ic_close)
        params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        img_btn.layoutParams = params
        img_btn.setOnClickListener{
            removePopup(context)
        }

        this.addView(txt)
        this.addView(img_btn)
    }

    private fun removePopup(context: Context) {
        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    val parentLinearLayout = this.parent as FlowLayout
                    parentLinearLayout.removeView(this)
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                }
            }
        }
        YesNoDialog(context, context.getString(R.string.tags_are_you_sure),
                context.getString(R.string.tags_really_remove), dialogClickListener)
    }
}