package com.zsoft.SignalA.transport.longpolling;

import com.zsoft.SignalA.ConnectionBase;

public abstract class StopableStateWithCallback extends StopableState {
	protected Object mCallbackLock = new Object();
	//protected AjaxCallback<JSONObject> mCurrentCallback = null;
	
	public StopableStateWithCallback(ConnectionBase connection) {
		super(connection);
	}

	@Override
	public void Stop() {
		requestStop.set(true);
		synchronized (mCallbackLock) {
//			if(mCurrentCallback!=null)
//				mCurrentCallback.abort();
		}
		Run();	
	}

	@Override
    public void Run()
    {
    	if (mIsRunning.compareAndSet(false, true)) {
            try
            {
                OnRun();
            }
            finally
            {
            	//mIsRunning.set(false); Do this in the callback instead
            }
    	}
    }

}
