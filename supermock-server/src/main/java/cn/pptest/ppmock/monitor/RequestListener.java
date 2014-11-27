package cn.pptest.ppmock.monitor;

import cn.pptest.ppmock.Request;
import cn.pptest.ppmock.Response;

public interface RequestListener {

	void requestReceived(Request request, Response response);
}
