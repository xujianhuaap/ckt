package me.ketie.app.android.auth;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.mm.sdk.modelmsg.SendAuth;

import me.ketie.app.android.KApplication;
import me.ketie.app.android.R;

public class AuthActivity extends ActionBarActivity  implements  View.OnClickListener{
    private KApplication app;
    private SsoHandler mSsoHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app=(KApplication)getApplication();
        setContentView(R.layout.activity_auth);
        findViewById(R.id.login_wx).setOnClickListener(this);
        findViewById(R.id.login_wb).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this, "requestCode:" + requestCode, Toast.LENGTH_SHORT).show();
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
        } else {
            mSsoHandler = new SsoHandler(AuthActivity.this, app.mAuthInfo);
            mSsoHandler.authorize(new AuthListener());
        }
    }
}
