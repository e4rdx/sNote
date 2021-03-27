@file:Suppress("PackageName")

package com.e4rdx.snote.Activities.Main_NotebookDisplay

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.Gravity
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import com.e4rdx.snote.Activities.Attachments.AttachmentEditor
import com.e4rdx.snote.Activities.CheckList.ChecklistEditor
import com.e4rdx.snote.Activities.TextNote.TextEditor
import com.e4rdx.snote.R
import org.json.JSONObject
import java.io.File

@Suppress("LocalVariableName", "PropertyName")
class NoteKT(context: Context, jsonObj: JSONObject, index: Int): LinearLayout(context) {
    var i = Intent()
    var btn_open : Button? = null
    var jsonData : String? = null
    var index : Int? = null
    var dropdown : LinearLayout? = null

    init {
        this.orientation = VERTICAL
        this.isBaselineAligned = false

        val noteType = jsonObj.getString("type")
        val noteName = jsonObj.getString("name")
        jsonData = jsonObj.toString()

        this.index = index

        val buttonsRoot = LinearLayout(context)
        buttonsRoot.orientation = HORIZONTAL
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        layoutParams.bottomMargin = 7
        buttonsRoot.layoutParams = layoutParams
        //buttonsRoot.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.new_green, null))
        buttonsRoot.setBackgroundResource(R.drawable.background_note)

        val leftLinearLayout = LinearLayout(context)
        leftLinearLayout.orientation = HORIZONTAL
        leftLinearLayout.gravity = Gravity.LEFT
        var params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        params.weight = 0.8f
        leftLinearLayout.layoutParams = params

        val rightLinearLayout = LinearLayout(context)
        rightLinearLayout.orientation = HORIZONTAL
        rightLinearLayout.gravity = Gravity.RIGHT
        params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.weight = 0.2f
        params.rightMargin = 8
        rightLinearLayout.layoutParams = params

        //Create Dropdown
        dropdown = LinearLayout(context)
        dropdown!!.orientation = VERTICAL
        dropdown!!.visibility = GONE
        params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        dropdown!!.layoutParams = params

        //Create Image for displaying the type
        val img_type = ImageView(context)
        when(noteType){
            "checkliste" -> {
                img_type.setImageResource(R.drawable.ic_checkbox)
            }
            "text" -> {
                img_type.setImageResource(R.drawable.ic_text)
            }
            "image" -> {
                img_type.setImageResource(R.drawable.ic_image)
            }
        }
        params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        params.rightMargin = 3
        //params.weight = 2f
        img_type.layoutParams = params

        //Create Button for opening the note
        btn_open = Button(context)
        btn_open!!.transformationMethod = null
        btn_open!!.text = noteName
        params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        //params.weight = 1f
        btn_open!!.layoutParams = params
        btn_open!!.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.invisible, null))
        btn_open!!.textSize = 20f
        btn_open!!.textAlignment = Button.TEXT_ALIGNMENT_VIEW_START
        btn_open!!.setOnClickListener {
            //Toast.makeText(context, "Opening...", Toast.LENGTH_LONG).show()
            context.startActivity(i)
        }

        //Create Button for dropdown
        val btn_dropdown = ImageButton(context)
        btn_dropdown.setImageResource(R.drawable.ic_dropdown)
        btn_dropdown.setOnClickListener {
            //Toast.makeText(context, "Dropdown under construction...", Toast.LENGTH_LONG).show()
            if(dropdown!!.visibility == VISIBLE) {
                dropdown!!.visibility = GONE
            }
            else{
                dropdown!!.visibility = VISIBLE
            }
        }
        params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        //params.weight = 1f
        btn_dropdown.layoutParams = params
        btn_dropdown.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.invisible, null))

        //Add Buttons to ListView
        leftLinearLayout.addView(img_type)
        leftLinearLayout.addView(btn_open)
        rightLinearLayout.addView(btn_dropdown)
        buttonsRoot.addView(leftLinearLayout)
        buttonsRoot.addView(rightLinearLayout)

        this.addView(buttonsRoot)
        this.addView(dropdown)

        configureIntent(noteType)
        createDropdown(noteType)
    }

    private fun createDropdown(type: String){
        when(type){
            "text" -> {
                val t = TextView(context)
                t.text = JSONObject(jsonData).getString("text")
                t.textSize = 20F
                t.setTextColor(resources.getColor(R.color.black))
                val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                params.leftMargin = 50
                t.layoutParams = params
                dropdown?.addView(t)
            }
            "checkliste" -> {
                val entrys = JSONObject(jsonData).getJSONArray("entrys")
                for (entrynumber in 0 until entrys.length()) {
                    val jsonObj = entrys.getJSONObject(entrynumber)
                    val text = jsonObj.getString("text")
                    val state = jsonObj.getBoolean("state")
                    val checklistEntry = ChecklistDropdown(context, text, state)
                    dropdown?.addView(checklistEntry)
                }
            }
            "image" -> {
                val i = ImageView(context)
                val imgFile = File(JSONObject(jsonData).getString("src"))
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                i.setImageBitmap(myBitmap)
                val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                i.layoutParams = params
                dropdown?.addView(i)
            }
        }
    }

    private fun configureIntent(type: String){
        when(type){
            "text" -> {
                i = Intent(context, TextEditor::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                i.putExtra("jsonData", jsonData.toString())
                i.putExtra("edit", true)
                i.putExtra("index", index)
            }
            "checkliste" -> {
                i = Intent(context, ChecklistEditor::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                i.putExtra("jsonData", jsonData.toString())
                i.putExtra("edit", true)
                i.putExtra("index", index)
            }
            "image" -> {
                i = Intent(context, AttachmentEditor::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                i.putExtra("jsonData", jsonData.toString())
                i.putExtra("edit", true)
                i.putExtra("index", index)
            }
        }
    }

    fun remove(){
        val parent = this.parent as LinearLayout
        parent.removeView(this)
    }
}