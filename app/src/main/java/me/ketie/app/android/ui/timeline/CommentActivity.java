package me.ketie.app.android.ui.timeline;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import me.ketie.app.android.R;

/**
 * Created by android on 15-4-15.
 */
public class CommentActivity extends ActionBarActivity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_comment);
        ImageView mPic = (ImageView) findViewById(R.id.pic);
        ViewCompat.setTransitionName(mPic, "pic");
    }
}
