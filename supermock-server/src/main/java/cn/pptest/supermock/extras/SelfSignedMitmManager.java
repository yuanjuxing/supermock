package cn.pptest.supermock.extras;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;

import cn.pptest.supermock.MitmManager;

/**
 * {@link MitmManager} that uses self-signed certs for everything.
 */
public class SelfSignedMitmManager implements MitmManager {
    SelfSignedSslEngineSource selfSignedSslEngineSource =
            new SelfSignedSslEngineSource(true);

    @Override
    public SSLEngine serverSslEngine() {
        return selfSignedSslEngineSource.newSslEngine();
    }

    @Override
    public SSLEngine clientSslEngineFor(SSLSession serverSslSession) {
        return selfSignedSslEngineSource.newSslEngine();
    }
}
