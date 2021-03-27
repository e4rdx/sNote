package com.e4rdx.snote.Activities.Main_NotebookDisplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.e4rdx.snote.Activities.Startmenu.StartMenuActivity;
import com.e4rdx.snote.utils.ConfigManager;
import com.e4rdx.snote.Activities.NoteCreation.NoteCreator;
import com.e4rdx.snote.R;
import com.e4rdx.snote.utils.SNoteManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private JSONArray noteArray;
    private JSONObject jsonObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout noteList = findViewById(R.id.LinearLayoutNoteList);

        JSONObject additionalNote = null;
        Bundle b = getIntent().getExtras();
        int indexEditetString = 0;
        JSONObject editetNote = null;
        if(b != null){
            if(b.getBoolean("edit")){
                indexEditetString = b.getInt("index");
                try {
                    editetNote = new JSONObject(b.getString("jsonData"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                try {
                    additionalNote = new JSONObject(b.getString("jsonData"));
                    System.out.println(b.getString("jsonData"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("no new note");
                }
            }
        }

        try {
            jsonObj = new JSONObject(readFile("/actualFile/noteFile"));
            noteArray = jsonObj.getJSONArray("notes");
            if(additionalNote != null){
                noteArray.put(additionalNote);
                System.out.println(additionalNote.toString());
            }
            else if(editetNote != null){
                noteArray.put(indexEditetString, editetNote);
            }
            for(int i = 0; i < noteArray.length(); i++){
                System.out.println("Adding notes...");
                JSONObject noteObj = noteArray.getJSONObject(i);
                Note noteButton = new Note(this, noteObj, i);
                noteList.addView(noteButton);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Fail adding noteList");
        }

        //boolean s = new SNoteManager().unpackZip(Environment.getExternalStorageDirectory().getPath() + "/sNote/", "afds.zip");
    }

    public void closeNotebook(View v){
        System.out.println("Closing...");
        new ConfigManager(getApplicationContext()).closeFile();
        Intent i = new Intent(getApplicationContext(), NotebookDisplayer.class);
        startActivity(i);
    }

    public void clearNotebook(View v){
        noteArray = new JSONArray();
        Intent i = new Intent(getApplicationContext(), StartMenuActivity.class);
        startActivity(i);
    }

    @Override
    protected void onStop() {
        System.out.println("Stopping");
        //saveNotebook();
        super.onStop();
    }

    private void saveNotebook(){
        try {
            jsonObj.put("notes", noteArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        File f = new File(getApplicationContext().getFilesDir() + "/actualFile/" + "noteFile");
        try {
            FileWriter writer = new FileWriter(f);
            writer.append(jsonObj.toString());
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new SNoteManager().zipUpFile(getApplicationContext().getFilesDir() + "/actualFile/", new ConfigManager(getApplicationContext()).getCurrentFilePath());
    }

    public void newNote(View view){
        Intent i = new Intent(getApplicationContext(), NoteCreator.class);
        startActivity(i);
    }

    public String readFile(String fileName){
        File fileEvents = new File(getApplicationContext().getFilesDir()+fileName);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileEvents));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("File error");
        }
        String result = text.toString();
        return result;
    }
}