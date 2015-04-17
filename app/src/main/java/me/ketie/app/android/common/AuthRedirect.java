package me.ketie.app.android.common;

import android.app.Activity;
import android.content.Intent;

import me.ketie.app.android.ui.auth.AuthActivity;
import me.ketie.app.android.ui.launch.MainTabActivity;

/**
 * Created by henjue on 2015/3/30.
 */
public class AuthRedirect {
    public static void toAuth(Activity activity) {
        Intent intent = new Intent(activity, AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(intent, 100);
    }

    public static void toHome(Activity activity, int flag) {
        Intent intent = new Intent(activity, MainTabActivity.class);
        intent.addFlags(flag);
        activity.startActivity(intent);
    }

    public static void toHome(Activity activity) {
        toHome(activity, 0);
    }
}
