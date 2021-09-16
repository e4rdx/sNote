package com.e4rdx.snote.activities.texteditor;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

public class TextEditor extends BasicNoteEditor {
    private EditText textInputField;
    private boolean sttRunning;
    private Menu menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //setContentView(R.layout.activity_text_editor);
        super.onCreate(savedInstanceState);

        tag_flowlayout = R.id.textEditor_tags_flowlayout;
        sttRunning = false;

        if(showTags){
            findViewById(R.id.scrollView_texteditor_tags).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_text_editor;
    }

    @Override
    public void onEditNote(JSONObject receivedJson) {
        textInputField = (EditText)findViewById(R.id.texteditor_textfield);
        try {
            textInputField.setText(receivedJson.getString("text"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreatedNote(Bundle b) {

    }

    @Override
    public void onSaveAndExit() {
        String noteText = textInputField.getText().toString();
        try {
            jsonData.put("text", noteText);
            jsonData.put("tags", getTags());
            jsonData.put("type", "text");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent i = new Intent(getApplicationContext(), NotebookDisplayer.class);
        i.putExtra("jsonData", jsonData.toString());
        i.putExtra("edit", editMode);
        i.putExtra("index", index);
        startActivity(i);
    }

    @Override
    public void onLoadTags(JSONArray tags) {
        try {
            for(int i = 0; i < tags.length(); i++){
                Tag tag;
                tag = new Tag(TextEditor.this, tags.getString(i));
                FlowLayout fl = (FlowLayout) findViewById(R.id.textEditor_tags_flowlayout);
                fl.addView(tag);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_texteditor, menu);
        return true;
    }

    private void stt(){
        checkAudioPermission();

        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());

        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                //getting all the matches
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matches != null) {
                    textInputField.setText(textInputField.getText().insert(textInputField.getSelectionStart(), matches.get(0)));
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        if(sttRunning){
            mSpeechRecognizer.stopListening();
            Toast.makeText(getApplicationContext(), getString(R.string.toast_stt_stopped), Toast.LENGTH_SHORT).show();
            this.menu.getItem(0).setIcon(getApplicationContext().getDrawable(R.drawable.ic_micro));
        }
        else{
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            Toast.makeText(getApplicationContext(), getString(R.string.toast_stt_started), Toast.LENGTH_SHORT).show();
            this.menu.getItem(0).setIcon(getApplicationContext().getDrawable(R.drawable.ic_micro_red));
        }
        sttRunning = !sttRunning;
    }

    private void checkAudioPermission() {
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ScrollView textEditor = findViewById(R.id.scrollView_texteditor_tags);
        switch (item.getItemId()){
            case R.id.texteditor_save:
                onSaveAndExit();
                return true;
            case R.id.texteditor_stt:
                stt();
                return true;
            case  R.id.menu_texteditor_toggleTags:
                if (textEditor.getVisibility() == View.VISIBLE) {
                    textEditor.setVisibility(View.GONE);
                } else {
                    textEditor.setVisibility(View.VISIBLE);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addNewTag(View v){
        addTag();
    }
}