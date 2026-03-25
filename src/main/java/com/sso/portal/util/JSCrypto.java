package com.sso.portal.util;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

public class JSCrypto {
	
	static String strPassWord = "precursorEncryptKey";
	
	// 비밀번호와 salt 로 키를 생성한다.
	public static byte[] GetKey(int iKeyLen, byte[] arrSalt, byte[] arrPassWord) throws Exception {
		MessageDigest clsMd5 = MessageDigest.getInstance("MD5");

		int iDigestLen = clsMd5.getDigestLength();
		int iDataSize = (iKeyLen + iDigestLen - 1) / iDigestLen * iDigestLen;
		byte[] arrData = new byte[iDataSize];
		int iDataLen = 0;

		clsMd5.reset();

		while(iDataLen < iKeyLen) {
			if(iDataLen > 0) {
				clsMd5.update(arrData, iDataLen - iDigestLen, iDigestLen);
			}
			
			clsMd5.update(arrPassWord);
			
			if(arrSalt != null) {
				clsMd5.update(arrSalt, 0, 8);
			}
			
			clsMd5.digest(arrData, iDataLen, iDigestLen);

			iDataLen += iDigestLen;
		}

		return Arrays.copyOfRange(arrData, 0, iKeyLen);
	}

	public static String decrypt(JSONObject encJSON) {
		String decStr = "";
		
		try {
			String strSalt = encJSON.getString("s");
			String strIv = encJSON.getString("iv");
			String strCt = encJSON.getString("ct");
	
			byte[] arrSalt = DatatypeConverter.parseHexBinary(strSalt);
			byte[] arrIv = DatatypeConverter.parseHexBinary(strIv);
			byte[] arrCt = Base64.decodeBase64(strCt.getBytes("UTF-8"));
	
	        // 암호화/복호화 키를 생성한다.
			byte[] arrKey;
			arrKey = GetKey(32, arrSalt, strPassWord.getBytes("UTF-8"));
			
			SecretKeySpec key = new SecretKeySpec(arrKey, "AES");
	
	        // IV
			IvParameterSpec iv = new IvParameterSpec(arrIv);
	
	        // 복호화한다.
			Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
			aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
			byte[] arrOutput = aesCBC.doFinal(arrCt);
			
			decStr = new String(arrOutput, "UTF-8");
			Log.debug(decStr);

		} catch (Exception e) {
			Log.error(e);
		}
		
		return decStr;
	}

	public static void main(String[] args) {
		JSONObject pwdJSON = new JSONObject("{\"ct\":\"Qw4mlK8ZT0P5ekZk0fdpsw==\",\"iv\":\"141fbe72daf3efd8c749948091c32a48\",\"s\":\"2a772cabf4ba8d01\"}");
		
		System.out.println(JSCrypto.decrypt(pwdJSON));
		
		
	}
}
