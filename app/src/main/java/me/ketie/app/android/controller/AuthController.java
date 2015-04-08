package me.ketie.app.android.controller;

import android.content.Context;

import com.umeng.message.UmengRegistrar;

import me.ketie.app.android.net.RequestBuilder;
import me.ketie.app.android.net.StringListener;

/**
 * Created by henjue on 2015/4/8.
 */
public class AuthController {
    public void loginThird(){

    }

    /**
     * 用户登录或注册
     * @return
     */
    public static me.ketie.app.android.net.StringRequest auth(Context context,String mobile,String validataCode,final StringListener listener){
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.Type.POST, "user/reglogin");
        requestBuilder.addParams("mobile",mobile);
        requestBuilder.addParams("postcode",validataCode);
        requestBuilder.addParams("pushtoken",  UmengRegistrar.getRegistrationId(context));
        requestBuilder.addParams("pushtype","2");
        return requestBuilder.build(listener);
    }
    public static me.ketie.app.android.net.StringRequest getValiCode(final String mobile, final StringListener listener){
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.Type.POST, "user/sendpostcode");
        requestBuilder.addParams("mobile",mobile);
        return requestBuilder.build(listener);
    }
}
