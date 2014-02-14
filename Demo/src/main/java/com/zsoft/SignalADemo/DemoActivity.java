package com.zsoft.SignalADemo;

import java.util.HashMap;
import java.util.Map;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.util.Constants;
import com.zsoft.signala.transport.StateBase;
import com.zsoft.signala.transport.longpolling.LongPollingTransport;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zsoft.signala.SendCallback;

public class DemoActivity extends Activity {

	private AQuery aq;
	private com.zsoft.signala.Connection con = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
    
		aq = new AQuery(this);
//	    String url = "http://10.0.2.2:8081/echo/";
	    String url = "http://10.0.2.2:8888/echo/";
	    //String url = "http://ipv4.fiddler:8081/echo/";

	    aq.id(R.id.btnConnect).clicked(this, "startSignalA");
	    aq.id(R.id.btnDisconnect).clicked(this, "stopSignalA");
	    aq.id(R.id.btnSend).clicked(this, "sendMessage");
	    aq.id(R.id.etText).text("zsoft");
		aq.id(R.id.tvReceivedMessage).text("No message yet");
		aq.id(R.id.btnDelayTest).clicked(this, "delayTest");
	    
	    con = new com.zsoft.signala.Connection(url, this, new LongPollingTransport()) {

			@Override
			public void OnError(Exception exception) {
	            Toast.makeText(DemoActivity.this, "On error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
			}

			@Override
			public void OnMessage(String message) {
				aq.id(R.id.tvReceivedMessage).text(message);
	            Toast.makeText(DemoActivity.this, "Message: " + message, Toast.LENGTH_LONG).show();
			}

			@Override
			public void OnStateChanged(StateBase oldState, StateBase newState) {
				aq.id(R.id.tvStatus).text(oldState.getState() + " -> " + newState.getState());
			}
		};
	}
	
	public void startSignalA()
	{
		if(con!=null)
			con.Start();
	}
	
	public void stopSignalA()
	{
		if(con!=null)
			con.Stop();
	}
	
	public void sendMessage()
	{
		if(con!=null &&			
			aq.id(R.id.etText).getText().length()>0)
		{
			con.Send(aq.id(R.id.etText).getText(), new SendCallback() {
				public void OnError(Exception ex)
				{
		            Toast.makeText(DemoActivity.this, "Error when sending: " + ex.getMessage(), Toast.LENGTH_LONG).show();
				}
				public void OnSent(CharSequence message)
				{
		            Toast.makeText(DemoActivity.this, "Sent: " + message, Toast.LENGTH_SHORT).show();
				}
				
			});
		}
	}
	
	public void delayTest(View button){

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("milliseconds", "10000");


    	//update the text
        aq.id(R.id.tvStatus).clear().text("Starts....");
        
        String url = "http://delaytest.apphb.com/Delay";

        
		AjaxCallback<String> cb = new AjaxCallback<String>() {
            @Override
            public void callback(String url, String html, AjaxStatus status) {
            	if(status.getCode()==200)
            	{
            		aq.id(R.id.tvStatus).clear().text(html);

            		
            		// Second time
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("milliseconds", "110000");


                	//update the text
                    aq.id(R.id.tvStatus).clear().text("Second time we starts....");
                    
                    url = "http://delaytest.apphb.com/Delay";

                    
            		AjaxCallback<String> cb = new AjaxCallback<String>() {
                        @Override
                        public void callback(String url, String html, AjaxStatus status) {
                        	if(status.getCode()==200)
                        	{
                        		aq.id(R.id.tvStatus).clear().text(html);
                        		
                        	}
                           	else
                        	{
                        		aq.id(R.id.tvStatus).clear().text("Fel. Duration: " + status.getDuration() );
                        		
                        	}
                       }
            		};
            		cb.url(url).type(String.class).expire(-1).params(params).method(Constants.METHOD_POST).timeout(115000);
            		aq.ajax(cb);

            		
            		
            		
            	}
               	else
            	{
            		aq.id(R.id.tvStatus).clear().text("Fel. Duration: " + status.getDuration() );
            		
            	}
           }
		};
		
		//cb.timeout(12000);
		//aq.ajax(url, params, String.class, cb);

		cb.url(url).type(String.class).expire(-1).params(params).method(Constants.METHOD_POST).timeout(25000);
		aq.ajax(cb);

    }

	
}