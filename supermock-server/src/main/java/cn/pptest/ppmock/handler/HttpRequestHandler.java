package cn.pptest.ppmock.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.spi.monitoring.RequestListener;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObject;
import cn.pptest.ppmock.Request;
import cn.pptest.ppmock.RequestHandler;
import cn.pptest.ppmock.Response;
import cn.pptest.ppmock.SuperMockServer;
import cn.pptest.ppmock.model.DefaultHttpRequest;
import cn.pptest.ppmock.model.HttpRequest;

public class HttpRequestHandler implements RequestHandler{
	
	protected static final Logger LOG = LoggerFactory.getLogger(HttpRequestHandler.class);

    private volatile boolean tunneling = false;
    protected volatile long lastReadTime = 0;
    /**
     * Read is invoked automatically by Netty as messages arrive on the socket.
     * 
     * @param msg
     */
    protected void read(Object msg) {
        LOG.debug("Reading: {}", msg);

        lastReadTime = System.currentTimeMillis();

        if (tunneling) {
            // In tunneling mode, this connection is simply shoveling bytes
            //readRaw((ByteBuf) msg);
        } else {
            // If not tunneling, then we are always dealing with HttpObjects.
            //readHTTP((HttpObject) msg);
        }
    }
    
    

	
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



	@Override
	public Response handle(Request request) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void addRequestListener(RequestListener requestListener) {
		// TODO Auto-generated method stub
		
	}

}
