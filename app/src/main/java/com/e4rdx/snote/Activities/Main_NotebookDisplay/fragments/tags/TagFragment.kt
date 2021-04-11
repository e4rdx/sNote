@file:Suppress("PackageName")

package com.e4rdx.snote.Activities.Main_NotebookDisplay.fragments.tags

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.e4rdx.snote.Activities.Main_NotebookDisplay.NotebookDisplayer
import com.e4rdx.snote.FlowLayout
import com.e4rdx.snote.R
import com.e4rdx.snote.utils.SNoteManager
import org.json.JSONArray
import org.json.JSONException

class TagFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        val act = activity as NotebookDisplayer?
        act!!.fab.visibility = View.GONE
        return inflater.inflate(R.layout.fragment_tags, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadTags()
    }

    override fun onDestroy() {
        //saveTags()
        super.onDestroy()
    }

    override fun onStop() {
        //saveTags()
        super.onStop()
    }

    fun saveTags(){
        val jsonObj = SNoteManager.readNoteFile(requireContext())
        val linearLayoutTags = requireActivity().findViewById<FlowLayout>(R.id.tags_flowlayout)
        val tags = JSONArray()
        if(linearLayoutTags.childCount > 0) {
            for (i in 0..linearLayoutTags.childCount) {
                if( linearLayoutTags.getChildAt(i) != null) {
                    val currentTag = linearLayoutTags.getChildAt(i) as Tag
                    tags.put(currentTag.name)
                }
            }
        }
        jsonObj.put("tags", tags)
        SNoteManager().saveCurrent(jsonObj.toString(), requireContext())
    }

    private fun loadTags(){
        val linearLayoutTags = requireActivity().findViewById<FlowLayout>(R.id.tags_flowlayout)

        val jsonObj = SNoteManager.readNoteFile(requireContext())
        try {
            val jsonTags: JSONArray? = jsonObj.getJSONArray("tags")
            for (i in 0..jsonTags!!.length()) {
                linearLayoutTags.addView(Tag(requireContext(), jsonTags.getString(i), this))
            }
        } catch (e: JSONException){

        }
    }

    private fun newTagDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("New Tag")
        builder.setMessage("Enter the name for the new tag")

        // Set up the input
        val input = EditText(requireContext())
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("Create") { dialog, which ->
                val flowlayoutTags = requireActivity().findViewById<FlowLayout>(R.id.tags_flowlayout)
                flowlayoutTags.addView(Tag(requireContext(), input.text.toString(), this))
                saveTags()
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_tag, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_tag_addTag -> {
                newTagDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}