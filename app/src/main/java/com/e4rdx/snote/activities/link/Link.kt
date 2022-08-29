package com.e4rdx.snote.activities.link

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.e4rdx.snote.R
import com.e4rdx.snote.activities.basicNoteEditor.BasicNoteEditor
import com.e4rdx.snote.activities.checklistEditor.Tag
import com.e4rdx.snote.activities.notebookDisplayer.NotebookDisplayer
import com.e4rdx.snote.activities.notebookDisplayer.fragments.tags.FlowLayout
import com.e4rdx.snote.dialogs.TextInputDialog
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class Link : BasicNoteEditor() {
    private var webview: WebView? = null
    private var link: String = ""
    private var textviewLink: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tag_flowlayout = R.id.link_tags_flowlayout

        textviewLink = findViewById(R.id.textView_link)

        webview = findViewById(R.id.webview_link)
        webview!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return false
            }
        }

        if(editMode) {
            webview!!.loadUrl(link)
        }
        else{
            val unencodedHtml = getString(R.string.enter_link)
            val encodedHtml = Base64.encodeToString(unencodedHtml.toByteArray(), Base64.NO_PADDING)
            webview!!.loadData(encodedHtml, "text/html", "base64")
        }

        textviewLink!!.text = link

        if (showTags) {
            findViewById<View>(R.id.scrollView_link_tags).visibility = View.VISIBLE
        }
    }

    override fun getLayoutID(): Int {
        return R.layout.activity_link
    }

    override fun onEditNote(receivedJson: JSONObject?) {
        link = receivedJson!!.getString("link").toString()

    }

    override fun onCreatedNote(b: Bundle?) {
        link = getString(R.string.enter_link)
        noteName = b!!.getString("name").toString()
    }

    override fun onLoadTags(tags: JSONArray?) {
        try {
            for (i in 0 until tags!!.length()) {
                val fl = findViewById<View>(R.id.link_tags_flowlayout) as FlowLayout
                val t = Tag(this@Link, tags.getString(i))
                fl.addView(t)
            }
        }
        catch (e: JSONException){
            e.printStackTrace()
        }
    }

    override fun onSaveAndExit() {
        //val jsonData = JSONObject()
        try {
            jsonData.put("tags", tags)
            jsonData.put("link", link)
            jsonData.put("type", "link")
            jsonData.put("name", noteName)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val i = Intent(applicationContext, NotebookDisplayer::class.java)
        i.putExtra("jsonData", jsonData.toString())
        i.putExtra("edit", editMode)
        i.putExtra("index", index)
        startActivity(i)
    }

    fun addNewTag(v: View){
        addTag()
    }

    private fun changeLink(){
        val popup = TextInputDialog(this@Link, getString(R.string.edit_link), getString(R.string.edit_link_description))
        this.link.let { popup.setText(it) }
        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    link = popup.getText()
                    webview!!.loadUrl(link)
                    textviewLink!!.text = link
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                }
            }
        }
        popup.setupButtons(getString(R.string.submit), getString(R.string.cancel), dialogClickListener)
        popup.show()
    }

    private fun openInBrowser(){
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        if (browserIntent.resolveActivity(packageManager) != null) {
            startActivity(browserIntent);
        }
        else{
            Toast.makeText(applicationContext, getText(R.string.action_not_available), Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_link, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_link_edit -> {
                changeLink()
                return true
            }
            R.id.menu_link_save -> {
                onSaveAndExit()
                return true
            }
            R.id.menu_link_open -> {
                openInBrowser()
                return true
            }
            R.id.menu_link_toggleTags -> {
                val tagEditor = findViewById<ScrollView>(R.id.scrollView_link_tags)
                if (tagEditor.getVisibility() == View.VISIBLE) {
                    tagEditor.setVisibility(View.GONE)
                } else {
                    tagEditor.setVisibility(View.VISIBLE)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}