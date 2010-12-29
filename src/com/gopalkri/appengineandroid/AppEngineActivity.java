/**
 * This file is part of the AppEngineAndroid project.
 * 
 * Copyright (c) Gopal Sharma 2010.
 */
package com.gopalkri.appengineandroid;

import android.R;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * @author Gopal Sharma
 */
public class AppEngineActivity extends ListActivity {

	/**
	 * Key for extra string to be added to calling intent. Value must be name of
	 * AppEngine application URL without http/https. Ex:
	 * gopalkri-testing.appspot.com.
	 */
	public static final String APPLICATION_NAME = "ApplicationName";

	/**
	 * Key for intent extra that can be fetched out of the data intent returned
	 * by setResult. Value is an AppEngine object on which requests can be
	 * performed.
	 */
	public static final String APPENGINE_INSTANCE = "AppEngineInstance";

	/**
	 * Name of SharedPreferences file used to store account name.
	 */
	public static final String PREFS_FILE_NAME = "AppEngineAndroid.prefs";

	/**
	 * Request code to be passed into startActivityForResult when calling this
	 * activity.
	 */
	public static final int SETUP_AUTH = 872635; // Use a random number to avoid
	// conflicts.

	/**
	 * Return code returned via setResult when everything worked.
	 */
	public static final int RESULT_OK = 345246; // Use a random number to avoid
	// conflicts.

	/**
	 * Return code returned via setResult if something failed.
	 */
	public static final int RESULT_FAILED = RESULT_OK + 1;

	/**
	 * Tag for logging.
	 */
	private static final String TAG = "AppEngineAndroid";

	/**
	 * Key for account name preference.
	 */
	private static final String PREFS_ACCOUNT_NAME = "AccountName";

	/**
	 * AccountManager object used to interface with Accounts.
	 */
	private AccountManager mAccountManager;

	/**
	 * All Google accounts available.
	 */
	private Account[] mAllAccounts;

	/**
	 * Application Url. Ex: https://gopalkri-testing.appspot.com
	 */
	private String mApplicationUrl;

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mApplicationUrl = "https://"
				+ getIntent().getExtras().get(APPLICATION_NAME);

		fetchAllAccounts();
		if (mAllAccounts.length < 1) {
			Log.e(TAG, "No accounts found!");
			setResult(RESULT_FAILED);
			finish();
			return;
		}
		if (mAllAccounts.length == 1) {
			Log.d(TAG, "Only one account found, using it.");
			startAuthenticationActivity(mAllAccounts[0]);
		} else {
			Account account = readAccountFromPreferences();
			if (account == null) {
				Log.d(TAG, "Account not found in preferences.");
				setListAdapter(new ArrayAdapter<Account>(this,
						R.layout.simple_list_item_1, mAllAccounts));
			} else {
				Log.d(TAG, "Found account in preferences. Will use it.");
				startAuthenticationActivity(account);
			}
		}
	}

	/**
	 * Release memory used by mAccountManager and mAllAccounts.
	 * 
	 * @see android.app.ListActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAccountManager = null;
		mAllAccounts = null;
	}

	/**
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView,
	 *      android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Account account = (Account) getListView().getItemAtPosition(position);
		startAuthenticationActivity(account);
	}

	/**
	 * @see android.app.Activity#onActivityResult(int, int,
	 *      android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SETUP_AUTH) {
			setResult(resultCode);
		}
		else {
			setResult(RESULT_CANCELED);
		}
		finish();
	}

	/**
	 * Gets all available Google accounts from mAccountManager and stores them
	 * in mAllAccounts.
	 */
	private void fetchAllAccounts() {
		mAccountManager = AccountManager.get(this);
		mAllAccounts = mAccountManager.getAccountsByType("com.google");
	}

	/**
	 * Reads SharedPreferences file and gets stored account name. Returns
	 * Account object for the stored account if found, null otherwise.
	 * 
	 * @return Account object for the stored account if found, null otherwise.
	 */
	private Account readAccountFromPreferences() {
		SharedPreferences preferences = getSharedPreferences(PREFS_FILE_NAME,
				Activity.MODE_PRIVATE);
		String accountName = preferences.getString(PREFS_ACCOUNT_NAME, null);
		if (accountName == null) {
			return null;
		}

		for (Account account : mAllAccounts) {
			if (account.name.compareTo(accountName) == 0) {
				return account;
			}
		}

		return null;
	}

	/**
	 * Starts AuthenticationActivity for result with account passed in through
	 * the intent extras.
	 * 
	 * @param account
	 *            Account to use for authentication.
	 */
	private void startAuthenticationActivity(Account account) {
		Log.i(TAG, "Using account: " + account.name);

		SharedPreferences preferences = getSharedPreferences(PREFS_FILE_NAME,
				MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(PREFS_ACCOUNT_NAME, account.name);
		editor.commit();

		Intent intent = new Intent(this, AuthenticationActivity.class);
		intent.putExtra(AuthenticationActivity.ACCOUNT, account);
		intent.putExtra(AuthenticationActivity.APPLICATION_URL,
						mApplicationUrl);
		startActivityForResult(intent, SETUP_AUTH);
	}

}
