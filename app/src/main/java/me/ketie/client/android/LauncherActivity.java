package me.ketie.client.android;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendAuth;

import java.util.HashMap;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import me.ketie.client.android.net.Builder;


public class LauncherActivity extends Activity implements  PtrHandler, View.OnClickListener {
    private ListView mList;
    private TextView mEmptyView;
    private PtrFrameLayout ptrLayout;
    private KApplication app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app=(KApplication)getApplication();
        setContentView(R.layout.activity_launcher);
        mList=(ListView)findViewById(R.id.list);
        mEmptyView = (TextView)findViewById(R.id.emptyView);
        mList.setEmptyView(mEmptyView);

        try {
            ApplicationInfo appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            String channel_name=appInfo.metaData.getString("CHANNEL_NAME");
            mEmptyView.setText(channel_name);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ptrLayout=(PtrFrameLayout)findViewById(R.id.store_house_ptr_frame);
        findViewById(R.id.login_wx).setOnClickListener(this);
        StoreHouseHeader header=new StoreHouseHeader(this);
        header.setPadding(30,30,30,30);
        header.setTextColor(0xff0000);
        header.setScale(1.5f);
        ptrLayout.setHeaderView(header);
        ptrLayout.addPtrUIHandler(header);
        ptrLayout.setPtrHandler(this);
        new Builder("path",new HashMap<String,Object>(){{
            put("cid","cid123");
            put("aid","aid674");
            put("uid",111);
            }}).setToken("sdu89afuawodjfcads890fad0uc").build();
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View view, View view2) {
        return true;
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
        mEmptyView.setText("请求数据中....");
        Log.d("LauncherActivity","onRefreshBegin");
        ptrLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrLayout.refreshComplete();
                mEmptyView.setText(R.string.empty_data);
            }
        },1500);
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
