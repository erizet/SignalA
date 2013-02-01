package com.zsoft.SignalA;

import com.zsoft.SignalA.Transport.ITransport;
import com.zsoft.SignalA.Transport.StateBase;

import android.content.Context;

public abstract class Connection {
	private Object mStateLock = new Object();
    private StateBase mCurrentState = null;
    private String mUrl = "";
	private String mConnectionId = "";
	private Context mContext;
	private String mMessageId;
	private ITransport mTransport;

    public Connection(String url, Context context, ITransport transport)
    {
    	mContext = context;
    	mTransport = transport;
        mCurrentState = mTransport.CreateInitialState(this);
        setUrl(url);
    }

    public void SetNewState(StateBase state)
    {
    	StateBase oldState;
    	synchronized (mStateLock) {
            oldState = mCurrentState;
    		mCurrentState = state;
		}

    	// Fire event
    	OnStateChanged(oldState, state);
    	
        state.Run();
    }

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		mUrl = url;
	}

	public Context getContext() {
		return mContext;
	}

	public String getConnectionId() {
		return mConnectionId ;
	}
	
	public void setConnectionId(String connectionId) {
		mConnectionId = connectionId;
	}

	public StateBase getCurrentState()
	{
    	synchronized (mStateLock) {
    		return mCurrentState;
    	}
	}
	
	public boolean VerifyProtocolVersion(String protocolVersion) {
		return protocolVersion.compareTo("1.1") == 0;
	}

	public String getMessageId() {
		return mMessageId;
	}

	public void setMessageId(String messageId) {
		mMessageId = messageId;
	}

	
	// Methods for the user to implement
	
	public abstract void OnError(Exception exception);
	public abstract void OnMessage(String message);
	public abstract void OnStateChanged(StateBase oldState, StateBase newState);

	public void Start() {
		getCurrentState().Start();
	}

	public void Stop() {
		getCurrentState().Stop();
	}

	public void Send(CharSequence text, SendCallback callback) {
		getCurrentState().Send(text, callback);
	}


    
}
