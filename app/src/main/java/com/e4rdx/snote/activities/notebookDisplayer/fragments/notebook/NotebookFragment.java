package com.e4rdx.snote.activities.notebookDisplayer.fragments.notebook;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.e4rdx.snote.activities.notebookDisplayer.Note;
import com.e4rdx.snote.activities.notebookDisplayer.NotebookDisplayer;
import com.e4rdx.snote.utils.ConfigManager;
import com.e4rdx.snote.utils.ExternalNotebookManager;
import com.e4rdx.snote.utils.SNoteManager;
import com.e4rdx.snote.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotebookFragment extends Fragment {
    private View myRoot;
    private JSONObject jsonObj;
    private Note currentContextItem;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NotebookDisplayer act = (NotebookDisplayer) getActivity();
        if(act.fab != null) {
            act.fab.setVisibility(View.VISIBLE);
        }
        myRoot = inflater.inflate(R.layout.fragment_home, container, false);
        return myRoot;
    }

    @Override
    public void onStop() {
        LinearLayout noteList = myRoot.findViewById(R.id.LinearLayoutHomeNoteList);

        JSONArray notes = new JSONArray();
        if(noteList.getChildCount() > 0) {
            for (int i = 0; i < noteList.getChildCount(); i++) {
                Note currentEntry = (Note) noteList.getChildAt(i);
                try {
                    notes.put(new JSONObject(currentEntry.getJsonData()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            jsonObj.put("notes", notes);

            final ScrollView scrollview = ((ScrollView) myRoot.findViewById(R.id.scrollview_notelist));
            jsonObj.put("scrolled", scrollview.getScrollY());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            Toast.makeText(getActivity().getApplicationContext(), "Error while saving :(", Toast.LENGTH_LONG).show();
            jsonObj = new JSONObject();
        }

        if(SNoteManager.notesChanged(getActivity().getApplicationContext(), jsonObj.toString())) {
            if (new ConfigManager(getActivity().getApplicationContext()).isExternalOpen()) {
                ExternalNotebookManager.saveExternalNotebook(getActivity().getApplicationContext(), jsonObj.toString(),
                        Uri.parse(new ConfigManager(getActivity().getApplicationContext()).getExternalUri()));
            } else {
                new SNoteManager().saveCurrent(jsonObj.toString(), getActivity().getApplicationContext());
            }
        }

        super.onStop();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LinearLayout noteList = myRoot.findViewById(R.id.LinearLayoutHomeNoteList);

        JSONObject additionalNote = null;
        Bundle b = getActivity().getIntent().getExtras();
        int indexEditetString = 0;
        JSONObject editetNote = null;
        if(b != null){
            if(b.getBoolean("edit")){
                indexEditetString = b.getInt("index");
                try {
                    editetNote = new JSONObject(b.getString("jsonData"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                try {
                    additionalNote = new JSONObject(b.getString("jsonData"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            jsonObj = new JSONObject(new SNoteManager().readFile(getActivity().getApplicationContext().getFilesDir() + "/actualFile/noteFile"));
            JSONArray noteArray = jsonObj.getJSONArray("notes");
            if(additionalNote != null){
                noteArray.put(additionalNote);
            }
            else if(editetNote != null){
                noteArray.put(indexEditetString, editetNote);
            }
            for(int i = 0; i < noteArray.length(); i++){
                JSONObject noteObj = noteArray.getJSONObject(i);
                Note noteButton = new Note(getActivity().getApplicationContext(), noteObj, i);
                noteList.addView(noteButton);
                registerForContextMenu(noteButton.getBtn_open());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            try {
                final ScrollView scrollview = ((ScrollView) myRoot.findViewById(R.id.scrollview_notelist));
                scrollview.smoothScrollBy(0, jsonObj.getInt("scrolled"));
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }, 100);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.notecontext_remove:
                currentContextItem.remove(getActivity());
                break;
            case R.id.notecontext_rename:
                currentContextItem.rename(getActivity());
                break;
            case R.id.notecontext_duplicate:
                LinearLayout noteList = myRoot.findViewById(R.id.LinearLayoutHomeNoteList);
                Note noteButton = null;
                try {
                    noteButton = new Note(getActivity().getApplicationContext(),
                            new JSONObject(currentContextItem.getJsonData()), noteList.getChildCount());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                noteList.addView(noteButton);
                registerForContextMenu(noteButton.getBtn_open());

                //Scroll after short delay to bottom
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollToBottom();
                    }
                }, 100);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void scrollToBottom(){
        //Scroll to bottom
        final ScrollView scrollview = ((ScrollView) myRoot.findViewById(R.id.scrollview_notelist));
        View lastChild = scrollview.getChildAt(scrollview.getChildCount() - 1);
        int bottom = lastChild.getBottom() + scrollview.getPaddingBottom();
        int sy = scrollview.getScrollY();
        int sh = scrollview.getHeight();
        int delta = bottom - (sy + sh);
        scrollview.smoothScrollBy(0, delta);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        currentContextItem = (Note) v.getParent().getParent().getParent();
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.note_contextmenu, menu);
    }
}