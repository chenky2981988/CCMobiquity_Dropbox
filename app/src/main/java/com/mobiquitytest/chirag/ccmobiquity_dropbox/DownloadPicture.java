package com.mobiquitytest.chirag.ccmobiquity_dropbox;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by Chirag on 3/16/2015.
 */

public class DownloadPicture extends AsyncTask<Void,Void,Boolean> {

    private DropboxAPI<?> mApi;
    private Context mContext;
    private ProgressDialog progressDialog;
    private String mPath;
    private String mErrorMsg;
    private String TAG = "DownloadPicture";

    private DropboxAPI.Entry photoData;
    private Long mFileLen;
    private Drawable mDrawable;
    private FileOutputStream mFos;
    private DropboxAPI.ThumbSize thumbSize;
    DownloadPhotoListener listener;

    public DownloadPicture(Context context, DropboxAPI<?> api,String dropboxPath,DropboxAPI.Entry photoData,DropboxAPI.ThumbSize thumbSize)
    {
        this.mApi = api;
        this.mContext = context;
        this.mPath = dropboxPath;
        this.photoData = photoData;
        this.thumbSize = thumbSize;
        listener = (DownloadPhotoListener) mContext;

    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(progressDialog == null)
            progressDialog = ProgressDialog.show(mContext,"",mContext.getResources().getString(R.string.downloading));
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {

        String path = this.photoData.path;
        mFileLen = this.photoData.bytes;
        String cachePath = mContext.getCacheDir().getAbsolutePath() + "/" + this.photoData.fileName();

            mFos = new FileOutputStream(cachePath);


        // This downloads a smaller, thumbnail version of the file.  The
        // API to download the actual file is roughly the same.

            mApi.getThumbnail(path, mFos, this.thumbSize,
                    DropboxAPI.ThumbFormat.JPEG, null);

            mDrawable = Drawable.createFromPath(cachePath);
            return true;
        } catch (FileNotFoundException e) {
            mErrorMsg = "Couldn't create a local file to store the image";

        }catch (DropboxUnlinkedException e) {
            // The AuthSession wasn't properly authenticated or user unlinked.
        } catch (DropboxPartialFileException e) {
            // We canceled the operation
            mErrorMsg = "Download canceled";
        } catch (DropboxServerException e) {
            // Server-side exception.  These are examples of what could happen,
            // but we don't do anything special with them here.
            if (e.error == DropboxServerException._304_NOT_MODIFIED) {
                // won't happen since we don't pass in revision with metadata
            } else if (e.error == DropboxServerException._401_UNAUTHORIZED) {
                // Unauthorized, so we should unlink them.  You may want to
                // automatically log the user out in this case.
            } else if (e.error == DropboxServerException._403_FORBIDDEN) {
                // Not allowed to access this
            } else if (e.error == DropboxServerException._404_NOT_FOUND) {
                // path not found (or if it was the thumbnail, can't be
                // thumbnailed)
            } else if (e.error == DropboxServerException._406_NOT_ACCEPTABLE) {
                // too many entries to return
            } else if (e.error == DropboxServerException._415_UNSUPPORTED_MEDIA) {
                // can't be thumbnailed
            } else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
                // user is over quota
            } else {
                // Something else
            }
            // This gets the Dropbox error, translated into the user's language
            mErrorMsg = e.body.userError;
            if (mErrorMsg == null) {
                mErrorMsg = e.body.error;
            }

        }catch (DropboxIOException e) {
            // Happens all the time, probably want to retry automatically.
            mErrorMsg = "Network error.  Try again.";
        }catch (DropboxParseException e) {
            // Probably due to Dropbox server restarting, should retry
            mErrorMsg = "Dropbox error.  Try again.";
        } catch (DropboxException e) {
            // Unknown error
            mErrorMsg = "Unknown error.  Try again.";
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        if(result)
        {
            listener.setPhoto(mDrawable);
        }
        if(progressDialog != null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
    }
}
