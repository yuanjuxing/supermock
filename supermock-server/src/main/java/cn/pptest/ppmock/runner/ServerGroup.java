package cn.pptest.ppmock.runner;

import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.pptest.supermock.TransportProtocol;
import cn.pptest.supermock.UnknownTransportProtocolError;
import cn.pptest.supermock.impl.DefaultHttpProxyServer;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.util.concurrent.GlobalEventExecutor;


public class ServerGroup {
	
	  private static final Logger LOG = LoggerFactory
	            .getLogger(DefaultHttpProxyServer.class);
	
	private static final int INCOMING_ACCEPTOR_THREADS = 2;
    private static final int INCOMING_WORKER_THREADS = 8;
    private static final int OUTGOING_WORKER_THREADS = 8;
    
    private String name;
    
    /**
     * Keep track of all channels for later shutdown.
     */
    private final ChannelGroup allChannels = new DefaultChannelGroup(
            "supermock-server", GlobalEventExecutor.INSTANCE);
    /**
     * These {@link EventLoopGroup}s accept incoming connections to the
     * proxies. A different EventLoopGroup is used for each
     * TransportProtocol, since these have to be configured differently.
     * 
     * Thread safety: Only accessed while synchronized on the server group.
     */

    /**
     * These {@link EventLoopGroup}s are used for making outgoing
     * connections to servers. A different EventLoopGroup is used for each
     * TransportProtocol, since these have to be configured differently.
     */
    private final Map<TransportProtocol, EventLoopGroup> proxyToServerWorkerPools = new HashMap<TransportProtocol, EventLoopGroup>();

    private volatile boolean stopped = false;
    
    private final Map<TransportProtocol, EventLoopGroup> clientToProxyBossPools = new HashMap<TransportProtocol, EventLoopGroup>();


    public ServerGroup(String name) {
        this.name = name;

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            public void uncaughtException(final Thread t, final Throwable e) {
                LOG.error("Uncaught throwable", e);
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                stop();
            }
        }));
    }

    public synchronized void ensureProtocol(
            TransportProtocol transportProtocol) {
        if (!clientToProxyWorkerPools.containsKey(transportProtocol)) {
            initializeTransport(transportProtocol);
        }
    }

    private void initializeTransport(TransportProtocol transportProtocol) {
        SelectorProvider selectorProvider = null;
        switch (transportProtocol) {
        case TCP:
            selectorProvider = SelectorProvider.provider();
            break;
        case UDT:
            selectorProvider = NioUdtProvider.BYTE_PROVIDER;
            break;
        default:
            throw new UnknownTransportProtocolError(transportProtocol);
        }

        NioEventLoopGroup inboundAcceptorGroup = new NioEventLoopGroup(
                INCOMING_ACCEPTOR_THREADS,
                new CategorizedThreadFactory("ClientToProxyAcceptor"),
                selectorProvider);
        NioEventLoopGroup inboundWorkerGroup = new NioEventLoopGroup(
                INCOMING_WORKER_THREADS,
                new CategorizedThreadFactory("ClientToProxyWorker"),
                selectorProvider);
        inboundWorkerGroup.setIoRatio(90);
        NioEventLoopGroup outboundWorkerGroup = new NioEventLoopGroup(
                OUTGOING_WORKER_THREADS,
                new CategorizedThreadFactory("ProxyToServerWorker"),
                selectorProvider);
        outboundWorkerGroup.setIoRatio(90);
        this.clientToProxyBossPools.put(transportProtocol,
                inboundAcceptorGroup);
        this.clientToProxyWorkerPools.put(transportProtocol,
                inboundWorkerGroup);
        this.proxyToServerWorkerPools.put(transportProtocol,
                outboundWorkerGroup);
    }

    synchronized private void stop() {
        LOG.info("Shutting down proxy");
        if (stopped) {
            LOG.info("Already stopped");
            return;
        }

        LOG.info("Closing all channels...");

        final ChannelGroupFuture future = allChannels.close();
        future.awaitUninterruptibly(10 * 1000);

        if (!future.isSuccess()) {
            final Iterator<ChannelFuture> iter = future.iterator();
            while (iter.hasNext()) {
                final ChannelFuture cf = iter.next();
                if (!cf.isSuccess()) {
                    LOG.info(
                            "Unable to close channel.  Cause of failure for {} is {}",
                            cf.channel(),
                            cf.cause());
                }
            }
        }

        LOG.info("Shutting down event loops");
        List<EventLoopGroup> allEventLoopGroups = new ArrayList<EventLoopGroup>();
        allEventLoopGroups.addAll(clientToProxyBossPools.values());
        allEventLoopGroups.addAll(clientToProxyWorkerPools.values());
        allEventLoopGroups.addAll(proxyToServerWorkerPools.values());
        for (EventLoopGroup group : allEventLoopGroups) {
            group.shutdownGracefully();
        }

        for (EventLoopGroup group : allEventLoopGroups) {
            try {
                group.awaitTermination(60, TimeUnit.SECONDS);
            } catch (InterruptedException ie) {
                LOG.warn("Interrupted while shutting down event loop");
            }
        }

        stopped = true;

        LOG.info("Done shutting down proxy");
    }

    private class CategorizedThreadFactory implements ThreadFactory {
        private String category;
        private int num = 0;

        public CategorizedThreadFactory(String category) {
            super();
            this.category = category;
        }

        public Thread newThread(final Runnable r) {
            final Thread t = new Thread(r,
                    name + "-" + category + "-" + num++);
            return t;
        }
    }
    

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isStopped() {
		return stopped;
	}

	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}

	public ChannelGroup getAllChannels() {
		return allChannels;
	}

	public Map<TransportProtocol, EventLoopGroup> getClientToProxyBossPools() {
		return clientToProxyBossPools;
	}

	public Map<TransportProtocol, EventLoopGroup> getClientToProxyWorkerPools() {
		return clientToProxyWorkerPools;
	}

	public Map<TransportProtocol, EventLoopGroup> getProxyToServerWorkerPools() {
		return proxyToServerWorkerPools;
	}

	/**
     * These {@link EventLoopGroup}s process incoming requests to the
     * proxies. A different EventLoopGroup is used for each
     * TransportProtocol, since these have to be configured differently.
     * 
     * Thread safety: Only accessed while synchronized on the server group.
     * *
     */
    private final Map<TransportProtocol, EventLoopGroup> clientToProxyWorkerPools = new HashMap<TransportProtocol, EventLoopGroup>();

}
