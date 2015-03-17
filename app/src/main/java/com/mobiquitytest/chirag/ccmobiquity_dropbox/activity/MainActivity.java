package com.mobiquitytest.chirag.ccmobiquity_dropbox.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.mobiquitytest.chirag.ccmobiquity_dropbox.R;
import com.mobiquitytest.chirag.ccmobiquity_dropbox.application.DropBoxSession;
import com.mobiquitytest.chirag.ccmobiquity_dropbox.util.Utils;

/*
    This is Application Launcher activity used to create DropBox session before upload or download images
 */
public class MainActivity extends ActionBarActivity {

    private static final String APP_KEY = "55fdqp32bdt5hvl";
    private static final String APP_SECRET = "2kfi0fd4w66asll";

    // You don't need to change these, leave them alone.
    public static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    private static final boolean USE_OAUTH1 = false;
     private String TAG = "MainActivity";
    private boolean mLoggedIn;
    private DropBoxSession dropBoxSession;
    private Utils utils;
    private Button linkButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);
        dropBoxSession = ((DropBoxSession) getApplicationContext());
        // We create a new AuthSession so that we can use the Dropbox API.
        AndroidAuthSession session = buildSession();
        dropBoxSession.setmApi(new DropboxAPI<AndroidAuthSession>(session));
        // Initialize utils class object
        utils = new Utils(this);

        checkAppKeySetup();
        initUI();

    }
    /*
        Initialize UI components
    */
    private void initUI() {
        linkButton = (Button) findViewById(R.id.link_button);
        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (utils.isNetworkAvailable()) {
                    if (!mLoggedIn) {
                        if (USE_OAUTH1) {
                            dropBoxSession.getmApi().getSession().startAuthentication(MainActivity.this);
                        } else {
                            dropBoxSession.getmApi().getSession().startOAuth2Authentication(MainActivity.this);
                        }
                    }
                }else{
                    utils.showToast("Internet is not available");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

            AndroidAuthSession session = dropBoxSession.getmApi().getSession();

            // The next part must be inserted in the onResume() method of the
            // activity from which session.startAuthentication() was called, so
            // that Dropbox authentication completes properly.
            if (session.authenticationSuccessful()) {
                try {
                    // Mandatory call to complete the auth
                    session.finishAuthentication();

                    // Store it locally in our app for later use
                    storeAuth(session);
                    setLoggedIn(true);
                } catch (IllegalStateException e) {
                    utils.showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
                    Log.i(TAG, "Error authenticating", e);
                }
            }

    }
    // Create amd build Dropbox session
    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);

        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
    }

    /*
        OAuth Process
        Check Session token key , if not present then download redirect to dropbox login screen
     */
    private void checkAppKeySetup() {
        // Check to make sure that we have a valid app key
        if (APP_KEY.startsWith("CHANGE") ||
                APP_SECRET.startsWith("CHANGE")) {
            utils.showToast("You must apply for an app key and secret from developers.dropbox.com, and add them to the DBRoulette ap before trying it.");
            finish();
            return;
        }

        // Check if the app has set up its manifest properly.
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        String scheme = "db-" + APP_KEY;
        String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
        testIntent.setData(Uri.parse(uri));
        PackageManager pm = getPackageManager();
        if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
            utils.showToast("URL scheme in your app's " +
                    "manifest is not set up correctly. You should have a " +
                    "com.dropbox.client2.android.AuthActivity with the " +
                    "scheme: " + scheme);
            finish();
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void loadAuth(AndroidAuthSession session) {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0) return;

        if (key.equals("oauth2:")) {
            // If the key is set to "oauth2:", then we can assume the token is for OAuth 2.
            session.setOAuth2AccessToken(secret);
        } else {
            // Still support using old OAuth 1 tokens.
            session.setAccessTokenPair(new AccessTokenPair(key, secret));
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void storeAuth(AndroidAuthSession session) {
        // Store the OAuth 2 access token, if there is one.
        String oauth2AccessToken = session.getOAuth2AccessToken();
        if (oauth2AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, "oauth2:");
            edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
            edit.commit();
            return;
        }
        // Store the OAuth 1 access token, if there is one.  This is only necessary if
        // you're still using OAuth 1.
        AccessTokenPair oauth1AccessToken = session.getAccessTokenPair();
        if (oauth1AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, oauth1AccessToken.key);
            edit.putString(ACCESS_SECRET_NAME, oauth1AccessToken.secret);
            edit.commit();
            return;
        }
    }






    /**
     * Convenience function to change UI state based on being logged in
     */
    private void setLoggedIn(boolean loggedIn) {
        mLoggedIn = loggedIn;
        if (loggedIn) {
//            mSubmit.setText("Unlink from Dropbox");
//            mDisplay.setVisibility(View.VISIBLE);
            utils.showToast("Successfully logged in");
            Intent homeIntent = new Intent(this,HomeActivity.class);
            startActivity(homeIntent);
            finish();

        } else {
//            mSubmit.setText("Link with Dropbox");
//            mDisplay.setVisibility(View.GONE);
//            mImage.setImageDrawable(null);
            utils.showToast("Unable to log in");
        }
    }






}
