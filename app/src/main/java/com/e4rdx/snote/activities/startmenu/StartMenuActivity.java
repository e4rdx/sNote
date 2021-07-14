package com.e4rdx.snote.activities.startmenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.e4rdx.snote.activities.createNotebook.NotebookCreator;
import com.e4rdx.snote.activities.notebookDisplayer.NotebookDisplayer;
import com.e4rdx.snote.dialogs.TextInputDialog;
import com.e4rdx.snote.utils.ConfigManager;
import com.e4rdx.snote.utils.ExternalNotebookManager;
import com.e4rdx.snote.utils.SNoteManager;
import com.e4rdx.snote.R;
import com.e4rdx.snote.dialogs.YesNoDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
    private static final int SELECT_NOTEBOOK = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_menu);

        new SNoteManager().checkAttachments(getApplicationContext());

        checkConfigFile();

        try {
            JSONObject jsonConfig = new JSONObject(readFile(getApplicationContext().getFilesDir() + "config.json"));
            if(jsonConfig.getBoolean("fileOpened")){
                if(new ConfigManager(getApplicationContext()).isExternalOpen()){
                    ExternalNotebookManager.loadExternalNotebook(getApplicationContext(),
                            Uri.parse(new ConfigManager(getApplicationContext()).getExternalUri()));
                }
                Intent i = new Intent(getApplicationContext(), NotebookDisplayer.class);
                startActivity(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Create Folder if it not already exists
        File f = new File(getApplicationContext().getFilesDir().getPath() + "/sNote");
        File dirActualFile = new File(getApplicationContext().getFilesDir().getPath() + "/actualFile");
        if(!f.isDirectory() || !f.exists()) {
            File folder = new File(getApplicationContext().getFilesDir().getPath() + "/sNote");
            folder.mkdir();
        }
        if(!dirActualFile.exists()){
            File folder = new File(getApplicationContext().getFilesDir().getPath() + "/actualFile");
            folder.mkdir();
        }

        loadFiles();

        shouldOpenShortcut(getIntent());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean displayExternal = sharedPreferences.getBoolean("preference_externalNotebooks", false);
        if(!displayExternal){
            findViewById(R.id.button_openExternalNotebook).setVisibility(View.GONE);
        }
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
        TextInputDialog popup = new TextInputDialog(StartMenuActivity.this, getString(R.string.menu_rename), getString(R.string.notebook_rename));
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == DialogInterface.BUTTON_POSITIVE){
                    currentContextItem.rename(popup.getText());
                    loadFiles();
                }
            }
        };
        popup.setupButtons(getString(R.string.menu_rename), getString(R.string.cancel), dialogClickListener);
        popup.show();
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
                openFileDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openExternal(View v){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        startActivityForResult(intent, SELECT_NOTEBOOK);
    }

    private void addExternalNotebook(Uri uri){

        ExternalNotebookManager.addExternalNotebook(getApplicationContext(), uri);
    }

    private void openFileDialog() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

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
        new YesNoDialog(StartMenuActivity.this, getString(R.string.notebook_are_you_sure),
                getString(R.string.notebook_deleting_not_undone), dialogClickListener);
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
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, filename);

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
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Write bytes to other file
        String fullFilename = new SNoteManager().getFileName(uri, getApplicationContext());
        File f = new File(getFilesDir() + "/sNote/" + fullFilename);
        if(!f.exists()){
            try {
                f.createNewFile();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_file_already_exists), Toast.LENGTH_LONG).show();
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
                    writeFileToURI(currentUri);
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_file_succesfully_exported), Toast.LENGTH_LONG).show();
                }
            }
            else if(requestCode == PICK_FILE){
                if (data != null) {
                    currentUri = data.getData();
                    importNoteFile(currentUri);
                    loadFiles();
                }
            }
            else if(requestCode == SELECT_NOTEBOOK){
                if (data != null) {
                    currentUri = data.getData();
                    //importNoteFile(currentUri);
                    final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    getContentResolver().takePersistableUriPermission(currentUri, takeFlags);
                    addExternalNotebook(currentUri);
                    loadFiles();
                }
            }
        }
    }

    private void writeFileToURI(Uri uri){
        Path path = Paths.get(currentContextItem.getFilepath());
        byte[] fileContents = null;
        try {
            fileContents =  Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{ ParcelFileDescriptor pfd = this.getContentResolver().openFileDescriptor(uri, "w");
            FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());
            fos.write(fileContents);
            fos.close(); pfd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        currentContextItem = (SNoteFile)v.getParent();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notefile_contextmenu, menu);
    }

    public void loadFiles(){
        LinearLayout fileList = findViewById(R.id.LinearLayoutFileList);
        fileList.removeAllViews();

        String path = getApplicationContext().getFilesDir()+"/sNote/";
        File directory = new File(path);
        if(directory.isDirectory()) {
            File[] files = directory.listFiles();
            if(files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    SNoteFile s = new SNoteFile(this, files[i].getName(), path);
                    registerForContextMenu(s.myButton);
                    fileList.addView(s);
                }
            }
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean displayExternal = sharedPreferences.getBoolean("preference_externalNotebooks", false);

        if(displayExternal) {
            LinkedList<Uri> externals = ExternalNotebookManager.getExternalNotebooks(getApplicationContext());
            if (externals.size() > 0) {
                for (int i = 0; i < externals.size(); i++) {
                    ExternalFile s = new ExternalFile(this, externals.get(i));
                    fileList.addView(s);
                }
            }
        }
    }

    public void newNotebook(View v){
        Intent i = new Intent(getApplicationContext(), NotebookCreator.class);
        startActivity(i);
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
                e.printStackTrace();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }
}