package me.ketie.app.android.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

public class ScreenUtil {
    private static String tag = ScreenUtil.class.getSimpleName();

    public static float getScreenDensity(Context context) {
        DisplayMetrics dm = null;
        if (context == null)
            throw new RuntimeException("the arguments is null");
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.density;
    }

    /**
     * 获取dip�?
     *
     * @param context
     * @param data
     * @return
     */
    public static float pixToDip(Context context, int pixData) {
        return pixData / getScreenDensity(context);
    }

    public static int getScreenWidth(Context context) {
        int width = 0;
        if (context == null)
            throw new RuntimeException("the arguments is null");
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        width = dm.widthPixels;
        if (context.getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            width = dm.heightPixels;
        }

        return width;
    }

    /**
     * 获取屏幕高度，如果传的参数context为空的话，则会抛出一个RuntimeException
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        int height = 0;
        if (context == null)
            throw new RuntimeException("the arguments is null");
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        height = dm.heightPixels;
        float density = dm.density;
        if (context.getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            height = dm.widthPixels;
        }
        return height;
    }
}
