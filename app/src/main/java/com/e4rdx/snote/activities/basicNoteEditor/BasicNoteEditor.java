package com.e4rdx.snote.activities.basicNoteEditor;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.e4rdx.snote.R;
import com.e4rdx.snote.activities.checklistEditor.Tag;
import com.e4rdx.snote.activities.notebookDisplayer.fragments.tags.FlowLayout;
import com.e4rdx.snote.dialogs.SelectTagDialog;
import com.e4rdx.snote.utils.SNoteManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public abstract class BasicNoteEditor extends AppCompatActivity {
    //attributes
    protected int tag_flowlayout;
    protected boolean editMode;
    protected String noteName;
    protected int index;
    protected JSONObject jsonData;
    protected boolean showTags;

    //abstract methods
    public abstract void onEditNote(JSONObject receivedJson);
    public abstract void onCreatedNote(Bundle b);
    public abstract void onSaveAndExit();
    public abstract void onLoadTags(JSONArray tags);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getLayoutID() != 0){
            setContentView(getLayoutID());
        }

        Bundle extras = getIntent().getExtras();

        editMode = extras.getBoolean("edit");
        boolean isDropdownOpen = false;

        if(editMode){
            JSONObject receivedJson = new JSONObject();
            try {
                receivedJson = new JSONObject(extras.getString("jsonData"));
                noteName = receivedJson.getString("name");
                index = extras.getInt("index");
                onLoadTags(receivedJson.getJSONArray("tags"));
                isDropdownOpen = receivedJson.getBoolean("dropdownOpen");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            onEditNote(receivedJson);
        }
        else{
            noteName = extras.getString("name");
            onCreatedNote(extras);
        }
        getSupportActionBar().setTitle(noteName);

        jsonData = new JSONObject();
        try {
            jsonData.put("name", noteName);
            jsonData.put("dropdownOpen", isDropdownOpen);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        showTags = sharedPreferences.getBoolean("preference_showTagsDefault", false);
    }

    protected int getLayoutID(){
        return 0;
    }

    @Override
    public void onBackPressed() {
        onSaveAndExit();
    }

    public void addTag(){
        JSONArray jsonTags = SNoteManager.getAllTags(getApplicationContext());
        String[] tags = new String[jsonTags.length()];
        for(int i = 0; i < jsonTags.length(); i++){
            try {
                tags[i] = jsonTags.getString(i);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        SelectTagDialog dialog = new SelectTagDialog(BasicNoteEditor.this, getString(R.string.AddTag), tags);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int state) {
                if(state == DialogInterface.BUTTON_POSITIVE){
                    LinkedList<String> choices = dialog.getChoices();
                    for(int i = 0; i < choices.size(); i++){
                        Tag tag = new Tag(BasicNoteEditor.this, choices.get(i));
                        FlowLayout fl = (FlowLayout) findViewById(tag_flowlayout);
                        fl.addView(tag);
                    }
                }
            }
        };
        dialog.setupButtons(getString(R.string.add), getString(R.string.cancel), dialogClickListener);
        dialog.create().show();
    }

    protected JSONArray getTags(){
        JSONArray tags = new JSONArray();
        FlowLayout fl = (FlowLayout) findViewById(tag_flowlayout);
        if(fl.getChildCount() > 1) {
            for (int i = 1; i < fl.getChildCount(); i++) {
                try {
                    Tag current = (Tag) fl.getChildAt(i);
                    tags.put(current.getName());
                } catch (ClassCastException e){
                    e.printStackTrace();
                }
            }
        }
        return tags;
    }
}
