package me.ketie.app.android.auth;

import android.content.Context;

import me.ketie.app.android.auth.weibo.AccessTokenKeeper;

/**
 * Created by henjue on 2015/3/30.
 */
public class SessionTokenValidata {
    public static boolean isSessionValid(Context context){
        return AccessTokenKeeper.readAccessToken(context).isSessionValid();
    }
}
