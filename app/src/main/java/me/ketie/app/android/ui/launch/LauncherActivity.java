package me.ketie.app.android.ui.launch;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import org.w3c.dom.Text;

import me.ketie.app.android.KApplication;
import me.ketie.app.android.R;
import me.ketie.app.android.auth.SessionTokenValidata;
import me.ketie.app.android.bean.UserInfo;
import me.ketie.app.android.common.AuthRedirect;
import me.ketie.app.android.common.PushReceiveService;
import me.ketie.app.android.ui.auth.AuthSettingInfoActivity;
import me.ketie.app.android.ui.common.DialogFragment;
import me.ketie.app.android.ui.common.DrawActivity;
import me.ketie.app.android.ui.common.MeActivity;
import me.ketie.app.android.utils.UserInfoKeeper;


public class LauncherActivity extends ActionBarActivity implements View.OnClickListener, DialogFragment.NegativeListener, DialogFragment.PositiveListener {
    private ListView mList;
    private TextView mEmptyView;
    private Button mBtnCreation;
    private Button mBtnMe;
    private int REQUEST_SETINFO=0x1008;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PushAgent mPushAgent = PushAgent.getInstance(this);
        KApplication app = ((KApplication) getApplication());
        mPushAgent.setPushIntentServiceClass(PushReceiveService.class);
        mPushAgent.enable();
        if (TextUtils.isEmpty(UserInfoKeeper.readUser(this).token)) {
            AuthRedirect.toAuth(this);
            finish();
        }
        UserInfo user = UserInfoKeeper.readUser(this);
        if ("".equals(user.img) || "".equals(user.nickname)) {
            DialogFragment dialog = DialogFragment.newInstance("资料补全", "你的资料不完整，请补全资料!");
            dialog.setNegativeListener(this);
            dialog.setPositiveListener(this);
            dialog.show(getFragmentManager(), "dialog");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        mBtnCreation = (Button) findViewById(R.id.btn_creation);
        mBtnMe = (Button) findViewById(R.id.btn_me);
        mList = (ListView) findViewById(R.id.list);
        mEmptyView = (TextView) findViewById(R.id.emptyView);
        mList.setEmptyView(mEmptyView);
        mBtnCreation.setOnClickListener(this);
        mBtnMe.setOnClickListener(this);

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
        switch (v.getId()) {
            case R.id.btn_creation:
                startActivity(new Intent(this, DrawActivity.class));
                break;
            case R.id.btn_me:
                startActivity(new Intent(this, MeActivity.class));
                break;
        }
    }


    @Override
    public void onPositiveClick(DialogInterface dialog) {
        dialog.dismiss();
        startActivityForResult(new Intent(this, AuthSettingInfoActivity.class),REQUEST_SETINFO);
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
    public void onNegativeClick(DialogInterface dialog) {
        dialog.dismiss();
        finish();
    }

    @Override
    public String negativeText() {
        return "否";
    }
}

