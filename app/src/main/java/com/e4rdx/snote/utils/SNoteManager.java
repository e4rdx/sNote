package com.e4rdx.snote.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class SNoteManager {

    public SNoteManager(){

    }

    public static JSONObject readNoteFile(Context context){
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(new SNoteManager().readFile(context.getFilesDir() + "/actualFile/noteFile"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    public static boolean notesChanged(Context context, String notes){
        String savedNotes = readNoteFile(context).toString();
        return !savedNotes.equals(notes);
    }

    public static JSONArray getAllTags(Context context){
        JSONObject jsonObj = readNoteFile(context);
        JSONArray tags = new JSONArray();
        try {
            tags = jsonObj.getJSONArray("tags");
        }
        catch (JSONException e){
            System.out.println("no tags found");
        }
        return tags;
    }

    public void checkAttachments(Context context){
        File f = new File(context.getFilesDir() + "/actualFile/attachments/");
        if(!f.isDirectory()){
            f.mkdir();
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

    public void saveCurrent(String jsonData, Context context){
        File f = new File(context.getFilesDir() + "/actualFile/" + "noteFile");
        try {
            FileWriter writer = new FileWriter(f);
            writer.append(jsonData);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.zipUpFile(context.getFilesDir() + "/actualFile/attachments/", context.getFilesDir() + "/actualFile/attachments.zip");
        //new File(context.getFilesDir() + "actualFile/attachments/").delete();
        this.zipUpFile(context.getFilesDir() + "/actualFile/", new ConfigManager(context).getCurrentFilePath());
        //this.zipFileAtPath(context.getFilesDir() + "/actualFile/", new ConfigManager(context).getCurrentFilePath());
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

    public boolean unpackZip(String path, String zipname, String unpackPath)
    {
        System.out.println(unpackPath);
        System.out.println(path);
        System.out.println(zipname);
        InputStream is;
        ZipInputStream zis;
        try {
            String filename;
            is = new FileInputStream(path + zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null) {
                filename = ze.getName();
                System.out.println("Found entry: "+filename);

                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (ze.isDirectory()) {
                    System.out.println("Found dir!");
                    //File fmd = new File(path + filename);
                    File fmd = new File(unpackPath + filename);
                    fmd.mkdirs();
                    continue;
                }

                //FileOutputStream fout = new FileOutputStream(path + filename);
                System.out.println("Unpacking to: "+unpackPath+filename);
                FileOutputStream fout = new FileOutputStream(unpackPath + filename);

                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }
                fout.close();
                zis.closeEntry();
                System.out.println("Done!");
            }
            zis.close();
        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("Unzip Error");
            return false;
        }

        return true;
    }

    public static String getFileName(Uri uri, Context context) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
