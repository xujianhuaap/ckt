package me.ketie.app.android.ui.timeline;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.http.RequestManager;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import me.ketie.app.android.R;
import me.ketie.app.android.common.BitmapCache;
import me.ketie.app.android.common.StreamWrapper;
import me.ketie.app.android.gsonbean.Comment;
import me.ketie.app.android.gsonbean.Topic;
import me.ketie.app.android.model.UserInfo;
import me.ketie.app.android.net.JsonResponse;
import me.ketie.app.android.net.RequestBuilder;
import me.ketie.app.android.utils.LogUtil;

public class TimelineCommentActivity extends ActionBarActivity implements View.OnClickListener {

    private RadioGroup mTab;
    private LinearLayout mCommentContainer;
    private CheckBox mLikeCount;
    private UserInfo user;
    private int page=1;
    private LinearLayout mUserPhotos;
    private ImageLoader loader;
    private ListView mListView;
    private TimelineCommentAdapter adapter;
    private View mBottomContainer;
    private View mBtnSendText;
    private EditText mEdContent;
    private String cid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user= UserInfo.read(this);
        loader=new ImageLoader(RequestManager.getInstance().getRequestQueue(),BitmapCache.getInstance());
        setContentView(R.layout.activity_timeline_comment);
        mTab = (RadioGroup) findViewById(R.id.title_container);
        mCommentContainer = (LinearLayout) findViewById(R.id.comment_container);
        mBottomContainer=findViewById(R.id.bottom_container);
        mLikeCount=(CheckBox)findViewById(R.id.like);
        mUserPhotos = (LinearLayout) findViewById(R.id.user_photos);
        mListView = (ListView) findViewById(R.id.listView);
        mBtnSendText=findViewById(R.id.btn_sendtext);
        mEdContent=(EditText)findViewById(R.id.ed_content);
        ViewCompat.setTransitionName(mTab, "title");
//        ViewCompat.setTransitionName(mLikeCount, "ic_like");
//        ViewCompat.setTransitionName(mCommentContainer, "comment_container");
        ViewCompat.setTransitionName(mBottomContainer, "bottom");
//        ViewCompat.setTransitionName(mListView, "content");

        adapter=new TimelineCommentAdapter(this);
        mListView.setAdapter(adapter);
        mBtnSendText.setOnClickListener(this);
        refresh();

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            ActivityCompat.finishAfterTransition(this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    JsonResponse listener=new JsonResponse() {
        @Override
        public void onRequest() {

        }

        @Override
        public void onError(Exception e, String url, int actionId) {
            e.printStackTrace();
        }

        @Override
        public void onSuccess(JSONObject jsonObject, String url, int actionId) {
            LogUtil.d("TimelineCommentActivity",jsonObject.toString());

            try {
                if("20000".equals(jsonObject.getString("code"))){
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONObject like = data.getJSONObject("praise");
                    mLikeCount.setText(like.getString("num"));
                    mLikeCount.setChecked("0".equals(like.getString("praiseType")));
                    if(like.has("user")) {
                        loadImgs(like.getJSONArray("user"));
                    }
                    Gson gson=new Gson();
                    if(data.has("reply")){
                        JSONArray replys = data.getJSONArray("reply");
                        ArrayList<Comment> comments=new ArrayList<Comment>();
                        for(int i=0;i<replys.length();i++){
                            JSONObject reply = replys.getJSONObject(i);
                            Comment comment= gson.fromJson(reply.toString(), Comment.class);
                            comments.add(comment);
                        }
                        adapter.reload(comments,false);
                    }
                }
                //isLiked=
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
    private void refresh(){
        RequestBuilder builder=new RequestBuilder("/hall/replylist",user.token);
        cid= getIntent().getStringExtra("cid");
        builder.addParams("cid", cid);
        builder.addParams("page",page);
        builder.post(listener);
    }
    private void loadImgs(JSONArray jsonArray){
        try {
            //user_photos
            for(int i=0;i<jsonArray.length() && i<mUserPhotos.getChildCount();i++){
                //XCRoundImageView a;
                SimpleDraweeView view=(SimpleDraweeView)((ViewGroup)mUserPhotos.getChildAt(i)).getChildAt(0);
                view.setImageURI(Uri.parse(jsonArray.getJSONObject(i).getString("headimg")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if(v==mBtnSendText){
            final Editable tmp = mEdContent.getText();
            if(TextUtils.isEmpty(tmp)){
                Toast.makeText(this,"请输入内容",Toast.LENGTH_SHORT).show();
            }else{
                try {
                    RequestBuilder builder=new RequestBuilder("/hall/addreply",user.token);
                    builder.addParams("cid",cid).addParams("type","0");
                    builder.addParams("content", Base64.encodeToString(tmp.toString().getBytes("UTF-8"),0));
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
                            try {
                                if("20000".equals(jsonObject.getString("code"))){
                                    Toast.makeText(TimelineCommentActivity.this,"回复成功",Toast.LENGTH_SHORT).show();
                                    tmp.clear();
                                    refresh();
                                }else{
                                    Toast.makeText(TimelineCommentActivity.this,"回复失败",Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(TimelineCommentActivity.this,"回复失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
