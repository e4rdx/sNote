@file:Suppress("PackageName")

package com.e4rdx.snote.Activities.Main_NotebookDisplay.fragments.tags

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.e4rdx.snote.FlowLayout
import com.e4rdx.snote.R
import com.e4rdx.snote.ui.popups.YesNoPopup

@SuppressLint("ViewConstructor")
class Tag(context: Context, name: String, ref: TagFragment): LinearLayout(context) {
    var name: String = ""
    private var fragmentRef: TagFragment? = null
    var selectedByUser: Boolean = false

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
        txt.setOnClickListener{
            if(selectedByUser){
                this.setBackgroundResource(R.drawable.background_tag)
            } else{
                this.setBackgroundResource(R.drawable.background_tag_selected)
            }
            selectedByUser = !selectedByUser
            fragmentRef!!.listNotes()
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
                    fragmentRef!!.saveTags()
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                }
            }
        }
        YesNoPopup(context, "Are you sure?",
                "Do you really want to remove the tag?", dialogClickListener)
    }
}