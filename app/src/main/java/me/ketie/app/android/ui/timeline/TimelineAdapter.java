package me.ketie.app.android.ui.timeline;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;

import me.ketie.app.android.R;
import me.ketie.app.android.common.Adapter;
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
public class TimelineAdapter extends Adapter<Timeline> {

    private final ImageLoader loader;

    public TimelineAdapter(Context context, ImageLoader loader) {
        super(context);
        this.loader=loader;
    }

    @Override
    protected View buildView(int position, LayoutInflater inflater, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.timeline_item, null, false);
        ViewCache cache=new ViewCache(view);
        view.setTag(cache);
        return view;
    }

    @Override
    protected void bindView(int position, View view, Timeline data) {
        ViewCache cache=(ViewCache)view.getTag();
        loader.get(data.getImgurl(),ImageLoader.getImageListener(cache.img,R.drawable.default_pic,R.drawable.default_pic));
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    public static class ViewCache{
        public final ImageView img;
        ViewCache(View view){
            img=(ImageView)view.findViewById(R.id.img);
        }
    }
}
