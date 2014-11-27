package cn.pptest.ppmock.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.pptest.ppmock.Request;
import cn.pptest.ppmock.RequestHandler;
import cn.pptest.ppmock.Response;
import cn.pptest.ppmock.model.HttpServletRequestAdapter;
import cn.pptest.ppmock.stubbing.InMemoryStubMappings;
import cn.pptest.ppmock.stubbing.StubMappings;


public class MockHandler extends HttpServlet{

    private final Logger logger = LoggerFactory.getLogger(MockHandler.class);
    private final StubMappings stubMappings = new InMemoryStubMappings();
    
	RequestHandler requestHandler;
	
	public MockHandler(RequestHandler requestHandler) {
		this.requestHandler=requestHandler;
	}

	@Override
	public void service(ServletRequest httpServletRequest, ServletResponse httpServletResponse)
			throws ServletException, IOException {
		
		Request request = new HttpServletRequestAdapter((HttpServletRequest)httpServletRequest);
		logger.info("Received request: " + httpServletRequest.toString());

		Response response = requestHandler.handle(request);
	}

	

    
}
