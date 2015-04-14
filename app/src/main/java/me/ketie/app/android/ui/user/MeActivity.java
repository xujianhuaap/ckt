package me.ketie.app.android.ui.user;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.widget.TextView;

import com.android.http.RequestManager;
import com.android.volley.toolbox.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.ketie.app.android.R;
import me.ketie.app.android.common.BitmapCache;
import me.ketie.app.android.model.UserInfo;
import me.ketie.app.android.net.JsonResponse;
import me.ketie.app.android.net.ParamsBuilder;
import me.ketie.app.android.utils.LogUtil;
import me.ketie.app.android.view.RoundCornerImageView;
import me.ketie.app.android.view.ViewPagerIndicator;

public class MeActivity extends ActionBarActivity{
    private List<Fragment> mTabContents = new ArrayList<Fragment>();
    private FragmentPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private List<String> mDatas = Arrays.asList("作品", "贴纸", "话题");
    private ViewPagerIndicator mIndicator;


    private ImageLoader loader;
    private UserInfo user;
    private TextView mNickname;
    private RoundCornerImageView mUserImage;
    private JsonResponse listener=new JsonResponse() {
        @Override
        public void onRequest() {

        }

        @Override
        public void onError(Exception e, String url, int actionId) {

        }

        @Override
        public void onSuccess(JSONObject jsonObject, String url, int actionId) {
            try {
                LogUtil.d(jsonObject.toString());
                if("20000".equals(jsonObject.getString("code"))){
                    JSONObject data = jsonObject.getJSONObject("data");
                    loader.get(data.getString("headimg"), ImageLoader.getImageListener(mUserImage, R.drawable.avatar_me_default, R.drawable.avatar_me_default));
//                    mUserImage.setImageURI(Uri.parse(data.getString("headimg")));
                }else{

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_userhome);
        mViewPager = (ViewPager) findViewById(R.id.id_vp);
        mIndicator = (ViewPagerIndicator) findViewById(R.id.id_indicator);
        initFragment();
        //设置Tab上的标题
        mIndicator.setTabItemTitles(mDatas);
        mViewPager.setAdapter(mAdapter);
        //设置关联的ViewPager
        mIndicator.setViewPager(mViewPager,0);
        loader = new ImageLoader(RequestManager.getInstance().getRequestQueue(), new BitmapCache());
        user = UserInfo.read(this);
        ParamsBuilder builder=new ParamsBuilder("/ucenter/list");
        builder.addParams("token",user.token);
        builder.addParams("page","0");
        builder.post(listener);
        mNickname = (TextView) findViewById(R.id.nickName);
        mUserImage = (RoundCornerImageView) findViewById(R.id.userImg);
        mNickname.setText(user.nickname);;
    }
    private void initFragment()
    {

        for (String data : mDatas)
        {
            VpSimpleFragment fragment = VpSimpleFragment.newInstance(data);
            mTabContents.add(fragment);
        }

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager())
        {
            @Override
            public int getCount()
            {
                return mTabContents.size();
            }

            @Override
            public Fragment getItem(int position)
            {
                return mTabContents.get(position);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_me, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
