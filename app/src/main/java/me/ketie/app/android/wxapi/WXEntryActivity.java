package me.ketie.app.android.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

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
    private void handleIntent(Intent intent) {
        Log.i("WXEntryActivity","handleIntent");
        SendAuth.Resp resp = new SendAuth.Resp(intent.getExtras());
        if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
            Log.i("WXEntryActivity","登录成功,");
            for(String key:intent.getExtras().keySet()){
                Log.i("WXEntryActivity",String.format("%s:%s",key,intent.getExtras().get(key)));
            }
            finish();
            //用户同意
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
}
