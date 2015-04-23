package me.ketie.app.android.view.timeline;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import me.ketie.app.android.access.UserAuth;
import me.ketie.app.android.component.BitmapCache;
import me.ketie.app.android.controller.TimelineController;
import me.ketie.app.android.model.ReplyData;
import me.ketie.app.android.model.ReplyRoot;
import me.ketie.app.android.model.praise.PraiseItem;
import me.ketie.app.android.model.praise.PraiseUser;
import me.ketie.app.android.model.reply.ReplyItem;
import me.ketie.app.android.network.JsonResponseListener;
import me.ketie.app.android.network.RequestBuilder;
import me.ketie.app.android.utils.LogUtil;
import me.ketie.app.android.widget.HBaseLinearLayoutManager;
import me.ketie.app.android.widget.OnRecyclerViewScrollListener;
import me.ketie.app.android.widget.OnRecyclerViewScrollLocationListener;

public class TimelineReplyActivity extends ActionBarActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String LOG_TAG = TimelineReplyActivity.class.getSimpleName();
    private RadioGroup mTab;
    private LinearLayout mCommentContainer;
    private CheckBox mLikeCount;
    private UserAuth user;
    private int page = 1;
    private LinearLayout mUserPhotos;
    private ImageLoader loader;
    private RecyclerView mRecycleView;
    private TimelineReplyAdapter adapter;
    private View mBottomContainer;
    private View mBtnSendText;
    private EditText mEdContent;
    private String cid;
    private TextView mBtnVoice;
    private File filename;
    private MediaRecorder mRecorder;
    private ToggleButton mBtnSwitch;
    private PopupWindow popupWindow;
    private boolean append = false;
    private ImageView mVoiceHint;
    private View mPopRecord;
    private HBaseLinearLayoutManager mLayoutManager;
    SwipeRefreshLayout refreshLayout;
    int scrollToPosition;
    private View mType1;
    private View mType2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filename = new File(getFilesDir(), System.currentTimeMillis() + ".caf");
        user = UserAuth.read(this);
        loader = new ImageLoader(RequestManager.getInstance().getRequestQueue(), BitmapCache.getInstance());
        setContentView(R.layout.activity_timeline_reply);
        mTab = (RadioGroup) findViewById(R.id.title_container);
        mType1 = findViewById(R.id.type_1);
        mType2 = findViewById(R.id.type_2);
        mCommentContainer = (LinearLayout) findViewById(R.id.comment_container);
        mBottomContainer = findViewById(R.id.bottom_container);
        mLikeCount = (CheckBox) findViewById(R.id.like);
        mUserPhotos = (LinearLayout) findViewById(R.id.user_photos);
        mRecycleView = (RecyclerView) findViewById(R.id.listView);
        mBtnSendText = findViewById(R.id.btn_sendtext);
        mEdContent = (EditText) findViewById(R.id.ed_content);
        mBtnVoice = (TextView) findViewById(R.id.btn_voice);
        mBtnSwitch = (ToggleButton) findViewById(R.id.btn_switch);
        mRecycleView.setHasFixedSize(true);
        adapter = new TimelineReplyAdapter(this);
        mRecycleView.setAdapter(adapter);
        mBtnSwitch.setOnCheckedChangeListener(this);
        mBtnSendText.setOnClickListener(this);
        mBtnVoice.setOnTouchListener(new OnTouchRecorder());
        mPopRecord = getLayoutInflater().inflate(R.layout.pop_record, null, false);
        mVoiceHint = (ImageView) mPopRecord.findViewById(R.id.voice_hint);
        mLayoutManager = new HBaseLinearLayoutManager(this);
        ViewCompat.setTransitionName(mTab, "title");
        mRecycleView.setLayoutManager(mLayoutManager);
        mLayoutManager.setOnRecyclerViewScrollLocationListener(new OnRecyclerViewScrollLocationListener() {
            @Override
            public void onTopWhenScrollIdle(RecyclerView recyclerView) {
                page++;
                append = true;
                scrollToPosition = mLayoutManager.findFirstVisibleItemPosition();
                refresh();
            }

            @Override
            public void onBottomWhenScrollIdle(RecyclerView recyclerView) {

            }
        });
        mLayoutManager.addScrollListener(new OnRecyclerViewScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //当下拉刷新的时候
                //refreshLayout.setEnabled(mLayoutManager.findFirstCompletelyVisibleItemPosition() == 0);
            }
        });
        mEdContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mBtnSendText.setEnabled(!TextUtils.isEmpty(s));
            }
        });
        refresh();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        page=1;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActivityCompat.finishAfterTransition(this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void refresh() {
        cid = getIntent().getStringExtra("cid");
        TimelineController.listReply(user.token,Integer.parseInt(cid),page,listener);
    }

    private void showPraisePhoto(ArrayList<PraiseUser> users) {
        int i = 0;
        for (PraiseUser user : users) {
            if (i == mUserPhotos.getChildCount()) {
                break;
            }
            SimpleDraweeView view = (SimpleDraweeView) ((ViewGroup) mUserPhotos.getChildAt(i)).getChildAt(0);
            view.setImageURI(Uri.parse(user.getHeadimg()));
            i++;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnSendText) {
            final Editable tmp = mEdContent.getText();
            if (TextUtils.isEmpty(tmp)) {
                Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
            } else {
                postServer(tmp.toString(), 0);
            }
        }
    }

    private void postServer(String content, int timelength) {
            JsonResponseListener replyListener = new JsonResponseListener() {
                @Override
                public void onRequest() {

                }

                @Override
                public void onError(Exception e, String url, int actionId) {
                    e.printStackTrace();
                }

                @Override
                public void onSuccess(JSONObject jsonObject, String url, int actionId) {
                    LogUtil.d(LOG_TAG, jsonObject.toString());
                    try {
                        if ("20000".equals(jsonObject.getString("code"))) {
                            Toast.makeText(TimelineReplyActivity.this, "回复成功", Toast.LENGTH_SHORT).show();
                            if (mBtnSwitch.isChecked()) {
                                filename.deleteOnExit();
                            } else {
                                mEdContent.getText().clear();
                            }
                            page = 1;
                            append = false;
                            refresh();
                        } else {
                            filename.deleteOnExit();
                            Toast.makeText(TimelineReplyActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        filename.deleteOnExit();
                        e.printStackTrace();
                        Toast.makeText(TimelineReplyActivity.this, "回复失败", Toast.LENGTH_SHORT).show();
                    }
                }
            };
            if (mBtnSwitch.isChecked()) {
                TimelineController.addReply(user.token,Integer.parseInt(cid),filename,timelength / 1000,replyListener);
            } else {
                TimelineController.addReply(user.token,Integer.parseInt(cid),content,replyListener);
            }

    }

    private void stopRecord(boolean delete) {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        if (delete) {
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isChecked) {
            inputmanger.hideSoftInputFromWindow(mEdContent.getWindowToken(), 0);
            mEdContent.setVisibility(View.GONE);
            mBtnSendText.setVisibility(View.INVISIBLE);
            mBtnVoice.setVisibility(View.VISIBLE);
        } else {
            mEdContent.setVisibility(View.VISIBLE);

            mBtnVoice.setVisibility(View.GONE);
        }
    }
    private class OnTouchRecorder implements View.OnTouchListener {
        private float downY = 0;
        private float upY = 0;

        @Override
        public boolean onTouch(View v, MotionEvent e) {
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mBtnVoice.setText(R.string.record_ing);
                    downY = e.getRawY();
                    startRecord();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float tmpY = e.getRawY();
                    if (Math.abs(downY - tmpY) > 400) {
                        mBtnVoice.setText(R.string.record_cancel);
                        if(popupWindow==null || !popupWindow.isShowing()){
                            showVoiceCancelPop();
                        }
                    } else {
                        mBtnVoice.setText(R.string.record_ing);
                        if(popupWindow!=null && popupWindow.isShowing()){
                            hideVoiceCancelPop();
                        }
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    LogUtil.d("OnTouchRecorder", "ACTION_CANCEL");
                    mBtnVoice.setText("按住录音");
                    downY = 0;
                    upY = 0;
                    stopRecord(true);
                    break;
                case MotionEvent.ACTION_UP:
                    LogUtil.d("OnTouchRecorder", "ACTION_UP");
                    mBtnVoice.setText("按住录音");
                    upY = e.getRawY();
                    if (Math.abs(downY - upY) > 400) {
                        Toast.makeText(v.getContext(), "取消录音", Toast.LENGTH_SHORT).show();
                        if(popupWindow!=null && popupWindow.isShowing()){
                            hideVoiceCancelPop();
                        }
                        stopRecord(true);
                        downY = 0;
                        upY = 0;
                    } else {
                        stopRecord(false);
                        int timelength = getDuration();
                        postServer(filename.toString(), timelength);
                        downY = 0;
                        upY = 0;
                    }
                    break;

            }
            return true;
        }
    }

    private int getDuration() {
        MediaPlayer mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(filename.toString());
            mPlayer.prepare();
            int voiceTime = mPlayer.getDuration();
            return voiceTime;
        } catch (IOException e) {
            Log.e(LOG_TAG, "播放失败", e);
        } finally {
            mPlayer.release();
            mPlayer = null;
        }
        return 0;
    }

    private View getRootView() {
        return ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
    }

    private void showVoiceCancelPop(){
        if (popupWindow == null) {
            popupWindow = new PopupWindow(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.setContentView(mPopRecord);
            popupWindow.setBackgroundDrawable(new ColorDrawable(0x0));
            popupWindow.setFocusable(false);
        }
        popupWindow.showAtLocation(getRootView(), Gravity.CENTER, 0, 0);
    }
    private void hideVoiceCancelPop(){
        popupWindow.dismiss();
        popupWindow=null;
    }

    JsonResponseListener listener = new JsonResponseListener() {
        @Override
        public void onRequest() {

        }

        @Override
        public void onError(Exception e, String url, int actionId) {
            e.printStackTrace();
        }

        @Override
        public void onSuccess(JSONObject jsonObject, String url, int actionId) {
            LogUtil.d("TimelineCommentActivity", jsonObject.toString());
            ReplyRoot replyRoot = new Gson().fromJson(jsonObject.toString(), ReplyRoot.class);
            if (replyRoot.getCode() == 20000) {
                ReplyData data = replyRoot.getData();
                PraiseItem praise = data.getPraise();
                ArrayList<ReplyItem> replys = data.getReply();
                if (praise != null) {
                    mLikeCount.setText(praise.getNum() + "");
                    mLikeCount.setChecked(praise.getPraiseType() == 0);
                    if (page == 1) {
                        showPraisePhoto(praise.getUser());
                    }
                }
                if (replys != null && replys.size() > 0) {
                    adapter.reload(replys, append);

                }

                if (page == 1) {
                    adapter.notifyDataSetChanged();
                    mRecycleView.scrollToPosition(adapter.getItemCount() - 1);
                }else{
                    adapter.notifyItemRangeInserted(0, replys.size());
                }
            }

        }
    };
}
