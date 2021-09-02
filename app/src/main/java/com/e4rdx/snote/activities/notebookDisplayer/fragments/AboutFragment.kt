package com.e4rdx.snote.activities.notebookDisplayer.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.e4rdx.snote.R
import com.e4rdx.snote.activities.notebookDisplayer.NotebookDisplayer


class AboutFragment : Fragment() {
    lateinit var myRoot: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val act = activity as NotebookDisplayer?
        act!!.fab.visibility = View.GONE

        // Inflate the layout for this fragment
        myRoot = inflater.inflate(R.layout.fragment_about, container, false)
        return myRoot
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linkGithub = myRoot.findViewById<TextView>(R.id.about_link_github)
        val linkHomepage = myRoot.findViewById<TextView>(R.id.about_link_homepage)

        linkGithub.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/e4rdx/sNote")))
        }
        linkHomepage.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://e4rdx.github.io/projects/snote.html")))
        }

        val wv = myRoot.findViewById<WebView>(R.id.about_webview) as WebView
        wv.loadUrl("file:///android_asset/about.html")
    }
}