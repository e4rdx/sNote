package com.e4rdx.snote.Activities.Attachments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.e4rdx.snote.Activities.Main_NotebookDisplay.NotebookDisplayer;
import com.e4rdx.snote.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class AttachmentEditor extends AppCompatActivity {
    private ImageView image;
    private String name;
    private String path;
    private String type;
    private boolean edit;
    private JSONObject jsonData;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment_editor);

        image = findViewById(R.id.imageView_attachmentEditor);

        Intent i  = getIntent();
        Bundle b = i.getExtras();

        edit = b.getBoolean("edit");

        if(!edit){
            name = b.getString("name");
            path = b.getString("file");
            type = b.getString("type");
        }
        else{
            try {
                jsonData = new JSONObject(b.getString("jsonData"));
                name = jsonData.getString("name");
                path = jsonData.getString("src");
                type = jsonData.getString("type");
                index = b.getInt("index");
                System.out.println(index);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(type.matches("image")){
            File imgFile = new File(path);
            if(imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                image.setImageBitmap(myBitmap);
                System.out.println("File is here");
            }
            else{
                System.out.println("File not found");
            }
        }
        else{
            System.out.println("Wrong type");
        }
    }

    public void back(View v){
        jsonData = new JSONObject();
        try {
            jsonData.put("name", name);
            jsonData.put("type", type);
            jsonData.put("src", path);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(edit) {
            Intent i = new Intent(getApplicationContext(), NotebookDisplayer.class);
            i.putExtra("jsonData", jsonData.toString());
            i.putExtra("edit", edit);
            i.putExtra("index", index);
            startActivity(i);
        }
        else{
            Intent i = new Intent(getApplicationContext(), NotebookDisplayer.class);
            i.putExtra("jsonData", jsonData.toString());
            i.putExtra("edit", edit);
            startActivity(i);
        }
    }
}