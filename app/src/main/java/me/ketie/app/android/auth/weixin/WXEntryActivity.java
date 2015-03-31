package me.ketie.app.android.auth.weixin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import org.json.JSONException;
import org.json.JSONObject;

import me.ketie.app.android.KApplication;
import me.ketie.app.android.auth.weibo.AccessTokenKeeper;
import me.ketie.app.android.common.AuthUtils;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler, Response.ErrorListener, Response.Listener<JSONObject> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Log.i("WXEntryActivity","handleIntent");
        SendAuth.Resp resp = new SendAuth.Resp(intent.getExtras());
        switch (resp.errCode){
            case BaseResp.ErrCode.ERR_OK:{
                Toast.makeText(this,"登录中,请稍后...",Toast.LENGTH_SHORT).show();
                //用户同意
                String access_token_url="https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx1d9467a2fb82730d&secret=67100dc9c7e8e8dd6fed148b37b3f0f0&code="+resp.code+"&grant_type=authorization_code";
                JsonObjectRequest request = new JsonObjectRequest(access_token_url, null, this, this);
                RequestQueue reqManager = ((KApplication) getApplication()).reqManager;
                reqManager.add(request);
                reqManager.start();
                break;
            }
            case BaseResp.ErrCode.ERR_USER_CANCEL:{
                AuthUtils.toAuth(this);
                break;
            }

        }
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.i("WXEntryActivity","onReq");
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.i("WXEntryActivity","onResp");
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        volleyError.printStackTrace();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        try {
            Toast.makeText(this,"登录成功",Toast.LENGTH_SHORT).show();
            Oauth2AccessToken token = new Oauth2AccessToken();
            token.setExpiresTime(Long.parseLong(jsonObject.getString("expires_in")));
            token.setRefreshToken(jsonObject.getString("refresh_token"));
            token.setToken(jsonObject.getString("access_token"));
            token.setUid(jsonObject.getString("openid"));
            AccessTokenKeeper.writeAccessToken(this, token);
            AuthUtils.toHome(this);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
