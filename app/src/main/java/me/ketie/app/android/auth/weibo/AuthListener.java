package me.ketie.app.android.auth.weibo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;

import me.ketie.app.android.auth.weixin.LoginDialogActivity;
import me.ketie.app.android.common.AuthUtils;

/**
 * Created by henjue on 2015/3/30.
 */
public class AuthListener implements WeiboAuthListener {
    private final Activity mActivity;

    public AuthListener(Activity mActivity){
        this.mActivity=mActivity;
    }
    @Override
    public void onComplete(Bundle values) {
        // 从 Bundle 中解析 Token
        Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(values);
        if (mAccessToken.isSessionValid()) {
            Log.i("AuthListener","新浪微博获取token成功");
            Log.i("AuthListener","session:"+mAccessToken.getToken());
            Intent intent = new Intent(mActivity, LoginDialogActivity.class);
            intent.putExtras(mAccessToken.toBundle());
            intent.putExtra("loginType","weibo");
            mActivity.startActivity(intent);
            mActivity.finish();
        } else {
            // 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
            String code = values.getString("code", "");
            Log.i("AuthListener","新浪微博登录失败:");
            Log.i("AuthListener","error code:"+code);
            AuthUtils.toAuth(mActivity);
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
