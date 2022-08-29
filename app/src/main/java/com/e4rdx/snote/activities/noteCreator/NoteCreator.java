package com.e4rdx.snote.activities.noteCreator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.e4rdx.snote.activities.attachments.AttachmentEditor;
import com.e4rdx.snote.activities.checklistEditor.ChecklistEditor;
import com.e4rdx.snote.activities.drawing.Drawing;
import com.e4rdx.snote.activities.texteditor.TextEditor;
import com.e4rdx.snote.activities.link.Link;
import com.e4rdx.snote.activities.task.Task_manager;
import com.e4rdx.snote.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class NoteCreator extends AppCompatActivity {
    private EditText editText_noteName;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private RadioButton rb_imageFromCam;
    private RadioButton rb_task;
    private RadioButton rb_link;
    private RadioButton rb_text;
    private RadioButton rb_checklist;
    private RadioButton rb_drawing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_creator);

        getSupportActionBar().setTitle(getString(R.string.new_note));

        editText_noteName = (EditText) findViewById(R.id.editTextNoteName);

        rb_imageFromCam = findViewById(R.id.newNote_image_from_cam);
        rb_checklist = findViewById(R.id.newNote_checkliste);
        rb_link = findViewById(R.id.newNote_link);
        rb_task = findViewById(R.id.newNote_task);
        rb_text = findViewById(R.id.newNote_text);
        rb_drawing = findViewById(R.id.newNote_drawing);
    }

    public void createNote(View v){
        Intent i;
        if(!editText_noteName.getText().toString().matches("")) {
            if (rb_imageFromCam.isChecked()) {
                dispatchTakePictureIntent();
            } else if (rb_text.isChecked()) {
                i = new Intent(getApplicationContext(), TextEditor.class);
                i.putExtra("name", editText_noteName.getText().toString());
                i.putExtra("edit", false);
                startActivity(i);
            } else if (rb_task.isChecked()) {
                /*i = new Intent(getApplicationContext(), Task_manager.class);
                i.putExtra("name", editText_noteName.getText().toString());
                i.putExtra("edit", false);
                startActivity(i);*/
                Toast.makeText(getApplicationContext(), "Under Construction", Toast.LENGTH_LONG).show();
            } else if (rb_link.isChecked()) {
                i = new Intent(getApplicationContext(), Link.class);
                i.putExtra("name", editText_noteName.getText().toString());
                i.putExtra("edit", false);
                startActivity(i);
            } else if (rb_checklist.isChecked()) {
                i = new Intent(getApplicationContext(), ChecklistEditor.class);
                i.putExtra("name", editText_noteName.getText().toString());
                i.putExtra("edit", false);
                startActivity(i);
            } else if (rb_drawing.isChecked()) {
                i = new Intent(getApplicationContext(), Drawing.class);
                i.putExtra("name", editText_noteName.getText().toString());
                i.putExtra("edit", false);
                startActivity(i);
            }
        }
        else{
            Toast.makeText(getApplicationContext(), getString(R.string.toast_enter_name), Toast.LENGTH_LONG).show();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.action_not_available), Toast.LENGTH_LONG).show();
            e.printStackTrace();
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
            i.putExtra("file", dest);
            i.putExtra("type", "image");
            startActivity(i);
        }
    }
}