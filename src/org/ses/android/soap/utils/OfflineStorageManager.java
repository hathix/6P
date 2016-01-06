package org.ses.android.soap.utils;

/**
 * Created by Jtomaylla on 19/08/2015.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ses.android.seispapp120.R;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.ses.android.soap.models.Promoter;
import org.ses.android.soap.models.Saveable;


/**
 * Created by jfang on 1/7/15.
 */
public class OfflineStorageManager {

    private Context context;


    public OfflineStorageManager(Context c) {
        context = c;
    }

    /**
     * Reads the filename from local storage and returns the string
     *
     * @param fileName Name of the file to read from
     * @return String of the file contents that are read
     * @author JN
     */
    public String getStringFromLocal(String fileName) {
        try {
            // Opens file for reading
            FileInputStream fis = context.openFileInput(fileName);
            DataInputStream dis = new DataInputStream(fis);

            // Create buffer
            int length = dis.available();
            byte[] buf = new byte[length];

            // Read full data into buffer
            dis.readFully(buf);
            String fileContent = new String(buf);
            dis.close();
            fis.close();

            // Convert to string
            return fileContent;


        } catch (FileNotFoundException e) {
            Log.e("OfflineStorageManager: FileNotFoundException", "Cannot find file");
            e.printStackTrace();

        } catch (IOException e) {
            Log.e("OfflineStorageManager: IOException", "File error in finding patient files");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

     public <T extends Saveable> void SaveArrayListToLocal(ArrayList<T> a, String filename) {
         //JT
         File file = new File(context.getFilesDir(), filename);
         String mydir = context.getFilesDir().toString();
         //
        boolean file_deleting_result = context.deleteFile(filename);
        if (!file_deleting_result) {
            Log.e("OfflineStorageManager: SaveArrayListToLocal", "Delete file failed");
        }
        int num_objects = a.size();
        JSONArray ja = new JSONArray();
        try {
            // Queries web service for patients with the ids associated with this promoter
            for (int i = 0; i < num_objects; i++) {
                T o = a.get(i);
                JSONObject obj = new JSONObject(o.toString());
                ja.put(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Saves patients data of this promoter to a file named under patients_filename
        String data_to_write = ja.toString();

        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(data_to_write.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e("OfflineStorageManager: SaveArrayListToLocal", "Cannot write to file");
            e.printStackTrace();
        }

        // Testing only: read from file to see that data is not appended
        String s = getStringFromLocal(filename);
        Log.i("OfflineStorageManager: SaveArrayListToLocal", s);
    }


    public boolean SaveSaveableToLocal(Saveable o, String filename) {
        // Save to local file for the object passed in
        boolean file_deleting_result = context.deleteFile(filename);
        if (!file_deleting_result) {
            Log.e("OfflineStorageManager: SaveSaveableToLocal", "Delete file failed");
        }
        String data_to_write = o.toString();
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(data_to_write.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e("OfflineStorageManager: SaveSaveableToLocal", "Cannot write to file");
            e.printStackTrace();
            return false;
        }


        // Testing only: read from file to see that data is not appended
        String s = getStringFromLocal(filename);
        Log.i("OfflineStorageManager: SaveSaveableToLocal", s);


        return true;
    }



    /**
     * Sets in Shared Preferences the time of the update aka when this function is called
     * @author JN
     */
    public void SetLastLocalUpdateTime() {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = mPreferences.edit();
        Date currentTime = new Date();
        long milli_currentTime = currentTime.getTime();
        editor.putString(context.getString(R.string.date), String.valueOf(milli_currentTime));
        Log.i("OfflineStorageManager:SetLastLocalUpdateTime", String.valueOf(milli_currentTime));
        editor.commit();
    }

    /**
     * Checks SharedPreferences and returns the last update time
     * @return a primitive long of the miliseconds since last update
     * @author JN
     */
    public long GetLastLocalUpdateTime(){
        SharedPreferences sprefs = PreferenceManager.getDefaultSharedPreferences(context);
        String last_update = sprefs.getString((context.getString(R.string.date)), null);
        long time_updated;
        try{
            time_updated = Long.valueOf(last_update);
        }
        catch (NumberFormatException e){
            time_updated = Long.valueOf("0");
            Log.i("offline storage catch", e.toString());

        }
        return time_updated;
    }


    /**
     * Loads when MainMenuActivity is created and checks if can update local storage
     * @author JN
     * @return true if has internet and already logged in
     */
    public boolean CanUpdateLocalStorage() {
        boolean isConnected = InternetConnection.checkConnection(context);
        String logged_in = AccountLogin.CheckAlreadyLoggedIn(context);
        if (!isConnected || (logged_in==null) ){
            return false;
        }
        return true;
    }



}
