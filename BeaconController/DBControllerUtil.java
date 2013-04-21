package net.beaconcontroller.tutorial;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

public class DBControllerUtil {
	private static final Logger log = Logger.getLogger(DBControllerUtil.class.getName());
	Mongo mongoObj;
	DB db;
	
	private final static String OF_PACKETS = "OFPACKETS";
	private final static String OF_PACKETS_ID = "PID";
	private final static String OF_PACKETS_MSGID = "MSGID";
	private final static String OF_PACKETS_TIMESTAMP = "TIME";
	private final static String OF_PACKETS_SRC = "SRC";
	private final static String OF_PACKETS_NETSRC = "NETSRC";
	private final static String OF_PACKETS_SRCPORT = "SRCPORT";
	private final static String OF_PACKETS_DEST = "DEST";
	private final static String OF_PACKETS_NETDEST = "NETDEST";
	private final static String OF_PACKETS_DESTPORT = "DESTPORT";
	private final static String OF_PACKETS_SEQNO = "SEQNUM";
	private final static String OF_PACKETS_OBJ = "POBJ";
	private final static String OF_PACKETS_PACKETTYPE = "PTYPE";
	private final static String OF_PACKETS_SWITCHID = "SID";
	private final static String OF_PACKETS_IDEN = "IDENTIFIER";
	private SocketServer sock ;
	/*********** Managing DB CONNECTIONS ***************/

	public DBControllerUtil() throws UnknownHostException {

		try{
		// object will be a connection to a MongoDB server for the specified
		// database.
		mongoObj = new Mongo("127.0.0.1", 27017);
		sock = new SocketServer();
		String configXML = System.getProperty("configXML");
		String jWebHome = System.getProperty("jWebHome");
		sock.startConnection(configXML, jWebHome);
		// get a instance to db
		db = mongoObj.getDB("ControllerSDNData");
		}catch(Exception exp){
			exp.printStackTrace();
		}
	}

	public void closeConnection() {
		mongoObj.close();
	}

	/*********** Managing Celeb Info ***************/

	/**
	 * createUser Input : user details contained in the User object. Output :
	 * Status of the database transaction and new user details. Description :
	 * Adds a new user , creates default list and returns the new user id.
	 */

	public void createPacketInDB(HashMap tweetJson) {

		// Create your BSON
		BasicDBObject doc = new BasicDBObject();
	//	doc.put(OF_PACKETS_ID, tweetJson.get("OF_PACKETS_ID"));
		doc.put(OF_PACKETS_DEST, tweetJson.get("OF_PACKETS_DEST"));
		doc.put(OF_PACKETS_SRC, tweetJson.get("OF_PACKETS_SRC"));
		doc.put(OF_PACKETS_NETDEST, tweetJson.get("OF_PACKETS_NETDEST"));
        doc.put(OF_PACKETS_NETSRC, tweetJson.get("OF_PACKETS_NETSRC"));
        
        doc.put(OF_PACKETS_SRCPORT, tweetJson.get("OF_PACKETS_SRCPORT")); // Input port
		doc.put(OF_PACKETS_DESTPORT, tweetJson.get("OF_PACKETS_DESTPORT"));
		//doc.put(OF_PACKETS_MSGID, tweetJson.get("OF_PACKETS_MSGID"));
		doc.put(OF_PACKETS_OBJ, tweetJson.get("OF_PACKETS_OBJ"));
		doc.put(OF_PACKETS_PACKETTYPE, tweetJson.get("OF_PACKETS_PACKETTYPE"));
		doc.put(OF_PACKETS_IDEN, tweetJson.get("OF_PACKETS_IDEN"));
		doc.put(OF_PACKETS_SWITCHID, tweetJson.get("OF_PACKETS_SWITCHID"));
		doc.put(OF_PACKETS_TIMESTAMP, tweetJson.get("OF_PACKETS_TIMESTAMP"));
		
		// insert into the database
		DBCollection coll = db.getCollection(OF_PACKETS);
		coll.insert(doc);		

	}
	
	public void refreshDB(){
		DBCollection coll = db.getCollection(OF_PACKETS);
		coll.remove(new BasicDBObject());
			
	}
	
	
	
}
