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

import org.json.JSONObject;

import com.sso.portal.util.Log;
import com.sso.portal.util.PrecursorUtil;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	HttpSession session = request.getSession(false);
		response.setContentType("application/json;charset=utf-8");
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "UTF8"));
		JSONObject reqJSON = PrecursorUtil.getRequestJSON(request);
		JSONObject resultJSON = new JSONObject();
		
		if (session == null) Log.info("session null");
		Cookie cookie = new Cookie("SSOSESSION", "LOGGEDOFF");
		cookie.setDomain(".iamapp.com");
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);

		cookie = new Cookie("EZXSHASH", "LOGGEDOFF");
		cookie.setDomain(".iamapp.com");
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);

		resultJSON.put("success", true);
		resultJSON.put("url", "/logout.jsp");
				
		String sendURL = "<iframe src='https://portal.iamapp.com/pkmslogout' style='display:none;' onload=\"location.href='https://portal.iamapp.com'\"></iframe>";
		
		session.invalidate();
		resultJSON.put("success", true);
		resultJSON.put("url", sendURL);
		writer.write(resultJSON.toString());								
		writer.flush();			
    }
}
