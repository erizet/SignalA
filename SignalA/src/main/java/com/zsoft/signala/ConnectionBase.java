package com.zsoft.SignalA;

import org.json.JSONObject;

import com.zsoft.SignalA.Transport.ITransport;
import com.zsoft.SignalA.Transport.StateBase;

import android.content.Context;

public abstract class ConnectionBase {
	private Object mStateLock = new Object();
    private StateBase mCurrentState = null;
    private String mUrl = "";
	private String mConnectionId = null;
	private String mConnectionToken = null;
	private Context mContext;
	private String mMessageId = null;
	private ITransport mTransport;
	private String mGroupsToken = null;
	private String mQueryString = null;

    public ConnectionBase(String url, Context context, ITransport transport, String queryString)
    {
    	this(url, context, transport);
    	setQueryString(queryString);
    }
	
	public ConnectionBase(String url, Context context, ITransport transport)
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
    		
    		if(state.getState() == ConnectionState.Disconnected)
    		{
    			setConnectionId(null);
    			setConnectionToken(null);
    		}
		}

        state.Run();

        // Fire event
    	OnStateChanged(oldState, state);
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

    private void setQueryString(String queryString) {
		mQueryString = queryString;
	}

    public String getQueryString() {
    	return mQueryString;
    }

	public String getConnectionId() {
		return mConnectionId ;
	}
	
	public void setConnectionId(String connectionId) {
		mConnectionId = connectionId;
	}

	public String getConnectionToken() {
		return mConnectionToken;
	}
	
	public void setConnectionToken(String connectionToken) {
		mConnectionToken = connectionToken;
	}

	public StateBase getCurrentState()
	{
    	synchronized (mStateLock) {
    		return mCurrentState;
    	}
	}
	
	public boolean VerifyProtocolVersion(String protocolVersion) {
		return protocolVersion.compareTo("1.2") == 0;
	}

	public String getMessageId() {
		return mMessageId;
	}

	public void setMessageId(String messageId) {
		mMessageId = messageId;
	}
	
	public String getGroupsToken()
	{
		return mGroupsToken;
	}

	public void setGroupsToken(String groupsToken)
	{
		mGroupsToken = groupsToken;
	}
	
	public void setMessage(JSONObject response) 
	{
		OnMessage(response.toString());
	}

	public void setError(Exception exception)
	{
		OnError(exception);
	}
	
	// Methods for the user to implement
	
	protected abstract void OnError(Exception exception);
	protected abstract void OnMessage(String message);
	protected abstract void OnStateChanged(StateBase oldState, StateBase newState);
	public String OnSending()
	{
		return null;
	}

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
