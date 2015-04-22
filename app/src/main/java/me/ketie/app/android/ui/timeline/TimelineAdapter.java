package me.ketie.app.android.ui.timeline;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.ketie.app.android.R;
import me.ketie.app.android.gsonbean.Timeline;
import me.ketie.app.android.view.XCRoundImageView;

/**
 * <pre>
 * Description:
 * 2015/4/1315:19
 * PullToRefreshView
 *
 * @author henjue
 *         email:henjue@gmail.com
 * @version 1.0
 *          </pre>
 */
public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {
    private List<Timeline> datas=new ArrayList<Timeline>();
    private final ImageLoader loader;
    private OnHandlerLikeListener handlerLikeListener;

    public TimelineAdapter(OnHandlerLikeListener handlerLikeListener, ImageLoader loader) {
        this.loader = loader;
        this.handlerLikeListener = handlerLikeListener;
    }

    public TimelineItemClickListener getTimelineItemClickListener() {
        return timelineItemClickListener;
    }

    public void setTimelineItemClickListener(TimelineItemClickListener timelineItemClickListener) {
        this.timelineItemClickListener = timelineItemClickListener;
    }


    private TimelineItemClickListener timelineItemClickListener;
    @Override
    public int getItemCount() {
        return datas.size();
    }

    protected void sort() {
        Collections.sort(this.datas, new Comparator<Timeline>() {
            @Override
            public int compare(Timeline lhs, Timeline rhs) {
                return lhs.getDatetime() < rhs.getDatetime() ? 1 : -1;
            }
        });
    }
    public void reload(ArrayList<Timeline> timelines, boolean append) {
        if(!append){
            this.datas.clear();
        }
        this.datas.addAll(timelines);
//        sort();
    }
    private View.OnClickListener handlerLikeCheckListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (handlerLikeListener != null) {
                handlerLikeListener.onAction(((Timeline) view.getTag()).getId());
                CheckBox chbox = (CheckBox) view;
                int count = Integer.parseInt(chbox.getText().toString());
                if (chbox.isChecked()) {
                    count++;
                } else {
                    count--;
                }
                chbox.setText(String.valueOf(count));
            }
        }
    };

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        ViewHolder viewHolder = new ViewHolder(mInflater.inflate(R.layout.fragment_timeline_item, null, false),timelineItemClickListener);
        viewHolder.mLike.setOnClickListener(handlerLikeCheckListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Timeline data = datas.get(position);
        holder.mLike.setTag(data);
        holder.img.setImageURI(Uri.parse(data.getImgurl()));
        loader.get(data.getUser().getHeadimg(), ImageLoader.getImageListener(holder.userPhone, R.drawable.default_pic, R.drawable.default_pic));
        holder.mNickname.setText(data.getUser().getNickname());
        boolean checked = !"0".equals(data.getPraiseType());
        holder.mLike.setChecked(checked);
        holder.mLike.setText(data.getPraiseNum());
        holder.mComment.setText(data.getReplynum());
        holder.bindData(data);
    }


    public interface OnHandlerLikeListener {
        public void onAction(String cid);
    }

    interface TimelineItemClickListener{
        public void onItemClick(ViewHolder holder,Timeline data,int postion);
    }
    public static final class ViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final SimpleDraweeView img;
        public final XCRoundImageView userPhone;
        public final TextView mNickname;
        public final CheckBox mLike;
        public final TextView mComment;
        public final View mCommentContainer;
        private TimelineItemClickListener listener;

        public Timeline getData() {
            return data;
        }

        private Timeline data;

        ViewHolder(View view,TimelineItemClickListener listener) {
            super(view);
            if(listener!=null) {
                view.setOnClickListener(this);
                this.listener = listener;
            }
            img = (SimpleDraweeView) view.findViewById(R.id.img);
            userPhone = (XCRoundImageView) view.findViewById(R.id.user_photo);
            mNickname = (TextView) view.findViewById(R.id.user_nickname);
            mLike = (CheckBox) view.findViewById(R.id.like);
            mComment = (TextView) view.findViewById(R.id.comment);
            mCommentContainer=view.findViewById(R.id.comment_container);
        }
        public void bindData( Timeline data){
            this.data=data;
        }
        @Override
        public void onClick(View v) {
            listener.onItemClick(this,data,getPosition());
        }
    }
}
