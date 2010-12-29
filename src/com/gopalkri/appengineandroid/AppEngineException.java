/**
 * This file is part of the AppEngineAndroid project.
 * 
 * Copyright (c) Gopal Sharma 2010.
 */
package com.gopalkri.appengineandroid;

/**
 * @author Gopal Sharma
 */
public class AppEngineException extends Exception {
	
	/**
	 * Eclipse warns me to add this. Not sure why.
	 */
	private static final long serialVersionUID = -4110290260526062166L;

	public AppEngineException(String message) {
		super(message);
	}
	
	public AppEngineException(Exception innerException) {
		super(innerException);
	}
	
}
