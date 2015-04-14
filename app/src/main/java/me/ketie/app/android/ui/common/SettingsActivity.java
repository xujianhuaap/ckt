package me.ketie.app.android.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import me.ketie.app.android.R;
import me.ketie.app.android.common.AuthRedirect;
import me.ketie.app.android.model.UserInfo;
import me.ketie.app.android.net.JsonResponse;
import me.ketie.app.android.net.RequestBuilder;
import me.ketie.app.android.utils.LogUtil;

/**
 * <pre>
 * Description:
 * 2015/4/1418:57
 *
 * @author henjue
 *         email:henjue@gmail.com
 * @version 1.0
 *          </pre>
 */
public class SettingsActivity extends ActionBarActivity implements CompoundButton.OnCheckedChangeListener {
    private ToggleButton mTogglePush;
    private UserInfo user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mTogglePush=(ToggleButton)findViewById(R.id.togglePush);
        user = UserInfo.read(this);
        mTogglePush.setChecked(user.pushOn);
        mTogglePush.setOnCheckedChangeListener(this);

    }

    public void logout(View view) {
        UserInfo.clear(this);
        AuthRedirect.toHome(this,Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
        RequestBuilder builder=new RequestBuilder("ucenter/setpush");
        builder.addParams("uid",user.uid).addParams("status",String.valueOf(isChecked?0:1));
        builder.addParams("token",user.token);
        builder.post(new JsonResponse() {
            @Override
            public void onRequest() {

            }

            @Override
            public void onError(Exception e, String url, int actionId) {

            }

            @Override
            public void onSuccess(JSONObject jsonObject, String url, int actionId) {
                LogUtil.i("SettingsActivity",jsonObject.toString());
                try {
                    if("20000".equals(jsonObject.getString("code"))){
                        user.pushOn=isChecked;
                        user.write(SettingsActivity.this);
                    }else{
                        Toast.makeText(SettingsActivity.this,"设置失败",Toast.LENGTH_SHORT).show();
                        mTogglePush.setChecked(user.pushOn);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SettingsActivity.this,"设置失败",Toast.LENGTH_SHORT).show();
                    mTogglePush.setChecked(user.pushOn);
                }
            }
        });
    }
}
