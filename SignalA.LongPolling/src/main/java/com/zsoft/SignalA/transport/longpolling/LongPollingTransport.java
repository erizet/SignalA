package com.zsoft.signala.transport.longpolling;

import com.zsoft.signala.ConnectionBase;
import com.zsoft.signala.transport.ITransport;
import com.zsoft.signala.transport.StateBase;

public class LongPollingTransport implements ITransport {

	@Override
	public StateBase CreateInitialState(ConnectionBase connection) {
		return new DisconnectedState(connection);
	}

}
