package cn.pptest.ppmock;

public interface RequestHandler {
	
	public static final String HANDLER_CLASS_KEY = "RequestHandlerClass";

	Response handle(Object msg);
	
    Response proxy(Object msg);
    
    Response stub(Object msg);
}
