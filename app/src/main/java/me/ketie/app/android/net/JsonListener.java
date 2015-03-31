package me.ketie.app.android.net;

import com.android.volley.Response;

import org.json.JSONObject;

/**
 * Version 1.0
 * <p/>
 * Date: 2015-03-27 17:18
 * Author: henjue@ketie.net
 */
public interface JsonListener extends Response.Listener<JSONObject>,Response.ErrorListener {
}
