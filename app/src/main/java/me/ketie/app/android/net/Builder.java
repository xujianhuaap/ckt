package me.ketie.app.android.net;

import android.util.Log;

import com.android.volley.Request;

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
public class Builder {
    private String path;
    private Type type;
    private Map<String,Object> params;
    private String token;

    public Builder setPath(String path) {
        this.path = path;
        return this;
    }

    public Builder setType(Type type) {
        this.type = type;
        return this;
    }

    public Builder setParams(Map<String, Object> params) {
        this.params = params;
        return this;
    }
    public Builder addParams(String key,Object value){
        if(this.params==null){
            this.params=new HashMap<String,Object>();
        }
        this.params.put(key,value);
        return this;
    }
    public Builder setToken(String token) {
        this.token = token;
        return this;
    }

    public static enum Type{
        GET,POST
    }
    public Builder(String path){
        this.path=path;
        this.type=Type.GET;
        this.params=null;
        this.token=null;

    }
    public Builder(String path,Map<String,Object> params){
        this.path=path;
        this.params=params;
        this.type=Type.GET;;
        this.token=null;
    }
    public Builder(String path,Map<String,Object> params,String token){
        this.path=path;
        this.params=params;
        this.token=token;
        this.type=Type.GET;;
    }

    public Builder(Type type,String path){
        this.path=path;
        this.type=type;
        this.params=null;
        this.token=null;
    }
    public Builder(Type type,String path,Map<String,Object> params){
        this.path=path;
        this.type=type;
        this.params=params;
        this.token=null;
    }
    public Builder(Type type,String path,Map<String,Object> params,String token){
        this.path=path;
        this.type=type;
        this.params=params;
        this.token=token;
    }
    public JsonRequest build(){
        return build(null);
    }
    public JsonRequest build(JsonListener listener){
        int method=type==Type.GET? Request.Method.GET: Request.Method.POST;
        if(this.token!=null){
            if(this.params==null){
                this.params=new HashMap<String,Object>();
            }
            this.params.put("sign","maimengkeji@"+token);
        }
        if(params!=null){
            String key=getKey(params);
            Log.d("test","加密后的Key：");
            Log.d("test",key);
            Log.d("test","\n\r");
            if(key!=null){
                params.put("key",key);
            }
        }
        return new JsonRequest(method,path,null,listener);
    }
    private String getKey(Map<String,Object> params){
        if (params == null || params.isEmpty()) {
            return null;
        }
        Log.d("test","排序前：");
        for(String key:params.keySet()){
            Log.d("test",key);
        }
        Map<String, Object> sortMap = new TreeMap<String, Object>(new MapKeyComparator());
        sortMap.putAll(params);
        StringBuffer sb=new StringBuffer();
        Log.d("test","排序后：");
        for(String key:sortMap.keySet()){
            Log.d("test",key);
            sb.append(sortMap.get(key)).append("&");
        }

        String plaintext=sb.toString();
        plaintext = plaintext.endsWith("&") ? plaintext.substring(0, plaintext.length() - 1) : plaintext;
        Log.d("test","未加密的Key：");
        Log.d("test",plaintext);
        Log.d("test","\n\r");
        return MD5Util.MD5(plaintext);
    }
    public class MapKeyComparator implements Comparator<String> {
        public int compare(String str1, String str2) {
            return str1.compareTo(str2);
        }
    }
}
