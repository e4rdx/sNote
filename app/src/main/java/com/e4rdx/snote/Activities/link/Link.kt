package com.e4rdx.snote.Activities.link

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.e4rdx.snote.Activities.Main_NotebookDisplay.NotebookDisplayer
import com.e4rdx.snote.R
import com.e4rdx.snote.ui.popups.TextInputPopup
import org.json.JSONException
import org.json.JSONObject


class Link : AppCompatActivity() {
    private var webview: WebView? = null
    private var link: String = ""
    private var textviewLink: TextView? = null
    private var edit = false
    private var index = 0
    private var name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link)

        val extras = intent.extras

        edit = extras!!.getBoolean("edit")
        if(edit){
            index = extras.getInt("index")
            val jsonString = extras.getString("jsonData").toString()
            val jsonData = JSONObject(jsonString)
            name = jsonData.getString("name").toString()
            link = jsonData.getString("link").toString()
        }
        else{
            link = getString(R.string.enter_link)
            name = extras.getString("name").toString()
        }

        supportActionBar!!.title = name

        textviewLink = findViewById(R.id.textView_link)

        webview = findViewById(R.id.webview_link)
        webview!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return false
            }
        }

        if(edit) {
            webview!!.loadUrl(link)
        }
        else{
            val unencodedHtml = getString(R.string.enter_link)
            val encodedHtml = Base64.encodeToString(unencodedHtml.toByteArray(), Base64.NO_PADDING)
            webview!!.loadData(encodedHtml, "text/html", "base64")
        }

        textviewLink!!.text = link
    }

    private fun saveAndExit(){
        val jsonData = JSONObject()
        try {
            jsonData.put("link", link)
            jsonData.put("type", "link")
            jsonData.put("name", name)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val i = Intent(applicationContext, NotebookDisplayer::class.java)
        i.putExtra("jsonData", jsonData.toString())
        i.putExtra("edit", edit)
        i.putExtra("index", index)
        startActivity(i)
    }

    private fun changeLink(){
        val popup = TextInputPopup(this@Link, getString(R.string.edit_link), getString(R.string.edit_link_description))
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
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
    }

    override fun onBackPressed() {
        saveAndExit()
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
                saveAndExit()
                return true
            }
            R.id.menu_link_open -> {
                openInBrowser()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}