package com.e4rdx.snote.Activities.TextNote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.e4rdx.snote.Activities.Main_NotebookDisplay.NotebookDisplayer;
import com.e4rdx.snote.R;

import org.json.JSONException;
import org.json.JSONObject;

public class TextEditor extends AppCompatActivity {
    private EditText textInputField;
    private String noteName;
    private JSONObject jsonData;
    private String noteText;
    private boolean editMode;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_editor);

        textInputField = (EditText)findViewById(R.id.editTextTextMultiLine);
        Bundle extras = getIntent().getExtras();

        noteText = "";

        editMode = extras.getBoolean("edit");

        if(editMode){
            JSONObject recievedJson = null;
            try {
                recievedJson = new JSONObject(extras.getString("jsonData"));
                noteName = recievedJson.getString("name");
                noteText = recievedJson.getString("text");
                textInputField.setText(noteText);
                /*for(int i = 0; i < noteText.length(); i++){
                    LinearLayout parent = (LinearLayout)findViewById(R.id.noteList);
                    JSONObject actualEntry = noteText.getJSONObject(i);
                    parent.addView(new ChecklistEntry(this, actualEntry.getString("text"), actualEntry.getBoolean("state"), parent));
                }*/
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
            jsonData.put("type", "text");
            jsonData.put("text", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void saveNote(View v){
        /*entrys = new JSONArray();
        LinearLayout parent = (LinearLayout)findViewById(R.id.noteList);
        for(int i = 0; i < parent.getChildCount(); i++){
            ChecklistEntry current = (ChecklistEntry) parent.getChildAt(i);
            entrys.put(current.getJsonData());
        }*/
        noteText = textInputField.getText().toString();
        try {
            jsonData.put("text", noteText);
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