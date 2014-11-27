package cn.pptest.ppmock.model;


import cn.pptest.ppmock.Response;

public class HttpResponse extends Response{

	public HttpResponse(int status, byte[] body, HttpHeaders headers,
			boolean configured, Fault fault, boolean fromProxy) {
		super(status, body, headers, configured, fault, fromProxy);
		// TODO Auto-generated constructor stub
	}



}
