package me.ketie.app.android.ui.launch;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

import me.ketie.app.android.R;
import me.ketie.app.android.common.Adapter;
import me.ketie.app.android.gsonbean.Banner;
import me.ketie.app.android.utils.LogUtil;

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
public class HomeListAdapter extends Adapter<Banner> {
    public HomeListAdapter(Context context) {
        super(context);
    }

    @Override
    protected View buildView(int position, LayoutInflater inflater, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.home_list_item, null, false);
        ViewCache cache=new ViewCache(view);
        view.setTag(cache);
        return view;
    }

    @Override
    protected void bindView(int position, View view, Banner data) {
        ViewCache cache=(ViewCache)view.getTag();
        Uri parse = Uri.parse(data.getImgurl());
        LogUtil.i(HomeListAdapter.class.getSimpleName(),"Load Image Uri:"+parse.toString());
        cache.img.setImageURI(parse);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    private class ViewCache{
        private SimpleDraweeView img;
        ViewCache(View view){
            img=(SimpleDraweeView)view.findViewById(R.id.img);
        }
    }
}
