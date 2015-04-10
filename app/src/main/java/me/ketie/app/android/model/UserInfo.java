package me.ketie.app.android.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import me.ketie.app.android.common.LoginType;


/**
 * Created by henjue on 2015/4/7.
 */
public class UserInfo {
    private static final String PREFERENCES_NAME = "_userinfo";

    private static final String KEY_NICK = "_nickname";
    private static final String KEY_IMG = "_headimg";
    private static final String KEY_UID = "_uid";
    private static final String KEY_TOKEN = "_token";
    private static final String KEY_TYPE = "_type";
    public final String nickname;
    public final String uid;
    public final String img;
    public final String token;
    public final LoginType loginType;//0官方,1微信,2微博
    /**第三方token*/
    public final Oauth2Access oauth2Access;

    public UserInfo(Oauth2Access oauth2Access,LoginType loginType,String token,String uid,String nickname, String img) {
        this.oauth2Access=oauth2Access;
        this.uid=uid;
        this.nickname = nickname;
        this.img = img;
        this.loginType=loginType;
        this.token=token;
    }
    public static UserInfo read(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        String uid = pref.getString(KEY_UID, "");
        String nickname = pref.getString(KEY_NICK, "");
        String img = pref.getString(KEY_IMG, "");
        String token = pref.getString(KEY_TOKEN, "");
        int type = pref.getInt(KEY_TYPE, 0);
        LoginType lType=type==0?LoginType.DEFAULT:type==1?LoginType.WEIXIN :LoginType.WEIBO;
        Oauth2Access oauth = Oauth2Access.read(pref);
        return new UserInfo(oauth,lType,token,uid,nickname,img);
    }
    public void write(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_NICK, nickname==null?"":nickname);
        editor.putString(KEY_UID, uid==null?"":uid);
        editor.putString(KEY_IMG, img==null?"":img);
        editor.putString(KEY_TOKEN, token==null?"":token);
        int type=(loginType==LoginType.DEFAULT?0:loginType==LoginType.WEIBO?2:1);
        editor.putInt(KEY_TYPE, type);
        if(oauth2Access!=null){
            oauth2Access.write(editor);
        }
        editor.commit();
    }
    public Bundle toBundle(){
        Bundle bundle = new Bundle();
        bundle.putString(KEY_NICK, nickname==null?"":nickname);
        bundle.putString(KEY_IMG, img==null?"":img);
        bundle.putString(KEY_TOKEN, token==null?"":token);
        int type=(loginType==LoginType.DEFAULT?0:loginType==LoginType.WEIBO?2:1);
        bundle.putInt(KEY_TYPE, type);
        if(this.oauth2Access!=null){
            bundle.putAll(this.oauth2Access.toBundle());
        }
        return bundle;
    }
    public static UserInfo parse(Bundle bundle){
        String nickname = bundle.getString(KEY_NICK, "");
        String uid = bundle.getString(KEY_UID, "");
        String img = bundle.getString(KEY_IMG, "");
        String token = bundle.getString(KEY_TOKEN, "");
        int type = bundle.getInt(KEY_TYPE, 0);
        LoginType lType=type==0?LoginType.DEFAULT:type==1?LoginType.WEIXIN :LoginType.WEIBO;
        Oauth2Access oauth = Oauth2Access.parse(bundle);
        return new UserInfo(oauth,lType,token,uid,nickname,img);
    }
    public static void clear(Context context) {
        if (null == context) {
            return;
        }
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
