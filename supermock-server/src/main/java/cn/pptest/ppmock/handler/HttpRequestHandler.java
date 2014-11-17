package cn.pptest.ppmock.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObject;
import cn.pptest.ppmock.RequestHandler;
import cn.pptest.ppmock.Response;
import cn.pptest.ppmock.SuperMockServer;
import cn.pptest.ppmock.model.DefaultHttpRequest;
import cn.pptest.ppmock.model.HttpRequest;

public class HttpRequestHandler implements RequestHandler{
	
	protected static final Logger LOG = LoggerFactory.getLogger(HttpRequestHandler.class);

    private volatile boolean tunneling = false;
    protected volatile long lastReadTime = 0;
    

   
    
    

	@Override
	public Response handle(Object msg) {
		if(msg instanceof FullHttpRequest){			
	        HttpRequest request =new DefaultHttpRequest((FullHttpRequest)msg);
	        System.out.println(request.getAbsoluteUrl());
	        System.out.println(msg.toString());
		}
		return null;
	}

	@Override
	public Response proxy(Object request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response stub(Object request) {
		// TODO Auto-generated method stub
		return null;
	}

}
