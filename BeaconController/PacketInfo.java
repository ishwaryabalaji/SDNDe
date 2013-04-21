package net.beaconcontroller.tutorial;

public class PacketInfo implements Comparable<PacketInfo>{
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
	
	private String packetID;
	private String packetMsgId;
	private String timeStamp;
	private String src;
	private String netSrc;
	private String srcPort;
	private String dest;
	private String netDest;
	private String destPort;
	private String seqNumber;
	private String packetObj;
	private String packetType;
	private String switchID;
	private String identifier;
	/**
	 * @return the destPort
	 */
	public String getDestPort() {
		return destPort;
	}
	/**
	 * @param destPort the destPort to set
	 */
	public void setDestPort(String destPort) {
		this.destPort = destPort;
	}
	/**
	 * @return the packetType
	 */
	public String getPacketType() {
		return packetType;
	}
	/**
	 * @param packetType the packetType to set
	 */
	public void setPacketType(String packetType) {
		this.packetType = packetType;
	}
	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}
	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	/**
	 * @return the switchID
	 */
	public String getSwitchID() {
		return switchID;
	}
	/**
	 * @param switchID the switchID to set
	 */
	public void setSwitchID(String switchID) {
		this.switchID = switchID;
	}
	/**
	 * @return the packetObj
	 */
	public String getPacketObj() {
		return packetObj;
	}
	/**
	 * @param packetObj the packetObj to set
	 */
	public void setPacketObj(String packetObj) {
		this.packetObj = packetObj;
	}
	/**
	 * @return the seqNumber
	 */
	public String getSeqNumber() {
		return seqNumber;
	}
	/**
	 * @param seqNumber the seqNumber to set
	 */
	public void setSeqNumber(String seqNumber) {
		this.seqNumber = seqNumber;
	}
	/**
	 * @return the netDest
	 */
	public String getNetDest() {
		return netDest;
	}
	/**
	 * @param netDest the netDest to set
	 */
	public void setNetDest(String netDest) {
		this.netDest = netDest;
	}
	/**
	 * @return the dest
	 */
	public String getDest() {
		return dest;
	}
	/**
	 * @param dest the dest to set
	 */
	public void setDest(String dest) {
		this.dest = dest;
	}
	/**
	 * @return the srcPort
	 */
	public String getSrcPort() {
		return srcPort;
	}
	/**
	 * @param srcPort the srcPort to set
	 */
	public void setSrcPort(String srcPort) {
		this.srcPort = srcPort;
	}
	/**
	 * @return the netSrc
	 */
	public String getNetSrc() {
		return netSrc;
	}
	/**
	 * @param netSrc the netSrc to set
	 */
	public void setNetSrc(String netSrc) {
		this.netSrc = netSrc;
	}
	/**
	 * @return the src
	 */
	public String getSrc() {
		return src;
	}
	/**
	 * @param src the src to set
	 */
	public void setSrc(String src) {
		this.src = src;
	}
	/**
	 * @return the timeStamp
	 */
	public String getTimeStamp() {
		return timeStamp;
	}
	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	/**
	 * @return the packetMsgId
	 */
	public String getPacketMsgId() {
		return packetMsgId;
	}
	/**
	 * @param packetMsgId the packetMsgId to set
	 */
	public void setPacketMsgId(String packetMsgId) {
		this.packetMsgId = packetMsgId;
	}
	/**
	 * @return the packetID
	 */
	public String getPacketID() {
		return packetID;
	}
	/**
	 * @param packetID the packetID to set
	 */
	public void setPacketID(String packetID) {
		this.packetID = packetID;
	}
    @Override
    public int compareTo(PacketInfo info) {
        // TODO Auto-generated method stub
        if (Long.parseLong(this.timeStamp) < Long.parseLong(info.timeStamp)) {
            return -1;
        } else if (Long.parseLong(this.timeStamp) > Long.parseLong(info.timeStamp)) {
            return 1;
        } else {
            return 0;
        }
    }	
}
