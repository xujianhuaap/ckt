package me.ketie.app.android.net;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Version 1.0
 * <p/>
 * Date: 2015-03-27 17:18
 * Author: henjue@ketie.net
 */
public abstract class StringListener implements Response.Listener<String>, Response.ErrorListener {
    @Override
    public final void onResponse(String s) {
        try {
            onResponse(new JSONObject(s));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public abstract void onResponse(JSONObject JSON) throws JSONException;
}
