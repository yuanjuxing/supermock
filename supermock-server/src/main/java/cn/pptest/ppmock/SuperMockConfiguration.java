/*
 * Copyright (C) 2011 Thomas Akehurst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.pptest.ppmock;

import static cn.pptest.supermock.TransportProtocol.TCP;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import cn.pptest.ppmock.common.FileSource;
import cn.pptest.ppmock.setting.HttpsSettings;
import cn.pptest.ppmock.setting.ProxySettings;
import cn.pptest.supermock.ActivityTracker;
import cn.pptest.supermock.ChainedProxyManager;
import cn.pptest.supermock.DefaultHostResolver;
import cn.pptest.supermock.HostResolver;
import cn.pptest.supermock.MitmManager;
import cn.pptest.supermock.ProxyAuthenticator;
import cn.pptest.supermock.SslEngineSource;
import cn.pptest.supermock.TransportProtocol;


public class SuperMockConfiguration implements Options {

	private String name="supermock";
    private int portNumber = DEFAULT_PORT;
    private Integer httpsPort = null;
    private String keyStorePath = null;
    private boolean browserProxyingEnabled = false;
    private boolean requestJournalDisabled = false;
	private TransportProtocol transportProtocol = TCP;
	private InetSocketAddress address;
	private boolean allowLocalOnly = true;
	private boolean listenOnAllAddresses = true;
	private SslEngineSource sslEngineSource = null;
	private boolean authenticateSslClients = true;
	private ProxyAuthenticator proxyAuthenticator = null;
	private ChainedProxyManager chainProxyManager = null;
	private MitmManager mitmManager = null;
	private boolean transparent = false;
	private int idleConnectionTimeout = 70;
	private Collection<ActivityTracker> activityTrackers = new ConcurrentLinkedQueue<ActivityTracker>();
	private int connectTimeout = 40000;
	private HostResolver serverResolver = new DefaultHostResolver();
	private ProxySettings proxySettings;
	private FileSource filesRoot;
	private SuperMockMonitor superMockMonitor;



	public SuperMockConfiguration withName(String name) {
		this.name = name;
		return this;
	}

	public SuperMockConfiguration withPortNumber(int portNumber) {
		this.portNumber = portNumber;
		return this;
	}

	public SuperMockConfiguration withHttpsPort(Integer httpsPort) {
		this.httpsPort = httpsPort;
		return this;
	}

	public SuperMockConfiguration withKeyStorePath(String keyStorePath) {
		this.keyStorePath = keyStorePath;
		return this;
	}

	public SuperMockConfiguration withBrowserProxyingEnabled(boolean browserProxyingEnabled) {
		this.browserProxyingEnabled = browserProxyingEnabled;
		return this;
	}

	public SuperMockConfiguration withRequestJournalDisabled(boolean requestJournalDisabled) {
		this.requestJournalDisabled = requestJournalDisabled;
		return this;
	}

	public SuperMockConfiguration withTransportProtocol(TransportProtocol transportProtocol) {
		this.transportProtocol = transportProtocol;
		return this;
	}

	public SuperMockConfiguration withAddress(InetSocketAddress address) {
		this.address = address;
		return this;
	}

	public SuperMockConfiguration withAllowLocalOnly(boolean allowLocalOnly) {
		this.allowLocalOnly = allowLocalOnly;
		return this;
	}

	public SuperMockConfiguration withListenOnAllAddresses(boolean listenOnAllAddresses) {
		this.listenOnAllAddresses = listenOnAllAddresses;
		return this;
	}

	public SuperMockConfiguration withSslEngineSource(SslEngineSource sslEngineSource) {
		this.sslEngineSource = sslEngineSource;
		return this;
	}

	public SuperMockConfiguration withAuthenticateSslClients(boolean authenticateSslClients) {
		this.authenticateSslClients = authenticateSslClients;
		return this;
	}

	public SuperMockConfiguration withProxyAuthenticator(ProxyAuthenticator proxyAuthenticator) {
		this.proxyAuthenticator = proxyAuthenticator;
		return this;
	}

	public SuperMockConfiguration withChainProxyManager(ChainedProxyManager chainProxyManager) {
		this.chainProxyManager = chainProxyManager;
		return this;
	}

	public SuperMockConfiguration withMitmManager(MitmManager mitmManager) {
		this.mitmManager = mitmManager;
		return this;
	}

	public SuperMockConfiguration withTransparent(boolean transparent) {
		this.transparent = transparent;
		return this;
	}

	public SuperMockConfiguration withIdleConnectionTimeout(int idleConnectionTimeout) {
		this.idleConnectionTimeout = idleConnectionTimeout;
		return this;
	}

	public SuperMockConfiguration withActivityTrackers(Collection<ActivityTracker> activityTrackers) {
		this.activityTrackers = activityTrackers;
		return this;
	}

	public SuperMockConfiguration withConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	public SuperMockConfiguration withServerResolver(HostResolver serverResolver) {
		this.serverResolver = serverResolver;
		return this;
	}

	 @Override
	    public int portNumber() {
	        return portNumber;
	    }

	    @Override
	    public HttpsSettings httpsSettings() {
	        if (httpsPort == null) {
	            return HttpsSettings.NO_HTTPS;
	        }

	        if (keyStorePath == null) {
	            return new HttpsSettings(httpsPort);
	        }

	        return new HttpsSettings(httpsPort, keyStorePath);
	    }

	    @Override
	    public boolean browserProxyingEnabled() {
	        return browserProxyingEnabled;
	    }

	    @Override
	    public ProxySettings proxyVia() {
	        return proxySettings;
	    }

	    @Override
	    public FileSource filesRoot() {
	        return filesRoot;
	    }

	    @Override
	    public SuperMockMonitor superMockMonitor() {
	        return superMockMonitor;
	    }

	    public boolean requestJournalDisabled() {
	        return requestJournalDisabled;
	    }


    
}
