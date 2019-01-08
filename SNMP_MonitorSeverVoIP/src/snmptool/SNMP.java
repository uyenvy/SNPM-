package snmptool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SNMP {
	private static String ip_host = "";
	private static String oid = "";
	private static String communityString = "public"; //default:public
	private static int timeout = 1000;	
	private static int communitylenght = 0;
	private static int requestIdLenght = 0;
	private static int oid_length = 0;
	private static String error = "";
	private static String method = "";
	private static String value = "";
	
	
	public String getError() {
		return error;
	}

	public String getValue() {
		return value;
	}

	public SNMP(String ip_host, String oid, String communityString, String method) {
		super();
		this.ip_host = ip_host;
		this.oid = oid;
		this.communityString = communityString;
		this.method = method;
	}
	
//Send packet with UDP socket===================================================================================
	public byte[] sendPacket() throws IOException {
		int port = 161;
		byte[] requestPacket = createRequestPacket();
		InetAddress ia = InetAddress.getByName(ip_host);
		DatagramSocket socket = new DatagramSocket();
		socket.setSoTimeout(9000);
		DatagramPacket packet_send = new DatagramPacket(requestPacket, requestPacket.length, ia, port);
		socket.send(packet_send);
		
		byte[] receiveData = new byte[1024];
		DatagramPacket packet_recieve = new DatagramPacket(receiveData, receiveData.length);
		socket.receive(packet_recieve);
		socket.close();
		return receiveData;	
	}
	
	// Create SNMP request packet
	private static byte[] createRequestPacket() throws IOException{
		byte[] snmpData = createSNMPdata();
		return merge2ByteArrays(new byte[] {0x30, (byte) snmpData.length}, snmpData);
	}
	
	// Create SNMP data
	private static byte[] createSNMPdata() throws IOException {
		byte[] version = new byte[] {2,1,0};
		byte[] community = communityString.getBytes();
		communitylenght=community.length; 
		byte[] pdu = createRequest();
		byte[] snmpData = merge2ByteArrays(new byte[] {4,(byte) community.length},community);
		snmpData = merge2ByteArrays(version, snmpData);
		snmpData = merge2ByteArrays(snmpData, pdu);
		return snmpData;
	}
	
	// Create data request: PDU packet
	private static byte[] createRequest() throws IOException {
		byte[] request = createRequestData();
		if(method == "get-request") { //get-request: a0
			return merge2ByteArrays(new byte[] {(byte) 0xa0,(byte) request.length}, request);
		}//else method == get next request: a1
		return merge2ByteArrays(new byte[] {(byte) 0xa1,(byte) request.length}, request);	
	}
	
	// Create request: requestID, error status, error index, variable-binding 
	private static byte[] createRequestData() throws IOException {
		byte[] variableList = createVariableList();
		byte[] variableIdentify = merge2ByteArrays(new byte[] {0x30, (byte) variableList.length}, variableList);
		byte[] errorIndex = new byte[] {2,1,0};
		byte[] errorStatus = new byte[] {2,1,0};
		String requestIDhex = convertInt2Hex(generateRequestID());
		byte[] requestID = hexStringToByteArray(requestIDhex);
		requestIdLenght=requestID.length;
		
		byte[] request = merge2ByteArrays(new byte[]{2,(byte) requestID.length},requestID);
		request = merge2ByteArrays(request, errorStatus);
		request = merge2ByteArrays(request, errorIndex);
		request = merge2ByteArrays(request, variableIdentify);
		return request;		
	}
	
	
	//convert hex to string 
			public static byte[] hexStringToByteArray(String s) {
				byte[] b = s.getBytes();
			return b;
			   
			}
		
		/*//convert hex to string 
		public static byte[] hexStringToByteArray(String s) {
		    byte data[] = new byte[s.length()/2];
		    for(int i=0;i < s.length();i+=2) {
		        data[i/2] = (Integer.decode("0x"+s.charAt(i)+s.charAt(i+1))).byteValue();
		    }
		    return data;
		}*/
		//integer to hex
		public static String convertInt2Hex(int num) {
				return Integer.toHexString(num);			
		}
		//
		public static int generateRequestID() {
			return (int) Math.abs(Math.random());
		}
	
	// Create Variable binding list : 1 item - 0x30 type SEQUENCE
	private static byte[] createVariableList() throws IOException {
		byte[] variableValue = createVariableAndValue();
		return merge2ByteArrays(new byte[]{0x30,(byte) variableValue.length}, variableValue);
	}
	
	// Create Variable- binding: 6(first variable binding), OID Lenght, OID and value = 0 ===================================================================================
	private static byte[] createVariableAndValue() throws IOException {
		byte[] firtVariable = createVariableBind();
		byte[] value = new byte[] {5,0};
		return merge2ByteArrays(firtVariable, value);
	}
	
	// Create Variable-binding: 6 (first variable binding), OID lenght, OID ==========================================================================
	private static byte[] createVariableBind() throws IOException {
		byte[] oid = convertOIDtoByte();
		oid_length = oid.length;
		byte[] variable = new byte[] {6,(byte) oid.length};
		return merge2ByteArrays(variable, oid);
	}
	
	//Convert OID to Byte[], create OID =====================================================================================
	private static byte[] convertOIDtoByte() {
		String[] oidNumber = oid.split("\\.");
		byte[] oidBytes = new byte[oidNumber.length];		
		int i = 0;
		for(String s: oidNumber ) {
			oidBytes[i] = Byte.valueOf(s);
			i++;
		}
		byte[] oidHandle = new byte[oidBytes.length-1];
		oidHandle[0] = (byte) (oidBytes[0]*40+oidBytes[1]);
		for(int j=2; j<oidBytes.length;j++)
		{
			oidHandle[j-1] = oidBytes[j];
		}
		return oidHandle;
	}
	private static byte[] merge2ByteArrays(byte[] arr1, byte[] arr2) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(arr1);
		outputStream.write(arr2);
		return outputStream.toByteArray();
	}
	

// Get Response packet ====================================================
	
	public Object getResponse(){
		
		byte[] receiveData = null;
		try {
		    receiveData = sendPacket();
		}catch(IOException ex){
		    error = ex.toString();
		    return null;
		}
		if(receiveData == null){
		    return null;
		}
		
		int byteErrorPosition= communitylenght + requestIdLenght + 14 -1;  // array start at 0 => position -1
		int byteError = (int) receiveData[byteErrorPosition];
		int ret = (int) receiveData[receiveData[1]+1];//??????
		
		switch (byteError) {
		case 0: // noError
			error="noError (0)";
		    break;
		case 1:
		    error="tooBig (1)";
		    return null;
		case 2:
		    error="noSuchName (2)";
		    return null;
		case 3:
		    error="badValue (3)";
		    return null;
		case 4:
		    error="readOnly (4)";
		    return null;
		case 5:
		    error="genErr (5)";
		    return null;
		default:
		    error="unrecognized error=" + byteError;
		    return null;
	        }

		int byteDataTypePosition = 24 + communitylenght+ requestIdLenght +  oid_length - 1; // array start at 0 => position -1
		//System.out.println("byteDataTypePosition "+byteDataTypePosition);
		int byteDataType = (int) receiveData[byteDataTypePosition];
		//System.out.println(byteDataType);

		int byteDataLenghtPosition = byteDataTypePosition + 1;
		//System.out.println(byteDataLenghtPosition);
		int byteDataLength = (int) receiveData[byteDataLenghtPosition];
		//System.out.println(byteDataLength);
		int positionFirstByteData = byteDataLenghtPosition + 1;
		byte[] databytes = null;

		switch (byteDataType) {
		case 2: // INTEGER
		    
			if(oid == "1.3.6.1.4.1.2021.11.9.0") {
				value = "ssCpuUser.0 ";
			}
			
		    if(byteDataLength != 1){
			error = "data type=INTEGER but byteDataLength=" + byteDataLength;
			return null;
		    }
		    else {
				value += String.valueOf(receiveData[positionFirstByteData]);
				return value;
			}
		case 4: // OCTET STRING
		    if(oid == "1.3.6.1.4.1.2021.4.6.0")
		    {
		    	value = "memAvailable: ";
		    }else {
				if(oid == "1.3.6.1.4.1.2021.10.1.3.1")
					value = "laload.1: ";
			}
			
		    if(byteDataLength == 0){
		    	return null;
	 	    }else 
	 	    	if(byteDataLength == 1)
	 	    	{
	 	    		value = Integer.toHexString(0xff & receiveData[positionFirstByteData]);
	 	    		return value;
	 	    	}
		    databytes = createDataByte(receiveData, positionFirstByteData, byteDataLength);
		    if(databytes == null)
		    {
			   	error = "OCTET STRING: databytes array is null";
		    	return null;
		    }
		    else
		    {
			    value = new String ("OCTET STRING: " + databytes);
			   	return value;
			 }
		case 0x41:  // Counter (Counter32 in SNMPv2)
		    databytes = createDataByte(receiveData, positionFirstByteData, byteDataLength);

		    if(databytes == null)
		    {
				error = "Counter (Counter32 in SNMPv2): databytes null";
				return null;
		    }
		    else
		    {
				int lenght = databytes.length;
				
				int m=1;
				int r = 0;
				for(int i=lenght-1; i>=0; i--){
				    r += (int)(0xff & databytes[i]) * m;
				    m *= 256;
			}
				value = "Counter (Counter32 in SNMPv2): " + String.valueOf(r);
				return value;
		    }
		case 0x43: // TimeTicks 
		    databytes = createDataByte(receiveData, positionFirstByteData, byteDataLength);

		    if(databytes == null){
			error = "TimeTicks: databtes array is null";
			return null;
		    }else{
			int lenght = databytes.length;
			
			int m=1;
			int r = 0;
			for(int i=lenght-1; i>=0; i--){
			    r += (int)(0xff & databytes[i]) * m;
			    m *= 256;
			}
			value = "TimeTicks:" + String.valueOf(r);
			return value;
		    }
		}
		return value;
	    }
	
	// create byte array from array available
	private static byte[] createDataByte(byte[] a, int start, int lenght) {
		if(lenght<=0) {
			return null;
		}
		byte[] array = new byte[lenght];
		int j = start;
		for(int i=0; i<lenght; i++)
		{
			array[i] = a[j++];
		}
		return array;
	}
}
