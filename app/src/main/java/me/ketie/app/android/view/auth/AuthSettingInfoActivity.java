package me.ketie.app.android.view.auth;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.http.RequestManager;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import me.ketie.app.android.R;
import me.ketie.app.android.access.UserAuth;
import me.ketie.app.android.common.AuthRedirect;
import me.ketie.app.android.component.BitmapCache;
import me.ketie.app.android.component.StreamWrapper;
import me.ketie.app.android.constants.GlobalConfig;
import me.ketie.app.android.network.JsonResponseListener;
import me.ketie.app.android.network.RequestBuilder;
import me.ketie.app.android.widget.XCRoundImageView;

public class AuthSettingInfoActivity extends ActionBarActivity implements ImageLoader.ImageListener {
    private XCRoundImageView mUserImg;
    private EditText mNickname;
    private ImageLoader loader;
    private Bitmap bitmap;
    private UserAuth user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = UserAuth.read(this);
        setContentView(R.layout.activity_auth_setting_info);
        loader = new ImageLoader(RequestManager.getInstance().getRequestQueue(), BitmapCache.getInstance());
        mUserImg = (XCRoundImageView) findViewById(R.id.userImg);
        mNickname = (EditText) findViewById(R.id.nickName);
        UserAuth userAuth = UserAuth.read(this);
        if (TextUtils.isEmpty(userAuth.nickname)) {
            pull(userAuth);
        }
    }

    public void onClickNext(View v) {
        if (bitmap == null) {
            Toast.makeText(this, "头像为空", Toast.LENGTH_SHORT).show();
            return;
        }
        final String nickname = this.mNickname.getText().toString();
        RequestBuilder builder = new RequestBuilder("user/updatedata");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        builder.addParams("nickname", nickname)
                .addParams("uid", user.uid)
                .addParams("headimg", new StreamWrapper(isBm, "headimg.png"));
        builder.post(new JsonResponseListener() {
            @Override
            public void onRequest() {

            }

            @Override
            public void onError(Exception e, String url, int actionId) {
                e.printStackTrace();
            }

            @Override
            public void onSuccess(JSONObject jsonObject, String url, int actionId) {
                try {
                    if ("20000".equals(jsonObject.getString("code"))) {
                        Toast.makeText(AuthSettingInfoActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                        user = new UserAuth(user.oauth2Access, user.loginType, user.token, user.uid, nickname, "");
                        user.write(AuthSettingInfoActivity.this);
                        AuthRedirect.toHome(AuthSettingInfoActivity.this);
                        finish();
                    } else {
                        Toast.makeText(AuthSettingInfoActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 从第三方拉取信息
     *
     * @param userAuth
     */
    private void pull(UserAuth userAuth) {
        switch (userAuth.loginType) {
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
        UserAuth user = UserAuth.read(this);
        Oauth2AccessToken token = new Oauth2AccessToken(user.oauth2Access.token, String.valueOf(user.oauth2Access.expiresTime));
        token.setUid(user.oauth2Access.getUid());
        UsersAPI api = new UsersAPI(this, GlobalConfig.WEIBO_APP_KEY, token);
        api.show(Long.parseLong(token.getUid()), new RequestListener() {
            @Override
            public void onComplete(String s) {
                if (!TextUtils.isEmpty(s)) {
                    User user = User.parse(s);
                    setDefaultInfo(user.screen_name, user.avatar_hd);
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
                setDefaultInfo("", "");
            }
        });

    }

    private void setDefaultInfo(String nickname, String headUrl) {
        mUserImg.setTag(headUrl);
        mNickname.setText(nickname);
        loader.get(headUrl, this, 320, 320);
    }

    private void pullByWeixin() {
        UserAuth user = UserAuth.read(this);
        String uid = user.oauth2Access.getUid();
        RequestBuilder build = new RequestBuilder("https://api.weixin.qq.com/sns/userinfo?access_token=" + user.oauth2Access.getToken() + "&openid=" + uid);
        build.get(new JsonResponseListener() {
            @Override
            public void onRequest() {

            }

            @Override
            public void onError(Exception e, String url, int actionId) {
                e.printStackTrace();
            }

            @Override
            public void onSuccess(JSONObject jsonObject, String url, int actionId) {
                try {
                    String nickname = jsonObject.getString("nickname");
                    String headimgurl = jsonObject.getString("headimgurl");
                    setDefaultInfo(nickname, headimgurl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
        this.bitmap = response.getBitmap();
        if (bitmap != null) {
            mUserImg.setImageBitmap(bitmap);
        } else {
            mUserImg.setImageResource(R.drawable.avatar_round);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        mUserImg.setImageResource(R.drawable.avatar_round);
    }
}
