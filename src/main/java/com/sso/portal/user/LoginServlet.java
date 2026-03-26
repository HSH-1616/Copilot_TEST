package com.sso.portal.user;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SqlSession sqlSession = null;
        response.setContentType("application/json;charset=utf-8");
        setNoCache(request, response);

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "UTF8"));
        JSONObject reqJSON = PrecursorUtil.getRequestJSON(request);
        JSONObject resultJSON = new JSONObject();
        String user_id = null;

        if (reqJSON.has("user_id")) {
            JSONObject userJSON = new JSONObject(reqJSON.getString("user_id"));
            user_id = JSCrypto.decrypt(userJSON);

            if (user_id != null) {
                int index = user_id.indexOf("@");
                if (index > -1) {
                    user_id = user_id.substring(0, index);
                }
            }
        }

        try {
            if (user_id == null || user_id.isEmpty()) {
                Log.warn("Login attempt with empty or unresolvable user_id");
                resultJSON.put("success", false);
                writer.write(resultJSON.toString());
                writer.flush();
                return;
            }

            sqlSession = DBManager.getInstance().getSqlSession();
            HashMap<String, String> authMap = new HashMap<String, String>();
            Log.info("Login attempt for user_id: [PROTECTED]");
            authMap.put("USER_ID", user_id);

            Map<String, String> authObj = sqlSession.selectOne("com.sso.portal.sql.user.getAuthUser", authMap);
            Log.debug("authObj: " + (authObj != null ? "found" : "not found"));

            if (authObj != null && !authObj.isEmpty()) {
                HttpSession session = request.getSession(true);
                setSSOSession(request, response, session, reqJSON);
                session.setAttribute("USERID", user_id);

                Log.info(session.getId() + " : SSOSESSION Set Complete");

                resultJSON.put("success", true);
                resultJSON.put("url", "/main.jsp");
            } else {
                resultJSON.put("success", false);
            }

            writer.write(resultJSON.toString());
            writer.flush();

        } catch (Exception ex) {
            Log.error("Login processing error", this, ex);
            try {
                resultJSON.put("success", false);
                writer.write(resultJSON.toString());
                writer.flush();
            } catch (Exception ignored) {
                // best-effort error response
            }
        } finally {
            if (sqlSession != null) {
                sqlSession.clearCache();
                sqlSession.close();
            }
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
            String SYSTEM_CODE = "EZXS";
            Date date = new Date();
            long SESSION_TIME = date.getTime();
            SecureRandom secureRandom = new SecureRandom();
            int RANDOM_NUMBER = secureRandom.nextInt(1000);

            String SSOSESSION = SYSTEM_CODE;
            SSOSESSION += "@" + user_id;
            SSOSESSION += "@" + SESSION_TIME;
            SSOSESSION += "@" + RANDOM_NUMBER;
            SSOSESSION = SeedCrypto.encrypt(SSOSESSION);

            Cookie cookie = new Cookie("SSOSESSION", SSOSESSION);
            cookie.setDomain(".iamapp.com");
            cookie.setMaxAge(-1);
            cookie.setPath(context + "/");
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            response.addCookie(cookie);

            SHA1 sha = new SHA1();
            String EZXSHASH = sha.getEncrypt(user_id);
            cookie = new Cookie("EZXSHASH", EZXSHASH);
            cookie.setDomain(".iamapp.com");
            cookie.setMaxAge(-1);
            cookie.setPath(context + "/");
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            response.addCookie(cookie);

            session.setAttribute("SSOSESSION", SSOSESSION);
            Log.debug("SSOSESSION set successfully");
        } catch (Exception ex) {
            Log.error("setSSOSession error", this, ex);
        }
    }

    private void setNoCache(HttpServletRequest request, HttpServletResponse response) {
        Log.info("Protocol: " + request.getProtocol());

        if (request.getProtocol().compareTo("HTTP/1.0") == 0) {
            response.setHeader("Pragma", "no-cache");
        } else if (request.getProtocol().compareTo("HTTP/1.1") == 0) {
            response.setHeader("Cache-Control", "no-cache");
        }

        response.setDateHeader("Expires", 0);
    }
}
