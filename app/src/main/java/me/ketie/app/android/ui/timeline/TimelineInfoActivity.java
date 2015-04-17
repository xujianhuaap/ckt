package me.ketie.app.android.ui.timeline;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import com.android.http.RequestManager;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import me.ketie.app.android.R;
import me.ketie.app.android.common.BitmapCache;

public class TimelineInfoActivity extends ActionBarActivity implements ImageLoader.ImageListener {

    private ImageView pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_info);
        pic = (ImageView) findViewById(R.id.pic);
        setPic(getResources().getDrawable(R.drawable.default_pic));
        ViewCompat.setTransitionName(pic, "pic");
        ImageLoader loader = new ImageLoader(RequestManager.getInstance().getRequestQueue(), BitmapCache.getInstance());
        loader.get(getIntent().getStringExtra("url"), this);
    }

    @Override
    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
        Bitmap bitmap = imageContainer.getBitmap();
        if (bitmap != null) {
            setPic(new BitmapDrawable(getResources(), bitmap));
        }
    }

    private void setPic(Drawable drawable) {
        pic.setBackground(drawable);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

    }
}
