package com.sso.portal.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;


/** 
 * <pre>
 * Title: EasyFlow Database Connection Manager
 * Descriptio:n EasyFlow
 * Copyright: Copyright (c) 2009-2012 Precursor Inc. All Rights Reserved.
 * Company: www.precursor.co.kr</p>
 * </pre>
 * @author jyna@precursor.co.kr
 * @version 1.0
 * @version 1.1, 수정자 (진선호) codejin@precursor.co.kr mybatis버전으로 변형함
 */
public class DBManager {

    /**
     * Properties 파일 읽기
     */
    private static Properties pro_conf = new Properties();

    static {
    	String fileName = "db.properties";
    	
        try {
        	InputStream is = Resources.getResourceAsStream(fileName);
            pro_conf.load(is);
            pro_conf.setProperty("ezflow.pass", SeedCrypto.decrypt(pro_conf.getProperty("ezflow.pass")));

        } catch (Exception e) {
            System.err.println("find db.properties. but other problems are here.");
            System.err.println(e.getMessage());
        }
    }

    /**
     * use DBManager.getInstance()
     */
    private static DBManager instance = null;

    /**
     * use getInstance();
     */
    
    private String resource = "/Configuration.xml";
    private Reader reader;
    private SqlSessionFactory sqlMapper = null;
    
    public DBManager() {
    	
    	int hashCode = 0;
		try {
			InputStream is = this.getClass().getResourceAsStream(resource);
			reader = new InputStreamReader(is);
			sqlMapper = new SqlSessionFactoryBuilder().build(reader, pro_conf);
		} catch (PersistenceException e) {
			System.out.println("e.printStackTrace():" + e.getMessage());
			System.out.println("e.printStackTrace():" + e.getLocalizedMessage());
			hashCode = e.hashCode();
			System.out.println("e.printStackTrace():" + e.hashCode());
			e.printStackTrace();
		}
		if (hashCode != 0) {
			System.out.println("DB 접속 불가");
		}
    	
//    	try {
//			reader = Resources.getResourceAsReader(resource);
//			sqlMapper = new SqlSessionFactoryBuilder().build(reader,pro_conf);
//			//SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(reader);
//
//    	} catch (Exception e) {
//			System.out.println(e.getMessage());
//			e.printStackTrace();
//		}
    }


    /**
     * Double Check Lock처리
     * -- instance가 중복 생성되지 않도록 처리
     * @return PoolManager
     */
    public static DBManager getInstance() {
        if (instance == null) {
            synchronized (DBManager.class) {
                if (instance == null) {
                    instance = new DBManager();
                }
            }
        }
        return instance;
    }
    
    /*
     * 여러개의 jdbc를 만들기 위해서 셋팅되는 xml을 생성해야 하며 원하는 xml 파일명을 connName값을
     * 이용하여 호출
     */
    /*
    public SqlSession getSqlSession(String connName) throws SQLException {
        //String path = conf.getPath();
    	PropertiesConfiguration conf = null;
    	
    	String path = "/";
        Log.info("path:"+path);
        Log.info("connName:"+connName);
        System.out.println(connName);
        if(path.lastIndexOf("\\")> 0){
        	path = path.substring(0,path.lastIndexOf("\\"))+"\\";
        }else{
        	path = path.substring(0,path.lastIndexOf("/"))+"/";
        }
        
        Log.info("path:"+path);
        String resource = connName+".xml";
        Reader reader;
        SqlSession sqlSession = null;

        FileInputStream fis =null;
    	String fileName = path+connName+".properties";
    	Properties conn_conf = new Properties();
    	try{
    		conf = ConfigFactory.getInstance().getConfiguration(fileName);
    		
    		fis = new FileInputStream(conf.getPath()); 
    		conn_conf.load(fis);
    		Log.info("conn_conf.load(fis);");
    		conn_conf.setProperty("ezflow.pass", SeedCrypto.decrypt(conn_conf.getProperty("ezflow.pass")));
            
            reader = Resources.getResourceAsReader(resource);
            
            Log.info("Resources.getResourceAsReader(resource)");
            SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(reader, conn_conf);
            Log.info(conn_conf);
            Log.info("SqlSessionFactoryBuilder().build(reader,conn_conf)");
            sqlSession = sqlMapper.openSession();
            Log.info("onpenSession");
        } catch (Exception e) {
        	e.printStackTrace();
            System.err.println("find db.properties. but other problems are here.");
        }
		return sqlSession;
    }
	*/
    public SqlSession getSqlSession(String connName) {
    	
        Log.info("connName: " + connName);
        //String resource = connName+".xml";
        String resource = "Configuration2.xml";
        Reader reader;
        SqlSession sqlSession = null;
    	
    	Properties conn_conf = new Properties();
    	
    	try{
    		InputStream is = Resources.getResourceAsStream(connName+".properties");
    		conn_conf.load(is);
    		Log.info("conn_conf.load(is);");
    		conn_conf.setProperty("ezflow.pass", SeedCrypto.decrypt(conn_conf.getProperty("ezflow.pass")));
            
            reader = Resources.getResourceAsReader(resource);
            Log.info("Resources.getResourceAsReader(resource)");
            
            SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(reader, conn_conf);
            Log.info(conn_conf);
            Log.info("SqlSessionFactoryBuilder().build(reader,conn_conf)");
            sqlSession = sqlMapper.openSession();
            Log.info("openSession");
        } catch (Exception e) {
        	e.printStackTrace();
            System.err.println("find db.properties. but other problems are here.");
        }
		return sqlSession;
    }
    
    public SqlSession getSqlSession() throws SQLException {
        return sqlMapper.openSession();
    }

    public void freeConnection(SqlSession session) {
        try {
            if (session != null) {
            	session.close(); 
            }
        } catch (Exception ex) {}
    }
    public static void main(String[] args) throws IOException {
    	
    	System.out.println(SeedCrypto.encrypt("precurs0r!"));
        
	}
}
