package com.e4rdx.snote.activities.checklistEditor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.e4rdx.snote.activities.notebookDisplayer.NotebookDisplayer;
import com.e4rdx.snote.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChecklistEditor extends AppCompatActivity {
    private EditText noteInput;
    private String noteName;
    private JSONObject jsonData;
    private JSONArray entrys;
    private boolean editMode;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist_editor);

        noteInput = (EditText)findViewById(R.id.editTextNote);
        Bundle extras = getIntent().getExtras();

        entrys = new JSONArray();

        editMode = extras.getBoolean("edit");

        if(editMode){
            JSONObject recievedJson = null;
            try {
                recievedJson = new JSONObject(extras.getString("jsonData"));
                noteName = recievedJson.getString("name");
                entrys = recievedJson.getJSONArray("entrys");
                for(int i = 0; i < entrys.length(); i++){
                    LinearLayout parent = (LinearLayout)findViewById(R.id.noteList);
                    JSONObject actualEntry = entrys.getJSONObject(i);
                    parent.addView(new ChecklistEntry(this, actualEntry.getString("text"), actualEntry.getBoolean("state"), parent));
                }
                index = extras.getInt("index");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            noteName = extras.getString("name");
        }
        getSupportActionBar().setTitle(noteName);

        jsonData = new JSONObject();
        try {
            jsonData.put("name", noteName);
            jsonData.put("type", "checkliste");
            jsonData.put("entrys", new JSONArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        saveNote();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_checklisteditor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_checklisteditor_save){
            saveNote();
        }
        return super.onOptionsItemSelected(item);
    }

    public void addNote(View v){
        if(!noteInput.getText().toString().matches("")) {
            LinearLayout parent = (LinearLayout) findViewById(R.id.noteList);
            ChecklistEntry note = new ChecklistEntry(this, noteInput.getText().toString(), false, parent);
            parent.addView(note);

            noteInput.setText("");
        }
        else{
            Toast.makeText(getApplicationContext(), getString(R.string.checklist_enter_text), Toast.LENGTH_LONG).show();
        }
    }

    private void saveNote(){
        entrys = new JSONArray();
        LinearLayout parent = (LinearLayout)findViewById(R.id.noteList);
        for(int i = 0; i < parent.getChildCount(); i++){
            ChecklistEntry current = (ChecklistEntry) parent.getChildAt(i);
            entrys.put(current.getJsonData());
        }
        try {
            jsonData.put("entrys", entrys);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent i = new Intent(getApplicationContext(), NotebookDisplayer.class);
        i.putExtra("jsonData", jsonData.toString());
        i.putExtra("edit", editMode);
        i.putExtra("index", index);
        startActivity(i);
    }
}