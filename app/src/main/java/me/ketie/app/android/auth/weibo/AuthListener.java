package me.ketie.app.android.auth.weibo;

import android.os.Bundle;
import android.util.Log;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;

/**
 * Created by henjue on 2015/3/30.
 */
public class AuthListener implements WeiboAuthListener {
    @Override
    public void onComplete(Bundle values) {
        // 从 Bundle 中解析 Token
        Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(values);
        if (mAccessToken.isSessionValid()) {
            // 保存 Token 到 SharedPreferences
            //AccessTokenKeeper.writeAccessToken(WBAuthActivity.this, mAccessToken);
            Log.i("AuthListener","新浪微博登录成功:");
            Log.i("AuthListener","session:"+mAccessToken.getToken());
        } else {
            // 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
            String code = values.getString("code", "");
            Log.i("AuthListener","新浪微博登录失败:");
            Log.i("AuthListener","error code:"+code);
        }
    }

    @Override
    public void onWeiboException(WeiboException e) {
        Log.e("AuthListener","onWeiboException",e);
    }

    @Override
    public void onCancel() {
        Log.i("AuthListener","onCancel");
    }
}
