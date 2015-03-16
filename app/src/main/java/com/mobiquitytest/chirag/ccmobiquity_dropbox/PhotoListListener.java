package com.mobiquitytest.chirag.ccmobiquity_dropbox;

import com.dropbox.client2.DropboxAPI;

import java.util.ArrayList;

/**
 * Created by Chirag on 3/16/2015.
 */
public interface PhotoListListener {

    public void setPhotoList(ArrayList<DropboxAPI.Entry> photoList);
    public void updatePhotoList(boolean uploadResult);
}
