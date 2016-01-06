package org.ses.android.soap.models;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ses.android.seispapp120.R;
import org.ses.android.soap.database.Departamento;
import org.ses.android.soap.utils.OfflineStorageManager;

import java.util.ArrayList;

/**
 * Created by dvillanueva on 06/11/2015.
 */
public class Depart extends   Saveable {


    public String cod;
    public String descrip;
    public ArrayList<String>  ListaDep ;

    public Depart()
    {
        cod = "";
         descrip = "";
    }


    public Depart(String cod , String descripc)
    {
        this.cod = cod;
        this.descrip = descripc;

    }

    /** Added to parse a string back into the JSON form.
     *
     *  @author Brendan Bozorgmir
     *  @param JSONString A JSON Serialization of the Locale Object
     *
     */
    public Depart(String JSONString) {
        try {
            JSONObject n = new JSONObject(JSONString);
            this.cod = n.get("Codigo").toString();
            this.descrip = n.get("Descrip").toString();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        JSONObject temp = new JSONObject();
        try {
            temp.put("cod", this.cod);
            temp.put("descrip",  this.descrip);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp.toString();
    }

    /**
     * Parses out just the locale name from a list of Locale objects and returns as an array of strings
     * @author JN
     * @param departos ArrayList of Locale objects
     * @return array of strings that contain only the 'name' of the locales
     */
    public static String[] ConvertLocalObjsToStrings(ArrayList<Depart> departos) {
        String[] locale_strings = new String[departos.size()];

        for (int i = 0; i < departos.size(); i++) {
            locale_strings[i] = departos.get(i).descrip;


        }
        return locale_strings;
    }



    public ArrayList<String> GetDepatrtamentoList(ArrayList<Depart> departos){
            ListaDep = new ArrayList<>();
        for (int i = 0; i < departos.size(); i++) {
            ListaDep.add(departos.get(i).descrip );
        }



        return  ListaDep;

    }


    public static String GetLocaleNumber(Context context, String locale_name){
        OfflineStorageManager sm = new OfflineStorageManager(context);
        String locale_file = context.getString(R.string.locale_filename);
        try{
            JSONArray array = new JSONArray(sm.getStringFromLocal(locale_file));
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                 Depart l = new Depart(obj.toString());
                if (l.descrip.equals(locale_name)){
                    return String.valueOf(l.cod);
                }
            }
        }
        catch (JSONException e){
            e.printStackTrace();
            Log.e("Locale.java: GetLocaleNumber", "JSONException");
        }
        return null;
    }

}
