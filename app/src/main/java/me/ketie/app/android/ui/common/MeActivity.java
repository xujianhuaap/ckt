package me.ketie.app.android.ui.common;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

import me.ketie.app.android.KApplication;
import me.ketie.app.android.R;
import me.ketie.app.android.common.BitmapCache;
import me.ketie.app.android.model.UserInfo;

public class MeActivity extends ActionBarActivity {
    private RequestQueue requestQueue;
    private ImageLoader loader;
    private UserInfo user;
    private TextView mNickname;
    private ImageView mUserImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        KApplication application = (KApplication) getApplication();
        requestQueue = application.reqManager;
        loader = new ImageLoader(requestQueue, new BitmapCache());
        user = UserInfo.read(this);
        mNickname = (TextView) findViewById(R.id.nickName);
        mUserImage = (ImageView) findViewById(R.id.userImg);
        mNickname.setText(user.nickname);
        loader.get(user.img, ImageLoader.getImageListener(mUserImage, R.drawable.weibo_logo64, R.drawable.weibo_logo64));
    }

}
