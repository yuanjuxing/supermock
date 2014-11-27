package cn.pptest.ppmock.verification;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

import cn.pptest.ppmock.Request;
import cn.pptest.ppmock.Response;
import cn.pptest.ppmock.matcher.RequestPattern;
import cn.pptest.ppmock.monitor.RequestListener;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.size;



public class InMemoryRequestJournal implements RequestListener, RequestJournal {
	
	private ConcurrentLinkedQueue<LoggedRequest> requests = new ConcurrentLinkedQueue<LoggedRequest>();

	@Override
	public int countRequestsMatching(RequestPattern requestPattern) {
		return size(filter(requests, matchedBy(requestPattern))); 
	}

    @Override
    public List<LoggedRequest> getRequestsMatching(RequestPattern requestPattern) {
        return ImmutableList.copyOf(filter(requests, matchedBy(requestPattern)));
    }

    private Predicate<Request> matchedBy(final RequestPattern requestPattern) {
		return new Predicate<Request>() {
			public boolean apply(Request input) {
				return requestPattern.isMatchedBy(input);
			}
		};
	}

	@Override
	public void requestReceived(Request request, Response response) {
		requests.add(LoggedRequest.createFrom(request));
	}

    @Override
    public void requestReceived(Request request) {
        requestReceived(request, null);
    }

	@Override
	public void reset() {
		requests.clear();
	}



}
