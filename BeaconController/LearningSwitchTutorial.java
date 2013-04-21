/**
 * Copyright 2011, Stanford University. This file is licensed under GPL v2 plus
 * a special exception, as described in included LICENSE_EXCEPTION.txt.
 */
package net.beaconcontroller.tutorial;

import java.awt.peer.TextComponentPeer;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.beaconcontroller.core.IBeaconProvider;
import net.beaconcontroller.core.IOFMessageListener;
import net.beaconcontroller.core.IOFSwitch;
import net.beaconcontroller.core.IOFSwitchListener;
import net.beaconcontroller.packet.BasePacket;
import net.beaconcontroller.packet.Ethernet;
import net.beaconcontroller.packet.ICMP;
import net.beaconcontroller.packet.IPacket;
import net.beaconcontroller.packet.IPv4;

import org.json.simple.JSONObject;
import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFType;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tutorial class used to teach how to build a simple layer 2 learning switch.
 * 
 * @author David Erickson (daviderickson@cs.stanford.edu) - 10/14/12
 */
@SuppressWarnings("unused")
public class LearningSwitchTutorial implements IOFMessageListener,
        IOFSwitchListener,Serializable {
    protected static Logger log = LoggerFactory
            .getLogger(LearningSwitchTutorial.class);
    protected IBeaconProvider beaconProvider;
    protected static Map<IOFSwitch, Map<Long, Short>> macTables = new HashMap<IOFSwitch, Map<Long, Short>>();
    private static DBControllerUtil db;
    

    public Command receive(IOFSwitch sw, OFMessage msg) throws IOException {
        initMACTable(sw);
        System.out.println("msg type:" + msg.getType());
        OFPacketIn pi = (OFPacketIn) msg;

        /**
         * This is the basic flood-based forwarding that is enabled.
         */
        // forwardAsHub(sw, pi);

        /**
         * This is the layer 2 based switching you will create. Once you have
         * created the appropriate code in the forwardAsLearningSwitch method
         * (see below), comment out the above call to forwardAsHub, and
         * uncomment the call here to forwardAsLearningSwitch.
         */

        
        forwardAsLearningSwitch(sw, pi);
        return Command.CONTINUE;
    }

    /**
     * EXAMPLE CODE: Floods the packet out all switch ports except the port it
     * came in on.
     * 
     * @param sw
     *            the OpenFlow switch object
     * @param pi
     *            the OpenFlow Packet In object
     * @throws IOException
     */
    public static void forwardAsHub(IOFSwitch sw, OFPacketIn pi) throws IOException {
        // Create the OFPacketOut OpenFlow object
        OFPacketOut po = new OFPacketOut();

        // Create an output action to flood the packet, put it in the
        // OFPacketOut
        OFAction action = new OFActionOutput(OFPort.OFPP_FLOOD.getValue());
        po.setActions(Collections.singletonList(action));

        // Set the port the packet originally arrived on
        po.setInPort(pi.getInPort());

        // Reference the packet buffered at the switch by id
        po.setBufferId(pi.getBufferId());
        if (pi.getBufferId() == OFPacketOut.BUFFER_ID_NONE) {
            /**
             * The packet was NOT buffered at the switch, therefore we must copy
             * the packet's data from the OFPacketIn to our new OFPacketOut
             * message.
             */
            po.setPacketData(pi.getPacketData());
            // System.out.println("Pkt:"+pi.getPacketData());
        }
        // Send the OFPacketOut to the switch
        sw.getOutputStream().write(po);

     

        // Build the Match
        // OFMatch match = OFMatch.load(pi.getPacketData(), pi.getInPort());

        // Learn the port to reach the packet's source MAC
        // macTable.put(Ethernet.toLong(match.getDataLayerSource()),
        // pi.getInPort());

    }

    /**
     * TODO: Learn the source MAC:port pair for each arriving packet. Next send
     * the packet out the port previously learned for the destination MAC, if it
     * exists. Otherwise flood the packet similarly to forwardAsHub.
     * 
     * @param sw
     *            the OpenFlow switch object
     * @param pi
     *            the OpenFlow Packet In object
     * @throws IOException
     */
    /*
     * public void forwardAsLearningSwitch(IOFSwitch sw, OFPacketIn pi) throws
     * IOException { Map<Long,Short> macTable = macTables.get(sw);
     * 
     * /** START HERE: You'll find descriptions of what needs to be done below
     * here, and starter pseudo code. Your job is to uncomment and replace the
     * pseudo code with actual Java code.
     * 
     * First build the OFMatch object that will be used to match packets from
     * this new flow. See the OFMatch and OFPacketIn class Javadocs, which if
     * you are using the tutorial archive, are in the apidocs folder where you
     * extracted it.
     * 
     * OFMatch match = new OFMatch(); match.loadFromPacket(pi.getPacketData(),
     * pi.getInPort()); byte[] dlDst = match.getDataLayerDestination(); Long
     * dlDstId = Ethernet.toLong(dlDst); byte[] dlSrc =
     * match.getDataLayerSource(); Long dlSrcId = Ethernet.toLong(dlSrc); int
     * bufferId = pi.getBufferId();
     * 
     * /** Learn that the host with the source MAC address in this packet is
     * reachable at the port this packet arrived on. Put this source MAC:port
     * pair into the macTable object for future lookups. HINT: you can use
     * Ethernet.toLong to convert from byte[] to Long, which is the key for the
     * macTable Map object.
     * 
     * macTable.put(dlSrcId,pi.getInPort());
     * log.info("Learned MAC address {} is at port {}", dlSrcId,
     * pi.getInPort());
     * 
     * /** Retrieve the port this packet should be sent out by getting the port
     * associated with the destination MAC address in this packet from the
     * macTable object.
     * 
     * Short outPort = null; if (macTable.containsKey(dlDstId)) outPort =
     * macTable.get(dlDstId);
     * 
     * if(outPort!=null){
     * 
     * }else {
     * 
     * }
     * 
     * /** If the outPort is known for the MAC address (the return value from
     * macTable is not null), then Phase 1: Create and send an OFPacketOut,
     * sending it to the outPort learned previously. After this is tested and
     * works move to phase 2.
     * 
     * Phase 2: Instead of an OFPacketOut, create and send an OFFlowMod using
     * the match created earlier from the packet, and send matched packets to
     * the outPort. For extra credit, after sending the OFFlowMod, send an
     * OFPacketOut, but only if the switch did not buffer the packet
     * (pi.getBufferId() == OFPacketOut.BUFFER_ID_NONE), and be sure to set the
     * OFPacketOut's data with the data in pi.
     * 
     * Else if the outPort is not known (return value from macTable is null),
     * then use the forwardAsHub method to send an OFPacketOut that floods out
     * all ports except the port the packet came in.
     * 
     * 
     * // if (outPort != null) { // Phase 1: // OFPacketOut po = ... // ... fill
     * in po, unicast to outPort // ... send po to the switch // // Phase 2: //
     * Comment out the code from phase 1 // OFFlowMod fm = ... // ... fill in fm
     * // ... send fm to the switch // Extra credit: // if (...) { //
     * OFPacketOut po = ... // ... fill in po, unicast to outPort // ... set
     * po's data from pi's data // ... send po to the switch // } //} else { //
     * forwardAsHub(sw, pi); //} }
     */

    public static void forwardAsLearningSwitch(IOFSwitch sw, OFPacketIn pi)
            throws IOException {
        Map<Long, Short> macTable = macTables.get(sw);

        // Build the Match
        OFMatch match = OFMatch.load(pi.getPacketData(), pi.getInPort());

        // Learn the port to reach the packet's source MAC
        macTable.put(Ethernet.toLong(match.getDataLayerSource()),
                pi.getInPort());

        // Retrieve the port previously learned for the packet's dest MAC
        Short outPort = macTable.get(Ethernet.toLong(match
                .getDataLayerDestination()));
        byte[] dlDst = match.getDataLayerDestination();
        Long dlDstId = Ethernet.toLong(dlDst);
        byte[] dlSrc = match.getDataLayerSource();
        Long dlSrcId = Ethernet.toLong(dlSrc);
        int bufferId = pi.getBufferId();
        
      //  JSONObject doc = new JSONObject();
       HashMap doc = new HashMap();
        doc.put(("OF_PACKETS_DEST"),dlDstId);
        doc.put(("OF_PACKETS_SRC"),dlSrcId);
        doc.put(("OF_PACKETS_SRCPORT"),match.getInputPort());
        if(outPort != null) {
            doc.put(("OF_PACKETS_DESTPORT"), outPort);
        } else
        {
            doc.put(("OF_PACKETS_DESTPORT"), "Flood");
        }
        
        String netSrc = Inet4Address.getByAddress(BigInteger.valueOf(match.getNetworkSource()).toByteArray()).toString();
        netSrc = netSrc.substring(1, netSrc.length());
        //doc.put(("OF_PACKETS_NETSRC"),Inet4Address.getByAddress(BigInteger.valueOf(match.getNetworkSource()).toByteArray()).toString());
        doc.put(("OF_PACKETS_NETSRC"),netSrc);
        String netDest = Inet4Address.getByAddress(BigInteger.valueOf(match.getNetworkDestination()).toByteArray()).toString();
        netDest = netDest.substring(1,netDest.length());
        
        	doc.put(("OF_PACKETS_NETDEST"),Inet4Address.getByAddress(BigInteger.valueOf(match.getNetworkDestination()).toByteArray()).toString());
       
        doc.put(("OF_PACKETS_NETDEST"),netDest);
        doc.put(("OF_PACKETS_SWITCHID"),sw.getId());
        ByteBuffer datap = ByteBuffer.allocate(pi.getLength());
        pi.writeTo(datap);
        doc.put(("OF_PACKETS_OBJ"),datap.array());
        doc.put(("OF_PACKETS_PACKETTYPE"),pi.getType().toString());
        doc.put(("OF_PACKETS_TIMESTAMP"),System.currentTimeMillis());

        try {
            System.out.println("Src:" + dlSrcId + " Dst:" + dlDstId +"SWID:"
                    + sw.getId());
        } catch (Exception exp) {
            System.out.println(exp.toString());
        }

        System.out.println("1" + match.getInputPort());
        System.out.println("2"
                + Inet4Address.getByAddress(BigInteger.valueOf(
                        match.getNetworkSource()).toByteArray()).toString());
        System.out.println("3"
                + Inet4Address.getByAddress(BigInteger.valueOf(
                        match.getNetworkDestination()).toByteArray()).toString());
        System.out.println("4" + match.getNetworkTypeOfService() + " -- "
                + Ethernet.toLong(match.getDataLayerSource()));
        
        try {
            IPv4 ipd = new IPv4();
            ipd.deserialize(pi.getPacketData(), 0, pi.getPacketData().length);
            System.out.println("IP::"+ipd.getIdentification());
            doc.put(("OF_PACKETS_IDEN"),(Short)ipd.getIdentification());
        } catch (Exception e) {
            //e.printStackTrace();
        }
        
        System.out.println("5 " + pi.getPacketData());
        
        
        
        //SDNDe sdnde = new SDNDe(db);
        
        if(Settings.isSet() && Settings.BREAKPOINT.equals(Settings.getFnName())
        		&& String.valueOf(sw.getId()).equals(Settings.getSid())
        		&& doc.get("OF_PACKETS_NETSRC").equals(Settings.getSrc())
        		&& doc.get("OF_PACKETS_NETDEST").equals(Settings.getDest())){
        	Settings.bkQ.add(new AbstractMap.SimpleEntry(sw,new AbstractMap.SimpleEntry(pi, doc)));
        	System.out.println("In BreakPoint:"+Settings.bkQ.size());
        	for(Entry e:Settings.bkQ)
        		System.out.println("Entry:"+e.getKey()+"="+e.getValue());
        	SocketServer.sendAsyncPacket(Settings.BREAKPOINT,null);
        	return;
        	
        }else if(Settings.isSet() && Settings.MONITOR.equals(Settings.getFnName())
        		&& String.valueOf(sw.getId()).equals(Settings.getSid())
        		&& doc.get("OF_PACKETS_NETSRC").equals(Settings.getSrc())
        		&& doc.get("OF_PACKETS_NETDEST").equals(Settings.getDest())){
        	//Settings.bkQ.add(new AbstractMap.SimpleEntry(sw,new AbstractMap.SimpleEntry(pi, doc)));
        	//Settings.bkQ.put((Long)sw.getId(), new AbstractMap.SimpleEntry(sw,pi));
        	//System.out.println("In Monitor:"+Settings.bkQ.size());
        	//for(Entry e:Settings.bkQ)
        		//System.out.println("Entry:"+e.getKey()+"="+e.getValue());
        	SocketServer.sendAsyncPacket(Settings.MONITOR,doc);
        	//return;
        }else if(Settings.isSet() && Settings.STEP.equals(Settings.getFnName())
        		&& doc.get("OF_PACKETS_NETSRC").equals(Settings.getSrc())
        		&& doc.get("OF_PACKETS_NETDEST").equals(Settings.getDest())){
        	/*synchronized (Settings.bkQ) {
        		Settings.bkQ.add(new AbstractMap.SimpleEntry(sw,new AbstractMap.SimpleEntry(pi, doc)));
            	System.out.println("In Step:"+Settings.bkQ.size());
            	for(Entry e:Settings.bkQ)
            		System.out.println("Entry:"+e.getKey()+"="+e.getValue());
			}*/
        	
        	
        	SocketServer.sendAsyncPacket(Settings.STEP,null);
			if (!Settings.getSid().equals(String.valueOf(sw.getId()))) {
				Settings.setFnName(Settings.BREAKPOINT);
				Settings.setSid(String.valueOf(sw.getId()));
				Settings.bkQ.add(new AbstractMap.SimpleEntry(sw,new AbstractMap.SimpleEntry(pi, doc)));
				return;
			}
        	
        }
        db.createPacketInDB(doc);
        
        if (outPort != null) {
            // Destination port known, push down a flow
            OFFlowMod fm = new OFFlowMod();
            fm.setBufferId(pi.getBufferId());
            // Use the Flow ADD command
            fm.setCommand(OFFlowMod.OFPFC_ADD);
            // Time out the flow after 5 seconds if inactivity
            fm.setIdleTimeout((short) 5);
            // Match the packet using the match created above
            fm.setMatch(match);
            // Send matching packets to outPort
            OFAction action = new OFActionOutput(outPort);
            fm.setActions(Collections.singletonList((OFAction) action));
            // Send this OFFlowMod to the switch
            sw.getOutputStream().write(fm);

            if (pi.getBufferId() == OFPacketOut.BUFFER_ID_NONE) {
                /**
                 * EXTRA CREDIT: This is a corner case, the packet was not
                 * buffered at the switch so it must be sent as an OFPacketOut
                 * after sending the OFFlowMod
                 */
                OFPacketOut po = new OFPacketOut();
                action = new OFActionOutput(outPort);
                po.setActions(Collections.singletonList(action));
                po.setBufferId(OFPacketOut.BUFFER_ID_NONE);
                po.setInPort(pi.getInPort());
                po.setPacketData(pi.getPacketData());
                sw.getOutputStream().write(po);
            }
        } else {
            // Destination port unknown, flood packet to all ports
            forwardAsHub(sw, pi);
        }
    }

    // ---------- NO NEED TO EDIT ANYTHING BELOW THIS LINE ----------

    /**
     * Ensure there is a MAC to port table per switch
     * 
     * @param sw
     * @throws UnknownHostException 
     */
    private void initMACTable(IOFSwitch sw) throws UnknownHostException {
        Map<Long, Short> macTable = macTables.get(sw);
        if (macTable == null) {
            macTable = new HashMap<Long, Short>();
            macTables.put(sw, macTable);
        }
     
    }

    @Override
    public void addedSwitch(IOFSwitch sw) {
    }

    @Override
    public void removedSwitch(IOFSwitch sw) {
        macTables.remove(sw);
    }

    /**
     * @param beaconProvider
     *            the beaconProvider to set
     */
    public void setBeaconProvider(IBeaconProvider beaconProvider) {
        this.beaconProvider = beaconProvider;
    }

    public void startUp() throws UnknownHostException {
        log.trace("Starting");
        db=new DBControllerUtil();
        new SDNDe(db);
        db.refreshDB();
        beaconProvider.addOFMessageListener(OFType.PACKET_IN, this);
        beaconProvider.addOFMessageListener(OFType.FLOW_MOD, this);
        beaconProvider.addOFSwitchListener(this);
    }

    public void shutDown() {
        log.trace("Stopping");
        beaconProvider.removeOFMessageListener(OFType.PACKET_IN, this);
        beaconProvider.removeOFMessageListener(OFType.FLOW_MOD, this);
        beaconProvider.removeOFSwitchListener(this);
        db.closeConnection();
    }

    public String getName() {
        return "tutorial";
    }
}
