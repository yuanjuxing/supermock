package cn.pptest.ppmock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

import org.atmosphere.nettosphere.Config;
import org.atmosphere.nettosphere.Nettosphere;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.pptest.ppmock.common.FileSource;
import cn.pptest.ppmock.handler.AdminHandler;
import cn.pptest.ppmock.handler.MockHandler;
import cn.pptest.ppmock.handler.MonitorHandler;
import cn.pptest.ppmock.handler.StubRequestHandler;
import cn.pptest.ppmock.model.ProxyResponseRenderer;
import cn.pptest.ppmock.model.StubResponseRenderer;
import cn.pptest.ppmock.runner.JsonFileMappingsLoader;
import cn.pptest.ppmock.runner.JsonFileMappingsSaver;
import cn.pptest.ppmock.runner.MappingsLoader;
import cn.pptest.ppmock.setting.RequestDelayControl;
import cn.pptest.ppmock.setting.ThreadSafeRequestDelayControl;

public class MockServer implements Server{

	private static final Logger logger = LoggerFactory.getLogger(MockServer.class);
	
	public static final String FILES_ROOT = "__files";
    public static final String MAPPINGS_ROOT = "mappings";
	private static final String FILES_URL_MATCH = String.format("/%s/*", FILES_ROOT);
	
    private final Options options;
    private final FileSource fileSource;
	private final int port;
	private final MockMonitor monitor;
	private Nettosphere server;
	private final StubRequestHandler stubRequestHandler;
	private final RequestDelayControl requestDelayControl;
	private final MockApp mockApp;
	
	public MockServer(Options options){
		this.options=options;
		this.fileSource = options.filesRoot();
		this.monitor=options.superMockMonitor();
		this.port=options.portNumber();
		requestDelayControl = new ThreadSafeRequestDelayControl();

        MappingsLoader defaultMappingsLoader = makeDefaultMappingsLoader();
        JsonFileMappingsSaver mappingsSaver = new JsonFileMappingsSaver(fileSource.child(MAPPINGS_ROOT));
        mockApp = new MockApp(
                requestDelayControl,
                options.browserProxyingEnabled(),
                defaultMappingsLoader,
                mappingsSaver,
                options.requestJournalDisabled(),
                this
        );

        stubRequestHandler = new StubRequestHandler(mockApp,
                new StubResponseRenderer(fileSource.child(FILES_ROOT),
                		mockApp.getGlobalSettingsHolder(),
                        new ProxyResponseRenderer(options.proxyVia())));
	}
	
    private MappingsLoader makeDefaultMappingsLoader() {
    	FileSource mappingsFileSource = fileSource.child("mappings");
        if (mappingsFileSource.exists()) {
            return new JsonFileMappingsLoader(mappingsFileSource);
        } else {
            return null;
        }
    }
	
	@Override
	public int port() {
		return options.portNumber();
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
		Config.Builder b = new Config.Builder();
		b.resource("/*",new MockHandler(stubRequestHandler))
        .resource("/_admin/*",AdminHandler.class)
        .resource("/_monitor/*",MonitorHandler.class)
        .resource("src/main/resources")
        .port(options.portNumber()).host("127.0.0.1")
        .initParam("org.atmosphere.websocket.messageContentType","application/json")
        .initParam("org.atmosphere.websocket.messageMethod","POST")
        .initParam("com.sun.jersey.api.json.POJOMappingFeature","true").build();
        server = new Nettosphere.Builder().config(b.build()).build();
        server.start();
        String a = "";

        logger.info("Server started on port {}", options.portNumber());
        logger.info("Type quit to stop the server");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (!(a.equals("quit"))) {
            try {
				a = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        System.exit(-1);
	}

	@Override
	public void stop() {
		server.stop();
	}

	@Override
	public InetSocketAddress getListenAddress() {
		return new InetSocketAddress(options.portNumber());
	}
	
	public static void main(String ...args) throws IOException{
		MockServerConfiguration options=new MockServerConfiguration().withPortNumber(9090);
		MockServer server=new MockServer(options);
		server.start();
	}

}
