package org.ses.android.soap.models;

import org.json.JSONObject;

/**
 * Created by anyway on 1/18/16.
 */
public class Cacheable {

    public JSONObject jsonObject = null;

    public Cacheable(JSONObject jsonObject) { this.jsonObject = jsonObject; }

    public JSONObject toJSON() { return new JSONObject(); }

}
