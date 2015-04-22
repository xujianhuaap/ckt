package me.ketie.app.android.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by android on 15-4-22.
 */
public class HBaseLinearLayoutManager extends LinearLayoutManager implements RecyclerViewScrollManager.OnScrollManagerLocation {
    private static final String TAG = HBaseLinearLayoutManager.class.getSimpleName();

    private RecyclerViewScrollManager recyclerViewScrollManager;

    public void setOnRecyclerViewScrollLocationListener( OnRecyclerViewScrollLocationListener onRecyclerViewScrollLocationListener) {
        ensureRecyclerViewScrollManager();
        recyclerViewScrollManager.setOnRecyclerViewScrollLocationListener(onRecyclerViewScrollLocationListener);
        recyclerViewScrollManager.setOnScrollManagerLocation(this);
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        recyclerViewScrollManager.registerScrollListener(view);
        super.onAttachedToWindow(view);
    }

    public void addScrollListener(OnRecyclerViewScrollListener onRecyclerViewScrollListener) {
        if (null == onRecyclerViewScrollListener) {
            return;
        }
        recyclerViewScrollManager.addScrollListener(onRecyclerViewScrollListener);
    }
    public HBaseLinearLayoutManager(Context context) {
        super(context);
    }

    public HBaseLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public boolean isScrolling() {
        if (null != recyclerViewScrollManager) {
            return recyclerViewScrollManager.isScrolling();
        }
        return false;
    }
    private void ensureRecyclerViewScrollManager(){
        if (null == recyclerViewScrollManager) {
            recyclerViewScrollManager = new RecyclerViewScrollManager();
        }
    }
    @Override
    public boolean isTop(RecyclerView recyclerView, Type type) {
        int firstVisibleItemPosition = findFirstVisibleItemPosition();
        return 0 == firstVisibleItemPosition && recyclerView.getTop()==findViewByPosition(firstVisibleItemPosition).getTop();
    }

    @Override
    public boolean isBottom(RecyclerView recyclerView, Type type) {
        int lastVisiblePosition = findLastVisibleItemPosition();
        int lastPosition = recyclerView.getAdapter().getItemCount() - 1;
        return lastVisiblePosition == lastPosition && recyclerView.getBottom()==findViewByPosition(lastVisiblePosition).getBottom();
    }
}
