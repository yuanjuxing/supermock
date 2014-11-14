package cn.pptest.ppmock;

public interface RequestHandler {
	
	public static final String HANDLER_CLASS_KEY = "RequestHandlerClass";

	Response handle(Request request);
	
    Response proxy(Request request);
    
    Response stub(Request request);
}
