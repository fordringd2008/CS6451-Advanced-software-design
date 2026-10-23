<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hotel Points Admin — Sign in</title>
    <style>
        body { font-family: Georgia, serif; background: #FAF5EE; color: #5A4632;
            display: flex; justify-content: center; align-items: center; min-height: 100vh; margin: 0; }
        .login-card { background: #FFFBF5; border: 1px solid #EAD9C2; border-radius: 12px;
            padding: 32px 36px; width: 320px; }
        h2 { font-size: 18px; margin: 0 0 20px; color: #C08B5C; }
        label { display: block; color: #96785A; font-size: 12px; margin-bottom: 4px; }
        input { width: 100%; padding: 9px 10px; border: 1px solid #EAD9C2; border-radius: 6px;
            box-sizing: border-box; margin-bottom: 14px; font-size: 14px; }
        button { width: 100%; padding: 10px; background: #C08B5C; color: #fff; border: none;
            border-radius: 6px; font-size: 14px; cursor: pointer; }
        button:hover { background: #A9764B; }
        .error { background: #F5E0D8; color: #A05B3C; border-radius: 8px; padding: 10px;
            margin-bottom: 14px; font-size: 13px; }
        .hint { text-align: center; color: #96785A; font-size: 11px; margin-top: 16px; }
        .guest-link { color: #C08B5C; }
    </style>
</head>
<body>
    <div class="login-card">
        <h2>🛠️ Hotel Points — Admin Sign in</h2>
        <c:if test="${not empty param.error}">
            <div class="error"><c:out value="${param.error}"/></div>
        </c:if>
        <form action="${pageContext.request.contextPath}/auth/login" method="post">
            <input type="hidden" name="portal" value="admin"/>
            <label>Account</label>
            <input name="username" required autofocus/>
            <label>Password</label>
            <input type="password" name="password" required/>
            <button type="submit">Sign in</button>
        </form>
        <p class="hint">Guest? <a class="guest-link" href="${pageContext.request.contextPath}/login">Guest login</a></p>
    </div>
</body>
</html>