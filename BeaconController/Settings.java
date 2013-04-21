package net.beaconcontroller.tutorial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import net.beaconcontroller.core.IOFSwitch;

import org.json.JSONException;
import org.json.JSONObject;
import org.openflow.protocol.OFPacketIn;

public class Settings {

		private static String fnName;
		public static String getFnName() {
			return fnName;
		}
		public static void setFnName(String fnName) {
			Settings.fnName = fnName;
		}
		public static String getSid() {
			return sid;
		}
		public static void setSid(String sid) {
			Settings.sid = sid;
		}
		public static String getSrc() {
			return src;
		}
		public static void setSrc(String src) {
			Settings.src = src;
		}
		public static String getDest() {
			return dest;
		}
		public static void setDest(String dest) {
			Settings.dest = dest;
		}
		public static boolean isSet() {
			return isSet;
		}
		public static void setSet(boolean isSet) {
			Settings.isSet = isSet;
		}
		
		private static String sid;
		private static String src;
		private static String dest;
		private static boolean isSet;
		
		public static List<Entry<IOFSwitch,Entry<OFPacketIn,HashMap>>> bkQ;
		
		public static final String BREAKPOINT="BREAKPOINT";
		public static final String FWDTRACE="FWDTRACE";
		public static final String BWDTRACE="BWDTRACE";
		public static final String STEP="STEP";
		public static final String REFRESH="REFRESH";
		public static final String CONTINUE="CONTINUE";
		public static final String MONITOR="MONITOR";
		
		static {
			isSet = false;
			bkQ = new ArrayList();
		}
		
		public static synchronized void updateSettings(JSONObject json){
	    	
	    	Settings.setSet(true);
	    	
	    	try {
	    		Settings.setSid(!json.has("SWID")?"":json.getString("SWID"));
				Settings.setFnName(!json.has("FN")?"":(String)json.get("FN"));
				Settings.setSrc(!json.has("SRC")?"":json.getString("SRC"));
				Settings.setDest(!json.has("DEST")?"":json.getString("DEST"));
				if(Settings.getFnName().equals(Settings.BREAKPOINT))
					bkQ.clear();
				//else if(Settings.getFnName().equals(Settings.REFRESH))
					//Settings.setSet(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	
	    	
	    }
}
