<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
    
    <script type="text/javascript" src="/js/login.js" charset="utf-8"></script>
</head>

<body>
    <div class="background">
        <div class="noise"></div>
        <div class="grid"></div>
        <div class="gradient-sphere sphere-1"></div>
        <div class="gradient-sphere sphere-2"></div>
    </div>

    <div class="sso-layout">
        <header class="sso-header fade-in fade-in-1">
            <div class="brand">
                <div class="logo">IAM</div>
                <div class="logo-text">Application System</div>
            </div>
            <div class="user-profile">
                <div class="avatar"><i class="fa-solid fa-user"></i></div>
                <div class="user-info">
                    <span class="welcome-text">환영합니다</span>
                    <span class="user-name"><%= session.getAttribute("USERID") %></span>
                </div>
                <div class="header-actions" style="display: flex; gap: 0.5rem;">
			        <button class="secondary-btn" onclick="openPasswordPopup()" style="padding: 0.6rem 0.8rem; font-size: 0.85rem;">
			            <i class="fa-solid fa-key"></i> 비밀번호 변경
			        </button>
			        <button class="logout-btn" onclick="logout()">
			            <i class="fa-solid fa-right-from-bracket"></i> 로그아웃
			        </button>
			    </div>
            </div>
        </header>

        <main class="sso-main fade-in fade-in-2">
    <div class="dashboard-header">
        <h1>My Applications</h1>
        <p>접속 가능한 SSO 시스템 목록입니다.</p>
    </div>

    <div class="app-grid fade-in fade-in-3">
        <div class="app-column">
            <div class="column-title">Role Management System</div>
            <a href="https://rms.webseal.iamapp.com" class="app-card" target="_blank" rel="noopener noreferrer">
                <div class="app-icon-wrapper orange"><i class="fa-solid fa-chart-pie"></i></div>
                <div class="app-details">
                    <h3>RMS-WEBSEAL</h3>
                    <p>RMS WebSEAL 연동</p>
                </div>
                <i class="fa-solid fa-arrow-right app-arrow"></i>
            </a>

            <a href="https://rms.oidc.iamapp.com:9446/prerms_oidc_login.jsp" class="app-card" target="_blank" rel="noopener noreferrer">
                <div class="app-icon-wrapper orange"><i class="fa-solid fa-chart-pie"></i></div>
                <div class="app-details">
                    <h3>RMS-OIDC</h3>
                    <p>RMS OIDC 연동</p>
                </div>
                <i class="fa-solid fa-arrow-right app-arrow"></i>
            </a>

            <a href="https://rms.saml.iamapp.com:9446/prerms_saml_login.jsp" class="app-card" target="_blank" rel="noopener noreferrer">
                <div class="app-icon-wrapper orange"><i class="fa-solid fa-chart-pie"></i></div>
                <div class="app-details">
                    <h3>RMS-SAML</h3>
                    <p>RMS SAML 연동</p>
                </div>
                <i class="fa-solid fa-arrow-right app-arrow"></i>
            </a>
        </div>

        <div class="app-column">
            <div class="column-title">IBM Verify</div>
            <a href="https://isvg.iamapp.com/itim/console" class="app-card" target="_blank" rel="noopener noreferrer">
                <div class="app-icon-wrapper purple"><i class="fa-solid fa-users"></i></div>
                <div class="app-details">
                    <h3>IBM Security Verify Governance</h3>
                    <p>ISVG WebSEAL 연동</p>
                </div>
                <i class="fa-solid fa-arrow-right app-arrow"></i>
            </a>
        </div>

        <div class="app-column">
            <div class="column-title">Other Services</div>         
            <a href="https://acl.iamapp.com/ACL" class="app-card" target="_blank" rel="noopener noreferrer">
                <div class="app-icon-wrapper green"><i class="fa-solid fa-file-signature"></i></div>
                <div class="app-details">
                    <h3>ACL</h3>
                    <p>ACL WebSEAL 연동</p>
                </div>
                <i class="fa-solid fa-arrow-right app-arrow"></i>
            </a>
            
            <a href="https://schm.iamapp.com/schmgr" class="app-card" target="_blank" rel="noopener noreferrer">
                <div class="app-icon-wrapper green"><i class="fa-solid fa-calendar-check"></i></div>
                <div class="app-details">
                    <h3>Schedule Manager</h3>
                    <p>Schedule Manager WebSEAL 연동</p>
                </div>
                <i class="fa-solid fa-arrow-right app-arrow"></i>
            </a>
            
        </div>
    </div>
</main>

        <footer class="footer fade-in fade-in-4"
            style="margin: auto 2rem 2rem 2rem; padding-top: 2rem; border-top: 1px solid rgba(255,255,255,0.05);">
            <span>© 2026 IAM Application System</span>
             <div id="clock">
                    12:55 PM
             </div>
        </footer>
    </div>
</body>

</html>