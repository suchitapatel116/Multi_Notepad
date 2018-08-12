package com.example.spatel116.multinotepad1;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by user on 13-02-2018.
 */

public class JsonAsyncTask extends AsyncTask<String, Void, String> {
    //<parameter, progress, result>
    //Any no of input arguments can be passed
    //parameter is the input that is given to the async task
    //If I want to give the data back to main what data should I give then void. If you don't want to report anything then write void.
    //When I am done what resultant product should I give back ie. list filles with notes from json file

    private static final String TAG = "JsonAsyncTask";
    MainActivity mainActivity;

    public JsonAsyncTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    //This function is called when main Activity starts the Async task
    //Long... is the array of arguments that is passed and can be accessed by longs
    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: ");

        String filename = strings[0];
        String jsonString = "";

        //Load the data from the file into string
        try {
            int n;
            FileInputStream fis = mainActivity.getApplicationContext().openFileInput(filename);
            StringBuffer fileContent = new StringBuffer("");

            byte[] buffer = new byte[1024];

            while ((n = fis.read(buffer)) != -1) {
                fileContent.append(new String(buffer, 0, n));
            }
            jsonString = fileContent.toString();
            Log.d(TAG, "doInBackground: json string /n "+ jsonString);
        }
        catch (Exception e)
        {
            Log.d(TAG, "doInBackground: File Exception");
            e.printStackTrace();
        }

        return jsonString; //Goes to onPostExecute
    }

    //Runs in the same thread, only doInBackground runs in separate thread
    @Override
    protected void onPostExecute(String jsonString) {
        super.onPostExecute(jsonString);
        Log.d(TAG, "onPostExecute: ");

        //Parse the json string here
        Note note;
        ArrayList<Note> ret_list = new ArrayList<>();

        Log.d(TAG, "loadFromFile: ");
        try
        {
            Log.d(TAG, "onPostExecute: json string = "+jsonString);

            InputStream stream = new ByteArrayInputStream(jsonString.getBytes(mainActivity.getString(R.string.encoding)));
            JsonReader jReader = new JsonReader(new InputStreamReader(stream, mainActivity.getString(R.string.encoding)));
            Log.d(TAG, "onPostExecute: yupuieeee");

            jReader.beginArray();
            while (jReader.hasNext()) {
                note = new Note();

                jReader.beginObject();
                while (jReader.hasNext()) {
                    //Get the name of next tag in json file
                    String tag_name = jReader.nextName();
                    if (tag_name.equals("title"))
                        note.setTitle(jReader.nextString());
                    else if (tag_name.equals("description"))
                        note.setDescription(jReader.nextString());
                    else if (tag_name.equals("date_time"))
                        note.setDateTime(jReader.nextString());
                    else
                        //If some other tag found then skip it
                        jReader.skipValue();
                }
                jReader.endObject();

                ret_list.add(note);
                Log.d(TAG, "onPostExecute: s= "+ret_list.size());
            }
            jReader.endArray();
        }
        catch (FileNotFoundException e)
        {
            Log.d(TAG, "loadFromFile: File not found exception");
        }
        catch(Exception e)
        {
            Log.d(TAG, "loadFromFile: Exception");
            e.printStackTrace();
        }

        //Sort the list here
        mainActivity.getDataFromAsync(ret_list);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute: ");
    }
}
