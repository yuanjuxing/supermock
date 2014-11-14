package cn.pptest.ppmock.runner;

import static cn.pptest.supermock.TransportProtocol.TCP;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import cn.pptest.supermock.ActivityTracker;
import cn.pptest.supermock.ChainedProxyManager;
import cn.pptest.supermock.DefaultHostResolver;
import cn.pptest.supermock.HostResolver;
import cn.pptest.supermock.HttpFiltersSource;
import cn.pptest.supermock.HttpFiltersSourceAdapter;
import cn.pptest.supermock.MitmManager;
import cn.pptest.supermock.ProxyAuthenticator;
import cn.pptest.supermock.SslEngineSource;
import cn.pptest.supermock.TransportProtocol;
import cn.pptest.supermock.impl.DefaultHttpProxyServer;

public class SuperMockServerBootstrap {

	private String name = "LittleProxy";
	private TransportProtocol transportProtocol = TCP;
	private InetSocketAddress address;
	private int port = 8080;
	private boolean allowLocalOnly = true;
	private boolean listenOnAllAddresses = true;
	private SslEngineSource sslEngineSource = null;
	private boolean authenticateSslClients = true;
	private ProxyAuthenticator proxyAuthenticator = null;
	private ChainedProxyManager chainProxyManager = null;
	private MitmManager mitmManager = null;
	private HttpFiltersSource filtersSource = new HttpFiltersSourceAdapter();
	private boolean transparent = false;
	private int idleConnectionTimeout = 70;
	private DefaultHttpProxyServer original;
	private Collection<ActivityTracker> activityTrackers = new ConcurrentLinkedQueue<ActivityTracker>();
	private int connectTimeout = 40000;
	private HostResolver serverResolver = new DefaultHostResolver();

	private SuperMockServerBootstrap() {
	}

}