package me.ketie.app.android.ui.auth;

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
import me.ketie.app.android.common.AuthRedirect;
import me.ketie.app.android.common.BitmapCache;
import me.ketie.app.android.common.Constants;
import me.ketie.app.android.common.StreamWrapper;
import me.ketie.app.android.model.UserInfo;
import me.ketie.app.android.net.JsonResponse;
import me.ketie.app.android.net.ParamsBuilder;
import me.ketie.app.android.utils.LogUtil;
import me.ketie.app.android.view.XCRoundImageView;

public class AuthSettingInfoActivity extends ActionBarActivity implements ImageLoader.ImageListener {
    private XCRoundImageView mUserImg;
    private EditText mNickname;
    private ImageLoader loader;
    private BitmapCache imageCache;
    private Bitmap bitmap;
    private UserInfo user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user=UserInfo.read(this);
        setContentView(R.layout.activity_auth_setting_info);
        imageCache = new BitmapCache();
        loader=new ImageLoader(RequestManager.getInstance().getRequestQueue(), imageCache);
        mUserImg=(XCRoundImageView)findViewById(R.id.userImg);
        mNickname= (EditText)findViewById(R.id.nickName);
        UserInfo userInfo = UserInfo.read(this);
        if(TextUtils.isEmpty(userInfo.nickname)){
            pull(userInfo);
        }
    }
    public void onClickNext(View v){
        if(bitmap==null){
            Toast.makeText(this,"头像为空",Toast.LENGTH_SHORT).show();
            return;
        }
        final String nickname=this.mNickname.getText().toString();
        ParamsBuilder builder=new ParamsBuilder("user/updatedata");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        builder.addParams("nickname",nickname)
        .addParams("uid",user.uid)
        .addParams("headimg", new StreamWrapper(isBm,"headimg.png"));
        builder.post(new JsonResponse() {
            @Override
            public void onRequest() {

            }

            @Override
            public void onError(Exception e, String url, int actionId) {
        e.printStackTrace();
            }

            @Override
            public void onSuccess(JSONObject jsonObject, String url, int actionId) {
                LogUtil.d(jsonObject.toString());
                try {
                    if("20000".equals(jsonObject.getString("code"))){
                        Toast.makeText(AuthSettingInfoActivity.this,"设置成功",Toast.LENGTH_SHORT).show();
                        user=new UserInfo(user.oauth2Access,user.loginType,user.token,user.uid,nickname,"");
                        user.write(AuthSettingInfoActivity.this);
                        AuthRedirect.toHome(AuthSettingInfoActivity.this);
                        finish();
                    }else{
                        Toast.makeText(AuthSettingInfoActivity.this,jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
        String uid = user.oauth2Access.getUid();
        Oauth2AccessToken token=new Oauth2AccessToken(user.oauth2Access.token,String.valueOf(user.oauth2Access.expiresTime));
        token.setUid(user.oauth2Access.getUid());
        UsersAPI api = new UsersAPI(this, Constants.WEIBO_APP_KEY,token);

        api.show(uid,new RequestListener() {
            @Override
            public void onComplete(String s) {
                if(!TextUtils.isEmpty(s)){
                    LogUtil.d(s);
                    User user = User.parse(s);
                    setDefaultInfo(user.screen_name,user.avatar_hd);
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
                setDefaultInfo("","");
            }
        });

    }
    private void setDefaultInfo(String nickname,String headUrl){
        mUserImg.setTag(headUrl);
        mNickname.setText(nickname);
        loader.get(headUrl,this,320,320);
    }
    private void pullByWeixin() {
        UserInfo user = UserInfo.read(this);
        String uid = user.oauth2Access.getUid();
        ParamsBuilder build=new ParamsBuilder("https://api.weixin.qq.com/sns/userinfo?access_token="+user.oauth2Access.getToken()+"&openid="+uid);
        build.get(new JsonResponse() {
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
                    String nickname=jsonObject.getString("nickname");
                    String headimgurl=jsonObject.getString("headimgurl");
                    setDefaultInfo(nickname,headimgurl);
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
            mUserImg.setImageResource( R.drawable.avatar_round);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
            mUserImg.setImageResource(R.drawable.avatar_round );
    }
}
