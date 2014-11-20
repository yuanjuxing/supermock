package cn.pptest.ppmock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

import org.atmosphere.nettosphere.Config;
import org.atmosphere.nettosphere.Nettosphere;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.pptest.ppmock.handler.AdminHandler;
import cn.pptest.ppmock.handler.MockHandler;

public class MockServer implements Server{

	private static final Logger logger = LoggerFactory.getLogger(MockServer.class);
	@Override
	public int port() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIdleConnectionTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setIdleConnectionTimeout(int idleConnectionTimeout) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Server cloneServer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public InetSocketAddress getListenAddress() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String ...args) throws IOException{
		Config.Builder b = new Config.Builder();
        b.resource(AdminHandler.class)
            
                .port(9090).host("127.0.0.1")
                .initParam("org.atmosphere.websocket.messageContentType","application/json")
                .initParam("org.atmosphere.websocket.messageMethod","GET")
                .initParam("com.sun.jersey.api.json.POJOMappingFeature","true").build();
        Nettosphere s = new Nettosphere.Builder().config(b.build()).build();
        s.start();
        String a = "";

        logger.info("NettoSphere Chat Server started on port {}", 9090);
        logger.info("Type quit to stop the server");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (!(a.equals("quit"))) {
            a = br.readLine();
        }
        System.exit(-1);
	}

}
