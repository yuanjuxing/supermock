package cn.pptest.ppmock.handler;


import static com.google.common.base.Charsets.UTF_8;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.URLDecoder.decode;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.pptest.ppmock.MockApp;
import cn.pptest.ppmock.Request;
import cn.pptest.ppmock.RequestHandler;
import cn.pptest.ppmock.RequestMethod;
import cn.pptest.ppmock.Response;
import cn.pptest.ppmock.model.HttpServletRequestAdapter;
import cn.pptest.ppmock.stubbing.InMemoryStubMappings;
import cn.pptest.ppmock.stubbing.StubMappings;


public class MockHandler extends HttpServlet{	

    private final Logger logger = LoggerFactory.getLogger(MockHandler.class);
	
	public static final String SHOULD_FORWARD_TO_FILES_CONTEXT = "shouldForwardToFilesContext";

	private static final long serialVersionUID = -6602042274260495538L;

    private final StubMappings stubMappings = new InMemoryStubMappings();
    
	RequestHandler requestHandler;
	private String wiremockFileSourceRoot = "/";
	private boolean shouldForwardToFilesContext;
	
	public MockHandler(RequestHandler requestHandler) {
		this.requestHandler=requestHandler;
	}

	
	@Override
	public void init(ServletConfig config) {
	    ServletContext context = config.getServletContext();
	    shouldForwardToFilesContext = true;
	    
	    if (context.getInitParameter("WireMockFileSourceRoot") != null) {
	        wiremockFileSourceRoot = context.getInitParameter("WireMockFileSourceRoot");
	    }

	}

	@Override
	public void service(ServletRequest httpServletRequest, ServletResponse httpServletResponse)
			throws ServletException, IOException {
		
		Request request = new HttpServletRequestAdapter((HttpServletRequest)httpServletRequest);
		logger.info("Received request: " + httpServletRequest.toString());

		Response response = requestHandler.handle(request);
		if (response.wasConfigured()) {
		    response.applyTo((HttpServletResponse)httpServletResponse);
		} else if (request.getMethod() == RequestMethod.GET && shouldForwardToFilesContext) {
		    forwardToFilesContext((HttpServletRequest)httpServletRequest, (HttpServletResponse)httpServletResponse, request);
		} else {
			((HttpServletResponse) httpServletResponse).sendError(HTTP_NOT_FOUND);
		}
	}

    private void forwardToFilesContext(HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse, Request request) throws ServletException, IOException {
        String forwardUrl = wiremockFileSourceRoot + MockApp.FILES_ROOT + request.getUrl();
        RequestDispatcher dispatcher = httpServletRequest.getRequestDispatcher(decode(forwardUrl, UTF_8.name()));
        dispatcher.forward(httpServletRequest, httpServletResponse);
    }
	

    
}
