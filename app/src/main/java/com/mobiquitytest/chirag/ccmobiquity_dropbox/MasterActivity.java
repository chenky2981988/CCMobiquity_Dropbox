package com.mobiquitytest.chirag.ccmobiquity_dropbox;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

import java.util.ArrayList;


public abstract class MasterActivity extends ActionBarActivity {

    public static DropboxAPI<AndroidAuthSession> mApi;
    public static ArrayList<DropboxAPI.Entry> photoList = new ArrayList<DropboxAPI.Entry>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
    }

    public void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }
}
