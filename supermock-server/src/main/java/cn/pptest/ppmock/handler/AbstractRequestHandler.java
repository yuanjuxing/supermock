package cn.pptest.ppmock.handler;

import java.util.List;

import cn.pptest.ppmock.Request;
import cn.pptest.ppmock.RequestEventSource;
import cn.pptest.ppmock.RequestHandler;
import cn.pptest.ppmock.Response;
import cn.pptest.ppmock.ResponseDefinition;
import cn.pptest.ppmock.ResponseRenderer;
import cn.pptest.ppmock.monitor.RequestListener;
import static com.google.common.collect.Lists.newArrayList;

public abstract class AbstractRequestHandler implements RequestHandler, RequestEventSource {

	protected List<RequestListener> listeners = newArrayList();
	protected final ResponseRenderer responseRenderer;
	
	public AbstractRequestHandler(ResponseRenderer responseRenderer) {
		this.responseRenderer = responseRenderer;
	}

	@Override
	public void addRequestListener(RequestListener requestListener) {
		listeners.add(requestListener);
	}

	@Override
	public Response handle(Request request) {
		ResponseDefinition responseDefinition = handleRequest(request);
		responseDefinition.setOriginalRequest(request);
		Response response = responseRenderer.render(responseDefinition);
		for (RequestListener listener: listeners) {
			listener.requestReceived(request, response);
		}
		
		return response;
	}
	
	protected abstract ResponseDefinition handleRequest(Request request);
}
