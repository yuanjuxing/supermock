package cn.pptest.ppmock.handler;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import cn.pptest.ppmock.RequestHandler;
import cn.pptest.ppmock.SuperMockMonitor;
import cn.pptest.ppmock.model.DefaultHttpRequest;
import cn.pptest.ppmock.model.HttpRequest;

public class SuperMockHandler extends SimpleChannelInboundHandler<FullHttpRequest>{

	
	private RequestHandler requestHandler;
	private SuperMockMonitor monitor;
	
	public SuperMockHandler() {

	}
	
	public SuperMockHandler(RequestHandler requestHandler,
			SuperMockMonitor monitor) {
		super();
		this.requestHandler = requestHandler;
		this.monitor = monitor;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		handleRequest((FullHttpRequest)msg);
		
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg)
			throws Exception {
		handleRequest((FullHttpRequest)msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		super.exceptionCaught(ctx, cause);
	}
	
	private void handleRequest(FullHttpRequest message) {
        HttpRequest request =new DefaultHttpRequest(message);
        System.out.println(request.getAbsoluteUrl());
        System.out.println(message.toString());
        monitor.onRequestArrived(request);
    }
	
}
