package com.mobiquitytest.chirag.ccmobiquity_dropbox.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.mobiquitytest.chirag.ccmobiquity_dropbox.listeners.PhotoListListener;
import com.mobiquitytest.chirag.ccmobiquity_dropbox.R;

import java.util.ArrayList;

/**
 * Created by Chirag on 3/16/2015.
 * AsyncTask to Download Photo List from dropBox
 */
public class DownloadPhotosList extends AsyncTask<Void,Void,ArrayList<DropboxAPI.Entry>>
{
    private DropboxAPI<?> mApi;
    private Context mContext;
    private ProgressDialog progressDialog;
    private String mPath;
    private String mErrorMsg = "";
    private String TAG = "DownloadPhotoList";
    private PhotoListListener listener;
    public DownloadPhotosList(Context context, DropboxAPI<?> api,String dropboxPath)
    {
        this.mApi = api;
        this.mContext = context;
        this.mPath = dropboxPath;
        listener = (PhotoListListener) mContext;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Initialize and start Progress Dialog
        if(progressDialog == null)
            progressDialog = ProgressDialog.show(mContext,"",mContext.getResources().getString(R.string.loading));
    }

    @Override
    protected ArrayList<DropboxAPI.Entry> doInBackground(Void... params) {

        // Get the metadata for a directory
         try {
            DropboxAPI.Entry dirent = mApi.metadata(mPath, 1000, null, true, null);

            if (!dirent.isDir || dirent.contents == null) {
                // It's not a directory, or there's nothing in it
                mErrorMsg = "File or empty directory";
                return null;
            }
             // Make a list of everything in it that we can get a thumbnail for
             ArrayList<DropboxAPI.Entry> photoList = new ArrayList<DropboxAPI.Entry>();

             for (DropboxAPI.Entry ent: dirent.contents) {
                 if (ent.thumbExists) {
                     // Add it to the list of thumbs we can choose from
                     photoList.add(ent);

                 }
             }
            if(photoList.isEmpty())
                mErrorMsg = "Photo folder is empty";

            return photoList;

        } catch (DropboxException e) {
            e.printStackTrace();
            return null;
        }


    }

    @Override
    protected void onPostExecute(ArrayList<DropboxAPI.Entry> photoList) {
        super.onPostExecute(photoList);
        // Call listener method and pass data
        listener.setPhotoList(photoList,mErrorMsg);

        // Dismiss ProgressDialog
        if(progressDialog != null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
    }
}
