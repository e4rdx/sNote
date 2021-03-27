package com.e4rdx.snote.Activities.Startmenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.e4rdx.snote.Activities.FileCreation.FileCreation;
import com.e4rdx.snote.Activities.Main_NotebookDisplay.NotebookDisplayer;
import com.e4rdx.snote.utils.SNoteManager;
import com.e4rdx.snote.R;
import com.e4rdx.snote.ui.popups.YesNoPopup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;


public class StartMenuActivity extends AppCompatActivity {
    private SNoteFile currentContextItem;
    private static final int CREATE_FILE = 1;
    private static final int PICK_FILE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_menu);

        new SNoteManager().checkAttachments(getApplicationContext());

        checkConfigFile();

        try {
            JSONObject jsonConfig = new JSONObject(readFile(getApplicationContext().getFilesDir() + "config.json"));
            if(jsonConfig.getBoolean("fileOpened")){
                Intent i = new Intent(getApplicationContext(), NotebookDisplayer.class);
                startActivity(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Create Folder if it not already exists
        File f = new File(getApplicationContext().getFilesDir().getPath() + "/sNote");
        if(!f.isDirectory()) {
            File folder = new File(getApplicationContext().getFilesDir().getPath() + "/sNote");
            System.out.println(folder.mkdir());
            System.out.println("Created Folder");
        }
        else{
            System.out.println("Folder already exists");
        }

        loadFiles();

        shouldOpenShortcut(getIntent());
    }

    private void shouldOpenShortcut(Intent i){
        if(i != null) {
            Bundle b = i.getExtras();
            if(b != null) {
                String name = b.getString("notebook");

                LinearLayout fileList = findViewById(R.id.LinearLayoutFileList);
                for(int j = 0; j < fileList.getChildCount(); j++){
                    SNoteFile notebook = (SNoteFile) fileList.getChildAt(j);
                    if(notebook.getDisplayName().equals(name)) {
                        notebook.myButton.callOnClick();
                    }
                }
            }
        }
    }

    private void renameDialog(){
        final String[] m_Text = {""};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Umbenennen");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Umbenennen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentContextItem.rename(input.getText().toString());
                loadFiles();
            }
        });
        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_startmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.import_file:
                System.out.println("Import File...");
                openFileDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openFileDialog() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, PICK_FILE);
    }

    private void removeFileConversation(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        currentContextItem.deleteNotebook();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        new YesNoPopup(StartMenuActivity.this, "Datei Löschen",
                "Soll die Datei wirklich gelöscht werden?", dialogClickListener);
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.file_rename:
                renameDialog();
                break;
            case R.id.file_delete:
                removeFileConversation();
                break;
            case R.id.file_export:
                exportFile(currentContextItem.getFilepath(), currentContextItem.getFilename());
                break;
            case R.id.file_shortcut:
                createShortcut();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void createShortcut(){
        String label = currentContextItem.getDisplayName();
        ShortcutManager shortcutManager = getApplicationContext().getSystemService(ShortcutManager.class);

        List<ShortcutInfo> shortcuts = shortcutManager.getPinnedShortcuts();
        boolean shortcutExists = false;
        if(shortcuts != null) {
            for (int i = 0; i < shortcuts.size(); i++) {
                System.out.println(shortcuts.get(i).getId());
                if (shortcuts.get(i).getId().equals(label)) {
                    Toast.makeText(getApplicationContext(), "Link existiert bereits!", Toast.LENGTH_LONG).show();
                    shortcutExists = true;
                }
            }
        }

        if (shortcutManager.isRequestPinShortcutSupported() && !shortcutExists) {
            Intent shortcutIntent = new Intent(getApplicationContext(), StartMenuActivity.class);
            shortcutIntent.setAction(Intent.ACTION_VIEW);
            shortcutIntent.putExtra("shortcut", true);
            shortcutIntent.putExtra("notebook", label);

            ShortcutInfo pinShortcutInfo = new ShortcutInfo.Builder(getApplicationContext(), label)
                    .setShortLabel(label)
                    .setLongLabel("Link zu "+label)
                    .setIcon(Icon.createWithResource(getApplicationContext(), R.mipmap.ic_shortcut))
                    .setIntent(shortcutIntent)
                    .build();

            Intent pinnedShortcutCallbackIntent = new Intent();

            PendingIntent successCallback = PendingIntent.getBroadcast(getApplicationContext(), 0, pinnedShortcutCallbackIntent, 0);
            shortcutManager.requestPinShortcut(pinShortcutInfo, successCallback.getIntentSender());
        }
    }

    private void exportFile(String filepath, String filename){
        System.out.println(filepath);
        
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, filename);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, CREATE_FILE);
    }

    private void importNoteFile(Uri uri){
        //Read File from uri
        InputStream inputStream = null;
        byte[] filecontents = null;
        try {
            inputStream = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }

            filecontents = byteBuffer.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Write bytes to other file
        String fullFilename = new SNoteManager().getFileName(uri, getApplicationContext());
        File f = new File(getFilesDir() + "/sNote/" + fullFilename);
        System.out.println(f);
        if(!f.exists()){
            try {
                f.createNewFile();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Datei existiert bereits!", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(filecontents);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri currentUri = null;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CREATE_FILE) {
                if (data != null) {
                    currentUri = data.getData();
                    System.out.println(currentUri);
                    writeFileToURI(currentUri);
                    Toast.makeText(getApplicationContext(), "Datei erfolgreich exportiert!", Toast.LENGTH_LONG).show();
                }
            }
            else if(requestCode == PICK_FILE){
                if (data != null) {
                    currentUri = data.getData();
                    System.out.println(currentUri);
                    importNoteFile(currentUri);
                }
            }
        }
    }

    private void writeFileToURI(Uri uri){
        Path path = Paths.get(currentContextItem.getFilepath());
        byte[] fileContents = null;
        try {
            fileContents =  Files.readAllBytes(path);
            System.out.println("Got bytes!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{ ParcelFileDescriptor pfd = this.getContentResolver().openFileDescriptor(uri, "w");
            FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());
            //String txt = "Some sample text";
            //fos.write(txt.getBytes());
            fos.write(fileContents);
            fos.close(); pfd.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        System.out.println("ContextMenu!");
        super.onCreateContextMenu(menu, v, menuInfo);
        currentContextItem = (SNoteFile)v.getParent();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notefile_contextmenu, menu);
    }

    public void loadFiles(){
        System.out.println("Loading files...");

        LinearLayout fileList = findViewById(R.id.LinearLayoutFileList);
        fileList.removeAllViews();

        String path = getApplicationContext().getFilesDir()+"/sNote/";
        File directory = new File(path);
        if(directory.isDirectory()) {
            System.out.println("Getting files...");
            File[] files = directory.listFiles();
            if(files != null && files.length > 0) {
                System.out.println("List files...");
                for (int i = 0; i < files.length; i++) {
                    //Log.d("Files", "FileName:" + files[i].getName());
                    SNoteFile s = new SNoteFile(this, files[i].getName(), path);
                    registerForContextMenu(s.myButton);
                    fileList.addView(s);
                }
            }
        }
        System.out.println("Done");
    }

    public void newNotebook(View v){
        Intent i = new Intent(getApplicationContext(), FileCreation.class);
        startActivity(i);
    }

    private void updateConfig(String newConfig){
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

    private void checkConfigFile(){
        File configFile = new File(getApplicationContext().getFilesDir() + "config.json");
        if(!configFile.exists()){
            try {
                FileWriter writer = new FileWriter(configFile);
                writer.append("{\"fileOpened\"=false}");
                writer.flush();
                writer.close();

            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
    
    private String readFile(String filepath){
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
}