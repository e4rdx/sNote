package com.e4rdx.snote.activities.createNotebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.e4rdx.snote.activities.notebookDisplayer.NotebookDisplayer;
import com.e4rdx.snote.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class NotebookCreator extends AppCompatActivity {
    private LinearLayout encryptionSetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_creation);

        encryptionSetup = findViewById(R.id.LinearLayoutEncryptionSetup);

        getSupportActionBar().setTitle(getString(R.string.new_notebook));
    }

    public void encryptionSwitch(View v){
        Switch encryptionToggleSwitch = findViewById(R.id.switchEncrypt);
        if(encryptionToggleSwitch.isChecked()){
            encryptionSetup.setVisibility(View.VISIBLE);
        }
        else{
            encryptionSetup.setVisibility(View.GONE);
        }
    }

    public void newNotebook(View v){
        //Remove attachments
        File file = new File(getApplicationContext().getFilesDir() + "/actualFile/attachments/");
        if(file.exists()){
            file.delete();
            File[] files = file.listFiles();
            if(files != null) {
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
            }
            file.mkdir();
        }
        else{
            file.mkdir();
        }

        //Create Note.kt Files
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("version", "1.0.0");
            jsonObj.put("notes", new JSONArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonString = jsonObj.toString();

        File folder = new File(getApplicationContext().getFilesDir(), "/actualFile");
        if(!folder.isDirectory()){
            System.out.println(folder.mkdir());
        }
        File f = new File(getApplicationContext().getFilesDir() + "/actualFile/" + "noteFile");
        try {
            FileWriter writer = new FileWriter(f);
            writer.append(jsonString);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            System.out.println(e);
            Toast.makeText(getApplicationContext(), "Fehler", Toast.LENGTH_LONG).show();
        }

        EditText editTextNoteName = findViewById(R.id.editTextNameOfFile);
        if(!editTextNoteName.getText().toString().matches("")) {
            String filename = editTextNoteName.getText().toString() + ".snote";
            zipUpFile(getApplicationContext().getFilesDir() + "/actualFile", getApplicationContext().getFilesDir().getPath() + "/sNote/" + filename);

            try {
                System.out.println(readFile(getApplicationContext().getFilesDir() + "config.json"));
                JSONObject configData = new JSONObject(readFile(getApplicationContext().getFilesDir() + "config.json"));
                configData.put("fileOpened", true);
                configData.put("filepath", getApplicationContext().getFilesDir().getPath() + "/sNote/" + filename);
                updateConfig(configData.toString());

                Intent i = new Intent(getApplicationContext(), NotebookDisplayer.class);
                startActivity(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Gib einen Dateinamen an!", Toast.LENGTH_LONG).show();
        }
    }

    public String readFile(String filepath){
        File fileEvents = new File(filepath);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileEvents));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) { }
        String result = text.toString();
        return result;
    }

    public void zipUpFile(String inputFolderPath, String outZipPath){
        try {
            FileOutputStream fos = new FileOutputStream(outZipPath);
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);
            File[] files = srcFile.listFiles();
            Log.d("", "Zip directory: " + srcFile.getName());
            for (int i = 0; i < files.length; i++) {
                if(!files[i].isDirectory()) {
                    Log.d("", "Adding file: " + files[i].getName());
                    byte[] buffer = new byte[1024];
                    FileInputStream fis = new FileInputStream(files[i]);
                    zos.putNextEntry(new ZipEntry(files[i].getName()));
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                    fis.close();
                }
            }
            zos.close();
        } catch (IOException ioe) {
            Log.e("", ioe.getMessage());
        }
    }


    public void updateConfig(String newConfig){
        File configFile = new File(getApplicationContext().getFilesDir() + "config.json");
        try {
            FileWriter writer = new FileWriter(configFile);
            writer.append(newConfig);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}