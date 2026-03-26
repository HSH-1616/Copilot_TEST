package com.sso.portal.user;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 세션이 있으면 무효화
        HttpSession session = request.getSession(false);

        response.setContentType("application/json;charset=utf-8");

        // 응답에 사용할 writer
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"))) {
            JSONObject resultJSON = new JSONObject();

            if (session == null) {
                Log.info("session null");
            } else {
                session.invalidate();
            }

            // 로그아웃 쿠키 삭제 (만료 시간 0)
            String[] delCookies = { "SSOSESSION", "EZXSHASH" };
            for (String name : delCookies) {
                Cookie cookie = new Cookie(name, "LOGGEDOFF");
                cookie.setDomain(".iamapp.com");
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }

            // 싱글로그아웃용 iframe URL 응답 필드 생성
            String sendURL = "<iframe src='https://portal.iamapp.com/pkmslogout' style='display:none;' onload=\"location.href='https://portal.iamapp.com'\"></iframe>";

            resultJSON.put("success", true);
            resultJSON.put("url", sendURL);

            writer.write(resultJSON.toString());
            writer.flush();
        }
    }
}
