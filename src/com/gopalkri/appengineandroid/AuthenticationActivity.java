/**
 * This file is part of the AppEngineAndroid project.
 * 
 * Copyright (c) Gopal Sharma 2010.
 */
package com.gopalkri.appengineandroid;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.gopalkri.appengineandroid.R;

/**
 * @author Gopal Sharma
 */
public class AuthenticationActivity extends Activity {

	/**
	 * Key for intent extra for which the value is the app engine application's
	 * URL.
	 */
	public static final String APPLICATION_URL = "ApplicationUrl";

	/**
	 * Key for intent extra for which the value is the Account object used for
	 * authentication.
	 */
	public static final String ACCOUNT = "Account";

	/**
	 * Tag for logging.
	 */
	private static final String TAG = "AppEngineAndroid";

	/**
	 * Application Url. Ex: https://gopalkri-testing.appspot.com
	 */
	private String mApplicationUrl;

	/**
	 * AppEngine instance to use.
	 */
	private AppEngine mAEInstance;

	/**
	 * Account Manager object to use for getting auth token.
	 */
	private AccountManager mAccountManager;

	/**
	 * Google account to use for authentication.
	 */
	private Account mAccount;

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appengine);

		Bundle extras = getIntent().getExtras();
		mApplicationUrl = (String) extras.get(APPLICATION_URL);
		mAccount = (Account) extras.get(ACCOUNT);
		mAEInstance = AppEngine.createInstance(mApplicationUrl);
		mAccountManager = AccountManager.get(this);

		TextView status = (TextView) findViewById(R.id.appengine_status);
		status.setText("Connecting to: " + mApplicationUrl + "...");
	}

	/**
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "Fetching auth token for the first time.");
		// This needs to be in onResume, read comments below to understand why.
		mAccountManager.getAuthToken(mAccount, "ah", false,
				new GetAuthTokenCallback(), null);
	}

	// Some crazy stuff going on here. Needs to be explained.
	// There is a problem with authTokens - they sometimes expire. An expired
	// token cannot be used. The way to figure out if a token has expired is by
	// actually using it and examining the return code. This is hard to
	// implement so for the moment, I'm using the easy solution of guaranteeing
	// a fresh token every time. To do this, always discard the first authToken
	// returned and use the next one.
	// There is an added complication in that sometimes the user has to approve
	// access to the account. I believe this only happens the first time, but
	// I'm not sure. If the user needs to grant access, an intent has to be
	// fired and the user has to accept. The problem with this intent is that it
	// does not return a result. One way to figure out whether the intent needs
	// to be fired again is to check if intent is returned in the result bundle.
	// Therefore, getAuthToken must be called in onResume().
	private class GetAuthTokenCallback implements
			AccountManagerCallback<Bundle> {

		/**
		 * @see android.accounts.AccountManagerCallback#run(android.accounts.AccountManagerFuture)
		 */
		public void run(AccountManagerFuture<Bundle> result) {
			Bundle bundle;
			try {
				bundle = result.getResult();
				Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
				if (intent != null) {
					Log.d(TAG, "Need user input, firing intent.");
					startActivity(intent);
				} else {
					onGetAuthToken(bundle);
				}
			} catch (Exception e) {
				e.printStackTrace();
				setResult(AppEngineActivity.RESULT_FAILED);
				finish();
				return;
			}
		}
	}

	/**
	 * Start GetAuthTokenTask.
	 * 
	 * @param bundle
	 *            Bundle returned by AccountManager - should contain extra for
	 *            auth token.
	 */
	private void onGetAuthToken(Bundle bundle) {
		new GetAuthTokenTask().execute(bundle
				.getString(AccountManager.KEY_AUTHTOKEN));
	}

	private class GetAuthTokenTask extends AsyncTask<String, Void, String> {

		/**
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(String... params) {
			mAccountManager.invalidateAuthToken(mAccount.type, params[0]);
			return getAuthToken();
		}

		private String getAuthToken() {
			try {
				return mAccountManager.getAuthToken(mAccount, "ah", false,
						null, null).getResult().getString(
						AccountManager.KEY_AUTHTOKEN);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		/**
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String authToken) {
			if (authToken == null) {
				Log.e(TAG, "Failed to retrieve auth token!");
				setResult(AppEngineActivity.RESULT_FAILED);
				finish();
				return;
			}
			new GetCookiesTask().execute(authToken);
		}

	}

	private class GetCookiesTask extends AsyncTask<String, Void, Boolean> {

		/**
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				mAEInstance.fetchCookies(params[0]);
				return true;
			} catch (CookieException e) {
				e.printStackTrace();
				return false;
			}
		}

		/**
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				Log.i(TAG, "AppEngine instance is ready to go.");
				setResult(AppEngineActivity.RESULT_OK);
			} else {
				Log.e(TAG, "Failed to fetch cookies!");
				setResult(AppEngineActivity.RESULT_FAILED);
			}
			finish();
		}

	}

}
