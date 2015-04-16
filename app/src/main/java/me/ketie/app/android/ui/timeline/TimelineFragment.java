package me.ketie.app.android.ui.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.http.RequestManager;
import com.android.volley.toolbox.ImageLoader;
import com.google.gson.Gson;

import org.henjue.widget.PullLoadLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;

import me.ketie.app.android.R;
import me.ketie.app.android.common.BitmapCache;
import me.ketie.app.android.gsonbean.Timeline;
import me.ketie.app.android.model.UserInfo;
import me.ketie.app.android.net.JsonResponse;
import me.ketie.app.android.net.RequestBuilder;
import me.ketie.app.android.utils.LogUtil;


/**
 * Created by henjue on 2015/4/10.
 */
public class TimelineFragment extends Fragment implements RadioGroup.OnCheckedChangeListener, PullLoadLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private ImageLoader loader;
    private RadioGroup mFilter;
    private RadioButton mType1;
    private RadioButton mType2;
    private UserInfo user;
    private ListView mListView;
    private PullLoadLayout mPullLayout;
    private int type=1;
    private int page=1;
    private TimelineAdapter adapter;
    private final int POST_ACTION_LIKE=1001;
    private TimelineAdapter.OnHandlerLikeListener handlerLikeListener=new TimelineAdapter.OnHandlerLikeListener() {
        @Override
        public void onAction(String cid) {
            user=UserInfo.read(getActivity());
            RequestBuilder builder=new RequestBuilder("/hall/praise",user.token);
            builder.addParams("uid",user.uid);
            builder.addParams("cid",cid);
            builder.post(listener,POST_ACTION_LIKE);
        }
    };


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        user=UserInfo.read(getActivity());
        return inflater.inflate(R.layout.fragment_timeline,null,false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loader=new ImageLoader(RequestManager.getInstance().getRequestQueue(),BitmapCache.getInstance());
        mFilter=(RadioGroup)view.findViewById(R.id.filter_type);
        mType1=(RadioButton)view.findViewById(R.id.type_1);
        mType2=(RadioButton)view.findViewById(R.id.type_2);
        mListView=(ListView)view.findViewById(R.id.listView);
        mPullLayout=(PullLoadLayout)view.findViewById(R.id.pullLayout);
        TextView mEmptyView = (TextView) view.findViewById(R.id.emptyView);
        mListView.setEmptyView(mEmptyView);
        adapter=new TimelineAdapter(getActivity(),handlerLikeListener,loader);
        mListView.setOnItemClickListener(this);
        mPullLayout.setOnRefreshListener(this);
        mFilter.setOnCheckedChangeListener(this);
        mListView.setAdapter(adapter);
        refresh();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId==R.id.type_1){
            this.page=1;
            this.type=1;
            refresh();
        }else{
            this.page=1;
            this.type=2;
            refresh();
        }
    }
    private final  JsonResponse listener=new JsonResponse() {
        @Override
        public void onRequest() {

        }

        @Override
        public void onError(Exception e, String url, int actionId) {
            e.printStackTrace();
            mPullLayout.onHeaderRefreshComplete();
            mPullLayout.onFooterRefreshComplete();
        }

        @Override
        public void onSuccess(JSONObject json, String url, int actionId) {
            mPullLayout.onHeaderRefreshComplete();
            mPullLayout.onFooterRefreshComplete();
            try {
                LogUtil.i(TimelineFragment.class.getSimpleName(),"Url %s Result:%s",url,json.toString());
                String code=json.getString("code");
                if("20000".equals(code)){
                    if(actionId==POST_ACTION_LIKE){
                        Toast.makeText(getActivity(),json.getString("msg"),Toast.LENGTH_SHORT).show();
                    }else {
                        JSONArray datas = json.getJSONObject("data").getJSONArray("content");
                        Gson gson = new Gson();
                        ArrayList<Timeline> lists = new ArrayList<Timeline>();
                        for (int i = 0; i < datas.length(); i++) {
                            String data = datas.getJSONObject(i).toString();
                            Timeline timeline = gson.fromJson(data, Timeline.class);
                            lists.add(timeline);
                        }
                        adapter.reload(lists, page != 1);
                    }
                }else{
                    Toast.makeText(getActivity(),json.getString("msg"),Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private void refresh() {
        final RequestBuilder builder;
        if(this.type==1){
            builder=new RequestBuilder("/hall/finelist");
        }else{
            builder=new RequestBuilder("/hall/getallcontents");
        }
        builder.addParams("uid",user.uid);
        builder.addParams("token",user.token);
        builder.addParams("page",String.valueOf(this.page));
        builder.post(listener);

    }

    @Override
    public void onHeadRefresh(PullLoadLayout view) {
            this.page=1;
            refresh();
    }

    @Override
    public void onFootRefresh(PullLoadLayout view) {
            this.page++;
            refresh();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TimelineAdapter.ViewHolder holder = adapter.getHolder(view);
        Timeline data = adapter.getItem(position);
        Intent intent = new Intent(getActivity(), TimelineInfoActivity.class);
        intent.putExtra("url",data.getImgurl());
        Pair<View,String> pic=Pair.create((View)holder.img,"pic");
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),pic);
        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
        //ActivityCompat.startActivity(getActivity(), intent, null);
    }
}
