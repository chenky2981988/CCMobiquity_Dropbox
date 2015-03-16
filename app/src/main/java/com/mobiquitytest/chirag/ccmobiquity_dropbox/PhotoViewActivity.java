package com.mobiquitytest.chirag.ccmobiquity_dropbox;

import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.dropbox.client2.DropboxAPI;


public class PhotoViewActivity extends MasterActivity implements DownloadPhotoListener{

    private ImageView photoImageView;

    private String mPath;
    private int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        if(getIntent().getExtras() != null)
        {
            index = getIntent().getExtras().getInt("index");
            mPath = getIntent().getExtras().getString("dropbox_path");
        }
        initUI();

        DownloadPicture downloadPicture = new DownloadPicture(this,mApi,mPath,photoList.get(index),DropboxAPI.ThumbSize.BESTFIT_1024x768);
        downloadPicture.execute();
    }

    private void initUI() {
        photoImageView = (ImageView) findViewById(R.id.photoImageView);
    }


    @Override
    public void setPhoto(Drawable photoDrawable)
    {
        if(photoDrawable != null)
            photoImageView.setImageDrawable(photoDrawable);
    }
}
