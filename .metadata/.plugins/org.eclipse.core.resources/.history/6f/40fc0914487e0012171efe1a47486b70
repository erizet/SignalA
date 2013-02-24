package com.zsoft.SignalA;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.zsoft.SignalA.Transport.ITransport;
import com.zsoft.SignalA.Transport.StateBase;

import android.content.Context;

public abstract class Connection {
	private Object mStateLock = new Object();
    private StateBase mCurrentState = null;
    private String mUrl = "";
	private String mConnectionId = null;
	private String mConnectionToken = null;
	private Context mContext;
	private String mMessageId = null;
	private ITransport mTransport;
	private final HashSet<String> mGroups = new HashSet<String>();
	
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
    		
    		if(state.getState() == ConnectionState.Disconnected)
    		{
    			setConnectionId(null);
    			setConnectionToken(null);
    		}
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
	
	public List<String> getGroups()
	{
		return new ArrayList<String>(mGroups);
	}

	public void ResetGroups(Collection<String> groups)
	{
		mGroups.clear();
		mGroups.addAll(groups);
	}
	
	public void ModifyGroups(Collection<String> addedGroups, Collection<String> removedGroups)
	{
		if(removedGroups!= null)
		{
			mGroups.removeAll(removedGroups);
		}
		if(addedGroups!=null)
		{
			mGroups.addAll(addedGroups);
		}
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
