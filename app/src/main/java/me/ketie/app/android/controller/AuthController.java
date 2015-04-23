package me.ketie.app.android.controller;

import android.content.Context;

import com.umeng.message.UmengRegistrar;

import me.ketie.app.android.network.IResponseListener;
import me.ketie.app.android.network.RequestBuilder;

/**
 * Created by henjue on 2015/4/8.
 */
public class AuthController {

    /**
     * 用户登录或注册
     *
     * @return
     */
    public static void auth(Context context, String mobile, String validataCode, final IResponseListener listener) {
        RequestBuilder requestBuilder = new RequestBuilder("user/reglogin");
        requestBuilder.addParams("mobile", mobile);
        requestBuilder.addParams("postcode", validataCode);
        requestBuilder.addParams("pushtoken", UmengRegistrar.getRegistrationId(context));
        requestBuilder.addParams("pushtype", "2");
        requestBuilder.post(listener);
    }

    public static void getValiCode(final String mobile, final IResponseListener listener) {
        RequestBuilder requestBuilder = new RequestBuilder("user/sendpostcode");
        requestBuilder.addParams("mobile", mobile);
        requestBuilder.post(listener);
    }

}
