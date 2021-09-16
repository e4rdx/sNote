package com.e4rdx.snote.activities.attachments;

import android.app.Activity;
import android.content.DialogInterface;
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
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.e4rdx.snote.R;
import com.e4rdx.snote.activities.basicNoteEditor.BasicNoteEditor;
import com.e4rdx.snote.activities.checklistEditor.Tag;
import com.e4rdx.snote.activities.notebookDisplayer.NotebookDisplayer;
import com.e4rdx.snote.activities.notebookDisplayer.fragments.tags.FlowLayout;
import com.e4rdx.snote.dialogs.SelectTagDialog;
import com.e4rdx.snote.utils.SNoteManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class AttachmentEditor extends BasicNoteEditor {
    private String path;
    private String type = "";
    private String name;
    private boolean edit;
    private static final int CREATE_FILE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //setContentView(R.layout.activity_attachment_editor);
        super.onCreate(savedInstanceState);

        tag_flowlayout = R.id.attachments_tags_flowlayout;
        ImageView image = findViewById(R.id.imageView_attachmentEditor);

        if(type.matches("image")){
            File imgFile = new File(path);
            if(imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                image.setImageBitmap(myBitmap);
            }
            else{
                Toast.makeText(getApplicationContext(), "File not found :(", Toast.LENGTH_SHORT).show();
            }
        }

        if(showTags){
            findViewById(R.id.scrollView_attachments_tags).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_attachment_editor;
    }

    @Override
    public void onEditNote(JSONObject receivedJson) {
        try {
            name = receivedJson.getString("name");
            type = receivedJson.getString("type");
            path = receivedJson.getString("src");
        } catch (JSONException e){
            e.printStackTrace();
        }
        edit = true;
    }

    @Override
    public void onCreatedNote(Bundle b) {
        name = b.getString("name");
        path = b.getString("file");
        type = b.getString("type");

        edit = false;
    }

    @Override
    public void onLoadTags(JSONArray tags) {
        try {
            for (int j = 0; j < tags.length(); j++) {
                FlowLayout fl = (FlowLayout) findViewById(R.id.attachments_tags_flowlayout);
                Tag t = new Tag(AttachmentEditor.this, tags.getString(j));
                fl.addView(t);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveAndExit() {
        //jsonData = new JSONObject();
        try {
            jsonData.put("tags", getTags());
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
            case R.id.menu_attachments_toggleTags:
                ScrollView tagEditor = findViewById(R.id.scrollView_attachments_tags);
                if (tagEditor.getVisibility() == View.VISIBLE) {
                    tagEditor.setVisibility(View.GONE);
                } else {
                    tagEditor.setVisibility(View.VISIBLE);
                }
                break;
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
                    Toast.makeText(getApplicationContext(), getString(R.string.attachments_export_success), Toast.LENGTH_LONG).show();
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

    public void back(View v){
        onSaveAndExit();
    }

    public void addNewTag(View v){
        addTag();
    }
}