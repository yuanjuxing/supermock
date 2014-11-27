package cn.pptest.ppmock.handler;

import org.atmosphere.config.service.DeliverTo;
import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.Get;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Message;
import org.atmosphere.config.service.PathParam;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceFactory;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.MetaBroadcaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
@ManagedService(path = "/chat/*")
public class ServiceHandler {
    private final Logger logger = LoggerFactory.getLogger(ServiceHandler.class);

    private final ConcurrentHashMap<String, String> users = new ConcurrentHashMap<String, String>();

    private final static String CHAT = "/chat/";

    @PathParam("room")
    private String chatroomName;

    

    /**
     * Invoked when the connection as been fully established and suspended, e.g ready for receiving messages.
     *
     * @param r
     */
    @Ready
    @DeliverTo(DeliverTo.DELIVER_TO.ALL)
    public String onReady(final AtmosphereResource r) {
        logger.info("Browser {} connected.", r.uuid());
        return "hehlllll";
    }

    private static Collection<String> getRooms(Collection<Broadcaster> broadcasters) {
        Collection<String> result = new ArrayList<String>();
        for (Broadcaster broadcaster : broadcasters) {
            if (!("/*".equals(broadcaster.getID()))) {
                result.add(broadcaster.getID().split("/")[2]);
            }
        };
        return result;
    }

    /**
     * Invoked when the client disconnect or when an unexpected closing of the underlying connection happens.
     *
     * @param event
     */
    @Disconnect
    public void onDisconnect(AtmosphereResourceEvent event) {
        if (event.isCancelled()) {
            // We didn't get notified, so we remove the user.
            users.values().remove(event.getResource().uuid());
            logger.info("Browser {} unexpectedly disconnected", event.getResource().uuid());
        } else if (event.isClosedByClient()) {
            logger.info("Browser {} closed the connection", event.getResource().uuid());
        }
    }

    /**
     * Simple annotated class that demonstrate how {@link org.atmosphere.config.managed.Encoder} and {@link org.atmosphere.config.managed.Decoder
     * can be used.
     *
     * @param message an instance of {@link ChatProtocol }
     * @return
     * @throws IOException
     */
    @Message
    public String onMessage() throws IOException {
        return "entered room " ;
    }

    @Message
    public void onPrivateMessage(String user) throws IOException {
        System.out.print("12311");
    }
    
    @Get
    public void onGet(AtmosphereResource r) {
        System.out.print("12311fwef");

    }
}
