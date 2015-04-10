package me.ketie.app.android.model;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

/**
 * Created by henjue on 2015/4/9.
 */
public class Oauth2Access {
    private static final String KEY_UID = "t_uid";
    private static final String KEY_ACCESS_TOKEN = "t_access_token";
    private static final String KEY_EXPIRES_IN = "t_expires_in";
    public final String uid;
    public final String token;
    public final long expiresTime;
    public Oauth2Access(Oauth2AccessToken mAccessToken){
        this.uid=mAccessToken.getUid();
        this.token=mAccessToken.getToken();
        this.expiresTime=mAccessToken.getExpiresTime();
    }
    public Oauth2Access(String uid, String token, long expiresTime) {
        this.uid = uid;
        this.token = token;
        this.expiresTime = expiresTime;
    }
    static Oauth2Access read(SharedPreferences pref){
        String uid=pref.getString(KEY_UID,"");
        String token=pref.getString(KEY_ACCESS_TOKEN,"");
        long expiresTime=pref.getLong(KEY_EXPIRES_IN,0);
        Oauth2Access oauth = new Oauth2Access(uid, token, expiresTime);
        return oauth;
    }
    void write(SharedPreferences.Editor editor){
        editor.putString(KEY_UID,uid==null?"":uid);
        editor.putString(KEY_ACCESS_TOKEN, token==null?"":token);
        editor.putLong(KEY_EXPIRES_IN, expiresTime);
    }
    public Bundle toBundle(){
        Bundle bundle = new Bundle();
        bundle.putString(KEY_UID,uid==null?"":uid);
        bundle.putString(KEY_ACCESS_TOKEN, token==null?"":token);
        bundle.putLong(KEY_EXPIRES_IN, expiresTime);
        return bundle;
    }
    public static Oauth2Access parse(Bundle bundle){
        String uid=bundle.getString(KEY_UID,"");
        String token=bundle.getString(KEY_ACCESS_TOKEN,"");
        long expiresTime=bundle.getLong(KEY_EXPIRES_IN,0);
        Oauth2Access oauth = new Oauth2Access(uid, token, expiresTime);
        return oauth;
    }
    public String getUid() {
        return uid;
    }

    public String getToken() {
        return token;
    }

    public long getExpiresTime() {
        return expiresTime;
    }
}
