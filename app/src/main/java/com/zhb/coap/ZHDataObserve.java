package com.zhb.coap;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class ZHDataObserve extends CoapResource {
	private String msg="";
	
	public ZHDataObserve(String name) {
		super(name);
		setObservable(true); // enable observing
		setObserveType(Type.NON); // configure the notification type to CONs
		getAttributes().setObservable(); // mark observable in the Link-Format
	}
	public  void change(String msg){
		this.msg=msg;
		if(getObserverCount()>0){
			changed();
		}
	}
	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.CONTENT,msg,MediaTypeRegistry.TEXT_PLAIN);
	}

	
}
