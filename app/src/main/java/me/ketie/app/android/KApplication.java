package me.ketie.app.android;

import android.app.Application;
import android.util.Log;

import com.android.http.RequestManager;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import me.ketie.app.android.common.Constants;
import me.ketie.app.android.common.PushReceiveService;

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
        RequestManager.getInstance().init(this).setHost(Constants.API_HOST);

        enableLog(PushReceiveService.class.getSimpleName(), Log.DEBUG);
        enableLog("Volley", Log.ASSERT);
        api = WXAPIFactory.createWXAPI(this, Constants.WEIXIN_APP_KEY, true);
        api.registerApp(Constants.WEIXIN_APP_KEY);
        mWBAuthInfo = new AuthInfo(this, Constants.WEIBO_APP_KEY, Constants.REDIRECT_URL, null);
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
