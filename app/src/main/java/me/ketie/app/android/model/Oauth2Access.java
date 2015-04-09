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
    public final long uid;
    public final String token;
    public final long expiresTime;
    public Oauth2Access(Oauth2AccessToken mAccessToken){
        this.uid=Long.parseLong(mAccessToken.getUid());
        this.token=mAccessToken.getToken();
        this.expiresTime=mAccessToken.getExpiresTime();
    }
    public Oauth2Access(long uid, String token, long expiresTime) {
        this.uid = uid;
        this.token = token;
        this.expiresTime = expiresTime;
    }
    static Oauth2Access read(SharedPreferences pref){
        long uid=pref.getLong(KEY_UID,0);
        String token=pref.getString(KEY_ACCESS_TOKEN,"");
        long expiresTime=pref.getLong(KEY_EXPIRES_IN,0);
        Oauth2Access oauth = new Oauth2Access(uid, token, expiresTime);
        return oauth;
    }
    void write(SharedPreferences.Editor editor){
        editor.putLong(KEY_UID,this.uid);
        editor.putString(KEY_ACCESS_TOKEN, this.token);
        editor.putLong(KEY_EXPIRES_IN, this.expiresTime);
    }
    public Bundle toBundle(){
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_UID,this.uid);
        bundle.putString(KEY_ACCESS_TOKEN, this.token);
        bundle.putLong(KEY_EXPIRES_IN, this.expiresTime);
        return bundle;
    }
    public static Oauth2Access parse(Bundle bundle){
        long uid=bundle.getLong(KEY_UID,0);
        String token=bundle.getString(KEY_ACCESS_TOKEN,"");
        long expiresTime=bundle.getLong(KEY_EXPIRES_IN,0);
        Oauth2Access oauth = new Oauth2Access(uid, token, expiresTime);
        return oauth;
    }
    public long getUid() {
        return uid;
    }

    public String getToken() {
        return token;
    }

    public long getExpiresTime() {
        return expiresTime;
    }
}
