package cn.pptest.ppmock.handler;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.xml.bind.annotation.XmlRootElement;

import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Suspend;
import org.atmosphere.config.service.AtmosphereService;
import org.atmosphere.config.service.Get;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Message;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

@Path("/chat")
public class MockHandler {

    private final Logger logger = LoggerFactory.getLogger(MockHandler.class);


    /**
     * Suspend the response without writing anything back to the client.
     * @return a white space
     */
    @GET
    public String index(@Context HttpServletRequest   req) {
        return req.getHeaderNames().toString();
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
}
