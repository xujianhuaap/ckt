package me.ketie.app.android.ui.auth;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;

import org.json.JSONObject;

import me.ketie.app.android.R;
import me.ketie.app.android.bean.UserInfo;
import me.ketie.app.android.common.Constants;
import me.ketie.app.android.utils.AccessTokenKeeper;
import me.ketie.app.android.utils.LogUtil;
import me.ketie.app.android.utils.UserInfoKeeper;
import me.ketie.app.android.view.XCRoundImageView;

public class AuthSettingInfoActivity extends ActionBarActivity {
    private XCRoundImageView mUserImg;
    private EditText mNickname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_setting_info);
        mUserImg=(XCRoundImageView)findViewById(R.id.userImg);
        mNickname= (EditText)findViewById(R.id.nickName);
        UserInfo userInfo = UserInfoKeeper.readUser(this);
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
            case 1:
                pullByWeixin();
                break;
            case 2:
            pullByWeibo();
                break;
            case 0:
                break;
        }
    }
    private void pullByWeibo() {
        Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(this);
        long uid = Long.parseLong(token.getUid());
        UsersAPI api = new UsersAPI(this, Constants.WEIBO_APP_KEY, token);
        api.show(uid,new RequestListener() {
            @Override
            public void onComplete(String s) {
                if(!TextUtils.isEmpty(s)){
                    LogUtil.d(s);
                    User user = User.parse(s);
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {

            }
        });

    }

    private void pullByWeixin() {

    }

}
