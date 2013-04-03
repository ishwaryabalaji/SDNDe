import org.codehaus.jackson.map.introspect.BasicClassIntrospector.GetterMethodFilter;
import org.json.JSONException;
import org.json.JSONObject;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.config.JWebSocketConfig;
import org.jwebsocket.factory.JWebSocketFactory;
import org.jwebsocket.kit.RawPacket;
import org.jwebsocket.kit.WebSocketServerEvent;
import org.jwebsocket.listener.WebSocketServerTokenEvent;
import org.jwebsocket.listener.WebSocketServerTokenListener;
import org.jwebsocket.server.TokenServer;
import org.jwebsocket.token.Token;

public class SocketServer implements WebSocketServerTokenListener {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try {
            JWebSocketFactory.printCopyrightToConsole();  
            JWebSocketConfig.initForConsoleApp(args);  
            JWebSocketFactory.start();
            TokenServer tokenServer = (TokenServer) JWebSocketFactory
                    .getServer("ts0");
            if (tokenServer != null) {
                tokenServer.addListener(new SocketServer());
            }
        } catch (Exception lEx) {
            lEx.printStackTrace();
        }
    }

    public void processOpened(WebSocketServerEvent aEvent) {
        System.out.println("open");
        // overrride if needed
    }

    public void processPacket(WebSocketServerEvent aEvent,
            WebSocketPacket aPacket) {
        System.out.println("process"+aPacket.getString());
        
        WebSocketConnector wc = aEvent.getConnector();
        JSONObject json = new JSONObject();
        try {
            json.put("testing", "cool");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        WebSocketPacket swp = new RawPacket(json.toString());
        TokenServer tokenServer = (TokenServer) JWebSocketFactory
                .getServer("ts0");
        tokenServer.sendPacket(wc, swp);
        // here you can process any non-token low level message, if desired
    }

    public void processToken(WebSocketServerTokenEvent aEvent,
            Token tokenRequest) {
        System.out.println("process token");
        
        
        // here make something with request and response tokens
    }

    public void processClosed(WebSocketServerEvent aEvent) {
        System.out.println("clsoe");
        
        // override if needed
    }
    
    public void sendPacket() {
        System.out.println("clsoe");
//    /   Map conn = getTok
        // override if needed
    }
    
    
}
