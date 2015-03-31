package me.ketie.app.android;

import android.app.Application;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.Map;

import me.ketie.app.android.common.Constants;
import me.ketie.app.android.net.KHttpStack;

/**
 * Version 1.0
 * <p/>
 * Date: 2015-03-27 19:10
 * Author: henjue@ketie.net
 */
public class KApplication extends Application {
    public IWXAPI api;
    public AuthInfo mAuthInfo;
    public RequestQueue reqManager;

    @Override
    public void onCreate() {
        super.onCreate();


        reqManager=Volley.newRequestQueue(this,new KHttpStack(new DefaultHttpClient()));
        api= WXAPIFactory.createWXAPI(this, Constants.WEIXIN_APP_KEY, true);
        api.registerApp( Constants.WEIXIN_APP_KEY);
        mAuthInfo = new AuthInfo(this, Constants.WEIBO_APP_KEY, Constants.REDIRECT_URL,null);
    }
}
