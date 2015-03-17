package com.mobiquitytest.chirag.ccmobiquity_dropbox.application;

import android.app.Application;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

/**
 * Created by cs40655 on 17-03-2015.
 * Application class to store and access DropBox Auth Session throughout application
 */
public class DropBoxSession extends Application {

    private DropboxAPI<AndroidAuthSession> mApi;

    public DropboxAPI<AndroidAuthSession> getmApi() {
        return mApi;
    }

    public void setmApi(DropboxAPI<AndroidAuthSession> api) {
        this.mApi = api;
    }
}
