package com.e4rdx.snote.Activities.NoteCreation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.e4rdx.snote.Activities.Attachments.AttachmentEditor;
import com.e4rdx.snote.Activities.CheckList.ChecklistEditor;
import com.e4rdx.snote.Activities.TextNote.TextEditor;
import com.e4rdx.snote.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class NoteCreator extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner spinner1;
    private String noteType;
    private EditText editText_noteName;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_creator);

        spinner1 = (Spinner) findViewById(R.id.spinner2);
        spinner1.setOnItemSelectedListener(this);

        editText_noteName = (EditText) findViewById(R.id.editTextNoteName);

        noteType = "Text";
    }

    public void createNote(View v){
        Intent i;
        if(!editText_noteName.getText().toString().matches("")) {
            switch (noteType) {
                case "Text":
                    i = new Intent(getApplicationContext(), TextEditor.class);
                    i.putExtra("name", editText_noteName.getText().toString());
                    i.putExtra("edit", false);
                    startActivity(i);
                    break;
                case "Checkliste":
                    i = new Intent(getApplicationContext(), ChecklistEditor.class);
                    i.putExtra("name", editText_noteName.getText().toString());
                    i.putExtra("edit", false);
                    startActivity(i);
                    break;
                case "Untermenü":
                    Toast.makeText(getApplicationContext(), "Noch nicht verfügbar", Toast.LENGTH_LONG).show();
                    break;
                case "Aufgabe":
                    Toast.makeText(getApplicationContext(), "Noch nicht verfügbar", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Gib einen Namen an!", Toast.LENGTH_LONG).show();
        }
    }

    public void imageClick(View v){
        if(!editText_noteName.getText().toString().matches("")) {
            dispatchTakePictureIntent();
        }
        else{
            Toast.makeText(getApplicationContext(), "Gib einen Namen an!", Toast.LENGTH_LONG).show();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            String name = UUID.randomUUID().toString();
            String dest = getFilesDir() + "/actualFile/attachments/" + name +".png";
            System.out.println("Dest:"+dest);
            try {
                FileOutputStream out = new FileOutputStream(dest);
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent i = new Intent(getApplicationContext(), AttachmentEditor.class);
            i.putExtra("name", editText_noteName.getText().toString());
            i.putExtra("edit", false);
            System.out.println("Dest:"+dest);
            i.putExtra("file", dest);
            i.putExtra("type", "image");
            startActivity(i);
        }
    }



    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        System.out.println(parent.getItemAtPosition(pos));
        noteType = parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Nothing
    }
}