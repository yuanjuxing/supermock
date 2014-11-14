package cn.pptest.ppmock;

import cn.pptest.ppmock.common.FileSource;
import cn.pptest.ppmock.setting.HttpsSettings;
import cn.pptest.ppmock.setting.ProxySettings;


public interface Options {

    public static final int DEFAULT_PORT = 8181;

    int portNumber();
    HttpsSettings httpsSettings();
    boolean browserProxyingEnabled();
    ProxySettings proxyVia();
    FileSource filesRoot();
    SuperMockMonitor superMockMonitor();
    boolean requestJournalDisabled();

}
