package me.ketie.app.android.view.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.http.RequestManager;
import com.android.volley.toolbox.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.ketie.app.android.R;
import me.ketie.app.android.access.UserAuth;
import me.ketie.app.android.component.BitmapCache;
import me.ketie.app.android.network.JsonResponseListener;
import me.ketie.app.android.network.RequestBuilder;
import me.ketie.app.android.view.common.SettingsActivity;
import me.ketie.app.android.view.common.TempFragment;
import me.ketie.app.android.widget.DragTopLayout;
import me.ketie.app.android.widget.RoundCornerImageView;
import me.ketie.app.android.widget.ViewPagerIndicator;

/**
 * Created by henjue on 2015/4/10.
 */
public class UserHomeFragment extends Fragment implements View.OnClickListener {
    private View btnSettings;
    private View mTitleContainer;
    private DragTopLayout dragLayout;

    public static UserHomeFragment newInstance() {
        return new UserHomeFragment();
    }

    private List<Fragment> mTabContents = new ArrayList<Fragment>();
    private FragmentPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private List<String> mDatas = Arrays.asList("作品", "贴纸", "话题");
    private ViewPagerIndicator mIndicator;


    private ImageLoader loader;
    private UserAuth user;
    private TextView mNickname;
    private RoundCornerImageView mUserImage;
    private JsonResponseListener listener = new JsonResponseListener() {
        @Override
        public void onRequest() {

        }

        @Override
        public void onError(Exception e, String url, int actionId) {

        }

        @Override
        public void onSuccess(JSONObject jsonObject, String url, int actionId) {
            try {
                if ("20000".equals(jsonObject.getString("code"))) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    loader.get(data.getString("headimg"), ImageLoader.getImageListener(mUserImage, R.drawable.avatar_me_default, R.drawable.avatar_me_default));
//                    mUserImage.setImageURI(Uri.parse(data.getString("headimg")));
                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_userhome, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTitleContainer = view.findViewById(R.id.title_container);
        dragLayout = (DragTopLayout) view.findViewById(R.id.drag_layout);
        mViewPager = (ViewPager) view.findViewById(R.id.id_vp);
        mIndicator = (ViewPagerIndicator) view.findViewById(R.id.id_indicator);
        btnSettings = view.findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(this);
        initFragment();
        //设置Tab上的标题
        mIndicator.setTabItemTitles(mDatas);
        mViewPager.setAdapter(mAdapter);
        //设置关联的ViewPager
        mIndicator.setViewPager(mViewPager, 0);
        loader = new ImageLoader(RequestManager.getInstance().getRequestQueue(), BitmapCache.getInstance());
        user = UserAuth.read(getActivity());
        RequestBuilder builder = new RequestBuilder("/ucenter/list");
        builder.addParams("token", user.token);
        builder.addParams("page", "0");
        builder.post(listener);
        mNickname = (TextView) view.findViewById(R.id.nickName);
        mUserImage = (RoundCornerImageView) view.findViewById(R.id.userImg);
        mNickname.setText(user.nickname);
        ;
    }

    private void initFragment() {

        for (String data : mDatas) {
            TempFragment fragment = TempFragment.newInstance(data);
            mTabContents.add(fragment);
        }

        mAdapter = new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mTabContents.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabContents.get(position);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        dragLayout.openTopView(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_me, menu);
    }

    @Override
    public void onClick(View v) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), mTitleContainer, "title");
        ActivityCompat.startActivity(getActivity(), new Intent(getActivity(), SettingsActivity.class), options.toBundle());
    }
}
