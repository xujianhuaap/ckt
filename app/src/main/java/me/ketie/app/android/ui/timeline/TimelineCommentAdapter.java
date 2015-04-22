package me.ketie.app.android.ui.timeline;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.henjue.android.common.Adapter;
import org.henjue.android.common.Holder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.ketie.app.android.R;
import me.ketie.app.android.gsonbean.reply.ReplyItem;
import me.ketie.app.android.model.UserInfo;
import me.ketie.app.android.utils.LogUtil;

/**
 * Created by android on 15-4-17.
 */
public class TimelineCommentAdapter extends RecyclerView.Adapter<TimelineCommentAdapter.ViewHodler> {
    private final int ITEM_TYPE_RTL=0;
    private final int ITEM_TYPE_LTR=1;
    private UserInfo user;
    private List<ReplyItem> datas=new ArrayList<ReplyItem>();

    public TimelineCommentAdapter(Context context) {
        user= UserInfo.read(context);
    }

    @Override
    public ViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        if(viewType==ITEM_TYPE_LTR) {
            return new ViewHodler(mInflater.inflate(R.layout.comment_item_ltr, null, false));
        }else{
            return new ViewHodler(mInflater.inflate(R.layout.comment_item_rtl, null, false));
        }
    }

    @Override
    public void onBindViewHolder(ViewHodler holder, int position) {
        ReplyItem data = datas.get(position);
        String content = data.getContent();
        boolean isText = data.isText();
        showByType(holder,data);
        LogUtil.i(TimelineCommentAdapter.class.getSimpleName(), "data:%s,isText:%s",content,isText);
        if(isText){
            holder.mContent.setText(content);
        }else{
            holder.mVoice.setText("语音");
        }
        holder.mUserPhoto.setImageURI(Uri.parse(data.getUser().getHeadimg()));
    }
    private void showByType(ViewHodler holder,ReplyItem data){
        boolean isText = data.isText();
        holder.mContent.setVisibility(isText?View.VISIBLE:View.GONE);
        holder.mVoice.setVisibility(isText?View.GONE:View.VISIBLE);
    }
    @Override
    public int getItemCount() {
        return datas.size();
    }

    protected void sort() {
        Collections.sort(this.datas, new Comparator<ReplyItem>() {
            @Override
            public int compare(ReplyItem lhs, ReplyItem rhs) {
                return lhs.getDatetime() > rhs.getDatetime() ? 1 : -1;
            }
        });
    }
    @Override
    public int getItemViewType(int position) {
        int myid = Integer.parseInt(user.uid);
        return datas.get(position).getUid()==myid?ITEM_TYPE_RTL:ITEM_TYPE_LTR;
    }
    @Override
    public long getItemId(int position) {
        return datas.get(position).getId();
    }

    public void reload(ArrayList<ReplyItem> replys, boolean append) {
        if(!append){
            this.datas.clear();
        }
        this.datas.addAll(replys);
        sort();
    }

    public class ViewHodler extends RecyclerView.ViewHolder{
        public final SimpleDraweeView mUserPhoto;
        public final TextView mContent;
        public final TextView mVoice;
        public ViewHodler(View view){
            super(view);
            mUserPhoto=(SimpleDraweeView)view.findViewById(R.id.user_photo);
            mContent=(TextView)view.findViewById(R.id.content);
            mVoice=(TextView)view.findViewById(R.id.voice);
        }
    }
}
