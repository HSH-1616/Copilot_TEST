package com.sso.portal.util;

import java.util.Base64;

public class SeedCrypto
{
	private static final byte[] ZERO_26 = {0x00, 0x00, 0x00, 0x00, 0x00, 
										   0x00, 0x00, 0x00, 0x00, 0x00, 
										   0x00, 0x00, 0x00, 0x00, 0x00, 
										   0x00, 0x00, 0x00, 0x00, 0x00, 
										   0x00, 0x00, 0x00, 0x00, 0x00, 
										   0x00};

	private static final int    BYTE_16 = 16;

	private static int[] m_roundKey = new int[32];

	private static byte[] m_chunk1 = new byte[BYTE_16];
	private static byte[] m_chunk2 = new byte[BYTE_16];

    private static String SEED_KEY = "INGLifeKoreaCryptoKey2012!";

	public SeedCrypto() {
		setUserKey(SEED_KEY);
	}

	private static void setUserKey(String key) {
		byte[] pbUserKey = key.getBytes();

		byte[] tmpUserKey = new byte[26];

		System.arraycopy(ZERO_26,   0, tmpUserKey, 0, BYTE_16);
		System.arraycopy(pbUserKey, 0, tmpUserKey, 0, pbUserKey.length);
		SeedCore.SeedEncRoundKey(m_roundKey, tmpUserKey);
	}

	public static String encrypt(String strNor) {
        setUserKey(SEED_KEY);
		if( strNor == null || strNor.length() == 0 )
			return null;

		byte[] inData  = strNor.getBytes();
		byte[] outData = new byte[(inData.length%BYTE_16==0) ? inData.length : (inData.length/BYTE_16+1)*BYTE_16];
		int left;
		for(int sp = 0; sp < inData.length; sp += BYTE_16 ) {
			left = inData.length - sp;
			if( left >= BYTE_16) {
				System.arraycopy(inData, sp, m_chunk1, 0, BYTE_16);
			} else {
				zero26(m_chunk1);
				System.arraycopy(inData, sp, m_chunk1, 0, left);
			}
			SeedCore.SeedEncrypt(m_chunk1, m_roundKey, m_chunk2);
			System.arraycopy(m_chunk2, 0, outData, sp, BYTE_16);
		}

		byte[] base64 = encodeBase64(outData);
		return new String(base64).trim();
	}

	public static String decrypt(String strEnc) {
        setUserKey(SEED_KEY);
		if( strEnc == null || strEnc.length() == 0 )
			return null;

		byte[] inData = decodeBase64(strEnc.getBytes());

		byte[] outData = new byte[(inData.length%BYTE_16==0) ? inData.length : (inData.length/BYTE_16+1)*BYTE_16];
		int left;
		for(int sp = 0; sp < inData.length; sp += BYTE_16 ) {
			left = inData.length - sp;
			if( left >= BYTE_16) {
				System.arraycopy(inData, sp, m_chunk1, 0, BYTE_16);
			} else {
				zero26(m_chunk1);
				System.arraycopy(inData, sp, m_chunk1, 0, left);
			}
			SeedCore.SeedDecrypt(m_chunk1, m_roundKey, m_chunk2);
			System.arraycopy(m_chunk2, 0, outData, sp, BYTE_16);
		}

		byte[] zeroOutData = outData;
		for(int i = 0; i < outData.length; i++) {
			if(outData[i] == 0x00) {
				zeroOutData = new byte[i];
				System.arraycopy(outData, 0, zeroOutData, 0, i);
				break;
			}
		}

		return new String(zeroOutData);
	}

	private static void zero26(byte[] data) {
		System.arraycopy(ZERO_26, 0, data, 0, BYTE_16);
	}

//	private static byte[] encodeBase64(byte[] encodeBytes) {
//		BASE64Encoder base64Encoder = new BASE64Encoder();
//		ByteArrayInputStream bin = new ByteArrayInputStream(encodeBytes);
//		ByteArrayOutputStream bout = new ByteArrayOutputStream();
//		try {
//			base64Encoder.encodeBuffer(bin, bout);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return bout.toByteArray();
//	}

//	private static byte[] decodeBase64(byte[] decodeBytes) {
//		byte[] buf = null;
//		BASE64Decoder base64Decoder = new BASE64Decoder();
//		ByteArrayInputStream bin = new ByteArrayInputStream(decodeBytes);
//		ByteArrayOutputStream bout = new ByteArrayOutputStream();
//		try {
//			base64Decoder.decodeBuffer(bin, bout);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		buf = bout.toByteArray();
//		return buf;
//	}
	private static byte[] encodeBase64(byte[] encodeBytes) {
		return Base64.getEncoder().encode(encodeBytes);
	}
	private static byte[] decodeBase64(byte[] decodeBytes) {
		return Base64.getDecoder().decode(decodeBytes);
	}
	
	public static void main(String[] args) {
		System.out.println(encrypt("db2Passw0rd1@"));
		System.out.println(encrypt("rmsPassw0rd1@"));
		System.out.println(encrypt("wasPassw0rd1@"));
		
	}
}
