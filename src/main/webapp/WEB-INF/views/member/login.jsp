<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>로그인 | EduMate</title>
    <!-- CSS -->
    <link rel="stylesheet" href="/resources/css/member/login.css">
    <link rel="stylesheet" href="/resources/css/common/header.css" />
    <link rel="stylesheet" href="/resources/css/common/footer.css" />
</head>
<!-- JavaScript -->
<script src="/resources/js/member/login.js"></script>
<body>
<!-- Header -->
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<!-- Login -->
<section class="login-container">
    <div class="login-box">
        <div class="login-left">
            <div class="login-icon">👤</div>
            <h2>로그인</h2>
        </div>

        <div class="login-right">
            <h1 class="login-logo">LOGO</h1>

            <div class="error-message" id="error-message"></div>

            <form id="login-form" action="login.do" method="post">
                <input type="text" name="userId" placeholder="아이디">
                <input type="password" name="userPwd" placeholder="비밀번호">

                <button type="submit" class="btn-login">로그인</button>
            </form>

            <button type="button" class="btn-join" onclick="location.href='signup/terms'">회원가입</button>
            <p class="find-info">
                <a href="findInfo.jsp">아이디 / 비밀번호를 잊어버렸습니다.</a>
            </p>
        </div>
    </div>
</section>
<!-- Footer -->
<jsp:include page="/WEB-INF/views/common/footer.jsp" />
</body>
</html>
