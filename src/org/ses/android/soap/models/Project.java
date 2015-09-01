package org.ses.android.soap.models;

/**
 * Created by User on 19/08/2015.
 */

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ses.android.seispapp120.R;
import org.ses.android.soap.utils.OfflineStorageManager;

import java.util.ArrayList;


public class Project extends Saveable {
    public int id;
    public String name;


    public Project()
    {
        id = 0;
        name = "";
    }


    public Project(int id, String name)
    {
        this.id = id;
        this.name = name;

    }

    /** Added to parse a string back into the JSON form.
     *
     *  @author Jtomaylla
     *  @param JSONString A JSON Serialization of the Project Object
     *
     */
    public Project(String JSONString) {
        try {
            JSONObject n = new JSONObject(JSONString);
            this.name = n.get("name").toString();
            this.id = Integer.parseInt(n.get("id").toString());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        JSONObject temp = new JSONObject();
        try {
            temp.put("name", this.name);
            temp.put("id",  Integer.toString(this.id));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp.toString();
    }

    /**
     * Parses out just the project name from a list of Locale objects and returns as an array of strings
     * @author Jtomaylla
     * @param projects ArrayList of Project objects
     * @return array of strings that contain only the 'name' of the locales
     */
    public static String[] ConvertLocalObjsToStrings(ArrayList<Project> projects) {
        String[] project_strings = new String[projects.size()];

        for (int i = 0; i < projects.size(); i++) {
            project_strings[i] = projects.get(i).name;
        }
        return project_strings;
    }

    public static String GetProjectNumber(Context context, String project_name){
        OfflineStorageManager sm = new OfflineStorageManager(context);
        String project_file = context.getString(R.string.project_filename);
        try{
            JSONArray array = new JSONArray(sm.getStringFromLocal(project_file));
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Project p = new Project(obj.toString());
                if (p.name.equals(project_name)){
                    return String.valueOf(p.id);
                }
            }
        }
        catch (JSONException e){
            e.printStackTrace();
            Log.e("project.java: GetprojectNumber", "JSONException");
        }
        return null;
    }


}


