@file:Suppress("PackageName")

package com.e4rdx.snote.Activities.Main_NotebookDisplay.fragments.tags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.e4rdx.snote.R

class TagFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tags, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        println("Hello from Tags")

        super.onViewCreated(view, savedInstanceState)
    }
}