
package com.sso.portal.util;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;



/**
 * Servlet Filter implementation class AuthFilter
 */
public class AuthFilter implements Filter {

    /**
     * Default constructor. 
     */
    public AuthFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        String nextPage = "https://portal.iamapp.com";
        String context_root = "";
        //모니터링 인스턴스 생성
        
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;
        
        //세션을 가져오기만 해야함 세션이 없으면 null
        HttpSession session = httpRequest.getSession(false);

		/**
         * SSOSSESSION 체크
         */
        boolean check_session = false;
        
        try {
        	if(session != null){
            	//세션 키가 있는지 확인
            	if(session.getAttribute("SSOSESSION") != null) check_session = true;
            	else check_session = false;
            	
            	//쿠키 세션이 살아있는지 확인
    	        if(checkSSOSESSION(httpRequest,httpResponse,session)) check_session = true;
    	        else check_session = false;
            	            	
            	/*
            	 * 세션이 있지만 정합성에서 틀어진 경우 로그남김
            	 * 1. 세션은 있지만 세션 키가 없는경우
            	 * 2. 세션 쿠키가 있지만 접속시간, 도메인, 암호화한 값이 틀린경우
            	 * 
            	 * 세션 쿠키가 살아있으면 세션 키가 없어도 로그인 가능
            	 */    	        
            	if(!check_session){
    	        	Log.info("AuthFilter : Session Validation Failed \n reqeust_url : "+httpRequest.getRequestURL()+" ip:"+request.getRemoteAddr());
            	}
            } else {
            	//세션이 없는 경우
            	Log.info("AuthFilter : Session is null \n reqeust_url : "+httpRequest.getRequestURL()+" ip:"+request.getRemoteAddr());
            }
        	
        	if((context_root+"/").equals(httpRequest.getRequestURI()) || 
        			(context_root+"/login.jsp").equals(httpRequest.getRequestURI()) ||
        			(context_root+"/error.jsp").equals(httpRequest.getRequestURI()) ||
        			(context_root+"/main.jsp").equals(httpRequest.getRequestURI()) ||
        			(context_root+"/pwdChange.jsp").equals(httpRequest.getRequestURI()) ||
        			(context_root+"/pwdReset.jsp").equals(httpRequest.getRequestURI())
        		)    	
        	{
        		
        		check_session= true;
        	}
        	
        	System.out.println("URI : "+httpRequest.getRequestURI());
        	System.out.println("check_session : "+((check_session)?"true":"false"));
        	
        	
            if(!check_session){
            	System.out.println("check_session : BAD!!");
                System.out.println("Redirecting to LoginType Page: " + nextPage);
                
                httpResponse.sendRedirect(nextPage);
            }else{
            	System.out.println("check_session : GOOD!!");
    			chain.doFilter(request, response);
            }
        }catch (Exception e) {
            // 내부 로직에서 발생하는 모든 Exception은 여기서 잡아서 로그를 남겨야 함
            Log.error("AuthFilter 도중 예상치 못한 에러 발생", e);
        }
        
	}
	
    /**
     * 다음 페이지로 포워딩
     * @param request - HttpServletRequest
     * @param response - HttpServletResponse
     * @param nextPage - 포워딩될 다음 페이지
     * @throws AppException - 포워딩 실패 시 예외 발생
     */
    private void foward(HttpServletRequest request, HttpServletResponse response, String nextPage) throws Exception {
        try {        	
            RequestDispatcher dispatcher = request.getRequestDispatcher(nextPage);
            dispatcher.forward(request, response);
        } catch (IOException ex) {
            throw new Exception();
        } catch (ServletException ex) {
        	throw new Exception();
        }
    }
    
    private boolean checkSSOSESSION(HttpServletRequest request, HttpServletResponse response, HttpSession session)throws Exception{
    	boolean login_ok = false;
    	try{
    		String SSOSESSION = getSession(request, "SSOSESSION");
    		SSOSESSION.replace(' ', '+');
    		if(!"".equals(SSOSESSION)){
    			String[] values = SeedCrypto.decrypt(SSOSESSION).split("@");
    			if(values.length == 4){
    				/**
    				 * hashing 체크
    				 */
    				SHA1 sha = new SHA1();
    	            String enc_user_id = sha.getEncrypt(values[1]);
    				String EZXSHASH = getSession(request, "EZXSHASH");
    				
    				Log.debug("", this, "enc_user_id : " + enc_user_id);
    				Log.debug("", this, "EZXSHASH : " + EZXSHASH);
    				
    				if(enc_user_id.equals(EZXSHASH)){	
    					/**
    					 * 시간체크
    					 */
    					Date date = new Date();
    					long current_time = (date.getTime() - (540 * 60 * 1000));
    					Log.debug("", this, "current_time : " + current_time);
    					Log.debug("", this, "values[2] : " + values[2]);
    					    					
    					if(values[2].compareTo(current_time+"") >= 0){
    						/**
    						 * 세션이 있는지 확인한다.
    						 */    					
    						String USERID = values[1];                            
                            if (USERID != null && USERID.startsWith("{")) {
                                try {
                                    JSONObject userJSON = new JSONObject(USERID);
                                    USERID = JSCrypto.decrypt(userJSON);
                                    
                                    if (USERID != null) {
                                        int index = USERID.indexOf("@");
                                        if (index > -1) {
                                            USERID = USERID.substring(0, index);
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.debug("복호화 중 오류 발생: " + e.getMessage());
                                }
                            }
    						
    						session.setAttribute("SSOSESSION", SSOSESSION);
    						session.setAttribute("USERID", USERID);
    						Log.debug("", this, "SSOSESSION : " + SSOSESSION);
    						login_ok = true;
    					}
    					
    					
    				}
    			}	
    		}    		
    	}catch(Exception ex){
    		ex.printStackTrace();
    		throw new Exception();
    	}
    	
    	return login_ok;
    }
    
    private String getSession(HttpServletRequest request, String fnCookieName){
		String session = ""; 
		
		Cookie[] cookies = request.getCookies();
		for(int i=0; i<cookies.length; i++){
			if(fnCookieName.equals(cookies[i].getName())){
				session = cookies[i].getValue();
			}
		}

		return session;
	}
}
