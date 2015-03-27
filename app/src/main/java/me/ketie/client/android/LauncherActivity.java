package me.ketie.client.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import me.ketie.client.android.net.Builder;


public class LauncherActivity extends Activity implements  PtrHandler {
    private ListView mList;
    private TextView mEmptyView;
    private PtrFrameLayout ptrLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        mList=(ListView)findViewById(R.id.list);
        mEmptyView = (TextView)findViewById(R.id.emptyView);
        mList.setEmptyView(mEmptyView);
        ptrLayout=(PtrFrameLayout)findViewById(R.id.store_house_ptr_frame);
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
}
