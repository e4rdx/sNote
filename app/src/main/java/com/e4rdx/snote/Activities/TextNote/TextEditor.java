package com.e4rdx.snote.Activities.TextNote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.e4rdx.snote.Activities.Main_NotebookDisplay.NotebookDisplayer;
import com.e4rdx.snote.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class TextEditor extends AppCompatActivity {
    private EditText textInputField;
    private String noteName;
    private JSONObject jsonData;
    private String noteText;
    private boolean editMode;
    private int index;
    private boolean sttRunning;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_editor);

        textInputField = (EditText)findViewById(R.id.texteditor_textfield);
        Bundle extras = getIntent().getExtras();

        noteText = "";

        editMode = extras.getBoolean("edit");

        if(editMode){
            JSONObject recievedJson = null;
            try {
                recievedJson = new JSONObject(extras.getString("jsonData"));
                noteName = recievedJson.getString("name");
                noteText = recievedJson.getString("text");
                textInputField.setText(noteText);
                index = extras.getInt("index");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            noteName = extras.getString("name");
        }
        getSupportActionBar().setTitle(noteName);

        jsonData = new JSONObject();
        try {
            jsonData.put("name", noteName);
            jsonData.put("type", "text");
            jsonData.put("text", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //configSTT();
        sttRunning = false;
    }

    @Override
    public void onBackPressed() {
        saveNote();
    }

    public void saveNote(){
        noteText = textInputField.getText().toString();
        try {
            jsonData.put("text", noteText);
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
        switch (item.getItemId()){
            case R.id.texteditor_save:
                saveNote();
                return true;
            case R.id.texteditor_stt:
                stt();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}