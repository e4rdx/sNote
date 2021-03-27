package com.e4rdx.snote.utils;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    private Context context;
    private final String configFilePath;

    public ConfigManager(Context pContext){
        context = pContext;
        configFilePath = context.getFilesDir() + "config.json";
    }

    public void setFileOpen(String filepath){
        String jsonString = readFile(configFilePath);
        try {
            JSONObject jsonConfig = new JSONObject(jsonString);
            jsonConfig.put("fileOpened", true);
            jsonConfig.put("filepath", filepath);
            updateConfig(jsonConfig.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentFilePath(){
        String jsonString = readFile(configFilePath);
        try {
            JSONObject jsonConfig = new JSONObject(jsonString);
            return jsonConfig.getString("filepath");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean isFileOpen(){
        String jsonString = readFile(configFilePath);
        try {
            JSONObject jsonConfig = new JSONObject(jsonString);
            return jsonConfig.getBoolean("fileOpened");
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void closeFile(){
        String jsonString = readFile(configFilePath);
        try {
            JSONObject jsonConfig = new JSONObject(jsonString);
            jsonConfig.put("fileOpened", false);
            updateConfig(jsonConfig.toString());
        } catch (JSONException e) {
            e.printStackTrace();
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

    public void updateConfig(String newConfig){
        File configFile = new File(context.getFilesDir() + "config.json");
        try {
            FileWriter writer = new FileWriter(configFile);
            writer.append(newConfig);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void checkConfigFile(){
        File configFile = new File(context.getFilesDir() + "config.json");
        JSONObject jsonConfig = new JSONObject();
        try {
            jsonConfig.put("fileOpened", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(!configFile.exists()){
            try {
                FileWriter writer = new FileWriter(configFile);
                writer.append(jsonConfig.toString());
                writer.flush();
                writer.close();

            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
