package cn.pptest.ppmock;

import cn.pptest.ppmock.monitor.RequestListener;

public interface RequestEventSource {

    void addRequestListener(RequestListener requestListener);
}
