package me.ketie.app.android.ui.timeline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;

import org.henjue.android.common.Adapter;
import org.henjue.android.common.Holder;

import me.ketie.app.android.R;
import me.ketie.app.android.gsonbean.Timeline;

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

    public TimelineAdapter(Context context, ImageLoader loader) {
        super(context);
        this.loader=loader;
    }

    @Override
    protected ViewHolder newHolder(int position, View view, Timeline data) {
        return new ViewHolder(view);
    }

    @Override
    protected View newView(int position, LayoutInflater inflater, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.timeline_item, null, false);
        return view;
    }

    @Override
    protected void bindData(int position, ViewHolder holder, Timeline data) {
        loader.get(data.getImgurl(),ImageLoader.getImageListener(holder.img,R.drawable.default_pic,R.drawable.default_pic));
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }
    public static final class ViewHolder implements Holder{
        public final ImageView img;
        ViewHolder(View view){
            img=(ImageView)view.findViewById(R.id.img);
        }
    }
}
