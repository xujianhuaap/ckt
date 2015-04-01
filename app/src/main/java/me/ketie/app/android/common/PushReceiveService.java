package me.ketie.app.android.common;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.umeng.message.UmengBaseIntentService;
import com.umeng.message.entity.UMessage;

import org.android.agoo.client.BaseConstants;
import org.json.JSONObject;

/**
 * Created by henjue on 2015/4/1.
 */
public class PushReceiveService extends UmengBaseIntentService {
    private final static String LOG_TAG=PushReceiveService.class.getSimpleName();
    @Override
    protected void onMessage(Context context, Intent intent) {
        super.onMessage(context, intent);
        try {
            String message = intent.getStringExtra(BaseConstants.MESSAGE_BODY);
            UMessage msg = new UMessage(new JSONObject(message));
            Log.d(LOG_TAG,"收到推送消息"+ msg.custom);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }
}
