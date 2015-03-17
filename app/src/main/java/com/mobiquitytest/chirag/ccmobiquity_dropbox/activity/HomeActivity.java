package com.mobiquitytest.chirag.ccmobiquity_dropbox.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dropbox.client2.DropboxAPI;
import com.mobiquitytest.chirag.ccmobiquity_dropbox.R;
import com.mobiquitytest.chirag.ccmobiquity_dropbox.adapter.PhotoListAdapter;
import com.mobiquitytest.chirag.ccmobiquity_dropbox.application.DropBoxSession;
import com.mobiquitytest.chirag.ccmobiquity_dropbox.asynctask.DownloadPhotosList;
import com.mobiquitytest.chirag.ccmobiquity_dropbox.asynctask.DownloadPicture;
import com.mobiquitytest.chirag.ccmobiquity_dropbox.asynctask.UploadPicture;
import com.mobiquitytest.chirag.ccmobiquity_dropbox.listeners.DownloadPhotoListener;
import com.mobiquitytest.chirag.ccmobiquity_dropbox.listeners.PhotoListListener;
import com.mobiquitytest.chirag.ccmobiquity_dropbox.util.Utils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/*
  * Home Activity to display images list from dropbox Photo folder in application.
  * User can view image on selection of perticular image. User can Unlink session from menu selection.
 */
public class HomeActivity extends ActionBarActivity implements PhotoListListener, DownloadPhotoListener {

    private String mCameraFileName;
    private static final String TAG = "HomeActivity";
    private final String PHOTO_DIR = "/Photos/";
    private static final int NEW_PICTURE = 1;
    private ListView photoListView;
    public ArrayList<DropboxAPI.Entry> photoList = new ArrayList<DropboxAPI.Entry>();
    private PhotoListAdapter photoListAdapter;
    DropBoxSession dropBoxSession;
    Utils mUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dropBoxSession = ((DropBoxSession) getApplicationContext());
        mUtils = new Utils(this);
        initUI();

        // If Photo List is empty then Download list from dropbox
        if (photoList.isEmpty()) {
           getPhotoList();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /*
        Initialize UI components
     */
    private void initUI() {

        photoListView = (ListView) findViewById(R.id.photo_listview);
        photoListAdapter = new PhotoListAdapter(HomeActivity.this, this.photoList, PHOTO_DIR);
        photoListView.setAdapter(photoListAdapter);

        photoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                downloadPicture(position);
            }
        });
    }

    // Inflate Menu Options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_unlink:
                logOut();
                break;

            case R.id.action_sync:
                if(mUtils.isNetworkAvailable()) {
                    getPhotoList();
                }else{
                    mUtils.showToast("Internet is not available");
                }
                break;

            case R.id.action_upload:
                CaptureImage();
                break;


        }

        return super.onOptionsItemSelected(item);
    }

    /*
        Function to Start Camera and Store captured Image on Path
     */
    private void CaptureImage() {
        Intent intent = new Intent();
        // Picture from camera
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        // This is not the right way to do this, but for some reason, having
        // it store it in
        // MediaStore.Images.Media.EXTERNAL_CONTENT_URI isn't working right.

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss", Locale.US);

        String newPicFile = df.format(date) + ".jpg";
        String outPath = new File(Environment.getExternalStorageDirectory(), newPicFile).getPath();
        File outFile = new File(outPath);

        mCameraFileName = outFile.toString();
        Uri outuri = Uri.fromFile(outFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);
        Log.i(TAG, "Importing New Picture: " + mCameraFileName);
        try {
            startActivityForResult(intent, NEW_PICTURE);
        } catch (ActivityNotFoundException e) {
            mUtils.showToast("There doesn't seem to be a camera.");
        }
    }


    // This is what gets called on finishing a media piece to import
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_PICTURE) {
            // return from file upload
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                }
                if (uri == null && mCameraFileName != null) {
                    uri = Uri.fromFile(new File(mCameraFileName));
                }
                File file = new File(mCameraFileName);

                if (uri != null && dropBoxSession.getmApi() != null) {
                    if(mUtils.isNetworkAvailable()) {
                        UploadPicture upload = new UploadPicture(this, dropBoxSession.getmApi(), PHOTO_DIR, file);
                        upload.execute();
                    }else{
                        mUtils.showToast("Internet is not available");
                    }
                }
            } else {
                Log.w(TAG, "Unknown Activity Result from mediaImport: "
                        + resultCode);
            }
        }
    }

    /*
        Implimentation of SetPhotoList method of PhotoList Listener
        Set PhotoListg in List View
     */

    @Override
    public void setPhotoList(ArrayList<DropboxAPI.Entry> photolist, String errMsg) {
        this.photoList = photolist;
        if (this.photoList != null) {
            this.photoList = photolist;
            photoListAdapter.updatePhotoList(this.photoList);

            if(this.photoList.isEmpty())
            {
                mUtils.showToast("Photo folder is empty");
            }
        }
    }

    /*
        Implementation of update PhotoList after uploading picture if result is success
     */
    @Override
    public void updatePhotoList(boolean uploadResult) {
        if (uploadResult) {
            getPhotoList();
        }
    }
    /*
         Download Photo List from DropBox Photo folder
     */
    private void getPhotoList() {

        if(mUtils.isNetworkAvailable()) {
            DownloadPhotosList downloadPhotosList = new DownloadPhotosList(this, dropBoxSession.getmApi(), PHOTO_DIR);
            downloadPhotosList.execute();
        }else {
            mUtils.showToast("Internet is not available");
        }

    }
    /*
        Downalod Selected Picture
     */
    private void downloadPicture(int position)
    {
        if(mUtils.isNetworkAvailable()) {
            DownloadPicture downloadPicture = new DownloadPicture(HomeActivity.this, dropBoxSession.getmApi(),
                    PHOTO_DIR, photoList.get(position), DropboxAPI.ThumbSize.BESTFIT_1024x768);
            downloadPicture.execute();
        }else {
            mUtils.showToast("Internet is not available");
        }
    }

    /*
       Override method to redirect to PhotoViewActivity
     */
    @Override
    public void setPhoto(String photoPath) {
        if (!TextUtils.isEmpty(photoPath)) {
            Intent PhotoViewIntent = new Intent(HomeActivity.this, PhotoViewActivity.class);
            PhotoViewIntent.putExtra("photo_path", photoPath);
            startActivity(PhotoViewIntent);
        }
    }

    /*
        Log Out from Dropbox (Session Logout)
     */
    private void logOut() {
        // Remove credentials from the session
        dropBoxSession.getmApi().getSession().unlink();
        // Clear our stored keys
        clearKeys();
        /*
         Redirect to MainActivity
         */
        Intent mainIntent = new Intent(this,MainActivity.class);
        startActivity(mainIntent);
        finish();

    }
    /*
        Clear Stored data from Shared Preferences
     */
    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(MainActivity.ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }
}
