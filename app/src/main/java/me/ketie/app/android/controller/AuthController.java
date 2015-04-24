package me.ketie.app.android.controller;

import android.content.Context;

import com.android.http.RequestManager;
import com.umeng.message.UmengRegistrar;

import me.ketie.app.android.network.JsonResponseListener;
import me.ketie.app.android.network.RequestBuilder;

/**
 * 用户授权相关
 * Created by henjue on 2015/4/8.
 */
public class AuthController {

    /**
     * 用户登录或注册
     *
     * @return
     */
    public static void auth(Context context, String mobile, String validataCode, final JsonResponseListener listener) {
        RequestBuilder requestBuilder = new RequestBuilder("user/reglogin");
        requestBuilder.addParams("mobile", mobile);
        requestBuilder.addParams("postcode", validataCode);
        requestBuilder.addParams("pushtoken", UmengRegistrar.getRegistrationId(context));
        requestBuilder.addParams("pushtype", "2");
        requestBuilder.post(listener);
    }

    /**
     * 获取验证码
     * @param mobile
     * @param listener
     */
    public static void getValiCode(final String mobile, final JsonResponseListener listener) {
        RequestBuilder requestBuilder = new RequestBuilder("user/sendpostcode");
        requestBuilder.addParams("mobile", mobile);
        requestBuilder.post(listener);
    }

}
