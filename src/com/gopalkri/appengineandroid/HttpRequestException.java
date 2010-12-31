/**
 * This file is part of the AppEngineAndroid project.
 * 
 * Copyright (c) Gopal Sharma 2010.
 */
package com.gopalkri.appengineandroid;

/**
 * @author Gopal Sharma
 */
public class HttpRequestException extends AppEngineException {
	
	/**
	 * Eclipse warns me to add this. Not sure why.
	 */
	private static final long serialVersionUID = 5793144026828818264L;

	public HttpRequestException(String message) {
		super(message);
	}
	
	public HttpRequestException(Exception innerException) {
		super(innerException);
	}

}
