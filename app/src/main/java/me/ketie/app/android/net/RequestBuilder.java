package me.ketie.app.android.net;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;

import org.json.JSONObject;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import me.ketie.app.android.utils.MD5Util;


/**
 * Version 1.0
 * <p/>
 * Date: 2015-03-27 17:20
 * Author: henjue@ketie.net
 */
public class RequestBuilder {
    private static final String LOG_TAG = RequestBuilder.class.getSimpleName();
    private static final boolean DEBUG = true;
    private String path;
    private Type type;
    private Map<String, String> params;
    private String token;

    public RequestBuilder(String path) {
        this.path = path;
        this.type = Type.POST;
        this.params = null;
        this.token = null;

    }

    public RequestBuilder(String path, Map<String, String> params) {
        this.path = path;
        this.params = params;
        this.type = Type.POST;
        ;
        this.token = null;
    }

    public RequestBuilder(String path, Map<String, String> params, String token) {
        this.path = path;
        this.params = params;
        this.token = token;
        this.type = Type.POST;
        ;
    }

    public RequestBuilder(Type type, String path) {
        this.path = path;
        this.type = type;
        this.params = null;
        this.token = null;
    }

    public RequestBuilder(Type type, String path, Map<String, String> params) {
        this.path = path;
        this.type = type;
        this.params = params;
        this.token = null;
    }

    public RequestBuilder(Type type, String path, Map<String, String> params, String token) {
        this.path = path;
        this.type = type;
        this.params = params;
        this.token = token;
    }

    public RequestBuilder setPath(String path) {
        this.path = path;
        return this;
    }

    public RequestBuilder setType(Type type) {
        this.type = type;
        return this;
    }

    public RequestBuilder setParams(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public RequestBuilder addParams(String key, String value) {
        if (this.params == null) {
            this.params = new HashMap<String, String>();
        }
        this.params.put(key, value);
        return this;
    }

    public RequestBuilder setToken(String token) {
        this.token = token;
        return this;
    }

    public StringRequest build(StringListener listener) {

        int method = type == Type.GET ? Request.Method.GET : Request.Method.POST;
        if (this.token != null) {
            if (this.params == null) {
                this.params = new HashMap<String, String>();
            }
            this.params.put("sign", "maimengkeji@" + token);
        }
        if (params != null) {
            String key = getKey(params);
            if (DEBUG) {
                Log.d(LOG_TAG, "加密后的Key：" + key);
            }
            if (key != null) {
                params.put("key", key);
            }
        }

        JSONObject json = new JSONObject(params);
        if (DEBUG) {
            Log.d(LOG_TAG, "Params：" + json.toString());
        }
        StringRequest request = new StringRequest(method, path, listener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        return request;

    }

    public JsonRequest build(JsonListener listener) {
        int method = type == Type.GET ? Request.Method.GET : Request.Method.POST;
        if (this.token != null) {
            if (this.params == null) {
                this.params = new HashMap<String, String>();
            }
            this.params.put("sign", "maimengkeji@" + token);
        }
        if (params != null) {
            String key = getKey(params);
            if (DEBUG) {
                Log.d(LOG_TAG, "加密后的Key：" + key);
            }
            if (key != null) {
                params.put("key", key);
            }
        }

        JSONObject json = new JSONObject(params);
        if (DEBUG) {
            Log.d(LOG_TAG, "Params：" + json.toString());
        }
        return new JsonRequest(method, path, json, listener);
    }

    private String getKey(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        if (DEBUG) {
            StringBuffer sbLog = new StringBuffer();
            for (String key : params.keySet()) {
                sbLog.append(key).append(",");
            }
            Log.d(LOG_TAG, "排序前：" + sbLog.toString());
        }
        Map<String, String> sortMap = new TreeMap<String, String>(new MapKeyComparator());
        sortMap.putAll(params);
        StringBuffer sb = new StringBuffer();
        if (DEBUG) {
            StringBuffer sbLog = new StringBuffer();
            for (String key : sortMap.keySet()) {
                sbLog.append(key).append(",");
                sb.append(sortMap.get(key)).append("&");
            }
            Log.d(LOG_TAG, "排序后：" + sbLog.toString());
        } else {
            for (String key : sortMap.keySet()) {
                sb.append(sortMap.get(key)).append("&");
            }
        }
        String plaintext = sb.toString();
        plaintext = plaintext.endsWith("&") ? plaintext.substring(0, plaintext.length() - 1) : plaintext;
        if (DEBUG) {
            Log.d(LOG_TAG, "未加密的Key：" + plaintext);
        }
        return MD5Util.MD5(plaintext);
    }

    public static enum Type {
        GET, POST
    }

    public class MapKeyComparator implements Comparator<String> {
        public int compare(String str1, String str2) {
            return str1.compareTo(str2);
        }
    }
}
