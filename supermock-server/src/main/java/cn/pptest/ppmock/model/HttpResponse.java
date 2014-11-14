package cn.pptest.ppmock.model;


import javax.servlet.http.HttpServletResponse;

import cn.pptest.ppmock.Response;


import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Arrays;

import static com.google.common.base.Charsets.UTF_8;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

public class HttpResponse implements Response{

	private final int status;
	private final byte[] body;
	private final HttpHeaders headers;
	private final boolean configured;
	private final Fault fault;
	private final boolean fromProxy;
	
	public static HttpResponse notConfigured() {
        HttpResponse response = new HttpResponse(HTTP_NOT_FOUND,
                (byte[]) null,
                null,
                false,
                null,
                false);
		return response;
	}

    public static Builder response() {
        return new Builder();
    }

	public HttpResponse(int status, byte[] body, HttpHeaders headers, boolean configured, Fault fault, boolean fromProxy) {
		this.status = status;
        this.body = body;
        this.headers = headers;
        this.configured = configured;
        this.fault = fault;
        this.fromProxy = fromProxy;
	}

    public HttpResponse(int status, String body, HttpHeaders headers, boolean configured, Fault fault, boolean fromProxy) {
        this.status = status;
        this.headers = headers;
        this.body = body == null ? null : body.getBytes(encodingFromContentTypeHeaderOrUtf8());
        this.configured = configured;
        this.fault = fault;
        this.fromProxy = fromProxy;
    }

	public int getStatus() {
		return status;
	}

    public byte[] getBody() {
        return body;
    }
	
	public String getBodyAsString() {
        return new String(body, encodingFromContentTypeHeaderOrUtf8());
	}
	
	public HttpHeaders getHeaders() {
		return headers;
	}
	
	@Override
	public String getContent() {
		// TODO Auto-generated method stub
		return null;
	}


	
	private static void writeAndTranslateExceptions(HttpServletResponse httpServletResponse, byte[] content) {
		try {	
			httpServletResponse.getOutputStream().write(content);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

    private Charset encodingFromContentTypeHeaderOrUtf8() {
        ContentTypeHeader contentTypeHeader = headers.getContentTypeHeader();
        if (contentTypeHeader.isPresent() && contentTypeHeader.encodingPart().isPresent()) {
            return Charset.forName(contentTypeHeader.encodingPart().get());
        }

        return UTF_8;
    }
	
	public boolean wasConfigured() {
		return configured;
	}

    public boolean isFromProxy() {
        return fromProxy;
    }

    @Override
    public String toString() {
        return "Response [status=" + status + ", body=" + Arrays.toString(body) + ", headers=" + headers
                + ", configured=" + configured + ", fault=" + fault + ", fromProxy=" + fromProxy + "]";
    }

    public static class Builder {
        private int status = HTTP_OK;
        private byte[] body;
        private String bodyString;
        private HttpHeaders headers = new HttpHeaders();
        private boolean configured = true;
        private Fault fault;
        private boolean fromProxy;

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder body(byte[] body) {
            this.body = body;
            ensureOnlyOneBodySet();
            return this;
        }

        public Builder body(String body) {
            this.bodyString = body;
            ensureOnlyOneBodySet();
            return this;
        }

        private void ensureOnlyOneBodySet() {
            if (body != null && bodyString != null) {
                throw new IllegalStateException("Body should either be set as a String or byte[], not both");
            }
        }

        public Builder headers(HttpHeaders headers) {
            this.headers = headers == null ? null: headers;
            return this;
        }

        public Builder configured(boolean configured) {
            this.configured = configured;
            return this;
        }

        public Builder fault(Fault fault) {
            this.fault = fault;
            return this;
        }

        public Builder fromProxy(boolean fromProxy) {
            this.fromProxy = fromProxy;
            return this;
        }

        public HttpResponse build() {
            if (body != null) {
                return new HttpResponse(status, body, headers, configured, fault, fromProxy);
            } else if (bodyString != null) {
                return new HttpResponse(status, bodyString, headers, configured, fault, fromProxy);
            } else {
                return new HttpResponse(status, new byte[0], headers, configured, fault, fromProxy);
            }
        }
    }

}
