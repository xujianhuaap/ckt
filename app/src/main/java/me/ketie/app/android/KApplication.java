package me.ketie.app.android;

import android.app.Application;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import me.ketie.app.android.common.Constants;

/**
 * Version 1.0
 * <p/>
 * Date: 2015-03-27 19:10
 * Author: henjue@ketie.net
 */
public class KApplication extends Application {
    public IWXAPI api;
    public AuthInfo mAuthInfo;
    @Override
    public void onCreate() {
        super.onCreate();
        api= WXAPIFactory.createWXAPI(this, Constants.WEIXIN_APP_KEY, true);
        api.registerApp( Constants.WEIXIN_APP_KEY);

        mAuthInfo = new AuthInfo(this, Constants.WEIBO_APP_KEY, Constants.REDIRECT_URL,null);
    }
}
