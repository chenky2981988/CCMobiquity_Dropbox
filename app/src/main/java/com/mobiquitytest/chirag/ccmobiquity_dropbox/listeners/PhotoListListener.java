package com.mobiquitytest.chirag.ccmobiquity_dropbox.listeners;

import com.dropbox.client2.DropboxAPI;

import java.util.ArrayList;

/**
 * Created by Chirag on 3/16/2015.
 */
public interface PhotoListListener {

    public void setPhotoList(ArrayList<DropboxAPI.Entry> photoList,String errorMsg);
    public void updatePhotoList(boolean uploadResult);
}
