package com.mobiquitytest.chirag.ccmobiquity_dropbox;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dropbox.client2.DropboxAPI;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class HomeActivity extends MasterActivity implements PhotoListListener{

    private String mCameraFileName;
    private static final String TAG = "HomeActivity";
    private final String PHOTO_DIR = "/Photos/";
    private static final int NEW_PICTURE = 1;
    private ListView photoListView;

    private PhotoListAdapter photoListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (photoList.isEmpty())
        {
            getPhotoList();
        }

    }

    private void initUI() {

        photoListView = (ListView) findViewById(R.id.photo_listview);
        photoListAdapter = new PhotoListAdapter(HomeActivity.this, this.photoList);
        photoListView.setAdapter(photoListAdapter);

        photoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent PhotoViewIntent = new Intent(HomeActivity.this,PhotoViewActivity.class);
                PhotoViewIntent.putExtra("index", position);
                PhotoViewIntent.putExtra("dropbox_path", PHOTO_DIR);
                startActivity(PhotoViewIntent);

            }
        });
    }


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
                Log.d("TAG", "Unlink");
                break;

            case R.id.action_sync:
                Log.d("TAG", "Sync");
                break;

            case R.id.action_upload:
                Log.d("TAG", "Upload");
                CaptureImage();
                break;


        }

        return super.onOptionsItemSelected(item);
    }

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
        showToast("There doesn't seem to be a camera.");
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

                if (uri != null && mApi != null) {
                    UploadPicture upload = new UploadPicture(this, mApi, PHOTO_DIR, file);
                    upload.execute();
                }
            } else {
                Log.w(TAG, "Unknown Activity Result from mediaImport: "
                        + resultCode);
            }
        }
    }

    @Override
    public void setPhotoList(ArrayList<DropboxAPI.Entry> photolist) {
        this.photoList = photolist;
        Log.d(TAG,"Photo List Size : " + this.photoList.size());
        photoListAdapter.updatePhotoList(this.photoList);

    }

    @Override
    public void updatePhotoList(boolean uploadResult) {
        if(uploadResult)
        {
            getPhotoList();
        }
    }

    private void getPhotoList()
    {
        DownloadPhotosList  downloadPhotosList  = new DownloadPhotosList(this, mApi, PHOTO_DIR);
        downloadPhotosList.execute();
    }
}
