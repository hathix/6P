package org.ses.android.soap.utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.ses.android.soap.models.Cacheable;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.ses.android.soap.preferences.PreferencesActivity;
import android.util.Base64;
import android.util.Log;

/**
 * Created by franciscorivera on 1/12/16.
 */
public class PreferencesManager
{

    /*
     * takes a context and a fingerprint template byte array, formats it as a string
     * and saves it to share preferences
     */
    public static void setFingerprint(Context context, byte[] fingerprint)
    {
        // ensure that fingerprint template is valid
        if (fingerprint == null) {
            return;
        }

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String savedFingerprint = Base64.encodeToString(fingerprint, Base64.DEFAULT);
        Log.i("fingerprint", savedFingerprint);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("Fingerprint", savedFingerprint);
        editor.commit();
    }

    /*
     * if there is a fingerprint stored, this gets rid of it
     */
    public static void removeFingerprint(Context context) {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mPreferences.edit();
        if (mPreferences.getString("Fingerprint", "").length() > 0)
        {
            editor.remove("Fingerprint");
            editor.commit();
        }
    }

    /*
     * if there is currently a fingerprint stored in shared preferences, then retrieves
     * it as a byte array. Otherwise, returns null.
     */
    public static byte[] getFingerprint(Context context) {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String fingerprintString = mPreferences.getString("Fingerprint", "");
        byte[] fingerprint;

        if (fingerprintString.equals("")) {
            return null;
        }

        try{
            fingerprint = Base64.decode(fingerprintString, Base64.DEFAULT);
        }
        catch (Exception e)
        {
            return null;
        }

        return fingerprint;

    }

    // Take an arraylist of Cacheable things and save to preferences.
    public static void saveCacheableList(Context context, String saveName,
                                         ArrayList<Cacheable> objectList) {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        JSONArray arrayString = new JSONArray();
        for (Cacheable object : objectList) {
            JSONObject objectString = object.toJSON();
            arrayString.put(objectString);
        }
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(saveName, arrayString.toString());
        editor.commit();
    }

    public static void clearCacheable(Context context, String saveName) {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mPreferences.edit();
        if (mPreferences.getString(saveName, "").length() > 0)
        {
            editor.remove(saveName);
            editor.commit();
        }
    }

    // Try to receive Cacheable list. Need to upcast your Cacheable using constructor after retrieving.
    public static ArrayList<Cacheable> getCacheableList(Context context, String saveName) {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String objectString = mPreferences.getString(saveName, "");
        ArrayList<Cacheable> objectList = new ArrayList<Cacheable>();

        if (objectString.equals("")) {
            return null;
        }

        JSONArray objectJSONArray;
        try {
            objectJSONArray = new JSONArray(objectString);
        } catch (JSONException e) {
            return null;
        }

        for (int i=0; i < objectJSONArray.length(); i++) {
            try {
                Cacheable object = new Cacheable(objectJSONArray.getJSONObject(i));
                objectList.add(object);
            } catch (JSONException e) {
                // Move on to the next one
            }
        }

        return objectList;
    }


}
