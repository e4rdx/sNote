package com.e4rdx.snote.Activities.link

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.e4rdx.snote.R
import com.e4rdx.snote.ui.popups.TextInputPopup


class Link : AppCompatActivity() {
    var webview: WebView? = null
    var link: String? = null
    var textview_link: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link)

        link = "https://e4rdx.github.io"
        textview_link = findViewById(R.id.textView_link)

        webview = findViewById(R.id.webview_link)
        webview!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return false
            }
        }

        webview!!.loadUrl(link)

        textview_link!!.text = link
    }

    private fun saveAndExit(){

    }

    private fun changeLink(){
        println("OptionsItemSelected")
        val popup = TextInputPopup(this@Link, getString(R.string.edit_link), getString(R.string.edit_link_description))
        this.link?.let { popup.setText(it) }
        val dialogClickListener = DialogInterface.OnClickListener {dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    link = popup.getText()
                    webview!!.loadUrl(link)
                    textview_link!!.text = link
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