package com.zhb.coap;

import java.io.IOException;
import java.io.Serializable;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.proxy.DirectProxyCoapResolver;
import org.eclipse.californium.proxy.ProxyHttpServer;
import org.eclipse.californium.proxy.resources.ForwardingResource;
import org.eclipse.californium.proxy.resources.ProxyCoapClientResource;
import org.eclipse.californium.proxy.resources.ProxyHttpClientResource;

import com.zhb.coap.cmd.ICmd;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.IBinder;
import android.util.Log;

public class ZHCoapService extends Service {
	private static final String TAG="WLCoapService";
	private static final boolean DEBUG=true;
	private static final int HTTP_SERVER_PROXY_PORT = 8080;
	private static final int PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);
	
	private static CoapServer mCoapServer;
	private ProxyHttpServer mHttpServer = null;
	private ForwardingResource coap2coap = new ProxyCoapClientResource("coap2coap");
	private ForwardingResource coap2http = new ProxyHttpClientResource("coap2http");
	private ZHDataObserve mDataObserve;
	private ZHQueryResource mQueryResource;
	
	private boolean isConnected=false;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		initCoapResource();
		
		IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(connectReceiver, intentFilter);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Serializable serializable=intent.getSerializableExtra(Const.KEY_CMD);
		if(serializable != null && serializable instanceof ICmd){
			ICmd cmd=(ICmd) serializable;
			if(mDataObserve != null){
				mDataObserve.change(cmd.getCmdString());
			}
		}
		return START_STICKY;
	}
	
	
	/**
	 * 初始化CoapResource
	 */
	private void initCoapResource() {
		mCoapServer = new CoapServer(PORT);
		mCoapServer.add(coap2coap);
		mCoapServer.add(coap2http);
		mQueryResource=new ZHQueryResource(getApplicationContext(),"query");
		mDataObserve=new ZHDataObserve("notify");
		mCoapServer.add(mDataObserve);
		mCoapServer.add(mQueryResource);
	}
	
	/**
	 * 开启coap服务
	 */
	private void startCoapServer() {
		try {
			stopCoapServer();
			if (mHttpServer == null)
				try {
					mHttpServer = new ProxyHttpServer(HTTP_SERVER_PROXY_PORT);
				} catch (IOException e) {
					e.printStackTrace();
				}
			mHttpServer.setProxyCoapResolver(new DirectProxyCoapResolver(coap2coap));
			mCoapServer.start();
			if (DEBUG)
				Log.d(TAG, "CoapServer start");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 停止coap服务
	 */
	private void stopCoapServer() {
		if (mCoapServer != null) {
			mCoapServer.stop();
			if (DEBUG)
				Log.d(TAG, "CoapServer stop");
		}
	}

	private BroadcastReceiver connectReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			State state = State.DISCONNECTED;
			if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				ConnectivityManager contectivityMananger = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo mNetworkInfo = contectivityMananger.getActiveNetworkInfo();
				if (mNetworkInfo != null && mNetworkInfo.isConnected()) {
					state = mNetworkInfo.getState();
				}
			}
			if (state == State.CONNECTED &&!isConnected) {
				isConnected=true;
				startCoapServer();
			} else if (state == State.DISCONNECTED) {
				isConnected=false;
				stopCoapServer();
			}
		}
	};
	
	public void onDestroy() {
		unregisterReceiver(connectReceiver);
		super.onDestroy();
	};
}
