package cn.pptest.ppmock;


public interface SuperMockMonitor {
	
	void onRequestArrived(Request request);
	
	void onResponseReceived(Request request, Response response);
	
}
