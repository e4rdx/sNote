package com.e4rdx.snote.Activities.Main_NotebookDisplay.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.e4rdx.snote.R

class PreferencesFragment : PreferenceFragmentCompat(){
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
        }
}