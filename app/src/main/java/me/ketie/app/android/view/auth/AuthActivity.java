package me.ketie.app.android.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.mm.sdk.modelmsg.SendAuth;

import org.json.JSONException;
import org.json.JSONObject;

import me.ketie.app.android.KApplication;
import me.ketie.app.android.R;
import me.ketie.app.android.controller.AuthController;
import me.ketie.app.android.controller.AuthWeiboListener;
import me.ketie.app.android.network.JsonResponseListener;

/**
 * 用户授权，注册或登录
 */
public class AuthActivity extends ActionBarActivity implements View.OnClickListener {
    private KApplication app;
    private EditText editText;
    private View mBtnNext;
    private SsoHandler mSsoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (KApplication) getApplication();
        setContentView(R.layout.activity_auth);
        editText = (EditText) findViewById(R.id.edit_number);
        mBtnNext = findViewById(R.id.btn_next);
        findViewById(R.id.login_wx).setOnClickListener(this);
        findViewById(R.id.login_wb).setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    doNext();
                }
                return false;
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_wx) {
            if (!app.api.isWXAppInstalled()) {
                Toast.makeText(AuthActivity.this, "没有安装微信", Toast.LENGTH_SHORT).show();
                return;
            }
            final SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            app.api.sendReq(req);
            finish();
        } else if (v.getId() == R.id.login_wb) {
            mSsoHandler = new SsoHandler(AuthActivity.this, app.mWBAuthInfo);
            mSsoHandler.authorize(new AuthWeiboListener(AuthActivity.this));
        } else if (v.getId() == R.id.btn_next) {
            doNext();
        }
    }

    private void doNext() {
        mBtnNext.setEnabled(false);
        if (TextUtils.isEmpty(editText.getText())) {
            Toast.makeText(AuthActivity.this, R.string.input_phone_hint, Toast.LENGTH_SHORT).show();
            editText.setText("");
            editText.requestFocus();
            mBtnNext.setEnabled(true);
        } else {
            AuthController.getValiCode(editText.getText().toString(), new JsonResponseListener() {
                @Override
                public void onRequest() {

                }

                @Override
                public void onError(Exception errorMsg, String url, int actionId) {
                    errorMsg.printStackTrace();
                    mBtnNext.setEnabled(true);
                }

                @Override
                public void onSuccess(JSONObject json, String url, int actionId) {
                    try {
                        mBtnNext.setEnabled(true);
                        Log.d("AuthActivity", json.toString());
                        if ("20000".equals(json.getString("code"))) {
                            Intent intent = new Intent(AuthActivity.this, InputValidataActivity.class);
                            intent.putExtra("mobile", editText.getText().toString());
                            startActivityForResult(intent, 1001);
                            mBtnNext.setEnabled(false);
                        } else if ("55000".equals(json.getString("code"))) {
                            Toast.makeText(AuthActivity.this, json.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("AuthActivity", "onActivityResult");
        if (resultCode == RESULT_OK) {
            if (requestCode == 1001) {
                mBtnNext.setEnabled(true);
            } else {

                // SSO 授权回调
                // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
                if (mSsoHandler != null) {
                    mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
                }
            }
        }
    }

}
