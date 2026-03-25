/**
 * 파일명: KyeryongUtil.java
 * 작성일: 2004. 2. 23.
 * 작성자: 진헌규(hkjin@daou.co.kr)
 */
package com.sso.portal.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;



/**
 *
 * 다양한 static method를 갖고 있는 유틸리티 클래스.
 * 다음과 같은 기능을 지원한다.
 * <ul>
 * <li>정수/실수를 입력받아 문자열로 변환(컴마 삽입 및 유효자리수 조절)</li>
 * <li>현재 날짜/시간을 return</li>
 * <li>문자열 상호변환</li>
 * <li>byte 배열과 16진수 문자열의 상호변환</li>
 * </ul>
 *
 * @author 진헌규
 *
 * @version 2004-04-10
 * @version 2004-02-23
 */
public class PrecursorUtil {
      /**
       * 정수에 대한 기본 포맷: 3자리마다 ',' 삽입.
       * (정수는 2^31-1이 최대값이므로 10자리를 넘을 수 없다.)
       */
      private static final DecimalFormat DEFAULT_INTEGER_FORMAT = new DecimalFormat("#,###,###,###");

      /**
       * 실수에 대한 기본 포맷: 3자리마다 ',' 삽입하고 소수점이하는 2자리로 반올림.
       */
      private static final DecimalFormat DEFAULT_DOUBLE_FORMAT = new DecimalFormat("####,###,###,###,###,###,###,###,###,###.##");

      /**
       * 소수점 이하가 없는 실수 포맷
       */
      public static final DecimalFormat NO_FRACTION_FORMAT = new DecimalFormat("############################");

      /**
       * 3자리마다 ',' 삽입하지 않는 실수 포맷.
       */
      public static final DecimalFormat NO_COMMA_DOUBLE_FORMAT = new DecimalFormat("############################.##########");

      /**
       * 인스턴스를 생성할 수 없도록 기본생성자를 private로 선언.
       */
      private PrecursorUtil() {
      }

      /**
       * 주어진 포맷대로 실수값을 포맷하여 문자열로 변환한다.
       * 포맷형식은 <code>java.text.DecimalFormat</code> 의 API를 참조.
       *
       * @param src		변환하고자하는 실수값
       * @param format 	변환하려는 포맷
       * @return 변환된 문자열
       */
      public static String format(double src,String format) {
            try {
                  return new DecimalFormat(format).format(src);
            } catch(Exception ex) {
                  return "";
            }
      }

      /**
       * 주어진 포맷대로 실수값을 포맷하여 문자열로 변환한다.
       *
       * @param src
       *            변환하고자하는 실수값
       * @param format
       *            변환하려는 포맷
       * @return 변환된 문자열
       */
      public static String format(double src,DecimalFormat format) {
            try {
                  return format.format(src);
            } catch(Exception ex) {
                  return "";
            }
      }

      /**
       * 기본포맷대로 정수값을 포맷하여 문자열로 변환한다.
       * 포맷형식은 <code>java.text.DecimalFormat</code> 의 API를 참조.
       *
       * @param src	변환하고자하는 정수값
       * @return 		변환된 문자열
       */
      public static String format(int src) {
            return DEFAULT_INTEGER_FORMAT.format(src);
      }

      /**
       * 기본포맷대로 실수값을 포맷하여 문자열로 변환한다.
       * 포맷형식은 <code>java.text.DecimalFormat</code> 의 API를 참조.
       *
       * @param src
       *            변환하고자하는 실수값
       * @return 변환된 문자열
       */
      public static String format(double src) {
            return DEFAULT_DOUBLE_FORMAT.format(src);
      }

      /**
       * {@link formatNumber} method에서 내부적으로 사용하는 regex pattern.
       * 맨 앞의 숫자 1~2자리에서 match된다.
       */
      private static final Pattern REPLACE_1ST_DIGITS = Pattern.compile("^([+-]?\\d{1,2})((\\d{3})+)(?=$|\\.)");

      /**
       * {@link formatNumber} method에서 내부적으로 사용하는 regex pattern.
       * 세자리 숫자마다 match된다.
       */
      private static final Pattern REPLACE_OTHER_DIGITS = Pattern.compile("(\\d{3})(?=\\d)");

      /**
       * 문자열(주로 BigDecimal.toString()) 형태로 된 10진수값을 받아 세자리마다 ','를 삽입한다.
       * (포맷형식은 지정불가능. 3자리마다 ',' 삽입만 지원함)
       * double 형으로도 완전하게 표현할 수 없는 유효숫자를 가진 값에 대해 사용
       *
       * @param src
       * @return
       */
      public static String formatNumber(String src) {
            String tmp = REPLACE_1ST_DIGITS.matcher(src).replaceFirst("$1,$2");
            return REPLACE_OTHER_DIGITS.matcher(tmp).replaceAll("$1,");
      }

      /**
       *
       * @param src
       * @return
       */
      public String unformatNumber(String src) {
            return src.replaceAll(",","");
      }

      /**
       * 문자열(주로 BigDecimal.toString()) 형태로 된 10진수값을 받아 세자리마다 ','를 삽입하고,
       * 소수점 이하는 digitBelowZero 자리수로 반올림한다.
       * 단, 소수점 이하 반올림은 약 50자리 미만(double 형의 최대 유효자리수)만 지원한다.
       *
       * @param src
       * @param digitFraction
       * @return
       */
      public static String formatNumber(String src,int digitFraction) {
            int idx;
            if((idx = src.indexOf('.')) > 0) {
                  String fraction = src.substring(idx);
                  try {
                        DecimalFormat df1 = new DecimalFormat("." + repeat('0',digitFraction));
                        fraction = format(Double.parseDouble(fraction),df1);
                        return formatNumber(src.substring(0,idx) + fraction);
                  } catch(Throwable t) {
                        return src;
                  }
            }
            return formatNumber(src);
      }

      /**
       * 기본포맷대로 실수값을 포맷하여 문자열로 변환한다.
       * 음수는 모두 0으로 처리한다.
       * 포맷형식은 <code>java.text.DecimalFormat</code> 의 API를 참조.
       *
       * @param src 변환하고자하는 실수값
       * @return 변환된 문자열
       *
       */
      public static String formatPositive(double src) {
            if(src < 0) {
                  src = 0;
            }
            return DEFAULT_DOUBLE_FORMAT.format(src);
      }

      /**
       * 정수와 자리수를 받아, 자리수에 맞게 정수값 앞에 '0'을 덧붙여준다.
       * key 생성 등의 한정된 용도에 사용되므로, 0보다 큰 정수값에 대해서만 동작한다.
       * 예) getZeroInsertedString(23, 5) -> "00023"
       *
       * @param value
       * @param digit
       * @return
       */
      public static String getZeroInsertedString(int value,int digit) {
            if(value < 0) {
                  return "";
            }

            DecimalFormat f = new DecimalFormat(repeat('0',digit));
            return f.format(value);
      }

      /**
       *
       * DB에 접속하여 현재일시를 받아오는 {@link com.kyeryong.framework.sql.QueryRunner}
       *
       */
      
      private static final DateFormat currentTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss");


      private static final DateFormat todayFormat = new SimpleDateFormat("yyyyMMdd");

      /**
       * 오늘 날짜를 yyyyMMdd 형식으로 돌려준다.
       * 일단위 시간은 DB서버와 크게 다르지 않으므로 WAS의 시간을 기준으로 삼음.
       *
       * @return 오늘 날짜
       */
      public static String getToday() {
            return todayFormat.format(new Date());
      }


      /**
       * ISO-8859-1 인코딩의 문자열을 기본 인코딩의 문자열로 변환한다.
       * (UNIX의 경우 환경변수 LANG이 ko, 혹은 ko_KR이라면 기본 인코딩은 "EUC-KR"이 된다)
       *
       * @param src
       * @return
       */
      public static String fromISO8859(String src) {
            try {
                  return new String(src.getBytes("ISO-8859-1"));
            } catch(Exception ex) {
                  return null;
            }
      }

      /**
       *
       * 기본 인코딩의 문자열을 ISO-8859-1 인코딩의 문자열로 변환한다.
       *
       * (UNIX의 경우 환경변수 LANG이 ko, 혹은 ko_KR이라면 기본 인코딩은 "EUC-KR"이 된다)
       *
       * @param src
       *
       * @return
       */
      public static String toISO8859(String src) {
            try {
                  return new String(src.getBytes(),"ISO-8859-1");
            } catch(Exception ex) {
                  return null;
            }
      }

      /**
       * 주어진 배열의 원소를 순환하며 그 값을 System.out으로 출력해준다.
       *
       * @param objArray
       */
      public static void dump(byte[] byteArray) {
            if(byteArray == null) {
                  return;
            }
            System.out.print("array: [");
            for(int i = 0;i < byteArray.length;i++) {
                  System.out.print(byteArray[i]);
                  System.out.print(", ");
            }
            System.out.println("]");
      }

      /**
       * 주어진 배열의 원소를 순환하며 그 값을 System.out으로 출력해준다.
       *
       * @param objArray
       */
      public static void dump(Object[] objArray) {
            if(objArray == null) {
                  return;
            }
            System.out.print("array: [");
            for(int i = 0;i < objArray.length;i++) {
                  System.out.print(objArray[i]);
                  System.out.print(", ");
            }
            System.out.println("]");
      }

      /**
       * 주어진 Enumeration을 순환하며 그 값을 System.out으로 출력해준다.
       *
       * @param en
       */
      public static void dump(Enumeration en) {
            if(en == null) {
                  return;
            }
            System.out.print("enumeration: [");
            while(en.hasMoreElements()) {
                  System.out.print(en.nextElement());
                  System.out.print(", ");
            }
            System.out.println("]");
      }

      /**
       * 주어진 문자열에 한글이 한글자라도 들어있는지를 알려준다.
       *
       * @param str
       * @return
       */
      public static boolean containsHangul(String str) {
            for(int i = 0;i < str.length();i++) {
                  Character.UnicodeBlock unicode_block = Character.UnicodeBlock.of(str.charAt(i));
                  if(unicode_block == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO || unicode_block == Character.UnicodeBlock.HANGUL_JAMO || unicode_block == Character.UnicodeBlock.HANGUL_SYLLABLES) {
                        return true;
                  }
            }

            return false;
      }

      /**
       * 주어진 문자열에 white space(스페이스, 탭, 리턴)이 들어있는지 알려준다.
       *
       * @param str
       * @return
       */
      public static boolean containsWhiteSpace(String str) {
            for(int i = 0;i < str.length();i++) {
                  if(Character.isWhitespace(str.charAt(i))) {
                        return true;
                  }
            }
            return false;
      }

      /**
       * 문자열과 원하는길이를 받아서 문자열로 리턴한다.
       * 리턴시 문자열이 원하는 길이보다 짧을결우 공백으로 채워서 리턴한다.
       * 예) getFixedStrFormat("String", 10, '0')
       *       return = 'String0000'
       *
       * @return 문자열
       */
      public static String getFixedStrFormat(String str,int len,char ch) {
            StringBuffer sb = new StringBuffer(str);
            int byteLen = str.getBytes().length;

            try {
                  for(int i = 0;i < (len - byteLen);i++) {
                        sb.append(ch);
                  }

                  return sb.toString();
            } catch(Exception ex) {
                  return str;
            }
      }

      /**
       * 문자열과 원하는길이를 받아서 문자열로 리턴한다.
       * 리턴시 문자열이 원하는 길이보다 짧을 경우 공백으로 채워서 리턴한다.
       * 한글은 2글자로 처리된다.
       *
       * 예) getFixedStrFormat("String", 10)
       *       return = 'String    '
       *
       * @ return 문자열
       */
      public static String getFixedStrFormat(String str,int len) {
            return getFixedStrFormat(str,len,' ');
      }

      /**
       * 주어진 문자 ch를 length번만큼 반복한 문자열을 생성하여 돌려준다.
       *
       * @param ch
       * @param length
       * @return
       */
      public static String repeat(char ch,int length) {
            char[] ch_array = new char[length];
            Arrays.fill(ch_array,ch);
            return new String(ch_array);
      }

      /**
       * 호출된 시점에서의 stack trace를 문자열 형태로 생성하여 돌려준다.
       * (현재는 weblogic server 내부의 stack trace는 생략)
       *
       * @return
       */
      public static String getStackTrace() {
            try {
                  throw new RuntimeException();
            } catch(RuntimeException ex) {
                  StackTraceElement[] ste = ex.getStackTrace();
                  StringBuffer sb = new StringBuffer();
                  for(int i = ste.length - 17;i > 1;i--) {
                        sb.append(ste[i].getClassName()).append('.');
                        sb.append(ste[i].getMethodName());
                        sb.append(" -> ");
                  }
                  return sb.toString();
            }
      }

      /**
       * byte의 배열을 문자열로 변환한다.
       * 1byte는 2자리의 char로 변환되며, 다음과 같은 형태를 띤다.
       * 9 -> "9"
       * 10 (=0xa) -> "a"
       * 255 (=0xff) -> "ff"
       *
       * @param byteArray
       * @return
       */
      public static String toHexString(byte[] byteArray) {
            if(byteArray == null) {
                  return null;
            }
            StringBuffer sb = new StringBuffer();
            for(int i = 0;i < byteArray.length;i++) {
                  int b = (byteArray[i] & 0x80) == 0x80 ? 256 + byteArray[i] : byteArray[i];
                  sb.append(Character.forDigit(b / 16,16)).append(Character.forDigit(b % 16,16));
            }
            return sb.toString();
      }

      /**
       * 문자열을
       * @param str
       * @return
       */
      public static byte[] fromHexString(String str) {
            if(str == null) {
                  return null;
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            for(int i = 0;i < str.length();i++) {
                  int digit = Character.digit(str.charAt(i),16) * 16 + Character.digit(str.charAt(++i),16);
                  bos.write(digit);
            }
            return bos.toByteArray();
      }
      
      
	static public HashMap getRequestMap(HttpServletRequest request)  throws IOException {
  		HashMap parameterMap = new HashMap(); 
  		Enumeration enums = request.getParameterNames(); 
  		while(enums.hasMoreElements()){ 
  			String paramName = (String)enums.nextElement(); 
  			String[] parameters = request.getParameterValues(paramName); 
  			
  			// Parameter占쏙옙 占썼열占쏙옙 占쏙옙占?
  			if(parameters.length > 1){ 
  		    	parameterMap.put(paramName, parameters); 
  		    	// Parameter占쏙옙 占썼열占쏙옙 占싣댐옙 占쏙옙占?
  			}else{ 
  				parameterMap.put(paramName, parameters[0]); 
  			} 
  		}
  		
  		return parameterMap;
  	}
  	
//	static public JSONObject getRequestJSON(HttpServletRequest request)  throws IOException {
//  		HashMap parameterMap = new HashMap(); 
//  		Enumeration enums = request.getParameterNames(); 
//  		while(enums.hasMoreElements()){ 
//  			String paramName = (String)enums.nextElement(); 
//  			String[] parameters = request.getParameterValues(paramName); 
//  			
//  			// Parameter 배열인 경우
//  			if(parameters.length > 1){ 
//  		    	parameterMap.put(paramName, parameters); 
//  		    	// Parameter 배열이 아닌경우 
//  			}else{ 
//  				parameterMap.put(paramName, parameters[0]); 
//  			} 
//  		}
//  		
//  		return new JSONObject(parameterMap);
//  	}
  	
	static public JSONObject getRequestJSON(HttpServletRequest request) throws IOException {
	    HashMap parameterMap = new HashMap(); 
	    Enumeration enums = request.getParameterNames(); 
	    while(enums.hasMoreElements()){ 
	        String paramName = (String)enums.nextElement(); 
	        String[] parameters = request.getParameterValues(paramName); 
	        if(parameters.length > 1){ 
	            parameterMap.put(paramName, parameters); 
	        }else{ 
	            parameterMap.put(paramName, parameters[0]); 
	        } 
	    }
	    
	    if (parameterMap.isEmpty()) {
	        StringBuilder sb = new StringBuilder();
	        BufferedReader reader = request.getReader();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line);
	        }
	        String body = sb.toString();
	        if (body != null && !body.isEmpty()) {
	            return new JSONObject(body);
	        }
	    }

	    return new JSONObject(parameterMap);
	}
	
	public static String getClientIP(HttpServletRequest request) {
	    String ip = request.getHeader("X-Forwarded-For");
	    Log.info("X-FORWARDED-FOR : " + ip);

	    if (ip == null) {
	        ip = request.getHeader("Proxy-Client-IP");
	        Log.info("Proxy-Client-IP : " + ip);
	    }
	    if (ip == null) {
	        ip = request.getHeader("WL-Proxy-Client-IP");
	        Log.info("WL-Proxy-Client-IP : " + ip);
	    }
	    if (ip == null) {
	        ip = request.getHeader("HTTP_CLIENT_IP");
	        Log.info("HTTP_CLIENT_IP : " + ip);
	    }
	    if (ip == null) {
	        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
	        Log.info("HTTP_X_FORWARDED_FOR : " + ip);
	    }
	    if (ip == null) {
	        ip = request.getRemoteAddr();
	        Log.info("getRemoteAddr : "+ip);
	    }
	    Log.info("Result : IP Address : "+ip);

	    return ip;
	}
}
