package me.ketie.app.android.auth.weibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;

import me.ketie.app.android.common.AuthRedirect;
import me.ketie.app.android.model.Oauth2Access;
import me.ketie.app.android.ui.auth.LoginHandlerActivity;
import me.ketie.app.android.utils.LogUtil;

/**
 * Created by henjue on 2015/3/30.
 */
public class AuthListener implements WeiboAuthListener {
    private final Activity mActivity;

    public AuthListener(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public void onComplete(Bundle values) {
        // 从 Bundle 中解析 Token
        Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(values);
        LogUtil.i("AuthListener:","新浪微博授权信息:",mAccessToken.toString());
        LogUtil.i("AuthListener:",mAccessToken.toString());
        Oauth2Access temp=new Oauth2Access(mAccessToken);
        if (mAccessToken.isSessionValid()) {
            Intent intent = new Intent(mActivity, LoginHandlerActivity.class);
            intent.putExtras(temp.toBundle());
            intent.putExtra("loginType","weibo");
            mActivity.startActivity(intent);
            mActivity.finish();
        } else {
            // 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
            String code = values.getString("code", "");
            Log.i("AuthListener", "新浪微博登录失败:");
            Log.i("AuthListener", "error code:" + code);
            AuthRedirect.toAuth(mActivity);
        }
    }

    @Override
    public void onWeiboException(WeiboException e) {
        Log.e("AuthListener", "onWeiboException", e);
    }

    @Override
    public void onCancel() {
        Log.i("AuthListener", "onCancel");
    }
}
