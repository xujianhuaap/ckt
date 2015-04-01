package me.ketie.app.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import org.json.JSONException;
import org.json.JSONObject;

import me.ketie.app.android.R;
import me.ketie.app.android.auth.SessionTokenValidata;
import me.ketie.app.android.auth.weibo.AuthListener;
import me.ketie.app.android.common.AuthUtils;
import me.ketie.app.android.common.PushReceiveService;


public class LauncherActivity extends Activity implements View.OnClickListener {
    private ListView mList;
    private TextView mEmptyView;
    private Button mBtnCreation;


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
        mBtnCreation=(Button)findViewById(R.id.btn_creation);
        mList=(ListView)findViewById(R.id.list);
        mEmptyView = (TextView)findViewById(R.id.emptyView);
        mList.setEmptyView(mEmptyView);
        mBtnCreation.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_creation:
                startActivity(new Intent(this,CreationActivity.class));
                break;
        }
    }
}
