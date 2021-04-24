package com.e4rdx.snote.activities.startmenu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.e4rdx.snote.activities.notebookDisplayer.NotebookDisplayer;
import com.e4rdx.snote.utils.ConfigManager;
import com.e4rdx.snote.utils.SNoteManager;

import java.io.File;

@SuppressLint("AppCompatCustomView")
public class SNoteFile extends LinearLayout {
    private Context context;
    private String filepath;
    private String fileName;
    private String displayName;
    public Button myButton;
    private ImageButton deleteButton;

    public SNoteFile(Context pContext, String pFileName, String pFilepath) {
        super(pContext);

        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        context = pContext;
        filepath = pFilepath;
        fileName = pFileName;

        System.out.println(fileName.substring(fileName.length() - 6));
        if(fileName.length() > 6 && fileName.substring(fileName.length() - 6).matches(".snote")) {
            displayName = fileName.substring(0, fileName.length() - 6);
        }
        else{
            displayName = fileName;
        }

        myButton = new Button(context);
        myButton.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 3.0f));
        myButton.setBackgroundColor(0x000000);
        myButton.setTransformationMethod(null);
        myButton.setText(displayName);
        myButton.setTextSize(20);
        myButton.setGravity(Gravity.LEFT);
        myButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new ConfigManager(context).setFileOpen(filepath+fileName);

                overwriteCurrentNotebook();

                Intent i = new Intent(context, NotebookDisplayer.class);
                context.startActivity(i);
            }
        });

        this.addView(myButton);
    }

    public String getDisplayName(){
        return this.displayName;
    }

    public String getFilename(){
        return this.fileName;
    }

    public String getFilepath(){
        return filepath+fileName;
    }

    public void rename(String newName){
        File f = new File(filepath+fileName);
        f.renameTo(new File(filepath + newName + ".snote"));
    }

    public void deleteNotebook(){
        System.out.println("Remove...");
        System.out.println(filepath+fileName);
        File f = new File(filepath+fileName);
        if(f.exists() && f.isFile()){
            f.delete();
            LinearLayout parent = (LinearLayout)this.getParent();
            parent.removeView(this);
            System.out.println("Done");
        }
    }

    private void overwriteCurrentNotebook(){
        File noteFile = new File(context.getFilesDir() + "/actualFile/noteFile");
        if(noteFile.exists() && noteFile.isFile()) {
            //configFile.delete();
            System.out.println("Deleting notefile for overwrite...");
        }

        File f = new File(context.getFilesDir() + "/actualFile/attachments/");
        if(f.exists()){
            f.delete();
            System.out.println("Removing and creating new attachment dir");
            File[] files = f.listFiles();
            if(files != null) {
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
            }
            f.mkdir();
        }
        else{
            f.mkdir();
        }

        new SNoteManager().unpackZip(filepath, fileName, context.getFilesDir() + "/actualFile/");
        new SNoteManager().unpackZip(context.getFilesDir() + "/actualFile/", "attachments.zip", context.getFilesDir() + "/actualFile/attachments/");
    }
}
