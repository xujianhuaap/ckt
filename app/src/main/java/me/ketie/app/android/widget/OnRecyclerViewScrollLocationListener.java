package me.ketie.app.android.widget;

import android.support.v7.widget.RecyclerView;

/**
 * Created by android on 15-4-22.
 */
public interface OnRecyclerViewScrollLocationListener {
    void onTopWhenScrollIdle(RecyclerView recyclerView);

    void onBottomWhenScrollIdle(RecyclerView recyclerView);
}
