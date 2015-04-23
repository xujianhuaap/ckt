package me.ketie.app.android.widget;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android on 15-4-22.
 */
public class RecyclerViewScrollManager {
    private List<OnRecyclerViewScrollListener> scrollListeners;
    private RecyclerView recyclerView;

    public static interface OnScrollManagerLocation {
        public enum Type {
            /**
             * left or top
             */
            TOP,
            /**
             * right or bottom
             */
            BOTTOM,
            NO
        }

        boolean isTop(RecyclerView recyclerView, Type type);

        boolean isBottom(RecyclerView recyclerView, Type type);
    }

    private OnRecyclerViewScrollLocationListener onRecyclerViewScrollLocationListener;
    private OnScrollManagerLocation onScrollManagerLocation;

    public void setOnScrollManagerLocation(OnScrollManagerLocation onScrollManagerLocation) {
        this.onScrollManagerLocation = onScrollManagerLocation;
    }

    public void setOnRecyclerViewScrollLocationListener(OnRecyclerViewScrollLocationListener onRecyclerViewScrollLocationListener) {
        this.onRecyclerViewScrollLocationListener = onRecyclerViewScrollLocationListener;
    }

    private boolean isScrolling;

    public boolean isScrolling() {
        return isScrolling;
    }

    public void registerScrollListener(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        ensureScrollListener();
        addScrollListener(new OnRecyclerViewScrollListener() {
            private OnScrollManagerLocation.Type type = OnScrollManagerLocation.Type.NO;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isScrolling = false;
                    // 滚动到顶部和滚动到低部的回调
                    if (null != onRecyclerViewScrollLocationListener) {
                        checkTopWhenScrollIdle(recyclerView);
                        checkBottomWhenScrollIdle(recyclerView);
                        type = OnScrollManagerLocation.Type.NO;
                    }
                } else {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //LogUtil.i("OnRecyclerViewScrollListener","dy:%d,dx:%d",dy,dx);
                if (dy < 0) {
                    type = OnScrollManagerLocation.Type.BOTTOM;
                } else if (dy > 0) {
                    type = OnScrollManagerLocation.Type.TOP;
                }
            }

            private void checkBottomWhenScrollIdle(RecyclerView recyclerView) {
                if (onScrollManagerLocation.isBottom(recyclerView, type)) {
                    onRecyclerViewScrollLocationListener.onBottomWhenScrollIdle(recyclerView);
                }
            }

            private void checkTopWhenScrollIdle(RecyclerView recyclerView) {
                if (onScrollManagerLocation.isTop(recyclerView, type)) {
                    onRecyclerViewScrollLocationListener.onTopWhenScrollIdle(recyclerView);
                }
            }
        });
    }

    public void addScrollListener(OnRecyclerViewScrollListener onRecyclerViewScrollListener) {
        if (null == onRecyclerViewScrollListener) {
            return;
        }
        if (null == scrollListeners) {
            scrollListeners = new ArrayList<>();
        }
        scrollListeners.add(onRecyclerViewScrollListener);
    }

    RecyclerView.OnScrollListener onScrollListener;

    private void ensureScrollListener() {
        if (null == onScrollListener) {
            onScrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    for (OnRecyclerViewScrollListener listener : scrollListeners) {
                        listener.onScrollStateChanged(recyclerView, newState);
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    for (OnRecyclerViewScrollListener listener : scrollListeners) {
                        listener.onScrolled(recyclerView, dx, dy);
                    }
                }
            };
        }
        recyclerView.setOnScrollListener(onScrollListener);
    }
}
