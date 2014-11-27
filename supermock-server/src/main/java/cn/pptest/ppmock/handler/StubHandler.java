package cn.pptest.ppmock.handler;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.xml.bind.annotation.XmlRootElement;

import org.atmosphere.annotation.Broadcast;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StubHandler implements AtmosphereHandler{

    private final Logger logger = LoggerFactory.getLogger(StubHandler.class);


    /**
     * Suspend the response without writing anything back to the client.
     * @return a white space
     */
    @GET
    public String index(@Context HttpServletRequest   req) {
        return req.getHeaderNames().toString();
    }
    
    @Ready
    public String onReady(AtmosphereResource r)  {
        return r.getRequest().getHeaderNames().toString();
    }

    /**
     * Broadcast the received message object to all suspended response. Do not write back the message to the calling connection.
     * @param message a {@link Message}
     * @return a {@link Response}
     */
    @Broadcast(writeEntity = false)
    @GET
    @Produces("text/plain")
    public String broadcast(Message message) {
        return "hello world";
    }
    @XmlRootElement
    class Response {

        public String text;
        public String author;
        public long time;

        public Response(String author, String text) {
            this.author = author;
            this.text = text;
            this.time = new Date().getTime();
        }

        public Response() {
        }
    }
    @XmlRootElement
    class Message {
        public String author = "";
        public String message = "";

        public Message(){
        }

        public Message(String author, String message) {
            this.author = author;
            this.message = message;
        }
    }
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onRequest(AtmosphereResource arg0) throws IOException {
		arg0.getResponse().write("hello world ss", true);	
	}


	@Override
	public void onStateChange(AtmosphereResourceEvent arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}
}
