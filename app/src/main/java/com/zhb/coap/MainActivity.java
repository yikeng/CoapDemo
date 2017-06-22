package com.zhb.coap;

import com.zhb.coap.cmd.CmdNext;
import com.zhb.coap.cmd.CmdPause;
import com.zhb.coap.cmd.CmdPlay;
import com.zhb.coap.cmd.CmdPrevious;
import com.zhb.coap.cmd.ICmd;
import com.zhb.coapdemo.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener{

	private TextView mReceiverText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startCoapService();
		
		setContentView(R.layout.main);
		mReceiverText=(TextView) findViewById(R.id.text);
	}
	@Override
	protected void onStart() {
		IntentFilter filter=new IntentFilter(Const.ACTION_COAP_MSG);
		registerReceiver(coapMessageReceiver, filter);
		super.onStart();
	}
	@Override
	protected void onStop() {
		unregisterReceiver(coapMessageReceiver);
		super.onStop();
	}
	
	private void startCoapService(){
		startService(new Intent(this,ZHCoapService.class));
	}
	private void send(ICmd cmd){
		Intent intnet=new Intent(this,ZHCoapService.class);
		intnet.putExtra(Const.KEY_CMD, cmd);
		startService(intnet);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnPlay:
			send(new CmdPlay());
			break;
		case R.id.btnNext:
			send(new CmdNext());
			break;
		case R.id.btnPause:
			send(new CmdPause());
			break;
		case R.id.btnPrevious:
			send(new CmdPrevious());
			break;
		}
	}
	
	private BroadcastReceiver coapMessageReceiver=new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String text=intent.getStringExtra(Const.KEY_MSG);
			mReceiverText.append(text);
			mReceiverText.append("\n");
		}
		
	};
}
