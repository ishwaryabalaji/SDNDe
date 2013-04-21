package net.beaconcontroller.tutorial;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.beaconcontroller.core.IOFSwitch;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.config.JWebSocketConfig;
import org.jwebsocket.factory.JWebSocketFactory;
import org.jwebsocket.kit.RawPacket;
import org.jwebsocket.kit.WebSocketFrameType;
import org.jwebsocket.kit.WebSocketServerEvent;
import org.jwebsocket.listener.WebSocketServerTokenEvent;
import org.jwebsocket.listener.WebSocketServerTokenListener;
import org.jwebsocket.server.TokenServer;
import org.jwebsocket.token.Token;
import org.openflow.protocol.OFPacketIn;

public class SocketServer implements WebSocketServerTokenListener {
    /**
     * @param args
     */
       
    public static WebSocketConnector connector=null;
    
    public void startConnection(String configXml, String jWebHome) {
        // TODO Auto-generated method stub
        try {
            JWebSocketFactory.printCopyrightToConsole();  
            String[] args = {"-config",configXml,"-home",jWebHome};
            JWebSocketConfig.initForConsoleApp(args);  
            JWebSocketFactory.start();
            TokenServer tokenServer = (TokenServer) JWebSocketFactory
                    .getServer("ts0");
            if (tokenServer != null) {
                tokenServer.addListener(new SocketServer());
            }
        } catch (Exception lEx) {
            /*
             * -config /home/shunmu/ji/CN/SDNDe/SDN_ServerSocket/SocketServer/jWebSocket-1.0/conf/jWebSocket_override.xml 
             * -home /home/shunmu/ji/CN/SDNDe/SDN_ServerSocket/SocketServer/jWebSocket-1.0/
             */
            System.out.println("Usage : -config <config xml file> -home <jWebSocket Home>");
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
        if(connector==null)
        	connector = wc;
        //JSONObject json = new JSONObject();
        JSONObject json =  new JSONObject();
        try {
        	
			json = new JSONObject(aPacket.getString());
	        System.out.println("Parsing JSON - FN:"+(!json.has("FN")?"":json.getString("FN"))+
	        		" SWID:"+(!json.has("SWID")?"":json.getString("SWID"))+
	        		" SRC:"+(!json.has("SRC")?"":json.getString("SRC"))+
	        		" DEST:"+(!json.has("DEST")?"":json.getString("DEST")));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //json.put("testing", "cool");
        
        //
        Settings.updateSettings(json);
        //
        JSONObject sndArr = new JSONObject();
        
        try {
			 sndArr = SDNDe.handleCmd(json);
			 /*for(int i=0;i<sndArr.length();i++){
				 JSONObject o = sndArr.getJSONObject(i);
				 System.out.println("SendBack:"+o.toString());
			 }*/
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /*catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        if(sndArr.has("CMD")){
        	WebSocketPacket swp = new RawPacket(sndArr.toString());
            
            TokenServer tokenServer = (TokenServer) JWebSocketFactory
                    .getServer("ts0");
            tokenServer.sendPacket(wc, swp);
        }
        
        // here you can process any non-token low level message, if desired
    }

    public static void sendAsyncPacket(String fn,HashMap inMap){
    	JSONObject toReturn = new JSONObject();
    	SimpleDateFormat fr = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
		JSONArray sndArr = new JSONArray();
    	try {
			toReturn.put("TIME", fr.format(Calendar.getInstance().getTime()));
			toReturn.put("type", "RESULT");
			StringBuffer buf = new StringBuffer();
			buf.append(fn+"@");
			buf.append("Switch:"+Settings.getSid());
			buf.append(" Src:"+Settings.getSrc());
			buf.append(" Dst:"+Settings.getDest());
			toReturn.put("TITLE", buf.toString());
			toReturn.put("CMD", fn);
			
        	JSONArray inArr = new JSONArray();
        	if((fn.equals(Settings.BREAKPOINT) || fn.equals(Settings.STEP))&& Settings.bkQ!=null){
        		for(Entry<IOFSwitch, Entry<OFPacketIn,HashMap>> e:Settings.bkQ){
        			Map hMap = e.getValue().getValue();
        			inArr.put(hMap.get("OF_PACKETS_NETSRC"));
        			inArr.put(hMap.get("OF_PACKETS_SRCPORT"));
        			inArr.put(hMap.get("OF_PACKETS_SWITCHID"));
        			inArr.put(hMap.get("OF_PACKETS_NETDEST"));
        			inArr.put(hMap.get("OF_PACKETS_DESTPORT"));
        			inArr.put(hMap.get("OF_PACKETS_PACKETTYPE"));
        			//inArr.put((String)hMap.get(""));
        			sndArr.put(inArr);
        		}
        	}
        	else if(inMap!=null && fn.equals(Settings.MONITOR)){
        		inArr.put(inMap.get("OF_PACKETS_NETSRC"));
    			inArr.put(inMap.get("OF_PACKETS_SRCPORT"));
    			inArr.put(inMap.get("OF_PACKETS_SWITCHID"));
    			inArr.put(inMap.get("OF_PACKETS_NETDEST"));
    			inArr.put(inMap.get("OF_PACKETS_DESTPORT"));
    			inArr.put(inMap.get("OF_PACKETS_PACKETTYPE"));
    			sndArr.put(inArr);
        	}
        	toReturn.put("OUTPUT",sndArr);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	WebSocketPacket swp = new RawPacket(toReturn.toString());
        
        TokenServer tokenServer = (TokenServer) JWebSocketFactory
                .getServer("ts0");
        tokenServer.sendPacket(connector, swp);
    	
    }
    
    public void processToken(WebSocketServerTokenEvent aEvent,
            Token tokenRequest) {
        System.out.println("process token");    
        
        // here make something with request and response tokens
    }

    public void processClosed(WebSocketServerEvent aEvent) {
        System.out.println("close");
        
        // override if needed
    }
    
    public void sendPacket() {
        System.out.println("close");
//    /   Map conn = getTok
        // override if needed
    }
   
    
}
