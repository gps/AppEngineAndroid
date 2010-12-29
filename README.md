# About
AppEngineAndroid is an Android library (with test application) which makes
it easy to perform authenticated requests to a Google App Engine
application.

# Author
Gopal Sharma <gopalkrishnaps@gmail.com>

# Licensing
Licensed under modified version of the MIT X license
(http://www.opensource.org/licenses/mit-license.php). See LICENSE file.

Although this is not part of the license (I'm not sure whether I want to
add it), I request that you notify me if you do in fact use this code in
any way. It always helps to know that someone else is using my work :)

# Usage
All java source files required are in the package com.gopalkri.appengineandroid.

You will need to add a few things to your AndroidManifest.xml. 

* Add permissions
* * android.permission.GET_ACCOUNTS
* * android.permission.USE_CREDENTIALS
* * android.permission.INTERNET
* Add activities
* * com.gopalkri.appengineandroid.AppEngineActivity
* * com.gopalkri.appengineandroid.AuthenticationActivity

You will also need to add a layout xml file to your project: res/layout/appengine.xml.

The interface to AppEngineAndroid will be exposed via the AppEngine class. AppEngine
is a Singleton, but it's construction is non standard. The way to construct the
AppEngine instance is to start AppEngineActivity for result with requestCode
AppEngineActivity.SETUP_AUTH. AppEngineActivity will do what is needed and return.
It will call setResult with either AppEngineActivity.RESULT_OK or
AppEngineActivity.RESULT_FAILED. Once AppEngineActivity returns, you can get an
instance of AppEngine via it's static method getInstance(). Note that getInstance()
will return null if something went wrong with its creation. 

See the com.gopalkri.appengineandroid.test.Home Activity for an example.
