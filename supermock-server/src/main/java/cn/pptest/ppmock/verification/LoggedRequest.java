package cn.pptest.ppmock.verification;

import cn.pptest.ppmock.Request;
import cn.pptest.ppmock.RequestMethod;
import cn.pptest.ppmock.model.ContentTypeHeader;
import cn.pptest.ppmock.model.HttpHeader;
import cn.pptest.ppmock.model.HttpHeaders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown=true)
public class LoggedRequest implements Request {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	private final String url;
	private final String absoluteUrl;
	private final RequestMethod method;
	private final HttpHeaders headers;
	private final String body;
	private final boolean isBrowserProxyRequest;
    private final Date loggedDate;
	
	public static LoggedRequest createFrom(Request request) {
        return new LoggedRequest(request.getUrl(),
                request.getAbsoluteUrl(),
                request.getMethod(),
                HttpHeaders.copyOf(request.getHeaders()),
                request.getBodyAsString(),
                request.isBrowserProxyRequest(),
                new Date());
	}

    @JsonCreator
    public LoggedRequest(@JsonProperty("url") String url,
                         @JsonProperty("absoluteUrl") String absoluteUrl,
                         @JsonProperty("method") RequestMethod method,
                         @JsonProperty("headers") HttpHeaders headers,
                         @JsonProperty("body") String body,
                         @JsonProperty("browserProxyRequest") boolean isBrowserProxyRequest,
                         @JsonProperty("loggedDate") Date loggedDate) {

        this.url = url;
        this.absoluteUrl = absoluteUrl;
        this.method = method;
        this.body = body;
        this.headers = headers;
        this.isBrowserProxyRequest = isBrowserProxyRequest;
        this.loggedDate = loggedDate;
    }

	@Override
	public String getUrl() {
		return url;
	}
	
	@Override
	public String getAbsoluteUrl() {
		return absoluteUrl;
	}

	@Override
	public RequestMethod getMethod() {
		return method;
	}

	@Override
    @JsonIgnore
	public String getHeader(String key) {
		HttpHeader header = header(key);
        if (header.isPresent()) {
            return header.firstValue();
        }
		
		return null;
	}

    @Override
    public HttpHeader header(String key) {
        return headers.getHeader(key);
    }

    @Override
    public ContentTypeHeader contentTypeHeader() {
        return headers.getContentTypeHeader();
    }

    @Override
	public boolean containsHeader(String key) {
		return getHeader(key) != null;
	}

	@Override
    @JsonProperty("body")
	public String getBodyAsString() {
		return body;
	}

	@Override
    @JsonIgnore
	public Set<String> getAllHeaderKeys() {
		return headers.keys();
	}

    public HttpHeaders getHeaders() {
        return headers;
    }
	
	@Override
	public boolean isBrowserProxyRequest() {
		return isBrowserProxyRequest;
	}

    public Date getLoggedDate() {
        return loggedDate;
    }

    public String getLoggedDateString() {
        return format(loggedDate);
    }

    private String format(Date date) {
        return new SimpleDateFormat(DATE_FORMAT).format(date);
    }

	@Override
	public String getContent() {
		// TODO Auto-generated method stub
		return null;
	}
}
