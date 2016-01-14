package org.ses.android.soap.utils;
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


}
