package com.zsoft.signala.transport;

import com.zsoft.signala.ConnectionBase;

public interface ITransport {
	StateBase CreateInitialState(ConnectionBase connectionBase);
}
