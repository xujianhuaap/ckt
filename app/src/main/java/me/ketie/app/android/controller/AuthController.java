package me.ketie.app.android.controller;

import java.util.HashMap;

import me.ketie.app.android.net.RequestBuilder;

/**
 * Created by henjue on 2015/4/8.
 */
public class AuthController {
    public void loginThird(){

    }
    public void getValiCode(final String mobile){
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.Type.POST, "user/sendpostcode");
        requestBuilder.addParams("mobile",mobile);
    }
}
