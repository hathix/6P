package org.ses.android.soap.models;

import org.json.JSONObject;

/**
 * Created by anyway on 1/18/16.
 */
public class Cacheable {

    public JSONObject jsonObject = null;

    /**
     * Needed as a default constructor because it's subclassed.
     */
    public Cacheable() {}

    public Cacheable(JSONObject jsonObject) { this.jsonObject = jsonObject; }

    public JSONObject toJSON() { return new JSONObject(); }

}
