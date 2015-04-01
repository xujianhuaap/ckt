package me.ketie.app.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import org.json.JSONException;
import org.json.JSONObject;

import me.ketie.app.android.auth.SessionTokenValidata;
import me.ketie.app.android.auth.weibo.AuthListener;
import me.ketie.app.android.common.AuthUtils;
import me.ketie.app.android.common.PushReceiveService;


public class LauncherActivity extends Activity {
    private ListView mList;
    private TextView mEmptyView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setPushIntentServiceClass(PushReceiveService.class);
        mPushAgent.enable();
        if(!SessionTokenValidata.isSessionValid(this)) {
            AuthUtils.toAuth(this);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        mList=(ListView)findViewById(R.id.list);
        mEmptyView = (TextView)findViewById(R.id.emptyView);
        mList.setEmptyView(mEmptyView);


    }
    @Override
    protected void onResume() {
        super.onResume();

        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
