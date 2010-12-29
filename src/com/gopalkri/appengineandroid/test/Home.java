/**
 * This file is part of the AppEngineAndroid project.
 * 
 * Copyright (c) Gopal Sharma 2010.
 */
package com.gopalkri.appengineandroid.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.gopalkri.appengineandroid.AppEngine;
import com.gopalkri.appengineandroid.AppEngineActivity;
import com.gopalkri.appengineandroid.HttpGetException;
import com.gopalkri.appengineandroid.R;

/**
 * @author Gopal Sharma.
 * 
 */
public class Home extends Activity {

	/**
	 * Tag for logging.
	 */
	private static final String TAG = "AppEngineAndroid";

	/**
	 * TextView which shows the current status.
	 */
	private TextView mStatus;

	/**
	 * Whether an attempt to authenticate was already made.
	 */
	private boolean mTriedAuthenticating = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mStatus = (TextView) findViewById(R.id.status);

		if (!mTriedAuthenticating) {
			Intent intent = new Intent(this, AppEngineActivity.class);
			intent.putExtra(AppEngineActivity.APPLICATION_NAME,
					"gopalkri-testing.appspot.com");
			startActivityForResult(intent, AppEngineActivity.SETUP_AUTH);
		}
	}

	/**
	 * @see android.app.Activity#onActivityResult(int, int,
	 *      android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.d(TAG, "Home.onActivityResult: Request code: " + requestCode
				+ ". Result Code: " + resultCode + ".");
		mTriedAuthenticating = true;

		if (requestCode == AppEngineActivity.SETUP_AUTH
				&& resultCode == AppEngineActivity.RESULT_OK
				&& AppEngine.getInstance() != null) {
			doAuthenticationSucceededStuff();
		} else {
			doAuthenticationFailedStuff();
		}
	}

	/**
	 * Do whatever it is that needs to be done after authentication has succeeded.
	 */
	private void doAuthenticationSucceededStuff() {
		Log.i(TAG, "Authentication successful!.");
		mStatus.setText("Authenticated!");

		testGet();
	}

	/**
	 * Do a test get request.
	 */
	private void testGet() {
		AppEngine ae = AppEngine.getInstance();
		try {
			String result = ae.fetchResponseFromHttpGet("");
			mStatus.setText(result);
			Log.d(TAG, "Result for test GET: " + result);
		} catch (HttpGetException e) {
			e.printStackTrace();
			mStatus.setText(e.getMessage());
		}
	}

	/**
	 * Do whatever it is that needs to be done when authentication fails.
	 */
	private void doAuthenticationFailedStuff() {
		mStatus.setText("Authentication failed!");
		Log.e(TAG, "Authentication failed!");
	}

}