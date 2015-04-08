package me.ketie.app.android.net;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
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
                onSuccess(new JSONObject(s));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public abstract void onSuccess(JSONObject obj) throws JSONException;
}
