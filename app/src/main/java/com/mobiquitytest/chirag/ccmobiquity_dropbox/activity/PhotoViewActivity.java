package com.mobiquitytest.chirag.ccmobiquity_dropbox.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import com.mobiquitytest.chirag.ccmobiquity_dropbox.R;

/*
    Selected Photo View Activity
 */
public class PhotoViewActivity extends ActionBarActivity {

    private ImageView photoImageView;
    private String mPath;
    private int index;
    private String photoPath;
    private Drawable mDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Take passed data from Parent/Previous Activity
        if(getIntent().getExtras() != null)
        {
            photoPath = getIntent().getExtras().getString("photo_path");
        }
        initUI();
  }
    /*
       Initialize UI components
    */
    private void initUI() {

        photoImageView = (ImageView) findViewById(R.id.photoImageView);

        // Create Drawable from Path where downloaded image placed
        mDrawable = Drawable.createFromPath(photoPath);
        // Set Image drawable to ImageView
        if(mDrawable != null)
        {
            photoImageView.setImageDrawable(mDrawable);
        }
    }

    // Menu Option Item Selection Action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
               finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
