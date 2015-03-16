package com.mobiquitytest.chirag.ccmobiquity_dropbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;

import java.util.ArrayList;

/**
 * Created by Chirag on 3/16/2015.
 */
public class PhotoListAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<DropboxAPI.Entry> photoList;
    private LayoutInflater inflater;
    public PhotoListAdapter(Context mContext, ArrayList<DropboxAPI.Entry> photoList)
    {
        this.mContext = mContext;
        this.photoList = photoList;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            holder.photoImageView = (ImageView)  convertView.findViewById(R.id.photo_thumbnail);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.photoTextView.setText(photoList.get(position).fileName());

        return convertView;
    }

    static class ViewHolder
    {
        TextView photoTextView;
        ImageView photoImageView;
    }
}
