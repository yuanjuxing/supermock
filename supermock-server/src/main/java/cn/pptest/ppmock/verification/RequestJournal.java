package cn.pptest.ppmock.verification;

import java.util.List;

import cn.pptest.ppmock.Request;
import cn.pptest.ppmock.matcher.RequestPattern;


public interface RequestJournal {

	int countRequestsMatching(RequestPattern requestPattern);
    List<LoggedRequest> getRequestsMatching(RequestPattern requestPattern);
	void reset();

    void requestReceived(Request request);
}
