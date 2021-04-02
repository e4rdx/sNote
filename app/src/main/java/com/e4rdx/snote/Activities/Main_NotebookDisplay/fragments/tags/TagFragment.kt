@file:Suppress("PackageName")

package com.e4rdx.snote.Activities.Main_NotebookDisplay.fragments.tags

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.e4rdx.snote.Activities.Main_NotebookDisplay.NotebookDisplayer
import com.e4rdx.snote.R

class TagFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        val act = activity as NotebookDisplayer?
        act!!.fab.visibility = View.GONE
        return inflater.inflate(R.layout.fragment_tags, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_tag, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}