package cn.pptest.ppmock;

import com.sun.jersey.spi.monitoring.RequestListener;

public interface RequestHandler {
	
	public static final String HANDLER_CLASS_KEY = "RequestHandlerClass";

	
    Response proxy(Object msg);
    
    Response stub(Object msg);

	Response handle(Request request);

	void addRequestListener(RequestListener requestListener);
}
