package me.ketie.app.android.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

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
public abstract class Adapter<T> extends BaseAdapter {
    protected final Context mContext;
    private List<T> datas=new ArrayList<T>();
    protected final LayoutInflater mInflater;
    public Adapter(Context context){
        this.mContext=context;
        this.mInflater=LayoutInflater.from(context);
    }
    public void reload(List<T> datas,boolean append){
        if(!append){
            this.datas.clear();
        }
        this.datas.addAll(datas);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public T getItem(int position) {
        return datas.get(position);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=buildView(position,mInflater, convertView, parent);
        }
        T data=getItem(position);
        bindView(position, convertView, data);
        return convertView;
    }

    protected abstract View buildView(int position, LayoutInflater inflater, View convertView, ViewGroup parent);
    protected abstract void bindView(int position, View view, T data);
}
