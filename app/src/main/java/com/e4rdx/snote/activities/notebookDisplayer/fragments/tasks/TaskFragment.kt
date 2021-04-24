@file:Suppress("PackageName")

package com.e4rdx.snote.activities.notebookDisplayer.fragments.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.e4rdx.snote.R

class TaskFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        println("Hello from Tasks")

        super.onViewCreated(view, savedInstanceState)
    }
}