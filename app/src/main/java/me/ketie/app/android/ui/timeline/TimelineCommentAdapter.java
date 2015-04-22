package me.ketie.app.android.ui.timeline;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.henjue.android.common.Adapter;
import org.henjue.android.common.Holder;

import java.util.Collections;
import java.util.Comparator;

import me.ketie.app.android.R;
import me.ketie.app.android.gsonbean.reply.ReplyItem;
import me.ketie.app.android.model.UserInfo;
import me.ketie.app.android.utils.LogUtil;

/**
 * Created by android on 15-4-17.
 */
public class TimelineCommentAdapter extends Adapter<ReplyItem,TimelineCommentAdapter.ViewHodler> {
    private final int ITEM_TYPE_RTL=0;
    private final int ITEM_TYPE_LTR=1;
    private final int ITEM_TYPE_COUNT=2;
    private UserInfo user;
    public TimelineCommentAdapter(Context context) {
        super(context);
        user= UserInfo.read(context);
    }

    @Override
    protected void sort() {
        Collections.sort(this.datas, new Comparator<ReplyItem>() {
            @Override
            public int compare(ReplyItem lhs, ReplyItem rhs) {
                return lhs.getDatetime() > rhs.getDatetime() ? 1 : -1;
            }
        });
    }
    @Override
    protected ViewHodler newHolder(int position, View view, ReplyItem data) {
        return new ViewHodler(view,data);
    }

    @Override
    protected View newView(int position, LayoutInflater inflater, View convertView, ViewGroup parent) {
        if(getItemViewType(position)==ITEM_TYPE_LTR) {
            return mInflater.inflate(R.layout.comment_item_ltr, null, false);
        }else{
            return mInflater.inflate(R.layout.comment_item_rtl, null, false);
        }
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        int myid = Integer.parseInt(user.uid);
        return getItem(position).getUid()==myid?ITEM_TYPE_RTL:ITEM_TYPE_LTR;
    }

    @Override
    protected void bindData(int position, ViewHodler holder, ReplyItem data) {
        String content = data.getContent();
        boolean isText = data.isText();
        LogUtil.i(TimelineCommentAdapter.class.getSimpleName(), "count:%d,data:%s,isText:%s",getCount(),content,isText);
        if(isText){
            holder.mContent.setText(content);
        }else{
            holder.mVoice.setText("语音");
        }
        holder.mUserPhoto.setImageURI(Uri.parse(data.getUser().getHeadimg()));
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public class ViewHodler implements Holder{
        public final SimpleDraweeView mUserPhoto;
        public final TextView mContent;
        public final TextView mVoice;
        public ViewHodler(View view, ReplyItem data){
            mUserPhoto=(SimpleDraweeView)view.findViewById(R.id.user_photo);
            mContent=(TextView)view.findViewById(R.id.content);
            mVoice=(TextView)view.findViewById(R.id.voice);
            mContent.setVisibility(data.isText()?View.VISIBLE:View.GONE);
            mVoice.setVisibility(data.isText()?View.GONE:View.VISIBLE);
        }
    }
}
