package me.ketie.app.android;

import android.app.Application;
import android.util.Log;

import com.android.http.RequestManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import me.ketie.app.android.component.PushReceiveService;
import me.ketie.app.android.constants.GlobalConfig;

/**
 * Version 1.0
 * <p/>
 * Date: 2015-03-27 19:10
 * Author: henjue@ketie.net
 */
public class KApplication extends Application {
    public IWXAPI api;
    public AuthInfo mWBAuthInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        RequestManager instance = RequestManager.getInstance();
        instance.init(this).setHost(GlobalConfig.API_HOST);
        instance.enableLog(true);
        enableLog(PushReceiveService.class.getSimpleName(), Log.DEBUG);
//        enableLog("Volley", Log.ASSERT);
        api = WXAPIFactory.createWXAPI(this, GlobalConfig.WEIXIN_APP_KEY, true);
        api.registerApp(GlobalConfig.WEIXIN_APP_KEY);
        mWBAuthInfo = new AuthInfo(this, GlobalConfig.WEIBO_APP_KEY, GlobalConfig.REDIRECT_URL, null);
    }

    private void enableLog(String tag, int level) {
        try {
            Process process = Runtime.getRuntime().exec("setprop log.tag." + tag + " " + level);
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            BufferedReader input = new BufferedReader(ir);
            ir.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
