/**
 * This file is part of the AppEngineAndroid project.
 * 
 * Copyright (c) Gopal Sharma 2010.
 */
package com.gopalkri.appengineandroid;

/**
 * @author Gopal Sharma
 */
public class CookieException extends AppEngineException {

	/**
	 * Eclipse warns me to add this. Not sure why.
	 */
	private static final long serialVersionUID = 5373212168747591149L;

	public CookieException(String message) {
		super(message);
	}

	public CookieException(Exception innerException) {
		super(innerException);
	}

}
