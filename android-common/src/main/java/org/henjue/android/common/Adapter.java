package org.henjue.android.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * abstract Adapter
 * @param <T>
 * @param <H>
 */
public abstract class Adapter<T,H extends Holder> extends BaseAdapter {
    protected final Context mContext;
    private List<T> datas=new ArrayList<T>();
    protected final LayoutInflater mInflater;
    public Adapter(Context context){
        this.mContext=context;
        this.mInflater= LayoutInflater.from(context);
    }

    /**
     * reload Data
     * @param datas
     * @param append should append data
     */
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
        final T data;
        final H holder;
        if(convertView==null){
            convertView= newView(position, mInflater, convertView, parent);
            data=getItem(position);
            holder = newHolder(position, convertView, data);
            convertView.setTag(mContext.getResources().getIdentifier("item_holder","id",mContext.getPackageName()),holder);
        }else{
            data=getItem(position);
            holder=getHolder(convertView);
        }
        bindData(position, holder, data);
        return convertView;
    }
    public H getHolder(View view){
       return (H)view.getTag(mContext.getResources().getIdentifier("item_holder","id",mContext.getPackageName()));
    };

    /**
     * create ViewHolder
     * @param position
     * @param view
     * @param data
     * @return
     */
    protected abstract H newHolder(int position, View view, T data);

    /**
     * create Item View
     * @param position
     * @param inflater
     * @param convertView
     * @param parent
     * @return
     */
    protected abstract View newView(int position, LayoutInflater inflater, View convertView, ViewGroup parent);

    /**
     * Bind Item Data to View
     * @param position
     * @param holder
     * @param data
     */
    protected abstract void bindData(int position, H holder, T data);
}
