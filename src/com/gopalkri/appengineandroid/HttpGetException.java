/**
 * This file is part of the AppEngineAndroid project.
 * 
 * Copyright (c) Gopal Sharma 2010.
 */
package com.gopalkri.appengineandroid;

/**
 * @author Gopal Sharma
 */
public class HttpGetException extends AppEngineException {
	
	/**
	 * Eclipse warns me to add this. Not sure why.
	 */
	private static final long serialVersionUID = 5793144026828818264L;

	public HttpGetException(String message) {
		super(message);
	}
	
	public HttpGetException(Exception innerException) {
		super(innerException);
	}

}
