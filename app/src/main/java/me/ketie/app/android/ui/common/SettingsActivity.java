package me.ketie.app.android.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import me.ketie.app.android.R;
import me.ketie.app.android.common.AuthRedirect;
import me.ketie.app.android.model.UserInfo;

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
public class SettingsActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void logout(View view) {
        UserInfo.clear(this);
        AuthRedirect.toHome(this,Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
    }
}
