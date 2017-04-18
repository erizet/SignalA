[sr]: http://signalr.net/
[aq]: https://github.com/androidquery/androidquery
[bhc]: https://code.google.com/p/basic-http-client/
[calc]: http://signalrcalc.apphb.com/

# SignalA #

# Important note #
I will no longer update this library.

## Updates ##

### 2014-02-27 version 0.20 ###
- Updated protocol version to 1.3, which is used by SignalR 2.0.
- Changed AndroidManifest to lowercase to fix https://github.com/erizet/SignalA/issues/39

### 2014-02-14 version 0.16 ###
- Set timeouts before reconnecting, [9d2af153444e318ed7611f5b28d76729c3b0ac57](https://github.com/mrbirdman/SignalA/commit/9d2af153444e318ed7611f5b28d76729c3b0ac57). Thanks to [mrbirdman](https://github.com/mrbirdman).
- Changed package names to lowercase.

### 2014-01-17 version 0.15 ###
- Added support for custom headers, [52a75bca5f15a0d242c3f6acd02cde7637e6582b](https://github.com/erizet/SignalA/commit/52a75bca5f15a0d242c3f6acd02cde7637e6582b). Thanks to [egoetschalckx](https://github.com/egoetschalckx).

### 2014-01-01 version 0.14 ###
- Converted project to Android Studio and Gradle.
- Published SignalA on Maven Central.

### 2013-08-12 version 0.13 ###
- I've introduced the ability to send a custom querystring. https://github.com/erizet/SignalA/issues/24
- Changed when OnStateChanged is fired. Now the event is fired AFTER the new state is run. See https://github.com/erizet/SignalA/issues/21#issuecomment-22296485 

### 2013-07-31 version 0.12 ###
- Fixed handling of groupstoken.
- Removed .metadata folder from repo.
- Use 'poll' instead of 'reconect' in urls for polling the server.

### 2013-06-27 version 0.11 ###
Two fixes in this release.
- https://github.com/erizet/SignalA/issues/11
- https://github.com/erizet/SignalA/issues/22


### 2013-02-20 version 0.10beta released ###
I've changed a coupe of things in this release.
- I have changed the transport. LongPolling now uses [basic-http-client][bhc] instead of [Aquery][aq] for http communication. I've removed all dependencies on [Aquery][aq].
- Hubs is now implemented. Checkout my implementation and say what you think about it. Calling functions on server from client, and calling functions on client from server is supported. NOTE! State is not implemented yet.
- I've included a new sample, HubDemo, in the repo. It shows how you can implement hubs.
- I have also set up a [SignalR][sr] server on Appharbor so HubDemo can talk to it. You'll find it [here][calc].


### 2013-02-20 version 0.9beta released ###
Version 0.9beta uses the SignalR-protocol version 1.2 which is used in the 1.0 release of SignalR.
It has also (beta)support for groups.

## Description ##
SignalA is a [SignalR][sr]-client for Android. It's implemented as a Android-library. At this moment is long polling the only implemented transport. Long polling is a separate library.
Http-requests in the Long polling library is using [basic-http-client][bhc].

## Add SignalA to your project ##
SignalA is published on Maven Central as a ARR. If you're using Android Studio/Gradle all you have to do is add it on your gradle build:

	dependencies {
	    compile 'com.github.erizet.signala:signala-longpolling:0.20'
	}

## How to use? ##
Add the following code to your activity.

      String url = "http://<address to your SignalR-server>";
      con = new com.zsoft.signala.Connection(url, this, new LongPollingTransport()) {

			@Override
			public void OnError(Exception exception) {
	            Toast.makeText(DemoActivity.this, "On error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
			}

			@Override
			public void OnMessage(String message) {
	            Toast.makeText(DemoActivity.this, "Message: " + message, Toast.LENGTH_LONG).show();
			}

			@Override
			public void OnStateChanged(StateBase oldState, StateBase newState) {
			}
		};

To start and stop the SignalA connection use code similar to the following.

	public void startSignalA()
	{
		if(con!=null)
			con.Start();
	}
	
	public void stopSignalA()
	{
		if(con!=null)
			con.Stop();
	}

That's it!

For a complete sample see the Demo-project.

## Limitations ##
Hubs don't support State yet.

## Contributions ##
I'll be more than happy to get contributions!!!

## Are you using SignalA? ##
If you're using SignalA I would appreciate to hear from you - where and how are you using it?

## License ##

    Copyright 2013 Erik Zetterqvist
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

Disclaimer: I'm a .NET-coder and this is my first attemt to write a Java-library so bare with me.
