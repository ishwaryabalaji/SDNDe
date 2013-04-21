package net.beaconcontroller.tutorial;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.ldap.SortKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openflow.protocol.OFPacketIn;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import net.beaconcontroller.core.IOFSwitch;
import net.beaconcontroller.tutorial.DBControllerUtil;

public class SDNDe {
	static DBControllerUtil db;
	static SDNDe instance;
	public SDNDe(DBControllerUtil db){
		SDNDe.db = db; 
		instance=this;
	}
	
	public SDNDe(){
			
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray backwardTrace (String switchID, String netSrc, String netDest){
		ArrayList<PacketInfo> packetInstanceList = new ArrayList<PacketInfo>();
	    DBCollection coll = SDNDe.db.db.getCollection("OFPACKETS");
         
        BasicDBObject query = new BasicDBObject(); 

        query.put("NETSRC", netSrc);
        query.put("NETDEST", netDest);  

        DBCursor cursor = coll.find(query);
        JSONArray traceList = new JSONArray();
        System.out.println("COUNT :"+cursor.count());
        while (cursor.hasNext()) {
            DBObject bt = cursor.next();
            System.out.println("bt is"+bt.toString());
            traceList.put(bt);
            
            PacketInfo packetInstance = new PacketInfo();
         
            packetInstance.setSwitchID(bt.get("SID").toString());
            packetInstance.setNetSrc(bt.get("NETSRC").toString());
            packetInstance.setNetDest(bt.get("NETDEST").toString());
            packetInstance.setTimeStamp(bt.get("TIME").toString());
            packetInstance.setSrcPort(bt.get("SRCPORT").toString());
            packetInstance.setDestPort(bt.get("DESTPORT")!=null?bt.get("DESTPORT").toString():null);
            packetInstance.setPacketType(bt.get("PTYPE").toString());
            
            packetInstanceList.add(packetInstance);            
        }
        Collections.sort(packetInstanceList);
        int switchIndex = getIndexofPacketList(switchID, packetInstanceList);
        JSONArray jsonArr = new JSONArray();
        if(switchIndex == -1) {
            System.out.println("SwitchID not found !");
        }
        
        else {
        for(int index = 0; index <= switchIndex; index++) {
        
        	JSONObject json = new JSONObject();
        	JSONArray inArr = new JSONArray();
        	try {
        		inArr.put(packetInstanceList.get(index).getNetSrc()==null?"":packetInstanceList.get(index).getNetSrc());
        		inArr.put(packetInstanceList.get(index).getSrcPort()==null?"":packetInstanceList.get(index).getSrcPort());
        		inArr.put(packetInstanceList.get(index).getSwitchID()==null?"":packetInstanceList.get(index).getSwitchID());
        		inArr.put(packetInstanceList.get(index).getNetDest()==null?"":packetInstanceList.get(index).getNetDest());
        		inArr.put(packetInstanceList.get(index).getDestPort()==null?"":packetInstanceList.get(index).getDestPort());
        		inArr.put(packetInstanceList.get(index).getPacketType()==null?"":packetInstanceList.get(index).getPacketType());
				json.put("SwitchID", packetInstanceList.get(index).getSwitchID()==null?"":packetInstanceList.get(index).getNetSrc());
				json.put("Source", packetInstanceList.get(index).getNetSrc());
	        	json.put("Dest", packetInstanceList.get(index).getNetDest());
	        	json.put("Src Port", packetInstanceList.get(index).getSrcPort());
	        	json.put("Dest Port", packetInstanceList.get(index).getDestPort());
	        	json.put("Packet Type", packetInstanceList.get(index).getPacketType());
	        	
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	
        	//jsonArr.put(json);
        	jsonArr.put(inArr);
        	System.out.println("SwitchID:"+packetInstanceList.get(index).getSwitchID()+
                               "Source:"+packetInstanceList.get(index).getNetSrc()+
                               "Dest:"+packetInstanceList.get(index).getNetDest()+
                               "Src Port:"+packetInstanceList.get(index).getSrcPort()+
                               "Dest Port:"+packetInstanceList.get(index).getDestPort()+
                               "Packet Type:"+packetInstanceList.get(index).getPacketType());
        }
        }
        cursor.close();
      
        return jsonArr;
	}
	
	private int getIndexofPacketList(String switchID,
            ArrayList<PacketInfo> packetInstanceList) {
	    int index = 0;
        for(index = 0; index < packetInstanceList.size(); index++) {            
            if(switchID.equals(packetInstanceList.get(index).getSwitchID())) {
                return index;
            }
        }
        return -1;
        
        
    }

    @SuppressWarnings("unchecked")
    public JSONArray forwardTrace (String switchID, String netSrc, String netDest){
	       ArrayList<PacketInfo> packetInstanceList = new ArrayList<PacketInfo>();
	        DBCollection coll = SDNDe.db.db.getCollection("OFPACKETS");
	       
	        BasicDBObject query = new BasicDBObject(); 

	        query.put("NETSRC", netSrc);
	        query.put("NETDEST", netDest);        


	        DBCursor cursor = coll.find(query);
	        JSONArray traceList = new JSONArray();
	        System.out.println("COUNT :"+cursor.count());
	        while (cursor.hasNext()) {
	            DBObject bt = cursor.next();
	            traceList.put(bt);
	            
	            PacketInfo packetInstance = new PacketInfo();
	            packetInstance.setSwitchID(bt.get("SID").toString());
	            packetInstance.setNetSrc(bt.get("NETSRC").toString());
	            packetInstance.setNetDest(bt.get("NETDEST").toString());
	            packetInstance.setTimeStamp(bt.get("TIME").toString());
	            packetInstance.setSrcPort(bt.get("SRCPORT").toString());
	            packetInstance.setDestPort(bt.get("DESTPORT")!=null?bt.get("DESTPORT").toString():null);
	            packetInstance.setPacketType(bt.get("PTYPE").toString());
                packetInstanceList.add(packetInstance);	        
	        }
	        
            Collections.sort(packetInstanceList);
            int switchIndex = getIndexofPacketList(switchID, packetInstanceList);
            JSONArray jsonArr = new JSONArray();
            if(switchIndex == -1) {
                System.out.println("SwitchID not found !");
            }
            
            else {
            
            
            for(int index = switchIndex; index < packetInstanceList.size(); index++) {
            	JSONObject json = new JSONObject();
            	JSONArray inArr = new JSONArray();
            	try {
            		inArr.put(packetInstanceList.get(index).getNetSrc()==null?"":packetInstanceList.get(index).getNetSrc());
            		inArr.put(packetInstanceList.get(index).getSrcPort()==null?"":packetInstanceList.get(index).getSrcPort());
            		inArr.put(packetInstanceList.get(index).getSwitchID()==null?"":packetInstanceList.get(index).getSwitchID());
            		inArr.put(packetInstanceList.get(index).getNetDest()==null?"":packetInstanceList.get(index).getNetDest());
            		inArr.put(packetInstanceList.get(index).getDestPort()==null?"":packetInstanceList.get(index).getDestPort());
            		inArr.put(packetInstanceList.get(index).getPacketType()==null?"":packetInstanceList.get(index).getPacketType());
					json.put("SwitchID", packetInstanceList.get(index).getSwitchID());
					json.put("Source", packetInstanceList.get(index).getNetSrc());
	            	json.put("Dest", packetInstanceList.get(index).getNetDest());
	            	json.put("Src Port", packetInstanceList.get(index).getSrcPort());
	            	json.put("Dest Port", packetInstanceList.get(index).getDestPort());
	            	json.put("Packet Type", packetInstanceList.get(index).getPacketType());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            	//jsonArr.put(json);
            	jsonArr.put(inArr);
                System.out.println("SwitchID:"+packetInstanceList.get(index).getSwitchID()+
                                   "Source:"+packetInstanceList.get(index).getNetSrc()+
                                   "Dest:"+packetInstanceList.get(index).getNetDest()+
                                   "Src Port:"+packetInstanceList.get(index).getSrcPort()+
                                   "Dest Port:"+packetInstanceList.get(index).getDestPort()+
                                   "Packet Type:"+packetInstanceList.get(index).getPacketType());
            }
            }
	        cursor.close();

	        return jsonArr;
	}
	
    public JSONArray doContinue(){
    	JSONArray sndArr = new JSONArray();
    	JSONArray inArr = new JSONArray();
    	try {
			if(Settings.bkQ!=null){
				for(Entry<IOFSwitch, Entry<OFPacketIn,HashMap>> e:Settings.bkQ){
					LearningSwitchTutorial.forwardAsLearningSwitch(e.getKey(), e.getValue().getKey());
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
				Settings.bkQ.clear();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return(sndArr);
    }
    
    public JSONArray doStep(){
    	JSONArray sndArr = new JSONArray();
    	JSONArray inArr = new JSONArray();
    	List<Entry<IOFSwitch,Entry<OFPacketIn,HashMap>>> tempQ = new ArrayList();
    	tempQ.addAll(Settings.bkQ);
    	Settings.bkQ.clear();
    	try {
			if(tempQ!=null){
				for(Entry<IOFSwitch, Entry<OFPacketIn,HashMap>> e:tempQ){
					LearningSwitchTutorial.forwardAsLearningSwitch(e.getKey(), e.getValue().getKey());
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
				tempQ.clear();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return(sndArr);
    }
    
    public static JSONObject handleCmd (JSONObject json) throws UnknownHostException {
        //DBControllerUtil db=new DBControllerUtil();
        //SDNDe sdnde = new SDNDe(db);
        JSONObject toReturn = new JSONObject();
        System.out.println("*************** START PRINT HERE ***************");
      //  sdnde.backwardTrace("4", "192.168.1.3", "10.0.0.1");
       // sdnde.forwardTrace("4", "192.168.1.3", "10.0.0.1");
        
        try {
			String fnName = (!json.has("FN")?"":(String)json.get("FN"));
			SimpleDateFormat fr = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
			
			toReturn.put("TIME", fr.format(Calendar.getInstance().getTime()));
			toReturn.put("type", "RESULT");
			StringBuffer buf = new StringBuffer();
			buf.append(fnName+"@");
			buf.append("Switch:"+(!json.has("SWID")?"":(String)json.get("SWID")));
			buf.append(" Src:"+(!json.has("SRC")?"":(String)json.get("SRC")+":"));
			buf.append(" Dst:"+(!json.has("DEST")?"":(String)json.get("DEST")));
			toReturn.put("TITLE", buf.toString());
			if(fnName.equals(Settings.BWDTRACE)){
				toReturn.put("CMD", Settings.BWDTRACE);
				toReturn.put("OUTPUT", instance.backwardTrace((String)json.get("SWID"), (String)json.get("SRC"), (String)json.get("DEST")));
			}else if(fnName.equals(Settings.FWDTRACE)){
				toReturn.put("CMD", Settings.FWDTRACE);
				toReturn.put("OUTPUT", instance.forwardTrace((String)json.get("SWID"), (String)json.get("SRC"), (String)json.get("DEST")));
			}else if(fnName.equals(Settings.BREAKPOINT)){
				toReturn.put("CMD", Settings.BREAKPOINT);
				//return sdnde.backwardTrace((String)json.get("SWID"), (String)json.get("SRC"), (String)json.get("DEST"));
			}else if(fnName.equals(Settings.STEP)){
				toReturn.put("CMD", Settings.STEP);
				toReturn.put("OUTPUT", instance.doStep());
				//return sdnde.backwardTrace((String)json.get("SWID"), (String)json.get("SRC"), (String)json.get("DEST"));
			}else if(fnName.equals(Settings.CONTINUE)){
				toReturn.put("CMD", Settings.CONTINUE);
				toReturn.put("OUTPUT", instance.doContinue());
			}else if(fnName.equals(Settings.REFRESH)){
				toReturn.put("CMD", Settings.REFRESH);
				instance.refresh();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        //sdnde.forwardTrace("5", "10.0.0.1", "192.168.1.3");
        System.out.println("*************** END PRINT HERE ***************");
        return toReturn;
    }
	public void monitorFlow (String switchID){
		
	}
	
	public void monitorFlow (String switchID, String netSrc, String netDest, String type){
		
	}
	
	public void monitorFlow (String switchID, String addr, String type){
		
	}
	public void refresh(){
		SDNDe.db.refreshDB();
	}
}
