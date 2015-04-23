package me.ketie.app.android.widget;

/**
 * Created by android on 15-4-22.
 */
public interface OnRecyclerViewScrollListener {
    public void onScrollStateChanged(android.support.v7.widget.RecyclerView recyclerView, int newState);

    public void onScrolled(android.support.v7.widget.RecyclerView recyclerView, int dx, int dy);
}
