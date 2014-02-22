package com.zsoft.signala.transport.longpolling;

import java.util.concurrent.atomic.AtomicBoolean;

import com.zsoft.signala.ConnectionBase;
import com.zsoft.signala.ConnectionState;
import com.zsoft.signala.transport.StateBase;
import com.zsoft.signala.SendCallback;

public class DisconnectedState extends StateBase {
	private AtomicBoolean requestStart = new AtomicBoolean(false);
	
	public DisconnectedState(ConnectionBase connection) {
		super(connection);
	}

	@Override
	public ConnectionState getState() {
		return ConnectionState.Disconnected;
	}

	@Override
	public void Start() {
		requestStart.set(true);
		Run();
	}

	@Override
	public void Stop() {
	}

	@Override
	public void Send(CharSequence text, SendCallback callback) {
		callback.OnError(new Exception("Not connected"));
	}

	@Override
	protected void OnRun() {
		if(requestStart.get())
		{
	        NegotiatingState s = new NegotiatingState(mConnection);
	        mConnection.SetNewState(s);
		}
	}

}
