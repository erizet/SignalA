package com.zsoft.SignalA.Transport;

import com.zsoft.SignalA.ConnectionBase;

public interface ITransport {
	StateBase CreateInitialState(ConnectionBase connectionBase);
}
