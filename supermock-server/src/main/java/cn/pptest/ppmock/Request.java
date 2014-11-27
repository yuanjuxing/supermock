package cn.pptest.ppmock;

import java.util.Set;

import cn.pptest.ppmock.model.ContentTypeHeader;
import cn.pptest.ppmock.model.HttpHeader;
import cn.pptest.ppmock.model.HttpHeaders;


public interface Request extends Message{
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
}
