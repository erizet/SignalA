package com.zsoft.signala;

import org.json.JSONObject;

import com.zsoft.signala.transport.ITransport;
import com.zsoft.signala.transport.StateBase;

import android.content.Context;

import java.util.Map;
import java.util.TreeMap;

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
	private Map<String, String> mHeaders = new TreeMap<String, String>();

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
		return protocolVersion.compareTo(getProtocolVersion()) == 0;
	}

    public String getProtocolVersion() { return "1.3"; }

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

    public void addHeader(String header, String value)
    {
        mHeaders.put(header, value);
    }

    public Map<String, String> getHeaders() { return mHeaders; }
	
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
