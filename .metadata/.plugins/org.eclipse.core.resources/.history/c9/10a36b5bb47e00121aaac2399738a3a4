package com.zsoft.SignalA.transport.longpolling;

import com.zsoft.SignalA.Connection;
import com.zsoft.SignalA.Transport.ITransport;
import com.zsoft.SignalA.Transport.StateBase;

public class LongPollingTransport implements ITransport {

	@Override
	public StateBase CreateInitialState(Connection connection) {
		return new DisconnectedState(connection);
	}

}
