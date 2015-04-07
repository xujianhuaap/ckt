package me.ketie.app.android.ui.common;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

import me.ketie.app.android.KApplication;
import me.ketie.app.android.R;
import me.ketie.app.android.bean.UserInfo;
import me.ketie.app.android.utils.UserInfoKeeper;

public class MeActivity extends Activity {
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
        user = UserInfoKeeper.readUser(this);
        mNickname = (TextView) findViewById(R.id.nickName);
        mUserImage = (ImageView) findViewById(R.id.userImg);
        mNickname.setText(user.nickname);
        loader.get(user.img, ImageLoader.getImageListener(mUserImage, R.drawable.weibo_logo64, R.drawable.weibo_logo64));
    }

    public class BitmapCache implements ImageLoader.ImageCache {

        private LruCache<String, Bitmap> cache;

        public BitmapCache() {
            cache = new LruCache<String, Bitmap>(8 * 1024 * 1024) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return cache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            cache.put(url, bitmap);
        }
    }
}
