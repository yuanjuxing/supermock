package cn.pptest.ppmock.model;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.list;

import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import cn.pptest.ppmock.Request;
import cn.pptest.ppmock.RequestMethod;
import cn.pptest.ppmock.common.ServletContainerUtils;

import com.google.common.io.CharStreams;

public class HttpServletRequestAdapter implements Request {
	
	private final HttpServletRequest request;
	private String cachedBody;
	
	public HttpServletRequestAdapter(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public String getUrl() {
		String url = request.getRequestURI();

		if (!isNullOrEmpty(request.getContextPath())) {
			url = url.replace(request.getContextPath(), "");
		}

		return withQueryStringIfPresent(url);
	}
	
	@Override
	public String getAbsoluteUrl() {
		return withQueryStringIfPresent(request.getRequestURL().toString());
	}

    private String withQueryStringIfPresent(String url) {
        return url + (isNullOrEmpty(request.getQueryString()) ? "" : "?" + request.getQueryString());
    }

	@Override
	public RequestMethod getMethod() {
		return RequestMethod.valueOf(request.getMethod().toUpperCase());
	}

	@Override
	public String getBodyAsString() {
		if (cachedBody == null) {
			try {
				cachedBody = CharStreams.toString(request.getReader());
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}
		
		return cachedBody;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getHeader(String key) {
	    List<String> headerNames = list(request.getHeaderNames());
		for (String currentKey: headerNames) {
			if (currentKey.toLowerCase().equals(key.toLowerCase())) {
				return request.getHeader(currentKey);
			}
		}
		
		return null;
	}

    @Override
    @SuppressWarnings("unchecked")
    public HttpHeader header(String key) {
        List<String> headerNames = list(request.getHeaderNames());
        for (String currentKey: headerNames) {
            if (currentKey.toLowerCase().equals(key.toLowerCase())) {
                List<String> valueList = list(request.getHeaders(currentKey));
                return new HttpHeader(key, valueList);
            }
        }

        return HttpHeader.absent(key);
    }

    @Override
    public ContentTypeHeader contentTypeHeader() {
        return getHeaders().getContentTypeHeader();
    }

    @Override
	public boolean containsHeader(String key) {
		return header(key).isPresent();
	}

    @Override
    public HttpHeaders getHeaders() {
        List<HttpHeader> headerList = newArrayList();
        for (String key: getAllHeaderKeys()) {
            headerList.add(header(key));
        }

        return new HttpHeaders(headerList);
    }

    @SuppressWarnings("unchecked")
	@Override
	public Set<String> getAllHeaderKeys() {
		LinkedHashSet<String> headerKeys = new LinkedHashSet<String>();
		for (Enumeration<String> headerNames = request.getHeaderNames(); headerNames.hasMoreElements();) {
			headerKeys.add(headerNames.nextElement());
		}
		
		return headerKeys;
	}

	@Override
	public boolean isBrowserProxyRequest() {
		return ServletContainerUtils.isBrowserProxyRequest(request);
	}

	@Override
	public String getContent() {
		// TODO Auto-generated method stub
		return null;
	}
}
