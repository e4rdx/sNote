package com.e4rdx.snote.Activities.Attachments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.e4rdx.snote.Activities.Main_NotebookDisplay.NotebookDisplayer;
import com.e4rdx.snote.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AttachmentEditor extends AppCompatActivity {
    private ImageView image;
    private String name;
    private String path;
    private String type;
    private boolean edit;
    private JSONObject jsonData;
    private int index;
    private static final int CREATE_FILE = 1;

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

        getSupportActionBar().setTitle(name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_attachment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.attachment_saveas:
                exportFile(name);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CREATE_FILE) {
                if (data != null) {
                    Uri targetFile = data.getData();
                    writeFileToURI(targetFile);
                    Toast.makeText(getApplicationContext(), "Datei erfolgreich exportiert!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void writeFileToURI(Uri uri){
        Path path = Paths.get(new File(this.path).getAbsolutePath());
        byte[] fileContents = null;
        try {
            fileContents =  Files.readAllBytes(path);
            System.out.println("Got bytes!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{ ParcelFileDescriptor pfd = this.getContentResolver().openFileDescriptor(uri, "w");
            FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());
            fos.write(fileContents);
            fos.close(); pfd.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportFile(String filename){
        String extension = this.path.substring(this.path.lastIndexOf("."));

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, filename + extension);

        startActivityForResult(intent, CREATE_FILE);
    }

    @Override
    protected void onStop() {
        super.onStop();
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