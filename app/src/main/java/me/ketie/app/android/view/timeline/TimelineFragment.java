package me.ketie.app.android.view.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.http.RequestManager;
import com.android.volley.toolbox.ImageLoader;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.ketie.app.android.R;
import me.ketie.app.android.access.UserAuth;
import me.ketie.app.android.component.BitmapCache;
import me.ketie.app.android.controller.TimelineController;
import me.ketie.app.android.model.Timeline;
import me.ketie.app.android.network.JsonResponseListener;
import me.ketie.app.android.network.RequestBuilder;
import me.ketie.app.android.utils.LogUtil;
import me.ketie.app.android.view.MainTabActivity;
import me.ketie.app.android.widget.HBaseLinearLayoutManager;
import me.ketie.app.android.widget.OnRecyclerViewScrollListener;
import me.ketie.app.android.widget.OnRecyclerViewScrollLocationListener;


/**
 * Created by henjue on 2015/4/10.
 */
public class TimelineFragment extends Fragment implements RadioGroup.OnCheckedChangeListener, TimelineAdapter.TimelineItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private ImageLoader loader;
    private RadioGroup mFilter;
    private RadioButton mType1;
    private RadioButton mType2;
    private UserAuth user;
    private RecyclerView mRecycleView;
    private SwipeRefreshLayout refreshLayout;
    private HBaseLinearLayoutManager mLayoutManager;
    private int type = 1;
    private int page = 1;
    private TimelineAdapter adapter;
    private final int POST_ACTION_LIKE = 1001;
    private TimelineAdapter.OnHandlerLikeListener handlerLikeListener = new TimelineAdapter.OnHandlerLikeListener() {
        @Override
        public void onAction(String cid) {
            user = UserAuth.read(getActivity());
            RequestBuilder builder = new RequestBuilder("/hall/praise", user.token);
            builder.addParams("uid", user.uid);
            builder.addParams("cid", cid);
            builder.post(listener, POST_ACTION_LIKE);
        }
    };


    public static TimelineFragment newInstence() {
        return new TimelineFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        user = UserAuth.read(getActivity());
        return inflater.inflate(R.layout.fragment_timeline, null, false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loader = new ImageLoader(RequestManager.getInstance().getRequestQueue(), BitmapCache.getInstance());
        mFilter = (RadioGroup) view.findViewById(R.id.filter_type);
        mType1 = (RadioButton) view.findViewById(R.id.type_1);
        mType2 = (RadioButton) view.findViewById(R.id.type_2);
        mRecycleView = (RecyclerView) view.findViewById(R.id.listView);
        mRecycleView.setHasFixedSize(true);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayout);
        TextView mEmptyView = (TextView) view.findViewById(R.id.emptyView);
        mEmptyView.setVisibility(View.GONE);
        adapter = new TimelineAdapter(handlerLikeListener, loader);
        adapter.setTimelineItemClickListener(this);
        refreshLayout.setOnRefreshListener(this);
        mFilter.setOnCheckedChangeListener(this);
        mLayoutManager = new HBaseLinearLayoutManager(getActivity());
        mLayoutManager.setOnRecyclerViewScrollLocationListener(new OnRecyclerViewScrollLocationListener() {
            @Override
            public void onTopWhenScrollIdle(RecyclerView recyclerView) {

            }

            @Override
            public void onBottomWhenScrollIdle(RecyclerView recyclerView) {
                page++;
                refresh();
            }
        });
        mLayoutManager.addScrollListener(new OnRecyclerViewScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
                boolean enabled = firstVisibleItemPosition == 0;
                refreshLayout.setEnabled(enabled);
            }
        });
        mRecycleView.setLayoutManager(mLayoutManager);
        mRecycleView.setAdapter(adapter);
        refresh();
    }

    boolean shouldReset = false;

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.type_1) {
            this.page = 1;
            this.type = 1;
            shouldReset = true;
            mRecycleView.stopScroll();
            refresh();
        } else {
            this.page = 1;
            this.type = 2;
            shouldReset = true;
            mRecycleView.stopScroll();
            refresh();
        }
    }

    private final JsonResponseListener listener = new JsonResponseListener() {
        @Override
        public void onRequest() {

        }

        @Override
        public void onError(Exception e, String url, int actionId) {
            e.printStackTrace();
            refreshLayout.setRefreshing(false);
        }

        @Override
        public void onSuccess(JSONObject json, String url, int actionId) {
            refreshLayout.setRefreshing(false);
            try {
                LogUtil.i(TimelineFragment.class.getSimpleName(), "Url %s Result:%s", url, json.toString());
                String code = json.getString("code");
                if ("20000".equals(code)) {
                    if (actionId == POST_ACTION_LIKE) {
                        Toast.makeText(getActivity(), json.getString("msg"), Toast.LENGTH_SHORT).show();
                    } else {
                        JSONArray datas = json.getJSONObject("data").getJSONArray("content");
                        Gson gson = new Gson();
                        ArrayList<Timeline> lists = new ArrayList<Timeline>();
                        for (int i = 0; i < datas.length(); i++) {
                            String data = datas.getJSONObject(i).toString();
                            Timeline timeline = gson.fromJson(data, Timeline.class);
                            lists.add(timeline);
                        }
                        if (page != 1) {
                            adapter.reload(lists, true);
                            adapter.notifyDataSetChanged();
                        } else {
                            if (shouldReset) {
                                mLayoutManager.scrollToPosition(0);
                                shouldReset = false;
                            }
                            adapter.reload(lists, false);
                            adapter.notifyDataSetChanged();

                        }
                    }
                } else {
                    Toast.makeText(getActivity(), json.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void refresh() {
        if (this.type == 1) {
            TimelineController.listBoutique(user.token,user.uid,page,listener);
        } else {
            TimelineController.listAll(user.token,user.uid,page,listener);
        }

    }

    @Override
    public void onItemClick(TimelineAdapter.ViewHolder holder, Timeline data, int postion) {
        Intent intent = new Intent(getActivity(), TimelineReplyActivity.class);
        intent.putExtra("cid", data.getId());
        Pair<View, String> title = Pair.create((View) mFilter, "title");
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), title);
        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
    }

    @Override
    public void onRefresh() {
        refresh();
    }
}
