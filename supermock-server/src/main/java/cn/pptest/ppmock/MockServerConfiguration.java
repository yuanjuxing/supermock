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


public class MockServerConfiguration implements Options {

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
	private MockMonitor superMockMonitor;



	public MockServerConfiguration withName(String name) {
		this.name = name;
		return this;
	}

	public MockServerConfiguration withPortNumber(int portNumber) {
		this.portNumber = portNumber;
		return this;
	}

	public MockServerConfiguration withHttpsPort(Integer httpsPort) {
		this.httpsPort = httpsPort;
		return this;
	}

	public MockServerConfiguration withKeyStorePath(String keyStorePath) {
		this.keyStorePath = keyStorePath;
		return this;
	}

	public MockServerConfiguration withBrowserProxyingEnabled(boolean browserProxyingEnabled) {
		this.browserProxyingEnabled = browserProxyingEnabled;
		return this;
	}

	public MockServerConfiguration withRequestJournalDisabled(boolean requestJournalDisabled) {
		this.requestJournalDisabled = requestJournalDisabled;
		return this;
	}

	public MockServerConfiguration withTransportProtocol(TransportProtocol transportProtocol) {
		this.transportProtocol = transportProtocol;
		return this;
	}

	public MockServerConfiguration withAddress(InetSocketAddress address) {
		this.address = address;
		return this;
	}

	public MockServerConfiguration withAllowLocalOnly(boolean allowLocalOnly) {
		this.allowLocalOnly = allowLocalOnly;
		return this;
	}

	public MockServerConfiguration withListenOnAllAddresses(boolean listenOnAllAddresses) {
		this.listenOnAllAddresses = listenOnAllAddresses;
		return this;
	}

	public MockServerConfiguration withSslEngineSource(SslEngineSource sslEngineSource) {
		this.sslEngineSource = sslEngineSource;
		return this;
	}

	public MockServerConfiguration withAuthenticateSslClients(boolean authenticateSslClients) {
		this.authenticateSslClients = authenticateSslClients;
		return this;
	}

	public MockServerConfiguration withProxyAuthenticator(ProxyAuthenticator proxyAuthenticator) {
		this.proxyAuthenticator = proxyAuthenticator;
		return this;
	}

	public MockServerConfiguration withChainProxyManager(ChainedProxyManager chainProxyManager) {
		this.chainProxyManager = chainProxyManager;
		return this;
	}

	public MockServerConfiguration withMitmManager(MitmManager mitmManager) {
		this.mitmManager = mitmManager;
		return this;
	}

	public MockServerConfiguration withTransparent(boolean transparent) {
		this.transparent = transparent;
		return this;
	}

	public MockServerConfiguration withIdleConnectionTimeout(int idleConnectionTimeout) {
		this.idleConnectionTimeout = idleConnectionTimeout;
		return this;
	}

	public MockServerConfiguration withActivityTrackers(Collection<ActivityTracker> activityTrackers) {
		this.activityTrackers = activityTrackers;
		return this;
	}

	public MockServerConfiguration withConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	public MockServerConfiguration withServerResolver(HostResolver serverResolver) {
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
	    public MockMonitor superMockMonitor() {
	        return superMockMonitor;
	    }

	    public boolean requestJournalDisabled() {
	        return requestJournalDisabled;
	    }


    
}
