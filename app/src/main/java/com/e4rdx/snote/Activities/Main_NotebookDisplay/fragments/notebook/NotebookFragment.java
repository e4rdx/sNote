package com.e4rdx.snote.Activities.Main_NotebookDisplay.fragments.notebook;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.e4rdx.snote.Activities.Main_NotebookDisplay.Note;
import com.e4rdx.snote.Activities.Main_NotebookDisplay.NoteKT;
import com.e4rdx.snote.utils.SNoteManager;
import com.e4rdx.snote.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotebookFragment extends Fragment {
    private View myRoot;
    private JSONArray noteArray;
    private JSONObject jsonObj;
    private NoteKT currentContextItem;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myRoot = inflater.inflate(R.layout.fragment_home, container, false);
        return myRoot;
    }

    @Override
    public void onStop() {
        System.out.println("Stopping");
        LinearLayout noteList = myRoot.findViewById(R.id.LinearLayoutHomeNoteList);

        JSONArray notes = new JSONArray();
        for(int i = 0; i < noteList.getChildCount(); i++){
            NoteKT currentEntry = (NoteKT) noteList.getChildAt(i);
            try {
                notes.put(new JSONObject(currentEntry.getJsonData()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Debugging
        System.out.println(notes.toString());
        System.out.println("\n################################\n");
        System.out.println(noteArray.toString());

        try {
            //jsonObj.put("notes", noteArray);
            jsonObj.put("notes", notes);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SNoteManager().saveCurrent(jsonObj.toString(), getActivity().getApplicationContext());

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
                System.out.print("index: ");
                System.out.println(indexEditetString);
                try {
                    editetNote = new JSONObject(b.getString("jsonData"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                try {
                    additionalNote = new JSONObject(b.getString("jsonData"));
                    System.out.println(b.getString("jsonData"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("no new note");
                }
            }
        }

        try {
            jsonObj = new JSONObject(new SNoteManager().readFile(getActivity().getApplicationContext().getFilesDir() + "/actualFile/noteFile"));
            noteArray = jsonObj.getJSONArray("notes");
            if(additionalNote != null){
                noteArray.put(additionalNote);
                System.out.println(additionalNote.toString());
            }
            else if(editetNote != null){
                noteArray.put(indexEditetString, editetNote);
            }
            for(int i = 0; i < noteArray.length(); i++){
                System.out.println("Adding notes...");
                JSONObject noteObj = noteArray.getJSONObject(i);
                //Note noteButton = new Note(getActivity().getApplicationContext(), noteObj, i);
                NoteKT noteButton = new NoteKT(getActivity().getApplicationContext(), noteObj, i);
                noteList.addView(noteButton);
                registerForContextMenu(noteButton.getBtn_open());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Fail adding noteList");
        }


        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.notecontext_remove:
                System.out.println("Removing note...");
                currentContextItem.remove();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        //super.onCreateContextMenu(menu, v, menuInfo);
        currentContextItem = (NoteKT) v.getParent().getParent().getParent();
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.note_contextmenu, menu);
    }
}