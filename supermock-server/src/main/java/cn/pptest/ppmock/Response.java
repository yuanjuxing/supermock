package cn.pptest.ppmock;

import static com.google.common.base.Charsets.UTF_8;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import cn.pptest.ppmock.model.ContentTypeHeader;
import cn.pptest.ppmock.model.Fault;
import cn.pptest.ppmock.model.HttpHeader;
import cn.pptest.ppmock.model.HttpHeaders;


public class Response {

	private final int status;
	private final byte[] body;
	private final HttpHeaders headers;
	private final boolean configured;
	private final Fault fault;
	private final boolean fromProxy;
	
	public static Response notConfigured() {
        Response response = new Response(HTTP_NOT_FOUND,
                (byte[]) null,
                HttpHeaders.noHeaders(),
                false,
                null,
                false);
		return response;
	}

    public static Builder response() {
        return new Builder();
    }

	public Response(int status, byte[] body, HttpHeaders headers, boolean configured, Fault fault, boolean fromProxy) {
		this.status = status;
        this.body = body;
        this.headers = headers;
        this.configured = configured;
        this.fault = fault;
        this.fromProxy = fromProxy;
	}

    public Response(int status, String body, HttpHeaders headers, boolean configured, Fault fault, boolean fromProxy) {
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
	
	public void applyTo(HttpServletResponse httpServletResponse) {
		if (fault != null) {
            //Socket socket = ActiveSocket.get();
			//fault.apply(httpServletResponse, socket);
			return;
		}
		
		httpServletResponse.setStatus(status);
		for (HttpHeader header: headers.all()) {
            for (String value: header.values()) {
                httpServletResponse.addHeader(header.key(), value);
            }
		}
		
		writeAndTranslateExceptions(httpServletResponse, body);
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
            this.headers = headers == null ? HttpHeaders.noHeaders() : headers;
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

        public Response build() {
            if (body != null) {
                return new Response(status, body, headers, configured, fault, fromProxy);
            } else if (bodyString != null) {
                return new Response(status, bodyString, headers, configured, fault, fromProxy);
            } else {
                return new Response(status, new byte[0], headers, configured, fault, fromProxy);
            }
        }
    }
}
	
