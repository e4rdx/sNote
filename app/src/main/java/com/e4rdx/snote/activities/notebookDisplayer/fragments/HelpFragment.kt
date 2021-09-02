package com.e4rdx.snote.activities.notebookDisplayer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.e4rdx.snote.R
import com.e4rdx.snote.activities.notebookDisplayer.NotebookDisplayer

class HelpFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val act = activity as NotebookDisplayer?
        act!!.fab.visibility = View.GONE

        return inflater.inflate(R.layout.fragment_help, container, false)
    }
}