package com.mobiquitytest.chirag.ccmobiquity_dropbox;

import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.dropbox.client2.DropboxAPI;


public class PhotoViewActivity extends MasterActivity {

    private ImageView photoImageView;

    private String mPath;
    private int index;
    private String photoPath;
    private Drawable mDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        if(getIntent().getExtras() != null)
        {
            photoPath = getIntent().getExtras().getString("photo_path");
        }
        initUI();


    }

    private void initUI() {

        photoImageView = (ImageView) findViewById(R.id.photoImageView);

        mDrawable = Drawable.createFromPath(photoPath);
        if(mDrawable != null)
        {
            photoImageView.setImageDrawable(mDrawable);
        }
    }


//    @Override
//    public void setPhoto(Drawable photoDrawable)
//    {
//        if(photoDrawable != null)
//            photoImageView.setImageDrawable(photoDrawable);
//    }
}
