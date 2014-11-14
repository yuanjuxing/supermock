package cn.pptest.ppmock.model;

import java.util.Set;

import com.google.common.collect.ImmutableMap;

import cn.pptest.ppmock.Request;
import cn.pptest.ppmock.RequestMethod;

public interface HttpRequest extends Request{

	String getUrl();
	String getAbsoluteUrl();
	RequestMethod getMethod();
	String getHeader(String key);
    HttpHeader header(String key);
    ContentTypeHeader contentTypeHeader();
    HttpHeaders getHeaders();
	boolean containsHeader(String key);
	Set<String> getAllHeaderKeys();
	String getBodyAsString();
	boolean isBrowserProxyRequest();
    ImmutableMap<String, String> getQueries();
	
}
