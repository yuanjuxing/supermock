package cn.pptest.ppmock.handler;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.xml.bind.annotation.XmlRootElement;

import org.atmosphere.annotation.Broadcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.pptest.ppmock.handler.MockHandler.Message;
import cn.pptest.ppmock.handler.MockHandler.Response;

@Path("/")
public class AdminHandler {

	 private final Logger logger = LoggerFactory.getLogger(AdminHandler.class);


	    /**
	     * Suspend the response without writing anything back to the client.
	     * @return a white space
	     */
	    @GET
	    public String index(@Context HttpServletRequest   req) {
	        return "hello world";
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
