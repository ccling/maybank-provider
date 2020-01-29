package com.example.provider.constant;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;


/*
 * for demo purpose only
 */
@Component
public class Common {

	public static Map<String, Byte> trxTypeMap = new HashMap<String, Byte>();
	
	public static Map<Byte, String> trxIdMap = new HashMap<Byte, String>();
	
	// suppose to load from table T_TRXTYPE 
	private Common() {
		trxTypeMap.put("BILL PAYMENT", (byte)0);
		
		trxTypeMap.put("ATM WITHDRWAL", (byte)1);
		
		trxTypeMap.put("3rd Party FUND TRANSFER", (byte)2);
		
		trxTypeMap.put("FUND TRANSFER", (byte)3);
		
		for (Map.Entry<String, Byte> entry : trxTypeMap.entrySet()) {
			trxIdMap.put(entry.getValue(), entry.getKey());
		}
	}
	
}
