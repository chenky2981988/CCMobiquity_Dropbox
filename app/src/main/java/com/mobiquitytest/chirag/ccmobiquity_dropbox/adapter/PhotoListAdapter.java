package com.mobiquitytest.chirag.ccmobiquity_dropbox.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.mobiquitytest.chirag.ccmobiquity_dropbox.R;

import java.util.ArrayList;

/**
 * Created by Chirag on 3/16/2015.
 * Photo List Adapter to fill list on HomeActivity with Images list got from dropbox
 */
public class PhotoListAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<DropboxAPI.Entry> photoList;
    private LayoutInflater inflater;
    private String PHOTO_DIR;

    public PhotoListAdapter(Context mContext, ArrayList<DropboxAPI.Entry> photoList,String photoPath)
    {
        this.mContext = mContext;
        this.photoList = photoList;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.PHOTO_DIR = photoPath;
    }

    @Override
    public int getCount() {
        return photoList.size();
    }

    @Override
    public Object getItem(int position) {
        return photoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updatePhotoList(ArrayList<DropboxAPI.Entry> newlist) {
        photoList.clear();
        photoList.addAll(newlist);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.listitem_photolist, parent, false);
            holder = new ViewHolder();
            holder.photoTextView = (TextView) convertView.findViewById(R.id.photo_name);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.photoTextView.setText(photoList.get(position).fileName());
        return convertView;
    }

    static class ViewHolder{
        TextView photoTextView;
    }
}
