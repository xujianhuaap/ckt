package me.ketie.app.android;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.test.ApplicationTestCase;
import android.util.Log;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testGetChannel() {
        try {
            ApplicationInfo appInfo = getContext().getPackageManager().getApplicationInfo(getContext().getPackageName(), PackageManager.GET_META_DATA);
            String channel_name = appInfo.metaData.getString("CHANNEL_NAME");
            Log.i("ApplicationTest", String.format("ChannelName:%s", channel_name));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}