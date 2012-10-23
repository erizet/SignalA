**SignalA**

SignalA is a SignalR-client for Android. It's implemented as a Android-library. At this moment is long polling the only implemented transport. Long polling is a separate library.

**How to use?**

To use SignalA you have to get a copy of the two libraries, SignalA and SignalA.LongPolling. Add a reference to SignalA.LongPolling under Properties > Android > Library.
Then add the following code to your activity.

      String url = "http://<address to your SignalR-server>";
      con = new com.zsoft.SignalA.Connection(url, this, new LongPollingTransport()) {

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


That's it!

Currently is Groups and Hubs not implemented, only PersistentConnection. I'll be more than happy to get contributions!!!

Disclaimer: I'm a .NET-coder and this is my first attemt to write a Java-library so bare with me.