@file:Suppress("PackageName")

package com.e4rdx.snote.activities.notebookDisplayer.fragments.tags

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.e4rdx.snote.activities.notebookDisplayer.Note
import com.e4rdx.snote.activities.notebookDisplayer.NotebookDisplayer
import com.e4rdx.snote.R
import com.e4rdx.snote.utils.SNoteManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class TagFragment : Fragment() {
    var jsonObj: JSONObject = JSONObject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        val act = activity as NotebookDisplayer?
        act!!.fab.visibility = View.GONE
        return inflater.inflate(R.layout.fragment_tags, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        jsonObj = SNoteManager.readNoteFile(requireContext())
        loadTags()
    }

    data class SearchResult(val jsonData: JSONObject, val index: Int)

    override fun onDestroy() {
        //saveTags()
        super.onDestroy()
    }

    override fun onStop() {
        //saveTags()
        super.onStop()
    }

    fun saveTags(){
        //val jsonObj = SNoteManager.readNoteFile(requireContext())
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

    fun listNotes(){
        val ll = requireActivity().findViewById<LinearLayout>(R.id.tags_linearLayout_foundNotes)
        ll.removeAllViews()

        val tags = getSelectedTags()

        val results = searchNotes(tags)
        if(results.isNotEmpty()){
            for(result in results){
                ll.addView(Note(requireContext(), result.jsonData,result.index))
            }
        }
    }

    private fun getSelectedTags(): LinkedList<String>{
        val found = LinkedList<String>()

        val linearLayoutTags = requireActivity().findViewById<FlowLayout>(R.id.tags_flowlayout)
        if(linearLayoutTags.childCount > 0) {
            for (i in 0..linearLayoutTags.childCount) {
                if( linearLayoutTags.getChildAt(i) != null) {
                    val currentTag = linearLayoutTags.getChildAt(i) as Tag
                    if(currentTag.selectedByUser) {
                        found.add(currentTag.name)
                    }
                }
            }
        }

        return found
    }
    
    private fun searchNotes(tags: LinkedList<String>): LinkedList<SearchResult>{
        val jsonNotes = jsonObj.getJSONArray("notes")
        //val results = JSONArray()
        val results = LinkedList<SearchResult>()
        //Loop trough notes
        if(jsonNotes.length() > 0){
            for (i in 0 until jsonNotes.length()){
                //get tags of note
                var noteTags = JSONArray()
                try {
                    noteTags = jsonNotes.getJSONObject(i).getJSONArray("tags")
                } catch (e: JSONException){
                    println("Missing tags in json data")
                }
                if(tags.isNotEmpty() && noteTags.length() > 0){
                    var missingTag = false
                    for(tag in tags){
                        var foundTag = false
                        for(j in 0 until noteTags.length()){
                           if(tag == noteTags.getString(j)){
                               foundTag = true
                               break
                           }
                        }
                        if(!foundTag){
                            missingTag = true
                            break
                        }
                    }
                    if(!missingTag){
                        //results.put(jsonNotes.getJSONObject(i))
                        results.add(SearchResult(jsonNotes.getJSONObject(i), i))
                    }
                }
            }
        }
        return results
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