package me.ketie.app.android.view.auth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsMessage;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.ketie.app.android.KApplication;
import me.ketie.app.android.R;
import me.ketie.app.android.access.UserAuth;
import me.ketie.app.android.common.AuthRedirect;
import me.ketie.app.android.constants.LoginType;
import me.ketie.app.android.controller.AuthController;
import me.ketie.app.android.network.JsonResponseListener;
import me.ketie.app.android.utils.LogUtil;

public class InputValidataActivity extends ActionBarActivity implements View.OnClickListener {
    private android.content.IntentFilter filter;
    private final int maxTime = 60;
    private int timeout = maxTime;
    private TextView mTimeout;
    private EditText mCode;


    //android.provider.Telephony.SMS_RECEIVED
    private ValidataReceiver receiver;

    private KApplication app;
    private Button mBtnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (KApplication) getApplication();
        setContentView(R.layout.activity_input_validata);
        receiver = new ValidataReceiver(this);
        filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        mTimeout = (TextView) findViewById(R.id.retry);
        mCode = (EditText) findViewById(R.id.validata_code);
        mBtnNext = (Button) findViewById(R.id.btn_next);
        mBtnNext.setOnClickListener(this);
        mTimeout.setEnabled(false);
        mTimeHandler.sendEmptyMessage(0);


    }

    public void reSendCode() {
        AuthController.getValiCode(getIntent().getStringExtra("mobile"), new JsonResponseListener() {
            @Override
            public void onRequest() {

            }

            @Override
            public void onError(Exception e, String url, int actionId) {
                e.printStackTrace();
            }

            @Override
            public void onSuccess(JSONObject json, String url, int actionId) {
                try {
                    if ("20000".equals(json.getString("code"))) {
                        Toast.makeText(InputValidataActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                    } else if ("55000".equals(json.getString("code"))) {
                        Toast.makeText(InputValidataActivity.this, json.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void receiverSms(String validateCode) {
        if (mCode.getText() == null || mCode.getText().toString().equals("")) {
            mCode.setText(validateCode);
        }
    }

    private final Handler mTimeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                reSendCode();
            }
            if (timeout > 0) {
                mTimeout.setEnabled(false);
                mTimeHandler.sendEmptyMessageDelayed(0, 1000);
            } else {
                timeout = 0;
                mTimeout.setEnabled(true);
            }
            updateTimeout();
            timeout--;
        }
    };

    @Override
    public void onClick(View v) {
        mBtnNext.setEnabled(false);
        AuthController.auth(this, getIntent().getStringExtra("mobile"), mCode.getText().toString(), new JsonResponseListener() {
            @Override
            public void onRequest() {

            }

            @Override
            public void onError(Exception e, String url, int actionId) {
                e.printStackTrace();
                mBtnNext.setEnabled(true);
            }

            @Override
            public void onSuccess(JSONObject json, String url, int actionId) {
                try {
                    mBtnNext.setEnabled(true);
                    if ("20000".equals(json.getString("code"))) {
                        JSONObject data = json.getJSONObject("data");
                        UserAuth userAuth = new UserAuth(null, LoginType.DEFAULT, data.getString("token"), data.getString("uid"), data.getString("nickname"), data.getString("headimg"));
                        userAuth.write(InputValidataActivity.this);
                        if (!TextUtils.isEmpty(userAuth.token)) {
                            Toast.makeText(InputValidataActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                            AuthRedirect.toHome(InputValidataActivity.this);
                            finish();
                        } else {
                            Toast.makeText(InputValidataActivity.this, R.string.login_faile, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(InputValidataActivity.this, R.string.login_faile, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class ValidataReceiver extends BroadcastReceiver {
        private final InputValidataActivity activity;

        ValidataReceiver(InputValidataActivity activity) {
            this.activity = activity;
        }

        //验证码：1916，请输入以上验证码完成步骤，欢迎体验贴纸社交应用【创可贴】
        Pattern pattern = Pattern.compile("^验证码：(\\d{4})，请输入以上验证码完成步骤，欢迎体验贴纸社交应用【创可贴】$");

        public void onReceive(Context context, Intent intent) {
            SmsMessage[] msg = null;
            StringBuffer sb = new StringBuffer();
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                //StringBuilder buf = new StringBuilder();
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdusObj = (Object[]) bundle.get("pdus");
                    msg = new SmsMessage[pdusObj.length];
                    for (int i = 0; i < pdusObj.length; i++) {
                        msg[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        sb.append(msg[i].getMessageBody());
                    }

                    LogUtil.i("ValidataReceiver", sb.toString());
                    Matcher m = pattern.matcher(sb.toString());
                    if (m.find()) {
                        String code = m.group(1);
                        LogUtil.i("ValidataReceiver", "ValidateCode:" + code);
                        activity.receiverSms(code);
                    } else {
                        LogUtil.i("ValidataReceiver", "不能提取出验证码");
                    }
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (timeout > 0 && keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        if (timeout <= 0) {
            setResult(RESULT_OK);
        }
        super.finish();
    }

    private void updateTimeout() {
        final String htmlLinkText;
        if (timeout == 0) {
            htmlLinkText = "没有收到验证码?<a href=\"#\">重新发送</a>";
        } else {
            htmlLinkText = String.format("没有收到验证码?%d秒后<a href=\"#\">重新发送</a>", timeout);
        }

        mTimeout.setText(Html.fromHtml(htmlLinkText));
        mTimeout.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence text = mTimeout.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) mTimeout.getText();
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            style.clearSpans();//should clear old spans
            for (URLSpan url : urls) {
                ClickableSpan myURLSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        timeout = maxTime;
                        mTimeHandler.sendEmptyMessage(1);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setColor(ds.linkColor);
                        ds.setUnderlineText(false);
                        ds.clearShadowLayer();
                    }
                };
                style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            mTimeout.setText(style);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, filter);
    }
}
