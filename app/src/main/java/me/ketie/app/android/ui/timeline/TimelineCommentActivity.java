package me.ketie.app.android.ui.timeline;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.http.RequestManager;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
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

    private static final String LOG_TAG = TimelineCommentActivity.class.getSimpleName();
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
    private TextView mBtnVoice;
    private File filename;
    private MediaRecorder mRecorder;
    private TextView mBtnSwitch;
    private CommentType commentType=CommentType.TEXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filename=new File(getFilesDir(),System.currentTimeMillis()+".caf");
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
        mBtnVoice=(TextView)findViewById(R.id.btn_voice);
        mBtnSwitch=(TextView)findViewById(R.id.btn_switch);

        ViewCompat.setTransitionName(mTab, "title");
        ViewCompat.setTransitionName(mBottomContainer, "bottom");

        adapter=new TimelineCommentAdapter(this);
        mListView.setAdapter(adapter);
        mBtnSwitch.setOnClickListener(this);
        mBtnVoice.setOnTouchListener(new OnTouchRecorder());
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
                postServer(tmp.toString(),0);
            }
        }else if(v==mBtnSwitch){
            if(commentType==CommentType.TEXT) {
                mBtnSwitch.setText("键盘");
                mEdContent.setVisibility(View.GONE);
                mBtnSendText.setVisibility(View.GONE);
                mBtnVoice.setVisibility(View.VISIBLE);
                commentType=CommentType.VOICE;
            }else{
                mBtnSwitch.setText("录音");
                mEdContent.setVisibility(View.VISIBLE);
                mBtnSendText.setVisibility(View.VISIBLE);
                mBtnVoice.setVisibility(View.GONE);
                commentType=CommentType.TEXT;
            }
        }
    }
    private void postServer(String content,int timelength){
            try{
            RequestBuilder builder=new RequestBuilder("/hall/addreply",user.token);
            if(commentType==CommentType.TEXT) {
                builder.addParams("cid",cid).addParams("type","0");
                builder.addParams("content", Base64.encodeToString(content.getBytes("UTF-8"), 0));
            }else{
                builder.addParams("cid",cid).addParams("type","1");
                builder.addParams("timeleng", timelength/1000);
                builder.addParams("sound",filename);
            }
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
                    LogUtil.d(LOG_TAG,jsonObject.toString());
                    try {
                        if("20000".equals(jsonObject.getString("code"))){
                            Toast.makeText(TimelineCommentActivity.this,"回复成功",Toast.LENGTH_SHORT).show();
                            if(commentType==CommentType.TEXT) {
                                mEdContent.getText().clear();
                            }else{
                                filename.deleteOnExit();
                            }
                            refresh();
                        }else{
                            filename.deleteOnExit();
                            Toast.makeText(TimelineCommentActivity.this,jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        filename.deleteOnExit();
                        e.printStackTrace();
                        Toast.makeText(TimelineCommentActivity.this,"回复失败",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private void stopRecord(boolean delete) {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        if(delete){
            filename.deleteOnExit();
        }
    }

    private void startRecord() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(filename.toString());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        mRecorder.start();
    }


    private enum CommentType{
        TEXT,VOICE
    }
    private class OnTouchRecorder implements View.OnTouchListener{
        private float downY=0;
        private float upY=0;
        @Override
        public boolean onTouch(View v, MotionEvent e) {
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    LogUtil.d("OnTouchRecorder","ACTION_DOWN");
                    downY=e.getRawY();
                    startRecord();

                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_CANCEL:
                    LogUtil.d("OnTouchRecorder","ACTION_CANCEL");
                    downY=0;
                    upY=0;
                    stopRecord(true);
                    break;
                case MotionEvent.ACTION_UP:
                    LogUtil.d("OnTouchRecorder","ACTION_UP");
                    upY=e.getRawY();
                    if(Math.abs(downY-upY)>400){
                        Toast.makeText(v.getContext(),"取消录音",Toast.LENGTH_SHORT).show();
                        stopRecord(true);
                        downY=0;
                        upY=0;
                    }else{
                        stopRecord(false);
                        int timelength = getDuration();
                        postServer(filename.toString(),timelength);
                        downY=0;
                        upY=0;
                    }
                    break;

            }
            return true;
        }
    }

    private int getDuration() {
        MediaPlayer mPlayer = new MediaPlayer();
        try{
            mPlayer.setDataSource(filename.toString());
            mPlayer.prepare();
            int voiceTime = mPlayer.getDuration();
            return voiceTime;
        }catch(IOException e){
            Log.e(LOG_TAG,"播放失败",e);
        }finally {
            mPlayer.release();
            mPlayer=null;
        }
        return 0;
    }
}
