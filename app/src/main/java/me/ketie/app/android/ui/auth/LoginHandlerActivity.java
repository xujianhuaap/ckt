package me.ketie.app.android.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.umeng.message.UmengRegistrar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.ketie.app.android.R;
import me.ketie.app.android.common.AuthRedirect;
import me.ketie.app.android.common.LoginType;
import me.ketie.app.android.model.Oauth2Access;
import me.ketie.app.android.model.UserInfo;
import me.ketie.app.android.net.JsonResponse;
import me.ketie.app.android.net.RequestBuilder;

public class LoginHandlerActivity extends ActionBarActivity implements IWXAPIEventHandler, Response.ErrorListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dialog);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(getIntent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void handleIntent(Intent intent) {
        String loginType = intent.getStringExtra("loginType");
        if ("weibo".equals(loginType)) {
            login(LoginType.WEIBO, Oauth2Access.parse(intent.getExtras()));
        } else {
            Log.i("WXEntryActivity", "handleIntent");
            SendAuth.Resp resp = new SendAuth.Resp(intent.getExtras());
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK: {
                    //用户同意
                    String access_token_url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx1d9467a2fb82730d&secret=67100dc9c7e8e8dd6fed148b37b3f0f0&code=" + resp.code + "&grant_type=authorization_code";
                    RequestBuilder build=new RequestBuilder(access_token_url);
                    build.get(new JsonResponse() {
                        @Override
                        public void onRequest() {
                        }

                        @Override
                        public void onError(Exception e, String url, int actionId) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onSuccess(JSONObject jsonObject, String url, int actionId) {
                            try {
                                Oauth2AccessToken token = new Oauth2AccessToken();
                                token.setExpiresTime(Long.parseLong(jsonObject.getString("expires_in")));
                                token.setRefreshToken(jsonObject.getString("refresh_token"));
                                token.setToken(jsonObject.getString("access_token"));
                                token.setUid(jsonObject.getString("openid"));
                                login(LoginType.WEIXIN, new Oauth2Access(token));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
                }
                case BaseResp.ErrCode.ERR_USER_CANCEL: {
                    AuthRedirect.toAuth(this);
                    break;
                }

            }
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.i("WXEntryActivity", "onReq");
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.i("WXEntryActivity", "onResp");
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        volleyError.printStackTrace();
    }


    /**
     * @param type        登录类型，微信，或微博
     * @param accennToken //第三方Token对象
     */
    private void login(final LoginType type, final Oauth2Access accennToken) {
        String device_token = UmengRegistrar.getRegistrationId(this);
        Map<String, String> params = new HashMap<String, String>();
        final int value = type == LoginType.WEIXIN ? 1 : type == LoginType.WEIBO ? 2 : 0;
        params.put("type", String.valueOf(value));
        params.put("token", accennToken.getToken());
        params.put("usid", String.valueOf(accennToken.getUid()));
        params.put("pushtoken", device_token);
        params.put("pushtype", "2");
        me.ketie.app.android.net.Response listener=new JsonResponse() {
            @Override
            public void onRequest() {
            }

            @Override
            public void onError(Exception volleyError, String url, int actionId) {
                volleyError.printStackTrace();
                Toast.makeText(LoginHandlerActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                AuthRedirect.toAuth(LoginHandlerActivity.this);
                finish();
            }

            @Override
            public void onSuccess(JSONObject json, String url, int actionId) {
                try {
                    Log.d(LoginHandlerActivity.class.getSimpleName(), "onResponse:" + json.toString());
                    if ("20000".equals(json.getString("code"))) {
                        JSONObject data = json.getJSONObject("data");
                        String token = data.getString("token");
                        String nickname = data.getString("nickname");
                        String headimg = data.getString("headimg");
                        String uid = data.getString("uid");
                        boolean pushOn=data.getInt("pushstatus")==0;
                        UserInfo userInfo =new UserInfo(accennToken,type,token,uid,nickname,headimg);
                        userInfo.pushOn=pushOn;
                        userInfo.write(LoginHandlerActivity.this);
                        if (!TextUtils.isEmpty(userInfo.token)) {
                            Toast.makeText(LoginHandlerActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                            AuthRedirect.toHome(LoginHandlerActivity.this);
                            finish();
                        } else {
                            Toast.makeText(LoginHandlerActivity.this, R.string.login_faile, Toast.LENGTH_SHORT).show();
                            AuthRedirect.toAuth(LoginHandlerActivity.this);
                            finish();
                        }
                    } else {
                        Toast.makeText(LoginHandlerActivity.this, R.string.login_faile, Toast.LENGTH_SHORT).show();
                        AuthRedirect.toAuth(LoginHandlerActivity.this);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBuilder builder = new RequestBuilder("user/thirdlogin", params);
        builder.post(listener);

    }
}
