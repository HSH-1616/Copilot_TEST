<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="org.json.*, java.util.*" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>비밀번호 초기화 - IAM Portal</title>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;600;700&display=swap" rel="stylesheet" />
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet" />
    <link rel="stylesheet" href="/css/styles.css" />
    
    <script>
    	var context_root = '/';
	</script>	
	
    <script type="text/javascript" src="/common/jquery/1.12.4/jquery.min.js" charset="utf-8"></script>		
    <script type="text/javascript" src="/common/vue/2.6.12/js/vue.min.js" charset="utf-8"></script>
    <link rel="stylesheet" href="/common/vue/4.6.1/css/bootstrap.min.css" type="text/css"/>
    <script type="text/javascript" src="/common/vue/2.23.0/js/bootstrap-vue.min.js" charset="utf-8"></script>
    <script type="text/javascript" src="/common/vue/1.3.5/js/axios.min.js" charset="utf-8"></script>
    <script src="/common/vue/11.7.3/js/sweetalert2@11.js"></script>	
    <script type="text/javascript" src="/common/cryptoJS/rollups/aes.js" charset="utf-8"></script>
	<script src="/js/pwd.js"></script>
	
    <style>
        body {
            margin: 0; padding: 0;
            background-color: #020617;
            color: #94a3b8;
            font-family: 'Plus Jakarta Sans', sans-serif;
            display: flex; align-items: center; justify-content: center;
            min-height: 100vh; overflow: hidden;
        }

        .background { position: fixed; inset: 0; z-index: -1; background: radial-gradient(circle at top right, #020617 0%, #0f172a 100%); }
        .grid { position: absolute; inset: 0; background-size: 40px 40px; background-image: linear-gradient(to right, rgba(255,255,255,0.02) 1px, transparent 1px), linear-gradient(to bottom, rgba(255,255,255,0.02) 1px, transparent 1px); }
        .sphere { position: absolute; border-radius: 50%; filter: blur(60px); opacity: 0.6; }
        .sphere-1 { width: 300px; height: 300px; top: -100px; right: -50px; background: rgba(99, 102, 241, 0.15); }

        .login-container {
            width: 100%; max-width: 440px;
            background: rgba(15, 23, 42, 0.6);
            backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 24px; padding: 2rem;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
        }

        .login-header { text-align: center; margin-bottom: 1.5rem; }
        .logo-box { display: flex; align-items: center; justify-content: center; gap: 10px; margin-bottom: 1rem; }
        .logo-square { width: 32px; height: 32px; background: #fff; color: #0f172a; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-weight: 800; }
        .login-header h2 { color: #fff; font-size: 1.5rem; font-weight: 700; margin: 0; }

        .form-group { margin-bottom: 1.2rem; }
        .form-group label { display: block; color: #cbd5e1; font-size: 0.85rem; font-weight: 600; margin-bottom: 0.5rem; }
        .input-wrapper { position: relative; display: flex; gap: 8px; }
        .input-wrapper i { position: absolute; left: 14px; top: 50%; transform: translateY(-50%); color: #64748b; font-size: 0.9rem; z-index: 2; }
        .input-field {
            width: 100%; padding: 0.75rem 1rem 0.75rem 2.5rem;
            background: rgba(255, 255, 255, 0.03); border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 12px; color: #fff; font-size: 0.9rem; outline: none; transition: all 0.2s;
        }
        .input-field:focus { border-color: #6366f1; background: rgba(255, 255, 255, 0.06); }
        .input-field:disabled { background: rgba(0, 0, 0, 0.3); color: #475569; border-color: rgba(255,255,255,0.05); }

        .side-btn { 
            white-space: nowrap; padding: 0 1rem; border-radius: 10px; 
            font-size: 0.8rem; font-weight: 600; border: none; cursor: pointer; transition: all 0.2s; 
        }
        .btn-send { background: rgba(99, 102, 241, 0.1); color: #818cf8; border: 1px solid rgba(99, 102, 241, 0.2); }
        .btn-verify { background: rgba(16, 185, 129, 0.1); color: #34d399; border: 1px solid rgba(16, 185, 129, 0.2); }
        .side-btn:disabled { opacity: 0.4; cursor: not-allowed; }

        .policy-list { list-style: none; padding: 0.8rem; background: rgba(0, 0, 0, 0.2); border-radius: 10px; margin-top: 8px; }
        .policy-list li { font-size: 0.75rem; margin-bottom: 4px; display: flex; align-items: center; gap: 6px; }
        .text-valid { color: #10b981 !important; }
        .text-invalid { color: #ef4444 !important; }

        .btn-primary {
            width: 100%; padding: 0.85rem; border: none; border-radius: 12px;
            background: linear-gradient(to right, #6366f1, #4f46e5);
            color: #fff; font-weight: 600; cursor: pointer; transition: all 0.2s;
        }
        .btn-primary:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 10px 20px rgba(99, 102, 241, 0.3); }
        .btn-primary:disabled { opacity: 0.5; cursor: not-allowed; }
    </style>
</head>

<body>
    <div class="background">
        <div class="grid"></div>
        <div class="sphere sphere-1"></div>
    </div>

    <div id="pwFormSection" class="login-container">
        <div class="login-header">
            <div class="logo-box">
                <div class="logo-square">IAM</div>
                <div style="color:#fff; font-weight:700; font-size:1.2rem;">Application</div>
            </div>
            <h2>비밀번호 초기화</h2>
        </div>

        <div class="form-group">
            <label>사용자 사번</label>
            <div class="input-wrapper">
                <i class="fa-solid fa-user"></i>
                <input v-model="empNo" type="text" class="input-field" @keyup="inputEmpNoCheck" placeholder="사번 입력">
            </div>
        </div>

        <div class="form-group">
            <label>휴대폰 번호</label>
            <div class="input-wrapper">
                <i class="fa-solid fa-mobile-screen"></i>
                <input type="text" class="input-field" placeholder="'-' 없이 입력" disabled>
                <button class="side-btn btn-send" disabled>발송</button>
            </div>
        </div>

        <div class="form-group">
            <label>인증번호</label>
            <div class="input-wrapper">
                <i class="fa-solid fa-shield-halved"></i>
                <input type="text" class="input-field" placeholder="인증번호 6자리" disabled>
                <button class="side-btn btn-verify" disabled>확인</button>
            </div>
        </div>

        <div class="form-group">
            <label>새 비밀번호</label>
            <div class="input-wrapper">
                <i class="fa-solid fa-key"></i>
                <input v-model="newPw" type="password" class="input-field" @keyup="inputNewPwdCheck" placeholder="8~16자 조합">
            </div>
            <ul class="policy-list" v-if="newPw.length > 0">
                <li :class="pwPolicy.length && pwPolicy.combination ? 'text-valid' : 'text-invalid'">
                    <i :class="pwPolicy.length && pwPolicy.combination ? 'fa-solid fa-circle-check' : 'fa-solid fa-circle-xmark'"></i>
                    8~16자 영문/숫자/특수문자 조합
                </li>
                <li :class="pwPolicy.noRepeat && pwPolicy.noSequence ? 'text-valid' : 'text-invalid'">
                    <i :class="pwPolicy.noRepeat && pwPolicy.noSequence ? 'fa-solid fa-circle-check' : 'fa-solid fa-circle-xmark'"></i>
                    연속/반복 문자 사용 금지
                </li>
                <li :class="pwPolicy.notId ? 'text-valid' : 'text-invalid'">
                    <i :class="pwPolicy.notId ? 'fa-solid fa-circle-check' : 'fa-solid fa-circle-xmark'"></i>
                    사번 포함 금지
                </li>
            </ul>
        </div>

        <div class="form-group">
            <label>비밀번호 확인</label>
            <div class="input-wrapper">
                <i class="fa-solid fa-shield-check"></i>
                <input v-model="confirmPw" type="password" class="input-field" @keyup="inputConfirmPwdCheck" placeholder="새 비밀번호 다시 입력">
            </div>
        </div>

        <button type="button" class="btn-primary" :disabled="pwdChangeBtnDisabled || isLoading" @click="pwReset">
            <span v-if="isLoading" class="spinner-border spinner-border-sm mr-2"></span>
            {{ isLoading ? '처리 중...' : '비밀번호 변경하기' }}
        </button>
    </div>
    
</body>
</html>