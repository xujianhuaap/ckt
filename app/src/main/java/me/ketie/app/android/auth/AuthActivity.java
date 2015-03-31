package me.ketie.app.android.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.mm.sdk.modelmsg.SendAuth;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.ketie.app.android.KApplication;
import me.ketie.app.android.R;
import me.ketie.app.android.auth.weibo.AuthListener;
import me.ketie.app.android.common.Constants;
import me.ketie.app.android.net.RequestBuilder;
import me.ketie.app.android.net.JsonListener;
import me.ketie.app.android.net.JsonRequest;
import me.ketie.app.android.net.StringListener;

/**
 * 用户授权，注册或登录
 */
public class AuthActivity extends Activity implements  View.OnClickListener{
    private KApplication app;
    private SsoHandler mSsoHandler;
    private EditText editText;
    private View mBtnNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app=(KApplication)getApplication();
        setContentView(R.layout.activity_auth);
        editText=(EditText)findViewById(R.id.edit_number);
        mBtnNext = findViewById(R.id.btn_next);
        findViewById(R.id.login_wx).setOnClickListener(this);
        findViewById(R.id.login_wb).setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT){
                    doNext();
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("AuthActivity","onActivityResult");
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mSsoHandler != null) {;
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_wx) {
            if (!app.api.isWXAppInstalled()) {
                Toast.makeText(this, "没有安装微信", Toast.LENGTH_SHORT).show();
                return;
            }
            final SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            app.api.sendReq(req);
            finish();
        } else if(v.getId()==R.id.login_wb){
            mSsoHandler = new SsoHandler(AuthActivity.this, app.mAuthInfo);
            mSsoHandler.authorize(new AuthListener(this));
        }else if(v.getId()==R.id.btn_next){
            doNext();
        }
    }

    private void doNext() {
        Toast.makeText(this,"请稍后....",Toast.LENGTH_SHORT).show();

        mBtnNext.setEnabled(false);
       if(TextUtils.isEmpty(editText.getText())){
           Toast.makeText(this,R.string.input_phone_hint,Toast.LENGTH_SHORT).show();
           editText.setText("");
           editText.requestFocus();
       }else{
           RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.Type.POST,"user/sendpostcode", new HashMap<String, String>() {
               {
                   put("mobile", editText.getText().toString());
               };
           });
           StringRequest request = requestBuilder.build(new StringListener() {
               @Override
               public void onErrorResponse(VolleyError volleyError) {

               }

               @Override
               public void onResponse(JSONObject json)  throws JSONException {
                   mBtnNext.setEnabled(true);
                   Log.d("AuthActivity",json.toString());
                   if("20000".equals(json.getString("code"))){
                        Toast.makeText(AuthActivity.this,"获取成功",Toast.LENGTH_SHORT).show();
                   }else if("55000".equals(json.getString("code"))){
                       Toast.makeText(AuthActivity.this,json.getString("msg"),Toast.LENGTH_SHORT).show();
                   }
               }
           });
           app.reqManager.add(request);
           app.reqManager.start();
       }
    }
}
