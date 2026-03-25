package com.sso.portal.util;

import java.sql.SQLException;

import org.apache.ibatis.session.SqlSession;

//import com.thortech.xl.util.logging.PropertyConfigurator;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * log4J를 구현한 클래스
 * <p>Title: </p>
 * <p>Description: </p>
 * Copyright: Copyright (c) 2006
 * Company: www.UbiwareLab.com
 * @author yunkidon@hotmail.com
 * @version 1.0
 * <pre>Usage
 *  ConfigFactory cf = ConfigFactory.getInstance();
    Config config = cf.getConfiguration("base1.properties");
    Log.debug("","",config.getString("ldap.server.primary.host")); -->base.log
    Log.debug("TRACE","",config.getString("ldap.server.primary.host"));-->trace.log
    Log.debug("ERROR","",config.getString("ldap.server.primary.host"));-->error.log
 * </pre>
 */



public class Log {
    private static Logger logger = null;

    static {
//        try { 
//        	PropertyConfigurator 
//            PropertyConfigurator.configureAndWatch("log4j.properties");
//            System.out.println("Log System configured!");
//        } catch (Exception ex) {
//            System.err.print("Log System not configured!");
//        }
    	//logger = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    	//logger.info(logger.ROOT_LOGGER_NAME);
    	
    	logger = LoggerFactory.getLogger(Log.class);
    	logger.info(logger.ROOT_LOGGER_NAME);
		/*
		 * System.setOut(new PrintStream(new OutputStream() { StringBuilder sb = new
		 * StringBuilder();
		 * 
		 * @Override public void write(int b) throws IOException { if (b == '\n') {
		 * //logger.info(new String(sb.toString().getBytes(Charset.),"UTF-8")); sb = new
		 * StringBuilder(); } else { sb.append((char) b); } }
		 * 
		 * 
		 * @Override public void write(byte[] b, int off, int len) throws IOException {
		 * if (b == null) { throw new NullPointerException(); } else if ((off < 0) ||
		 * (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) <
		 * 0)) { throw new IndexOutOfBoundsException(); } else if (len == 0) { return; }
		 * byte[] pb = new byte[len]; for (int i = 0 ; i < len ; i++) { pb[i] = (b[off +
		 * i]); } String str = new String(pb); if(str.indexOf("[DEBUG]") < 0)
		 * logger.info(str); }
		 * 
		 * }));
		 */
    	
    }

    public Log() {
    	
    }

    
    /**
     * 디버깅용 로그
     * @param Object 출력 스트링
     */
    public static void debug(Object pStr) {
        logger.debug(pStr.toString());
    }

    /**
     * 디버깅용 로그
     * @param String  로그파일명
     * @param Object 출력요청 클래스
     * @param Object 출력 스트링
     */
    public static void debug(String pCat, Class pCaller, Object pStr) {
//        logger = Logger.getLogger(pCat);
        logger.debug(pCaller.getName() + " - " + pStr);
    }

    /**
     * 경고성 로그
     * @param String  로그파일명
     * @param Object 출력요청 객체
     * @param Object 출력 스트링
     */
    public static void debug(String pCat, Object pCaller, Object pStr) {
//        logger = Logger.getLogger(pCat);
        logger.debug(pCaller.getClass().getName() + " - " + pStr);
    }

    /**
     * 에러 로그
     * @param Object 출력 스트링
     */
    public static void error(Object pStr) {
//        logger = Logger.getRootLogger();
        logger.error(pStr.toString());
    }

    /**
     * 에러 로그
     * @param String  로그파일명
     * @param Object 출력요청 클래스
     * @param Object 출력 스트링
     */
    public static void error(String pCat, Class pCaller, Object pStr) {
//        logger = Logger.getLogger(pCat);
        logger.error(pCaller.getName() + " - " + pStr);
    }

    /**
     * 에러 로그
     * @param String  로그파일명
     * @param Object 출력 스트링
     */
    public static void error(String pCat, Object pStr) {
//        logger = Logger.getLogger(pCat);
        logger.error(pStr.toString());
    }

    /**
     * 에러 로그
     * @param String  로그파일명
     * @param Object 출력요청 객체
     * @param Object 출력 스트링
     */
    public static void error(String pCat, Object pCaller, Object pStr) {
//        logger = Logger.getLogger(pCat);
        logger.error(pCaller.getClass().getName() + " - " + pStr);
    }

    /**
     * 정보성 로그
     * @param Object 출력 스트링
     */
    public static void info(Object pStr) {
//        logger = Logger.getRootLogger();
        logger.info(pStr.toString());
    }

    /**
     * 정보성 로그
     * @param String  로그파일명
     * @param Object 출력요청 클래스
     * @param Object 출력 스트링
     */
    public static void info(String pCat, Class pCaller, Object pStr) {
//        logger = Logger.getLogger(pCat);
        logger.info(pCaller.getName() + " - " + pStr);
    }

    /**
     * 정보성 로그
     * @param String  로그파일명
     * @param Object 출력요청 객체
     * @param Object 출력 스트링
     */
    public static void info(String pCat, Object pCaller, Object pStr) {
//        logger = Logger.getLogger(pCat);
        logger.info(pCaller.getClass().getName() + " - " + pStr);
    }

    /**
     * 경고성 로그
     * @param String  로그파일명
     * @param Object 출력요청 객체
     * @param Object 출력 스트링
     */
    public static void warn(Object pStr) {
//        logger = Logger.getRootLogger();
        logger.warn(pStr.toString());
    }

    /**
     * 경고성 로그
     * @param String  로그파일명
     * @param Object 출력요청 객체
     * @param Object 출력 스트링
     */
    public static void warn(String pCat, Object pStr) {
//        logger = Logger.getLogger(pCat);
        logger.warn(pStr.toString());
    }

    /**
     * 경고성 로그
     * @param String  로그파일명
     * @param Object 출력요청 객체
     * @param Object 출력 스트링
     */
    public static void warn(String pCat, Class pCaller, Object pStr) {
//        logger = Logger.getLogger(pCat);
        logger.warn(pCaller.getName() + " - " + pStr);
    }

    /**
     * 경고성 로그
     * @param String  로그파일명
     * @param Object 출력요청 객체
     * @param Object 출력 스트링
     */
    public static void warn(String pCat, Object pCaller, Object pStr) {
//        logger = Logger.getLogger(pCat);
        logger.warn(pCaller.getClass().getName() + " - " + pStr);
    }
    
    public static void errorDebug(String pCat,Exception e) {
    	SqlSession sqlSession = null;
		DBManager db = new DBManager();
    	
    	logger = (Logger)LoggerFactory.getLogger(pCat);
    	StackTraceElement[] st = e.getStackTrace();
    	logger.error(e.getMessage());
    	for(StackTraceElement msg : st){
    		logger.error(msg.toString());
    	}

    	try {
    		sqlSession = db.getSqlSession();
    		
    		JSONObject errObj = new JSONObject();
        	errObj.put("CLASS_NAME", pCat);
        	// 에러 메세지 3줄까지만 저장
        	String errString = e.getMessage() + "\n";
        	for(int i = 0; i < st.length; i++) {
        		errString += st[i] + "\n";
//        		if(i >= 10) {
//        			break;
//        		}
        	}
        	errObj.put("ERROR_MSG", errString);
        	
        	sqlSession.insert("com.precursor.sql.day_bacth.saveErrorCatch", errObj);
        	
        	sqlSession.commit();
    	} catch (SQLException e1) {
    		e1.printStackTrace();
    	} finally {
    		if(db != null) {
				db.freeConnection(sqlSession);
			}
			sqlSession.close();
		}
    }
    public static void infoDebug(String pCat, String text) {
		logger.info(pCat, "infoDebug insert start");
    	SqlSession sqlSession = null;
		DBManager db = new DBManager();
		
    	logger = (Logger)LoggerFactory.getLogger(pCat);
    	
    	try {
    		sqlSession = db.getSqlSession();
    		
    		JSONObject errObj = new JSONObject();
        	errObj.put("CLASS_NAME", pCat);
        	// 에러 메세지 3줄까지만 저장
        	String errString = text;
        	errObj.put("ERROR_MSG", errString);
        	
        	sqlSession.insert("com.precursor.sql.day_bacth.saveErrorCatch", errObj);
        	
        	sqlSession.commit();
    	} catch (SQLException e1) {
    		e1.printStackTrace();
    	} finally {
    		if(db != null) {
				db.freeConnection(sqlSession);
			}
			sqlSession.close();
		}
    	logger.info(pCat, "infoDebug insert end");
	}
}
