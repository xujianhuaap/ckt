package me.ketie.app.android.controller;

import android.content.Context;

import com.android.http.RequestManager;
import com.umeng.message.UmengRegistrar;

import me.ketie.app.android.net.ParamsBuilder;
import me.ketie.app.android.net.Response;

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
    public static void auth(Context context,String mobile,String validataCode,final Response listener){
        ParamsBuilder paramsBuilder = new ParamsBuilder("user/reglogin");
        paramsBuilder.addParams("mobile",mobile);
        paramsBuilder.addParams("postcode",validataCode);
        paramsBuilder.addParams("pushtoken",  UmengRegistrar.getRegistrationId(context));
        paramsBuilder.addParams("pushtype","2");
        paramsBuilder.post(listener);
    }
    public static void getValiCode(final String mobile, final Response listener){
        ParamsBuilder paramsBuilder = new ParamsBuilder("user/sendpostcode");
        paramsBuilder.addParams("mobile",mobile);
        paramsBuilder.post(listener);
    }
}
