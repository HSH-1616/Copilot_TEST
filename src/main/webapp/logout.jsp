<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" session="true"%>
<%
response.setHeader("P3P","CP='CAO PSA CONi OTR OUR DEM ONL'");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires", 0);
response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0, proxy-revalidate, no-transform, pre-check=0, post-check=0, private");

String ssoSession = (String) session.getAttribute("SSOSESSION");
boolean isLogout = false;

if (ssoSession == null || ssoSession.isEmpty()) {
	response.sendRedirect("https://portal.iamapp.com");
}else{
	isLogout = true;
}
%>
<!DOCTYPE html>
<html lang="ko">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>IAMAPP Portal</title>
    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap" rel="stylesheet" />
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet" />
    <link rel="stylesheet" href="/css/styles.css" />
    <script>
			var context_root = '';
	</script>
    <!-- Jquery -->
	<script type="text/javascript" src="/common/jquery/1.12.4/jquery.min.js" charset="utf-8"></script>
		
	<!-- VueJS -->
	<script type="text/javascript" src="/common/vue/2.6.12/js/vue.min.js" charset="utf-8"></script>
	<script type="text/javascript" src="/common/vue/vue-context-menu/js/vue-context-menu.js" charset="utf-8"></script>
	<script type="text/javascript" src="/common/vue/2.11.7/js/popper.min.js" charset="utf-8"></script>
    
    <!-- Axios -->
	<script type="text/javascript" src="/common/vue/1.3.5/js/axios.min.js" charset="utf-8"></script>
    
    <!-- Sweetalert2 : alert -->
	<script src="/common/vue/11.7.3/js/sweetalert2@11.js"></script>
    
    <!-- CryptoJS -->
    <script type="text/javascript" src="/common/cryptoJS/rollups/aes.js" charset="utf-8"></script>
	<script type="text/javascript" src="/common/cryptoJS/rollups/sha256.js" charset="utf-8"></script>
    
    <script type="text/javascript" src="/js/login.js" charset="utf-8"></script>
</head>
<body>
<% if (isLogout) { %>		
	<script type="text/javascript">
        $(function() {            
            logout();
        });        
    </script>
<% } %>
</body>
</html>