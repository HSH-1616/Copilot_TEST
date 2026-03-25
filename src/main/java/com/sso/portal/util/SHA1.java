package com.sso.portal.util;

import java.security.MessageDigest;
import sun.misc.BASE64Encoder;

public class SHA1 {
	
	public String getEncrypt(String str){
		String strSHA = "";
		try{
			byte[] data = str.getBytes();
	        MessageDigest md = MessageDigest.getInstance("SHA1");
	        md.update(data);
	        byte[] hash = md.digest();
	        
	        BASE64Encoder encoder = new BASE64Encoder();
	        
	        strSHA = encoder.encode(hash);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return strSHA;
	}
}
