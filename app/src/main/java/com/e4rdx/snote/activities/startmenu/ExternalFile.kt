package com.e4rdx.snote.activities.startmenu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.e4rdx.snote.activities.notebookDisplayer.NotebookDisplayer
import com.e4rdx.snote.utils.ConfigManager
import com.e4rdx.snote.utils.ExternalNotebookManager
import com.e4rdx.snote.utils.SNoteManager
import java.io.File

@SuppressLint("ViewConstructor")
class ExternalFile(context: Context, uri: Uri) : LinearLayout(context) {
    private var filepath: String = ""
    private var fileName: String = ""
    private var displayName: String = ""
    var myButton: Button? = null

    init {
        this.orientation = HORIZONTAL
        this.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        //this.filepath = pFilepath
        fileName = SNoteManager.getFileName(uri, context)

        println(fileName)
        //println(fileName.substring(fileName.length - 6))
        if (fileName.length > 6 && fileName.substring(fileName.length - 6) == ".snote") {
            displayName = fileName.substring(0, fileName.length - 6)
        } else {
            displayName = fileName
        }

        myButton = Button(context)
        myButton!!.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 3.0f)
        myButton!!.setBackgroundColor(0x000000)
        myButton!!.transformationMethod = null
        myButton!!.text = "Ext:" + displayName
        myButton!!.textSize = 20f
        myButton!!.gravity = Gravity.LEFT
        myButton!!.setOnClickListener(OnClickListener {
            ConfigManager(context).setFileOpen(filepath + fileName)
            ConfigManager(context).setExternalOpen(true)
            ConfigManager(context).setExternalUri(uri.toString())
            overwriteCurrentNotebook(context, uri)
            val i = Intent(context, NotebookDisplayer::class.java)
            context.startActivity(i)
        })

        this.addView(myButton)
    }

    private fun overwriteCurrentNotebook(context: Context, uri: Uri) {
        val noteFile = File(context.filesDir.toString() + "/actualFile/noteFile")
        val f = File(context.filesDir.toString() + "/actualFile/attachments/")
        if (f.exists()) {
            f.delete()
            val files = f.listFiles()
            if (files != null) {
                for (i in files.indices) {
                    files[i].delete()
                }
            }
            f.mkdir()
        } else {
            f.mkdir()
        }
        ExternalNotebookManager.unpackZip(context, uri, context.filesDir.toString() + "/actualFile/")
        //SNoteManager().unpackZip(filepath, fileName, context.filesDir.toString() + "/actualFile/")
        SNoteManager().unpackZip(context.filesDir.toString() + "/actualFile/", "attachments.zip", context.filesDir.toString() + "/actualFile/attachments/")
    }
}