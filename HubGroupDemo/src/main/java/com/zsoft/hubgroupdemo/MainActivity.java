package com.zsoft.hubgroupdemo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.zsoft.signala.hubs.HubConnection;
import com.zsoft.signala.hubs.HubInvokeCallback;
import com.zsoft.signala.hubs.HubOnDataCallback;
import com.zsoft.signala.hubs.IHubProxy;
import com.zsoft.signala.transport.StateBase;
import com.zsoft.signala.transport.longpolling.LongPollingTransport;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.OperationApplicationException;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private EditText mGroupNameTextBox;
	private EditText mMessageTextBox;
	private Button mJoinGroupButton;
	private Button mBroadcastToAllButton;
	private Button mBroadcastToGroupButton;
	
	protected HubConnection con = null;
	protected IHubProxy hub = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
	    mGroupNameTextBox = (EditText) this.findViewById(R.id.groupName);
	    mMessageTextBox	= (EditText) this.findViewById(R.id.message_text);
	    mJoinGroupButton = (Button) this.findViewById(R.id.joinGroupButton);
	    mJoinGroupButton.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
	  		JoinGroup(mGroupNameTextBox.getText().toString());
	      }
	    });
	    mBroadcastToAllButton = (Button) this.findViewById(R.id.broadcastToAllButton);
	    mBroadcastToAllButton.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  		HubInvokeCallback callback = new HubInvokeCallback() {
					@Override
					public void OnResult(boolean succeeded, String response) {
						Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
					}
					
					@Override
					public void OnError(Exception ex) {
						Toast.makeText(MainActivity.this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
					}
				};
				
				List<String> args = new ArrayList<String>(1);
				args.add(mMessageTextBox.getText().toString());
				hub.Invoke("SendMessageToAll", args, callback);

		      }
	    });
	    mBroadcastToGroupButton = (Button) this.findViewById(R.id.broadcastToGroupButton);
	    mBroadcastToGroupButton.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  		HubInvokeCallback callback = new HubInvokeCallback() {
					@Override
					public void OnResult(boolean succeeded, String response) {
						Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
					}
					
					@Override
					public void OnError(Exception ex) {
						Toast.makeText(MainActivity.this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
					}
				};
				
				List<String> args = new ArrayList<String>(2);
				args.add(mGroupNameTextBox.getText().toString());
				args.add(mMessageTextBox.getText().toString());
				hub.Invoke("SendMessageToGroup", args, callback);
	    	      	  
	      }
	    });

	    Connect(Uri.parse("http://signalrgrouptest.azurewebsites.net/"));
	}

	
	public void Connect(Uri address) {
		
		con = new HubConnection(address.toString(), this, new LongPollingTransport())
		{
			@Override
			public void OnStateChanged(StateBase oldState, StateBase newState) {
				//tvStatus.setText(oldState.getState() + " -> " + newState.getState());
				
				switch(newState.getState())
				{
					case Connected:
						mJoinGroupButton.setEnabled(true);
						mBroadcastToAllButton.setEnabled(true);
						mBroadcastToGroupButton.setEnabled(true);
						
						JoinGroup("test");
						break;
					default:
						mJoinGroupButton.setEnabled(false);
						mBroadcastToAllButton.setEnabled(false);
						mBroadcastToGroupButton.setEnabled(false);
						break;
				}
			}
				
			@Override
			public void OnError(Exception exception) {
	            Toast.makeText(MainActivity.this, "On error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
			}

		};
		
		try {
			hub = con.CreateHubProxy("testhub");
		} catch (OperationApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		hub.On("DisplayMessage", new HubOnDataCallback() 
		{
			@Override
			public void OnReceived(JSONArray args) {
				for(int i=0; i<args.length(); i++)
				{
					Toast.makeText(MainActivity.this, "New message\n" + args.opt(i).toString(), Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		
		con.Start();
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	private void JoinGroup(String groupName) {
		HubInvokeCallback callback = new HubInvokeCallback() {
			@Override
			public void OnResult(boolean succeeded, String response) {
				if(succeeded)
				{
					Toast.makeText(MainActivity.this, "Joined group", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(MainActivity.this, "Failed to join group", Toast.LENGTH_SHORT).show();
				}
			}
			
			@Override
			public void OnError(Exception ex) {
				Toast.makeText(MainActivity.this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
			}
		};
		
		List<String> args = new ArrayList<String>(1);
		args.add(groupName);
		hub.Invoke("JoinGroup", args, callback);
	}

}
