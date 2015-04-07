package me.ketie.app.android.net;

import me.ketie.app.android.common.Constants;


public class StringRequest extends com.android.volley.toolbox.StringRequest {
    StringRequest(int method, String path, StringListener listener) {
        super(method, Constants.API_HOST + path, listener, listener);
    }

    StringRequest(String path, StringListener listener) {
        super(Constants.API_HOST + path, listener, listener);
    }
}
