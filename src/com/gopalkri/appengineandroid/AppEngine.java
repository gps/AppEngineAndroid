/**
 * This file is part of the AppEngineAndroid project.
 * 
 * Copyright (c) Gopal Sharma 2010.
 */
package com.gopalkri.appengineandroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

/**
 * @author Gopal Sharma.
 * 
 */
public class AppEngine {

	private static final String TAG = "AppEngineAndroid";

	/**
	 * Http Client to perform various http requests.
	 */
	private final DefaultHttpClient mHttpClient = new DefaultHttpClient();

	/**
	 * App engine application's url. Ex: https://gopalkri-testing.appspot.com
	 */
	private final String mApplicationUrl;

	/**
	 * Lock to ensure only one thread accesses sInstance.
	 */
	private static ReentrantLock sInstanceLock = new ReentrantLock();

	/**
	 * Whether or not AppEngine instance is ready for use.
	 */
	private static boolean sReady = false;

	/**
	 * Singleton instance of AppEngine.
	 */
	private static AppEngine sInstance = null;

	/**
	 * Gets singleton instance of AppEngine. Returns null if the instance has
	 * not been constructed properly.
	 * 
	 * @return Singleton instance of AppEngine. Null if the instance has not
	 *         beeen constructed properly.
	 */
	public static AppEngine getInstance() {
		AppEngine ret = null;
		sInstanceLock.lock();
		if (sReady) {
			ret = sInstance;
		}
		sInstanceLock.unlock();
		return ret;
	}

	/**
	 * Creates new singleton instance of AppEngine.
	 * 
	 * @param applicationUrl
	 *            App engine application's url. Ex:
	 *            https://gopalkri-testing.appspot.com
	 */
	protected static AppEngine createInstance(String applicationUrl) {
		AppEngine instance = new AppEngine(applicationUrl);
		sInstanceLock.lock();
		sInstance = instance;
		sInstanceLock.unlock();
		return instance;
	}

	/**
	 * Private constructor to ensure only one instance can ever be created.
	 * 
	 * @param applicationUrl
	 *            App engine application's url. Ex:
	 *            https://gopalkri-testing.appspot.com
	 */
	private AppEngine(String applicationUrl) {
		if (applicationUrl.endsWith("/")) {
			mApplicationUrl = applicationUrl;
		} else {
			mApplicationUrl = applicationUrl + "/";
		}
	}

	/**
	 * Performs a HTTP get request on path.
	 * 
	 * @param path
	 *            Path on which to perform HTTP GET. Ex: pass in "test" to do a
	 *            HTTP GET on https://application-name/test.
	 * @return BufferedReader initialized with response.
	 * @throws HttpGetException
	 *             If there is an error in performing the request.
	 */
	public BufferedReader performHttpGet(String path) throws HttpGetException {
		try {
			HttpGet request = new HttpGet(mApplicationUrl + path);
			HttpResponse response = mHttpClient.execute(request);
			return new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new HttpGetException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new HttpGetException(e);
		}
	}

	/**
	 * Similar to performHttpGet, except also read the response into a String
	 * and return the String.
	 * 
	 * @param path
	 *            Path on which to perform HTTP GET. Ex: pass in "test" to do a
	 *            HTTP GET on https://appplication-name/test.
	 * @return Response from HTTP GET on path.
	 * @throws HttpGetException
	 *             If there is an error in performing the request.
	 */
	public String fetchResponseFromHttpGet(String path) throws HttpGetException {
		StringBuilder sb = new StringBuilder();
		String line;
		BufferedReader br = performHttpGet(path);
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new HttpGetException(e);
		}
		return sb.toString();
	}

	protected void fetchCookies(String authToken) throws CookieException {
		// Don't follow redirects. We only need cookies returned by
		// authentication URL. No need to follow the redirect it returns.
		mHttpClient.getParams().setBooleanParameter(
				ClientPNames.HANDLE_REDIRECTS, false);

		try {
			HttpGet request = new HttpGet(mApplicationUrl
					+ "_ah/login?continue=http://localhost/&auth=" + authToken);
			HttpResponse response = mHttpClient.execute(request);

			if (response.getStatusLine().getStatusCode() != 302) {
				String error = "Did not receive redirect! Response Code: "
						+ response.getStatusLine().getStatusCode()
						+ ". Message: "
						+ response.getStatusLine().getReasonPhrase();
				Log.e(TAG, error);
				mHttpClient.getParams().setBooleanParameter(
						ClientPNames.HANDLE_REDIRECTS, true);
				throw new CookieException(error);
			}

			for (Cookie cookie : mHttpClient.getCookieStore().getCookies()) {
				if (cookie.getName().equals("SACSID")) {
					Log.i(TAG, "Found SACSID cookie!");
					setReady();
				}
			}
		} catch (ClientProtocolException e) {
			throw new CookieException(e);
		} catch (IOException e) {
			throw new CookieException(e);
		} finally {
			mHttpClient.getParams().setBooleanParameter(
					ClientPNames.HANDLE_REDIRECTS, true);
		}
	}

	/**
	 * Atomically sets sReady to true.
	 */
	private void setReady() {
		sInstanceLock.lock();
		sReady = true;
		sInstanceLock.unlock();
	}
}
