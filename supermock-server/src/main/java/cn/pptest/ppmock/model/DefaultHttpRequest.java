package cn.pptest.ppmock.model;

import static com.google.common.collect.Lists.newArrayList;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.pptest.ppmock.RequestMethod;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;


@JsonDeserialize(builder = DefaultHttpRequest.class)
public class DefaultHttpRequest implements HttpRequest {
   
	private FullHttpRequest request;

	public DefaultHttpRequest(FullHttpRequest request) {
		super();
		this.request = request;
	}
	


	@Override
	public ImmutableMap<String, String> getQueries() {
        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        ImmutableMap<String, String> queries = toQueries(decoder);
		return queries;
    }



	@Override
	public String getContent() {
		return contentToString(request);
	}

	@Override
	public String getUrl() {
		return request.getUri();
	}

	@Override
	public String getAbsoluteUrl() {
		return request.getUri();
	}

	@Override
	public RequestMethod getMethod() {
		return RequestMethod.valueOf(request.getMethod().name().toUpperCase());
	}

	@Override
	public String getHeader(String key) {
		return request.headers().get(key);
	}

	@Override
	public HttpHeader header(String key) {
		List<String> valueList = request.headers().getAll(key);
        return new HttpHeader(key, valueList);
	}

	@Override
	public ContentTypeHeader contentTypeHeader() {
        return getHeaders().getContentTypeHeader();
	}

	@Override
	public cn.pptest.ppmock.model.HttpHeaders getHeaders() {
		 List<HttpHeader> headerList = newArrayList();
	        for (String key: getAllHeaderKeys()) {
	            headerList.add(header(key));
	        }

	        return new cn.pptest.ppmock.model.HttpHeaders(headerList);
	}

	@Override
	public boolean containsHeader(String key) {
		return header(key).isPresent();
	}

	@Override
	public Set<String> getAllHeaderKeys() {
		return request.headers().names(); 
	}

	@Override
	public String getBodyAsString() {
		return contentToString(request);
	}

	@Override
	public boolean isBrowserProxyRequest() {
		return false;
	}
	

	private static String contentToString(FullHttpRequest request) {
        long contentLength = HttpHeaders.getContentLength(request, -1);
        if (contentLength <= 0) {
            return "";
        }

        try {
            return CharStreams.toString(new InputStreamReader(new ByteBufInputStream(request.content()), Charset.defaultCharset()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	
	private static ImmutableMap<String, String> toQueries(QueryStringDecoder decoder) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        for (Map.Entry<String, List<String>> entry : decoder.parameters().entrySet()) {
            builder.put(entry.getKey(), entry.getValue().get(0));
        }
        return builder.build();
    }
}
