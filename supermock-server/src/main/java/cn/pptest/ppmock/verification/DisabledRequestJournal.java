package cn.pptest.ppmock.verification;

import java.util.List;

import cn.pptest.ppmock.Request;
import cn.pptest.ppmock.matcher.RequestPattern;

public class DisabledRequestJournal implements RequestJournal {

    @Override
    public int countRequestsMatching(RequestPattern requestPattern) {
        throw new RequestJournalDisabledException();
    }

    @Override
    public List<LoggedRequest> getRequestsMatching(RequestPattern requestPattern) {
        throw new RequestJournalDisabledException();
    }

    @Override
    public void reset() {
    }

    @Override
    public void requestReceived(Request request) {
    }
}
