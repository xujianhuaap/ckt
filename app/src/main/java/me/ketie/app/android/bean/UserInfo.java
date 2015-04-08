package me.ketie.app.android.bean;

/**
 * Created by henjue on 2015/4/7.
 */
public class UserInfo {
    public final String nickname;
    public final String img;
    public final String token;
    public final int loginType;//0官方,1微信,2微博

    public UserInfo(int loginType,String token,String nickname, String img) {
        this.nickname = nickname;
        this.img = img;
        this.loginType=loginType;
        this.token=token;
    }
}
