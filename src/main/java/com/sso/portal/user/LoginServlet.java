package com.sso.portal.user;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.json.JSONObject;

import com.sso.portal.util.DBManager;
import com.sso.portal.util.JSCrypto;
import com.sso.portal.util.Log;
import com.sso.portal.util.PrecursorUtil;
import com.sso.portal.util.SHA1;
import com.sso.portal.util.SeedCrypto;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	HttpSession session = request.getSession(false);
		SqlSession sqlSession = null;		
		response.setContentType("application/json;charset=utf-8");
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "UTF8"));
		JSONObject reqJSON = PrecursorUtil.getRequestJSON(request);
		JSONObject resultJSON = new JSONObject();
		JSONObject userJSON = new JSONObject();
		String user_id = "";

		if (reqJSON.has("user_id")) {
			userJSON = new JSONObject(reqJSON.getString("user_id"));
			user_id = JSCrypto.decrypt(userJSON);
			
			if (user_id != null) {
				int index = user_id.indexOf("@");

				if (index > -1) {
					user_id = user_id.substring(0, index);
				}
			}
		}
		
		try {
			sqlSession = DBManager.getInstance().getSqlSession();
			HashMap<String, String> authMap = new HashMap<String, String>();
			Log.info("user_id:" + user_id);
			authMap.put("USER_ID", user_id);
			
			Map<String, String> authObj = new HashMap<>();
			authObj = sqlSession.selectOne("com.sso.portal.sql.user.getAuthUser", authMap);
			Log.info("authObj:" + authObj);
			
			if (!authObj.isEmpty()) {
				setSSOSession(request, response, session, reqJSON);
				session.setAttribute("USERID", user_id);
				
				Log.info(session.getId() + " : SSOSESSION Set Complete");
				
				resultJSON.put("success", true);
				resultJSON.put("url", "/main.jsp");
			}else {
				resultJSON.put("success", false);
			}
						
			writer.write(resultJSON.toString());
			writer.flush();;
			
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.error("", this, ex);
		} finally {
			setNoCache(request, response);
			if (sqlSession != null) {
				sqlSession.clearCache();
				sqlSession.close();
			}

			writer.flush();
			writer.close();
			Log.debug("", this, ".....end SSOLoginServlet.doService()\n");
		}				
    }
    
    private void setSSOSession(HttpServletRequest request, HttpServletResponse response, HttpSession session, JSONObject reqJSON) {
		try {
			/**
			 * SSO를 위함 EZXSSESSION을 COOKIE에 담는다. EZXSSESSION값은 아래와 같은 값을 담는다. 1. 시스템 아이디 2.
			 * 사용자 아이디 3. 타임스탬프 4. 랜덤번호 5. HASHING
			 */

			String context = "";
			String user_id = reqJSON.getString("user_id");
			// Log.debug("-------------'-1'----------------");
			String SYSTEM_CODE = "EZXS";
			Date date = new Date();
			long SESSION_TIME = date.getTime();
			Random random = new Random();
			int RANDOM_NUMBER = random.nextInt(1000);
			// Log.debug("-------------0----------------");
			String SSOSESSION = SYSTEM_CODE;
			// byte[] enc_id = new sun.misc.BASE64Decoder().decodeBuffer(user_id);
			Log.debug("Set_UserId : " + new String(user_id));
			String usr_id = new String(user_id);
			SSOSESSION += "@" + usr_id;

			SSOSESSION += "@" + SESSION_TIME;
			SSOSESSION += "@" + RANDOM_NUMBER;
			SSOSESSION = SeedCrypto.encrypt(SSOSESSION);
			// Log.debug("-------------1----------------");
			Cookie cookie = new Cookie("SSOSESSION", SSOSESSION);
			// Log.debug("-------------2----------------");
			cookie.setDomain(".iamapp.com");
			cookie.setMaxAge(-1);
			cookie.setPath(context + "/");
			// Log.debug("-------------3----------------");
			response.addCookie(cookie);
			// Log.debug("-------------4----------------");
			SHA1 sha = new SHA1();
			String EZXSHASH = sha.getEncrypt(usr_id);
			Log.debug("---------------------------> " + EZXSHASH);
			cookie = new Cookie("EZXSHASH", EZXSHASH);
			cookie.setDomain(".iamapp.com");			
			cookie.setMaxAge(-1);
			cookie.setPath(context + "/");
			// Log.debug("-------------5----------------");
			response.addCookie(cookie);
			// Log.debug("-------------6----------------");
			session.setAttribute("SSOSESSION", SSOSESSION);
			Log.debug("SSOSESSION : " + SSOSESSION);
			Log.debug("EZXSHASH : " + EZXSHASH);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
    
    private void setNoCache(HttpServletRequest request, HttpServletResponse response) {
		Log.info("Protocol: " + request.getProtocol());

		if (request.getProtocol().compareTo("HTTP/1.0") == 0) {
			response.setHeader("Pragma", "no-cache");
			// response.setHeader("Pragma", "public");
		} else if (request.getProtocol().compareTo("HTTP/1.1") == 0) {
			response.setHeader("Cache-Control", "no-cache");
			// response.setHeader("Cache-Control", "public");
		}

		response.setDateHeader("Expires", 0);
	}
}
