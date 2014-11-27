package cn.pptest.ppmock.handler;

import cn.pptest.ppmock.MockApp;
import cn.pptest.ppmock.Request;
import cn.pptest.ppmock.Response;
import cn.pptest.ppmock.ResponseDefinition;
import cn.pptest.ppmock.ResponseRenderer;
import cn.pptest.ppmock.stubbing.StubMappings;
import cn.pptest.ppmock.verification.RequestJournal;

import com.sun.jersey.spi.monitoring.RequestListener;


public class StubRequestHandler extends AbstractRequestHandler {

	private final MockApp mockApp;

	public StubRequestHandler(MockApp mockApp, ResponseRenderer responseRenderer) {
		super(responseRenderer);
		this.mockApp = mockApp;
	}
	
	@Override
	public ResponseDefinition handleRequest(Request request) {

		ResponseDefinition responseDef = mockApp.serveStubFor(request);

		return responseDef;
	}

	

	@Override
	public Response proxy(Object msg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response stub(Object msg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addRequestListener(RequestListener requestListener) {
		// TODO Auto-generated method stub
		
	}

	

}
