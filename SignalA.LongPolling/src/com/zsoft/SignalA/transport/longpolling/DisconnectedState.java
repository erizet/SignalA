package com.zsoft.SignalA.transport.longpolling;

import java.util.concurrent.atomic.AtomicBoolean;

import com.zsoft.SignalA.Connection;
import com.zsoft.SignalA.ConnectionState;
import com.zsoft.SignalA.Transport.StateBase;

public class DisconnectedState extends StateBase {
	private AtomicBoolean requestStart = new AtomicBoolean(false);
	
	public DisconnectedState(Connection connection) {
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
	public boolean Send(CharSequence text) {
		return false;
	}

	@Override
	protected void OnRun() {
		if(requestStart.get())
		{
	        ConnectingState s = new ConnectingState(mConnection);
	        mConnection.SetNewState(s);
		}
	}

}
