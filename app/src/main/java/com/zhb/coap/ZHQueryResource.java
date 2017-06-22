package com.zhb.coap;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import android.content.Context;
import android.content.Intent;

public class ZHQueryResource extends CoapResource {
	private Context mContext;
	public ZHQueryResource(Context context,String name) {
		super(name);
		mContext=context;
	}
	
	@Override
	public void handleGET(CoapExchange exchange) {
		String reqText=exchange.getRequestText();
		String reqQueryString=exchange.getRequestOptions().getUriQueryString();
		handleRequest(reqText, reqQueryString);
		exchange.respond(ResponseCode.CONTENT, "{\"code\":200,\"msg\":\"ok\"}", MediaTypeRegistry.APPLICATION_JSON);
	}
	
	@Override
	public void handlePOST(CoapExchange exchange) {
		String reqText=exchange.getRequestText();
		String reqQueryString=exchange.getRequestOptions().getUriQueryString();
		handleRequest(reqText, reqQueryString);
		exchange.respond(ResponseCode.CONTENT, "{\"code\":200,\"msg\":\"ok\"}", MediaTypeRegistry.APPLICATION_JSON);
	}
	
	private void handleRequest(String reqText,String reqQueryString){
		Intent intent=new Intent(Const.ACTION_COAP_MSG);
		intent.putExtra(Const.KEY_MSG, reqText+","+reqQueryString);
		mContext.sendBroadcast(intent);
	}

	
}
