package me.ketie.app.android.ui.launch;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import java.util.ArrayList;
import java.util.List;

import me.ketie.app.android.R;
import me.ketie.app.android.common.AuthRedirect;
import me.ketie.app.android.common.PushReceiveService;
import me.ketie.app.android.model.UserInfo;
import me.ketie.app.android.ui.auth.AuthSettingInfoActivity;
import me.ketie.app.android.ui.common.DialogFragment;
import me.ketie.app.android.ui.common.DrawActivity;
import me.ketie.app.android.ui.timeline.TimelineFragment;
import me.ketie.app.android.ui.user.UserHomeFragment;
import me.ketie.app.android.ui.user.VpSimpleFragment;

/**
 * Created by android on 15-4-16.
 */
public class MainTabActivity extends FragmentActivity implements DialogFragment.NegativeListener, DialogFragment.PositiveListener, View.OnClickListener {
    private FragmentTabHost tabhost;
    private RadioGroup radioGroup;
    public List<Fragment> fragments = new ArrayList<Fragment>();
    private FragmentTabAdapter tabAdapter;
    private UserInfo user;
    private int REQUEST_SETINFO=0x1008;
    private View mBtnDraw;

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
            setContentView(R.layout.activity_main_tab);
            radioGroup = (RadioGroup) findViewById(android.R.id.tabs);
            mBtnDraw=findViewById(R.id.btn_draw);
            mBtnDraw.setOnClickListener(this);
            TimelineFragment timelineFragment = new TimelineFragment();
            VpSimpleFragment lableFragment = VpSimpleFragment.newInstance("我是标签页");
            VpSimpleFragment notificationFragment = VpSimpleFragment.newInstance("我是通知页");
            UserHomeFragment userHomeFragment = new UserHomeFragment();
            fragments.add(timelineFragment);
            fragments.add(lableFragment);
            fragments.add(notificationFragment);
            fragments.add(userHomeFragment);
            tabAdapter = new FragmentTabAdapter(this, fragments, R.id.tabcontent, radioGroup);
            tabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
                @Override
                public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
                    System.out.println("Extra---- " + index + " checked!!! ");
                }
            });
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

    @Override
    public void onClick(View v) {
        if(v==mBtnDraw){
            startActivity(new Intent(this, DrawActivity.class));
        }
    }
}
