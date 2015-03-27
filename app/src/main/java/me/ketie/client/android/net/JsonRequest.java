package me.ketie.client.android.net;

import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import me.ketie.client.android.common.Constants;

public class JsonRequest extends JsonObjectRequest {
    JsonRequest(int method, String path,JSONObject jsonRequest, JsonListener listener) {
        super(method, Constants.API_HOST+path, jsonRequest, listener, listener);
    }

    JsonRequest(String path,JSONObject jsonRequest,JsonListener listener) {
        super(Constants.API_HOST+path, jsonRequest, listener, listener);
    }
}
