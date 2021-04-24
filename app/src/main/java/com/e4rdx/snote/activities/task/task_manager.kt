package com.e4rdx.snote.activities.task

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.e4rdx.snote.R


class Task_manager : AppCompatActivity() {
    private var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_manager)

        val extras = intent.extras

        val editMode = extras!!.getBoolean("edit")
    }

    fun saveAndClose(){

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_tasks, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.task_save -> {
                saveAndClose()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}