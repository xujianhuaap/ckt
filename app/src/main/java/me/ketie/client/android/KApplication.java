package me.ketie.client.android;

import android.app.Application;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Version 1.0
 * <p/>
 * Date: 2015-03-27 19:10
 * Author: henjue@ketie.net
 */
public class KApplication extends Application {
    public IWXAPI api;

    @Override
    public void onCreate() {
        super.onCreate();
        api= WXAPIFactory.createWXAPI(this, "wx1d9467a2fb82730d", false);
    }
}
