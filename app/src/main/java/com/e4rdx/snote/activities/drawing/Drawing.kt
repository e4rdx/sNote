package com.e4rdx.snote.activities.drawing

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ScrollView
import androidx.core.view.isVisible
import com.e4rdx.snote.R
import com.e4rdx.snote.activities.basicNoteEditor.BasicNoteEditor
import com.e4rdx.snote.activities.checklistEditor.Tag
import com.e4rdx.snote.activities.notebookDisplayer.fragments.tags.FlowLayout
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Drawing : BasicNoteEditor() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tag_flowlayout = R.id.draw_tags_flowlayout

        if (showTags) {
            findViewById<View>(R.id.scrollview_draw_tags).visibility = View.VISIBLE
        }
    }

    override fun getLayoutID(): Int {
        return R.layout.activity_drawing
    }

    override fun onCreatedNote(b: Bundle?) {

    }

    override fun onEditNote(receivedJson: JSONObject?) {

    }

    override fun onLoadTags(tags: JSONArray?) {
        try {
            for (i in 0 until tags!!.length()) {
                var tag: Tag
                tag = Tag(this@Drawing, tags!!.getString(i))
                val fl = findViewById<View>(R.id.textEditor_tags_flowlayout) as FlowLayout
                fl.addView(tag)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onSaveAndExit() {

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_draw_save -> {
                onSaveAndExit()
                return true
            }
            R.id.menu_draw_addTag -> {
                val scrollview_tags = findViewById<ScrollView>(R.id.scrollview_draw_tags)
                if(scrollview_tags.isVisible){
                    scrollview_tags.visibility = View.GONE
                }
                else{
                    scrollview_tags.visibility = View.VISIBLE
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun addNewTag(v: View){
        addTag()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_drawing, menu)
        return true
    }
}