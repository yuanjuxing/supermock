package cn.pptest.ppmock;

import io.netty.bootstrap.ChannelFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.pptest.ppmock.handler.SuperMockHandler;
import cn.pptest.ppmock.runner.ServerGroup;
import cn.pptest.supermock.TransportProtocol;
import cn.pptest.supermock.UnknownTransportProtocolError;

public class SuperMockServer implements Server{
	
	private static final Logger LOG = LoggerFactory.getLogger(SuperMockServer.class);
	private ServerGroup serverGroup;
	private TransportProtocol transportProtocol;
    private final InetSocketAddress address;

    //private final Options options;


	public SuperMockServer(ServerGroup serverGroup,
			TransportProtocol transportProtocol, InetSocketAddress address) {
		super();
		this.serverGroup = serverGroup;
		this.transportProtocol = transportProtocol;
		this.address = address;
	}

	@Override
	public int port() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIdleConnectionTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setIdleConnectionTimeout(int idleConnectionTimeout) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public InetSocketAddress getListenAddress() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Server cloneServer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void start() {
		serverGroup.ensureProtocol(transportProtocol);
        ServerBootstrap serverBootstrap = new ServerBootstrap().group(
                serverGroup.getClientToProxyBossPools().get(transportProtocol),
                serverGroup.getClientToProxyWorkerPools().get(transportProtocol));

        ChannelInitializer<Channel> initializer = new ChannelInitializer<Channel>() {

			protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline=ch.pipeline();
                pipeline.addLast("codec", new HttpServerCodec(4096, 8192, 8192, false));
                pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
                pipeline.addLast("handler", new SuperMockHandler());
            };
        };
        switch (transportProtocol) {
        case TCP:
            LOG.info("Proxy listening with TCP transport");
            serverBootstrap.channelFactory(new ChannelFactory<ServerChannel>() {
                @Override
                public ServerChannel newChannel() {
                    return new NioServerSocketChannel();
                }
            });
            break;
        case UDT:
            LOG.info("Proxy listening with UDT transport");
            serverBootstrap.channelFactory(NioUdtProvider.BYTE_ACCEPTOR)
                    .option(ChannelOption.SO_BACKLOG, 10)
                    .option(ChannelOption.SO_REUSEADDR, true);
            break;
        default:
            throw new UnknownTransportProtocolError(transportProtocol);
        }
        serverBootstrap.childHandler(initializer);
        ChannelFuture future = serverBootstrap.bind(address)
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future)
                            throws Exception {
                        if (future.isSuccess()) {
                            registerChannel(future.channel());
                        }
                    }
                }).awaitUninterruptibly();
        Throwable cause = future.cause();
        if (cause != null) {
            throw new RuntimeException(cause);
        }
	}
	
    /**
     * Register a new {@link Channel} with this server, for later closing.
     * 
     * @param channel
     */
    protected void registerChannel(Channel channel) {
        this.serverGroup.getAllChannels().add(channel);
    }
	
    public static void main(String args[]){
    	ServerGroup sg=new ServerGroup("test");
    	SuperMockServer supermock=new SuperMockServer(sg, TransportProtocol.TCP, new InetSocketAddress(8989));
    	supermock.start();
    }
}
