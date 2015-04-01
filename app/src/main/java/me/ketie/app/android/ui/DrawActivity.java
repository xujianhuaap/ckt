package me.ketie.app.android.ui;

import android.app.Activity;
import android.os.Bundle;

import me.ketie.app.android.R;
import me.ketie.app.android.utils.FileUtil;
import me.ketie.app.android.view.DrawImageLayout;
import me.ketie.app.android.view.DrawImageView;

/**
 * Created by henjue on 2015/4/1.
 */
public class DrawActivity extends Activity {
    private DrawImageLayout drawLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_itmap);
        drawLayout=(DrawImageLayout)findViewById(R.id.drawLayout);
        DrawImageView imageView=new DrawImageView(this,120,160,240,320,0);
        imageView.setImageBitmap(FileUtil.getImagePath("2014-03big/1393830348113"));
        drawLayout.addImage(imageView);

        DrawImageView imageView2=new DrawImageView(this,130,190,240,320,0);
        imageView2.setImageBitmap(FileUtil.getImagePath("2014-03big/1393830348113"));
        drawLayout.addImage(imageView2);
    }

}
