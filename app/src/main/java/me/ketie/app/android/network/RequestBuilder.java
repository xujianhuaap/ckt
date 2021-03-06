package me.ketie.app.android.network;

import android.util.Log;

import com.android.http.LoadControler;
import com.android.http.RequestManager;
import com.android.http.RequestMap;

import org.json.JSONObject;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import me.ketie.app.android.component.StreamWrapper;
import me.ketie.app.android.utils.MD5Util;


/**
 * 构建 http 请求
 * Version 1.0
 * <p/>
 * Date: 2015-03-27 17:20
 * Author: henjue@ketie.me
 */
public class RequestBuilder {
    private static final String LOG_TAG = RequestBuilder.class.getSimpleName();
    private static final boolean DEBUG = true;
    private String path;
    private Map<String, String> params = new HashMap<String, String>();
    private Map<String, File> files = new HashMap<String, File>();
    private Map<String, StreamWrapper> streams = new HashMap<String, StreamWrapper>();
    private String token;
    private LoadControler mRequest;

    public RequestBuilder(String path) {
        this.path = path;
    }

    public RequestBuilder(String path, String token) {
        this.path = path;
        this.token = token;
    }

    public RequestBuilder(String path, Map<String, String> params) {
        this.path = path;
        if (params != null) {
            this.params.clear();
            this.params.putAll(params);
        }
    }

    public RequestBuilder(String path, Map<String, String> params, String token) {
        this.path = path;
        this.token = token;
        if (params != null) {
            this.params.clear();
            this.params.putAll(params);
        }
    }

    public void post(final JsonResponseListener listener, int actionId) {
        RequestMap data = new RequestMap(build(), files);
        for (String key : streams.keySet()) {
            StreamWrapper stream = streams.get(key);
            data.put(key, stream.stream, stream.filename);
        }
        this.mRequest=RequestManager.getInstance().post(this.path, data, listener, actionId == -1 ? ((int) System.currentTimeMillis()) : actionId);
    }

    public void post(final JsonResponseListener listener) {
        post(listener, -1);
    }

    public void get(final RequestManager.RequestListener listener) {
        this.mRequest=RequestManager.getInstance().get(this.path, listener, new Random().nextInt());
    }


    public RequestBuilder setPath(String path) {
        this.path = path;
        return this;
    }


    public RequestBuilder setParams(Map<String, String> params) {
        if (params != null) {
            this.params.clear();
            this.params.putAll(params);
        }
        return this;
    }

    public RequestBuilder addParams(String key, int value) {
        assert key != null;
        this.params.put(key, String.valueOf(value));
        return this;
    }

    public RequestBuilder addParams(String key, String value) {
        assert key != null;
        assert value != null;
        this.params.put(key, value);
        return this;
    }

    public RequestBuilder addParams(String key, File file) {
        assert key != null;
        assert file != null;
        this.files.put(key, file);
        return this;
    }

    public RequestBuilder addParams(String key, StreamWrapper file) {
        assert key != null;
        assert file != null;
        assert key != null;
        assert file.filename != null;
        assert file.filename != null;
        this.streams.put(key, file);
        return this;
    }

    public RequestBuilder setToken(String token) {
        this.token = token;
        return this;
    }

    private Map<String, String> build() {

        if (this.token != null) {
            if (!this.params.containsKey("sign")) {
                this.params.put("sign", "maimengkeji@" + token);
            }
            if (!this.params.containsKey("token")) {
                this.params.put("token", token);
            }
        }
        String key = getKey(params);
        if (DEBUG) {
            Log.d(LOG_TAG, "加密后的Key：" + key);
        }
        if (key != null) {
            params.put("key", key);
        }

        JSONObject json = new JSONObject(params);
        if (DEBUG) {
            Log.d(LOG_TAG, "Params：" + json.toString());
        }
        params.put("versionName", "2.0.0");
        return params;

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

    public void cancel(){
        if(mRequest!=null){
            mRequest.cancel();
        }
    }
    public class MapKeyComparator implements Comparator<String> {
        public int compare(String str1, String str2) {
            return str1.compareTo(str2);
        }
    }
}
