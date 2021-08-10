package com.e4rdx.snote.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import org.json.JSONArray
import java.io.*
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class ExternalNotebookManager {
    companion object{
        @JvmStatic
        fun getExternalNotebooks(context: Context): LinkedList<Uri>{
            val path = context.filesDir.toString() + "external.json"
            checkFile(path)
            val uris = LinkedList<Uri>()
            val json = JSONArray(readFile(path))
            println(json.toString())
            if (json.length() > 0){
                for (i in 0 until json.length()){
                    uris.add(Uri.parse(json.getString(i)))
                }
            }
            return uris
        }

        @JvmStatic
        fun loadExternalNotebook(context: Context, uri: Uri) {
            val noteFile = File(context.filesDir.toString() + "/actualFile/noteFile")
            val f = File(context.filesDir.toString() + "/actualFile/attachments/")
            if (f.exists()) {
                f.delete()
                val files = f.listFiles()
                if (files != null) {
                    for (i in files.indices) {
                        files[i].delete()
                    }
                }
                f.mkdir()
            } else {
                f.mkdir()
            }
            ExternalNotebookManager.unpackZip(context, uri, context.filesDir.toString() + "/actualFile/")
            //SNoteManager().unpackZip(filepath, fileName, context.filesDir.toString() + "/actualFile/")
            SNoteManager().unpackZip(context.filesDir.toString() + "/actualFile/", "attachments.zip", context.filesDir.toString() + "/actualFile/attachments/")
        }

        @JvmStatic
        fun addExternalNotebook(context: Context, uri: Uri){
            val path = context.filesDir.toString() + "external.json"
            checkFile(path)
            val json = JSONArray(readFile(path))
            json.put(uri.toString())
            updateExternal(path, json.toString())
        }

        @JvmStatic
        fun removeExternalNotebook(context: Context, uri: Uri){
            val path = context.filesDir.toString() + "external.json"
            checkFile(path)
            val json = JSONArray(readFile(path))
            val newJson = JSONArray()
            json.put(uri.toString())
            for (i in 0 until json.length()){
                if(!(json.getString(i) == uri.toString())){
                    newJson.put(json.getString(i))
                }
            }
            updateExternal(path, newJson.toString())
        }

        @JvmStatic
        fun hasAccessToUri(context: Context, uri: Uri) : Boolean{
            val cR = context.getContentResolver()

            try {
                val inputStream = cR.openInputStream(uri)
                if (inputStream != null) {
                    inputStream.close()
                    return true
                }
            }
            catch (e: java.lang.Exception) {
                //file not exists
            }
            return false
        }

        @JvmStatic
        fun checkFile(path: String){
            val f = File(path)
            if (!f.exists()){
                f.createNewFile()
                updateExternal(path, "[]")
            }
        }

        @JvmStatic
        fun updateExternal(path: String, newConfig: String?) {
            val configFile = File(path)
            try {
                val writer = FileWriter(configFile)
                writer.append(newConfig)
                writer.flush()
                writer.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        @JvmStatic
        fun readFile(filepath: String?): String {
            val fileEvents = File(filepath.toString())
            val text = StringBuilder()
            try {
                val br = BufferedReader(FileReader(fileEvents))
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    text.append(line)
                    text.append('\n')
                }
                br.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return text.toString()
        }

        @JvmStatic
        fun saveExternalNotebook(context: Context, jsonData: String, uri: Uri){
            val configFile = File(context.filesDir.toString() + "/actualFile/" + "noteFile")
            try {
                val writer = FileWriter(configFile)
                writer.append(jsonData)
                writer.flush()
                writer.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            SNoteManager().zipUpFile(context.filesDir.toString() + "/actualFile/attachments/", context.filesDir.toString() + "/actualFile/attachments.zip")
            this.zipUpFile(context.filesDir.toString() + "/actualFile/", uri, context)
        }

        @JvmStatic
        fun zipUpFile(inputFolderPath: String, uri: Uri, context: Context) {
            try {
                val fos = context.contentResolver.openOutputStream(uri);
                //val fos = FileOutputStream(outZipPath)
                val zos = ZipOutputStream(fos)
                val srcFile = File(inputFolderPath)
                val files = srcFile.listFiles()
                Log.d("", "Zip directory: " + srcFile.name)
                for (i in files.indices) {
                    if (!files[i].isDirectory) {
                        Log.d("", "Adding file: " + files[i].name)
                        val buffer = ByteArray(1024)
                        val fis = FileInputStream(files[i])
                        zos.putNextEntry(ZipEntry(files[i].name))
                        var length: Int
                        while (fis.read(buffer).also { length = it } > 0) {
                            zos.write(buffer, 0, length)
                        }
                        zos.closeEntry()
                        fis.close()
                    }
                }
                zos.close()
            } catch (ioe: IOException) {
                Log.e("", ioe.message)
            }
        }

        @JvmStatic
        fun unpackZip(context: Context, uri: Uri, unpackPath: String): Boolean {
            println(unpackPath)
            //println(path)
            //println(zipname)
            val inStream: InputStream?
            val zis: ZipInputStream
            try {
                var filename: String
                //inStream = FileInputStream(path + zipname)
                inStream = context.contentResolver.openInputStream(uri)
                zis = ZipInputStream(BufferedInputStream(inStream))
                //var ze: ZipEntry
                val buffer = ByteArray(1024)
                var count: Int
                while (true) {//zis.nextEntry.also { ze = it } != null
                    val ze = zis.nextEntry
                    if(ze == null){
                        break
                    }
                    filename = ze.name
                    println("Found entry: $filename")

                    // Need to create directories if not exists, or
                    // it will generate an Exception...
                    if (ze.isDirectory) {
                        println("Found dir!")
                        //File fmd = new File(path + filename);
                        val fmd = File(unpackPath + filename)
                        fmd.mkdirs()
                        continue
                    }

                    //FileOutputStream fout = new FileOutputStream(path + filename);
                    println("Unpacking to: $unpackPath$filename")
                    val fout = FileOutputStream(unpackPath + filename)
                    while (zis.read(buffer).also { count = it } != -1) {
                        fout.write(buffer, 0, count)
                    }
                    fout.close()
                    zis.closeEntry()
                    println("Done!")
                }
                zis.close()
            } catch (e: IOException) {
                println("Unzip Error")
                e.printStackTrace()
                return false
            }
            return true
        }
    }
}