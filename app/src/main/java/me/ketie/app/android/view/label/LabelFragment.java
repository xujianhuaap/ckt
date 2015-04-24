package me.ketie.app.android.view.label;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import org.json.JSONObject;

import java.security.Key;
import java.util.Timer;
import java.util.TimerTask;

import me.ketie.app.android.R;
import me.ketie.app.android.network.JsonResponseListener;
import me.ketie.app.android.utils.LogUtil;

public class LabelFragment extends Fragment implements TextWatcher, View.OnClickListener, View.OnKeyListener, JsonResponseListener {
    public static final String BUNDLE_TITLE = "title";
    private String mTitle = "DefaultValue";
    private EditText mEditSearch;
    private View mBtnClear;
    private final Timer timer=new Timer();
    private RequestTimer mRequestTask;
    protected final String LOG_TAG=LabelFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_label,null,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEditSearch=(EditText)view.findViewById(R.id.edit_search);
        mBtnClear=view.findViewById(R.id.btn_search_clear);
        mEditSearch.addTextChangedListener(this);
        mBtnClear.setOnClickListener(this);
        mEditSearch.setOnKeyListener(this);
    }

    public static LabelFragment newInstance() {
        return new LabelFragment();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        boolean empty = TextUtils.isEmpty(s);
        mBtnClear.setVisibility(empty ?View.GONE:View.VISIBLE);
        if(!empty) {
            //以下代码延时发起搜索请求
            if (mRequestTask != null) {
                mRequestTask.cancel();
                mRequestTask = null;
            }
            mRequestTask = new RequestTimer(s.toString(),this);
            timer.schedule(mRequestTask, 3000);
        }

    }

    @Override
    public void onClick(View v) {
        if(v==mBtnClear){
            mEditSearch.setText("");
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        LogUtil.i(LOG_TAG, "KeyCode:%d",keyCode);
        return false;
    }

    @Override
    public void onSuccess(JSONObject json, String url, int actionId) {

    }

    @Override
    public void onRequest() {

    }

    @Override
    public void onError(Exception errorMsg, String url, int actionId) {

    }

    /**
     * 延时请求任务类
     */
    private final class RequestTimer extends TimerTask{
        private final String keyWorld;

        public RequestTimer(String keyworld,JsonResponseListener listener) {
            this.keyWorld=keyworld;
        }

        @Override
        public void run() {

        }
    }
}
