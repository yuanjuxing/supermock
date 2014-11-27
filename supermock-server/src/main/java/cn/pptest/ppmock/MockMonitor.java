package cn.pptest.ppmock;


public interface MockMonitor {
	
	void onRequestArrived(Request request);
	
	void onResponseReceived(Request request, Response response);
	
}
