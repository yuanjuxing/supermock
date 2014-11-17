package cn.pptest.ppmock.handler;

import static org.littleshoot.proxy.impl.ConnectionState.AWAITING_CHUNK;
import static org.littleshoot.proxy.impl.ConnectionState.AWAITING_INITIAL;
import static org.littleshoot.proxy.impl.ConnectionState.AWAITING_PROXY_AUTHENTICATION;
import static org.littleshoot.proxy.impl.ConnectionState.DISCONNECT_REQUESTED;
import static org.littleshoot.proxy.impl.ConnectionState.NEGOTIATING_CONNECT;

import java.net.UnknownHostException;

import org.apache.commons.lang3.StringUtils;
import org.littleshoot.proxy.impl.ConnectionState;
import org.littleshoot.proxy.impl.ProxyToServerConnection;
import org.littleshoot.proxy.impl.ProxyUtils;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

public class ClientToProxyHandler extends SuperMockHandler{
	
	
	

    /***************************************************************************
     * Reading
     **************************************************************************/

    @Override
    protected ConnectionState readHTTPInitial(HttpRequest httpRequest) {
        LOG.debug("Got request: {}", httpRequest);

        boolean authenticationRequired = authenticationRequired(httpRequest);

        if (authenticationRequired) {
            LOG.debug("Not authenticated!!");
            return AWAITING_PROXY_AUTHENTICATION;
        } else {
            return doReadHTTPInitial(httpRequest);
        }
    }

    /**
     * <p>
     * Reads an {@link HttpRequest}.
     * </p>
     * 
     * <p>
     * If we don't yet have a {@link ProxyToServerConnection} for the desired
     * server, this takes care of creating it.
     * </p>
     * 
     * <p>
     * Note - the "server" could be a chained proxy, not the final endpoint for
     * the request.
     * </p>
     * 
     * @param httpRequest
     * @return
     */
    private ConnectionState doReadHTTPInitial(HttpRequest httpRequest) {
        // Make a copy of the original request
        HttpRequest originalRequest = copy(httpRequest);

        // Set up our filters based on the original request
        currentFilters = proxyServer.getFiltersSource().filterRequest(
                originalRequest, ctx);

        // Do the pre filtering
        if (shortCircuitRespond(currentFilters
                .clientToProxyRequest(httpRequest))) {
            return DISCONNECT_REQUESTED;
        }

        // Identify our server and chained proxy
        String serverHostAndPort = identifyHostAndPort(httpRequest);

        LOG.debug("Ensuring that hostAndPort are available in {}",
                httpRequest.getUri());
        if (serverHostAndPort == null || StringUtils.isBlank(serverHostAndPort)) {
            LOG.warn("No host and port found in {}", httpRequest.getUri());
            writeBadGateway(httpRequest);
            return DISCONNECT_REQUESTED;
        }

        LOG.debug("Finding ProxyToServerConnection for: {}", serverHostAndPort);
        currentServerConnection = isMitming() || isTunneling() ?
                this.currentServerConnection
                : this.serverConnectionsByHostAndPort.get(serverHostAndPort);

        boolean newConnectionRequired = false;
        if (ProxyUtils.isCONNECT(httpRequest)) {
            LOG.debug(
                    "Not reusing existing ProxyToServerConnection because request is a CONNECT for: {}",
                    serverHostAndPort);
            newConnectionRequired = true;
        } else if (currentServerConnection == null) {
            LOG.debug("Didn't find existing ProxyToServerConnection for: {}",
                    serverHostAndPort);
            newConnectionRequired = true;
        }

        if (newConnectionRequired) {
            try {
                currentServerConnection = ProxyToServerConnection.create(
                        proxyServer,
                        this,
                        serverHostAndPort,
                        currentFilters,
                        httpRequest);
                if (currentServerConnection == null) {
                    LOG.debug("Unable to create server connection, probably no chained proxies available");
                    writeBadGateway(httpRequest);
                    resumeReading();
                    return DISCONNECT_REQUESTED;
                }
                // Remember the connection for later
                serverConnectionsByHostAndPort.put(serverHostAndPort,
                        currentServerConnection);
            } catch (UnknownHostException uhe) {
                LOG.info("Bad Host {}", httpRequest.getUri());
                writeBadGateway(httpRequest);
                resumeReading();
                return DISCONNECT_REQUESTED;
            }
        } else {
            LOG.debug("Reusing existing server connection: {}",
                    currentServerConnection);
            numberOfReusedServerConnections.incrementAndGet();
        }

        modifyRequestHeadersToReflectProxying(httpRequest);
        if (shortCircuitRespond(currentFilters
                .proxyToServerRequest(httpRequest))) {
            return DISCONNECT_REQUESTED;
        }

        LOG.debug("Writing request to ProxyToServerConnection");
        currentServerConnection.write(httpRequest, currentFilters);

        // Figure out our next state
        if (ProxyUtils.isCONNECT(httpRequest)) {
            return NEGOTIATING_CONNECT;
        } else if (ProxyUtils.isChunked(httpRequest)) {
            return AWAITING_CHUNK;
        } else {
            return AWAITING_INITIAL;
        }
    }

    @Override
    protected void readHTTPChunk(HttpContent chunk) {
        currentFilters.clientToProxyRequest(chunk);
        currentFilters.proxyToServerRequest(chunk);
        currentServerConnection.write(chunk);
    }

    @Override
    protected void readRaw(ByteBuf buf) {
        currentServerConnection.write(buf);
    }

}
