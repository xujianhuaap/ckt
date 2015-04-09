package me.ketie.app.android.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.http.RequestManager;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import me.ketie.app.android.R;
import me.ketie.app.android.common.BitmapCache;
import me.ketie.app.android.model.UserInfo;
import me.ketie.app.android.net.JsonResponse;
import me.ketie.app.android.net.ParamsBuilder;

public class MeActivity extends ActionBarActivity{
    private RequestQueue requestQueue;
    private ImageLoader loader;
    private UserInfo user;
    private TextView mNickname;
    private ImageView mUserImage;
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
                if("20000".equals(jsonObject.getString("code"))){
                    JSONObject data = jsonObject.getJSONObject("data");
                    loader.get(data.getString("headimg"), ImageLoader.getImageListener(mUserImage, R.drawable.weibo_logo64, R.drawable.weibo_logo64));
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
        setContentView(R.layout.activity_home);
        loader = new ImageLoader(RequestManager.getInstance().getRequestQueue(), new BitmapCache());
        user = UserInfo.read(this);
        ParamsBuilder builder=new ParamsBuilder("/ucenter/list");
        builder.addParams("token",user.token);
        builder.addParams("page","0");
        builder.post(listener);
        mNickname = (TextView) findViewById(R.id.nickName);
        mUserImage = (ImageView) findViewById(R.id.userImg);
        mNickname.setText(user.nickname);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_me, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
