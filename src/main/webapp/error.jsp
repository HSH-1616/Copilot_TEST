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
</head>

<body>
    <div class="background">
        <div class="noise"></div>
        <div class="grid"></div>
        <div class="gradient-sphere sphere-1" style="background: radial-gradient(circle at center, rgba(239, 68, 68, 0.1), rgba(239, 68, 68, 0) 70%);"></div>
        <div class="gradient-sphere sphere-2"></div>
    </div>

    <div class="error-layout">
        <div class="error-card fade-in">
            <div class="error-icon-wrapper">
                <i class="fa-solid fa-circle-exclamation"></i>
            </div>
            <h1>문제가 발생했습니다</h1>
            <p>
                요청하신 작업을 처리하는 중에 오류가 발생했습니다.<br>
                세션이 만료되었거나 권한이 없을 수 있습니다.
            </p>
             <a href="javascript:history.back();" class="secondary-btn">이전 페이지로 돌아가기</a>
        </div>

        <footer class="footer fade-in-4" style="margin-top: 3rem; width: auto; border: none;">
            <span>© 2026 IBM Verify Identity Access</span>
        </footer>
    </div>
</body>

</html>