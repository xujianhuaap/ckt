package me.ketie.app.android.ui.launch;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import me.ketie.app.android.R;
import me.ketie.app.android.common.AuthRedirect;
import me.ketie.app.android.common.PushReceiveService;
import me.ketie.app.android.model.UserInfo;
import me.ketie.app.android.ui.auth.AuthSettingInfoActivity;
import me.ketie.app.android.ui.common.DialogFragment;
import me.ketie.app.android.ui.common.DrawActivity;
import me.ketie.app.android.ui.timeline.TimelineFragment;


public class LauncherActivity extends ActionBarActivity implements View.OnClickListener, DialogFragment.NegativeListener, DialogFragment.PositiveListener {
    private Button mBtnCreation;
    private Button mBtnMe;
    private int REQUEST_SETINFO=0x1008;
    private TimelineFragment homeList;
    private UserHomeFragment Me;
    private FragmentManager fm;
    private Button mBtnHome;
    private UserInfo user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setPushIntentServiceClass(PushReceiveService.class);
        mPushAgent.enable();
        user = UserInfo.read(this);
        if (TextUtils.isEmpty(user.token)) {
            AuthRedirect.toAuth(this);
            finish();
        }else {
            setContentView(R.layout.activity_launcher);
            mBtnCreation = (Button) findViewById(R.id.btn_creation);
            mBtnMe = (Button) findViewById(R.id.btn_me);
            mBtnHome = (Button) findViewById(R.id.btn_home);
            mBtnCreation.setOnClickListener(this);
            mBtnMe.setOnClickListener(this);
            mBtnHome.setOnClickListener(this);
            fm = getSupportFragmentManager();
            fm.beginTransaction().hide(fm.findFragmentByTag("me")).show(fm.findFragmentByTag("homelist")).commit();
            if (TextUtils.isEmpty(user.nickname)) {
                DialogFragment dialog = DialogFragment.newInstance("资料补全", "你的资料不完整，请补全资料!");
                dialog.setNegativeListener(this);
                dialog.setPositiveListener(this);
                dialog.show(getFragmentManager(), "dialog");
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        user = UserInfo.read(this);
        if (TextUtils.isEmpty(user.token)) {
            AuthRedirect.toAuth(this);
            finish();
        }
        MobclickAgent.onResume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction ft = fm.beginTransaction();
         ft.setCustomAnimations(
         R.anim.slide_left_in,
         R.anim.slide_left_out,
         R.anim.slide_right_in,
         R.anim.slide_right_out);
        switch (v.getId()) {
            case R.id.btn_creation:
                ActivityOptionsCompat options=ActivityOptionsCompat.makeSceneTransitionAnimation(this,v,"test");
                ActivityCompat.startActivity(this,new Intent(this, DrawActivity.class),options.toBundle());
                break;
            case R.id.btn_home:
                ft.hide(fm.findFragmentByTag("me")).show(fm.findFragmentByTag("homelist")).commit();
                break;
            case R.id.btn_me:
                ft.hide(fm.findFragmentByTag("homelist")).show(fm.findFragmentByTag("me")).commit();
                break;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==REQUEST_SETINFO){

            }
        }
    }

    @Override
    public String positiveText() {
        return "是";
    }
    @Override
    public void onPositiveClick(DialogInterface dialog) {
        dialog.dismiss();
        startActivityForResult(new Intent(this, AuthSettingInfoActivity.class),REQUEST_SETINFO);
    }

    @Override
    public void onNegativeClick(DialogInterface dialog) {
        dialog.dismiss();
        finish();
    }

    @Override
    public String negativeText() {
        return "否";
    }
}

