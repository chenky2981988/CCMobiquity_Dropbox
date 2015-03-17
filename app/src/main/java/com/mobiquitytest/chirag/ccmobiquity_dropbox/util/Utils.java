package com.mobiquitytest.chirag.ccmobiquity_dropbox.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by cs40655 on 17-03-2015.
 * Utility class
 */
public class Utils {

    Context mContext;

    public Utils(Context context)
    {
        this.mContext = context;
    }
    public void showToast(String msg) {
        Toast errorToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        errorToast.show();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
