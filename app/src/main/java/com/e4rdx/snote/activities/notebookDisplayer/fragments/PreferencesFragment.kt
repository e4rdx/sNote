package com.e4rdx.snote.activities.notebookDisplayer.fragments

import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import com.e4rdx.snote.R
import com.e4rdx.snote.activities.notebookDisplayer.NotebookDisplayer

class PreferencesFragment : PreferenceFragmentCompat(){
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
            val act = activity as NotebookDisplayer?
            act!!.fab.visibility = View.GONE
        }
}