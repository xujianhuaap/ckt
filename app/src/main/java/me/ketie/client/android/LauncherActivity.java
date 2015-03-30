package me.ketie.client.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.umeng.analytics.MobclickAgent;


public class LauncherActivity extends Activity implements  View.OnClickListener {
    private ListView mList;
    private TextView mEmptyView;
    private KApplication app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app=(KApplication)getApplication();
        setContentView(R.layout.activity_launcher);
        mList=(ListView)findViewById(R.id.list);
        mEmptyView = (TextView)findViewById(R.id.emptyView);
        mList.setEmptyView(mEmptyView);
        findViewById(R.id.login_wx).setOnClickListener(this);

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
        if(!app.api.isWXAppInstalled()){
            Toast.makeText(this,"没有安装微信",Toast.LENGTH_SHORT).show();
            return;
        }
        final SendAuth.Req req=new SendAuth.Req();
        req.scope="snsapi_userinfo";
        app.api.sendReq(req);

    }
}
