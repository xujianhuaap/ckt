package me.ketie.app.android.ui.auth;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;

import me.ketie.app.android.KApplication;
import me.ketie.app.android.R;
import me.ketie.app.android.common.BitmapCache;
import me.ketie.app.android.common.Constants;
import me.ketie.app.android.model.UserInfo;
import me.ketie.app.android.utils.LogUtil;
import me.ketie.app.android.view.XCRoundImageView;

public class AuthSettingInfoActivity extends ActionBarActivity {
    private XCRoundImageView mUserImg;
    private EditText mNickname;
    private ImageLoader loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_setting_info);
        RequestQueue reqManager = ((KApplication) getApplication()).reqManager;
        loader=new ImageLoader(reqManager,new BitmapCache());
        mUserImg=(XCRoundImageView)findViewById(R.id.userImg);
        mNickname= (EditText)findViewById(R.id.nickName);
        UserInfo userInfo = UserInfo.read(this);
        if("".equals(userInfo.nickname)){
            pull(userInfo);
        }
    }

    /**
     * 从第三方拉取信息
     * @param userInfo
     */
    private void pull(UserInfo userInfo) {
        switch (userInfo.loginType){
            case WEIXIN:
                pullByWeixin();
                break;
            case WEIBO:
            pullByWeibo();
                break;
            case DEFAULT:
                break;
        }
    }
    private void pullByWeibo() {
        UserInfo user = UserInfo.read(this);
        long uid = user.oauth2Access.getUid();
        Oauth2AccessToken token=new Oauth2AccessToken(user.oauth2Access.token,String.valueOf(user.oauth2Access.expiresTime));
        token.setUid(String.valueOf(user.oauth2Access.getUid()));
        UsersAPI api = new UsersAPI(this, Constants.WEIBO_APP_KEY,token);
        final ImageLoader.ImageListener listener=ImageLoader.getImageListener(mUserImg, R.drawable.weibo_logo64, R.drawable.weibo_logo64);
        api.show(uid,new RequestListener() {
            @Override
            public void onComplete(String s) {
                if(!TextUtils.isEmpty(s)){
                    LogUtil.d(s);
                    User user = User.parse(s);
                    mNickname.setText(user.screen_name);
                    loader.get(user.avatar_hd,listener,320,320);
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
            }
        });

    }

    private void pullByWeixin() {

    }

}
