package me.ketie.app.android.ui.timeline;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.facebook.drawee.view.SimpleDraweeView;

import org.henjue.android.common.Adapter;
import org.henjue.android.common.Holder;

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
public class TimelineAdapter extends Adapter<Timeline,TimelineAdapter.ViewHolder> {

    private final ImageLoader loader;
    private OnHandlerLikeListener handlerLikeListener;
    public TimelineAdapter(Context context,OnHandlerLikeListener handlerLikeListener,ImageLoader loader) {
        super(context);
        this.loader=loader;
        this.handlerLikeListener=handlerLikeListener;
    }
    private View.OnClickListener handlerLikeCheckListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(handlerLikeListener!=null){
                handlerLikeListener.onAction(((Timeline)view.getTag()).getId());
                CheckBox chbox = (CheckBox) view;
                int count=Integer.parseInt(chbox.getText().toString());
                if(chbox.isChecked()) {
                    count++;
                }else{
                    count--;
                }
                chbox.setText(String.valueOf(count));
            }
        }
    };
    @Override
    protected ViewHolder newHolder(int position, View view, Timeline data) {
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mLike.setOnClickListener(handlerLikeCheckListener);
        viewHolder.mLike.setTag(data);
        return viewHolder;
    }

    @Override
    protected View newView(int position, LayoutInflater inflater, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.fragment_timeline_item, null, false);
        return view;
    }

    @Override
    protected void bindData(int position, ViewHolder holder, Timeline data) {
        holder.img.setImageURI(Uri.parse(data.getImgurl()));
        loader.get(data.getUser().getHeadimg(),ImageLoader.getImageListener(holder.userPhone,R.drawable.default_pic,R.drawable.default_pic));
        holder.mNickname.setText(data.getUser().getNickname());
        boolean checked = !"0".equals(data.getPraiseType());
        holder.mLike.setChecked(checked);
        holder.mLike.setText(data.getPraiseNum());
        holder.mComment.setText(data.getReplynum());
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }
    public interface OnHandlerLikeListener{
        public void onAction(String cid);
    }
    public static final class ViewHolder implements Holder{
        public final SimpleDraweeView img;
        public final XCRoundImageView userPhone;
        public final TextView mNickname;
        public final CheckBox mLike;
        public final TextView mComment;
        ViewHolder(View view){
            img=(SimpleDraweeView)view.findViewById(R.id.img);
            userPhone=(XCRoundImageView)view.findViewById(R.id.user_photo);
            mNickname=(TextView)view.findViewById(R.id.user_nickname);
            mLike=(CheckBox)view.findViewById(R.id.like);
            mComment=(TextView)view.findViewById(R.id.comment);
        }
    }
}
