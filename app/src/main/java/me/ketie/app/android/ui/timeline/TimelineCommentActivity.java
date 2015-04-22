package me.ketie.app.android.ui.timeline;


import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.http.RequestManager;
import com.android.volley.toolbox.ImageLoader;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import me.ketie.app.android.R;
import me.ketie.app.android.common.BitmapCache;
import me.ketie.app.android.gsonbean.ReplyData;
import me.ketie.app.android.gsonbean.ReplyRoot;
import me.ketie.app.android.gsonbean.praise.PraiseItem;
import me.ketie.app.android.gsonbean.praise.PraiseUser;
import me.ketie.app.android.gsonbean.reply.ReplyItem;
import me.ketie.app.android.model.UserInfo;
import me.ketie.app.android.net.JsonResponse;
import me.ketie.app.android.net.RequestBuilder;
import me.ketie.app.android.utils.LogUtil;

public class TimelineCommentActivity extends ActionBarActivity implements View.OnClickListener, AbsListView.OnScrollListener {

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
    private PopupWindow popupWindow;
    private boolean append=false;
    private TextView mVoiceHint;
    private View mPopRecord;

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
        mBtnSendText.setOnClickListener(this);
        mBtnVoice.setOnTouchListener(new OnTouchRecorder());
        refresh();
        mListView.setOnScrollListener(this);
        mPopRecord = getLayoutInflater().inflate(R.layout.pop_record, null, false);
        mVoiceHint=(TextView)mPopRecord.findViewById(R.id.voice_hint);

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            ActivityCompat.finishAfterTransition(this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void refresh(){
        RequestBuilder builder=new RequestBuilder("/hall/replylist",user.token);
        cid= getIntent().getStringExtra("cid");
        builder.addParams("cid", cid);
        builder.addParams("page",page);
        builder.post(listener);
    }
    private void showPraisePhoto(ArrayList<PraiseUser> users){
            int i=0;
            for(PraiseUser user:users){
                if(i==mUserPhotos.getChildCount()){
                    break;
                }
                SimpleDraweeView view=(SimpleDraweeView)((ViewGroup)mUserPhotos.getChildAt(i)).getChildAt(0);
                view.setImageURI(Uri.parse(user.getHeadimg()));
                i++;
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
                            page=1;
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

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//        int lastItem = firstVisibleItem + visibleItemCount;
//        if(lastItem==totalItemCount) {
//            View lastItemView = (View) mListView.getChildAt(mListView.getChildCount() - 1);
//            if ((mListView.getBottom()) == lastItemView.getBottom()) {
//            }
//        }
        if(firstVisibleItem==0){
            View fastItemView = (View) mListView.getChildAt(0);
            if (fastItemView!=null && (mListView.getTop()) == fastItemView.getTop()) {
                page++;
                append=true;
                refresh();
            }
        }
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
                    mVoiceHint.setText(R.string.record_ing);
                    mVoiceHint.setTextColor(0xffffff);
                    toggleVoicePop();
                    downY=e.getRawY();
                    startRecord();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float tmpY=e.getRawY();
                    if(Math.abs(downY-tmpY)>400) {
                        mVoiceHint.setText(R.string.record_cancel);
                        mVoiceHint.setTextColor(0xffff0000);
                    }else{
                        mVoiceHint.setText(R.string.record_ing);
                        mVoiceHint.setTextColor(0xffffffff);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    LogUtil.d("OnTouchRecorder","ACTION_CANCEL");
                    downY=0;
                    upY=0;
                    stopRecord(true);
                    toggleVoicePop();
                    break;
                case MotionEvent.ACTION_UP:
                    LogUtil.d("OnTouchRecorder","ACTION_UP");
                    upY=e.getRawY();
                    toggleVoicePop();
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
    private View getRootView()
    {
        return ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
    }
    private void toggleVoicePop(){
        if(popupWindow==null) {
            popupWindow = new PopupWindow(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.setContentView(mPopRecord);
            popupWindow.setBackgroundDrawable(new ColorDrawable(0x0));
            popupWindow.setFocusable(false);
            popupWindow.showAtLocation(getRootView(), Gravity.CENTER,0,0);
        }else{
            popupWindow.dismiss();
            popupWindow=null;
        }
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
            ReplyRoot replyRoot=new Gson().fromJson(jsonObject.toString(), ReplyRoot.class);
            if(replyRoot.getCode()==20000){
                ReplyData data = replyRoot.getData();
                PraiseItem praise = data.getPraise();
                ArrayList<ReplyItem> replys = data.getReply();
                if(praise!=null){
                    mLikeCount.setText(praise.getNum()+"");
                    mLikeCount.setChecked(praise.getPraiseType()==0);
                    if(page==1) {
                        showPraisePhoto(praise.getUser());
                    }
                }
                if(replys!=null && replys.size()>0){
                    adapter.reload(replys,append);
                }
            }

        }
    };
}
