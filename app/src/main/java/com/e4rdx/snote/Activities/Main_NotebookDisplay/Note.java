package com.e4rdx.snote.Activities.Main_NotebookDisplay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.e4rdx.snote.Activities.Attachments.AttachmentEditor;
import com.e4rdx.snote.Activities.CheckList.ChecklistEditor;
import com.e4rdx.snote.Activities.TextNote.TextEditor;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("AppCompatCustomView")
public class Note extends Button {
    private Intent i;
    private int index;

    public Note(Context context, JSONObject jsonData, int pIndex){
        super(context);

        this.setTransformationMethod(null);

        index = pIndex;

        String noteType = null;
        String noteName = null;
        try {
            noteType = jsonData.getString("type");
            noteName = jsonData.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.setText(noteName);

        if(noteType.matches("image")){
            noteType = "image";
        }

        switch (noteType){
            case "checkliste":
                i = new Intent(context, ChecklistEditor.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("jsonData", jsonData.toString());
                i.putExtra("edit", true);
                i.putExtra("index", index);
                break;
            case "text":
                i = new Intent(context, TextEditor.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("jsonData", jsonData.toString());
                i.putExtra("edit", true);
                i.putExtra("index", index);
                break;
            case "image":
                i = new Intent(context, AttachmentEditor.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("jsonData", jsonData.toString());
                i.putExtra("edit", true);
                i.putExtra("index", index);
                System.out.print("Index image:");
                System.out.println(index);
                break;
        }

        this.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                context.startActivity(i);
            }
        });
    }
}
