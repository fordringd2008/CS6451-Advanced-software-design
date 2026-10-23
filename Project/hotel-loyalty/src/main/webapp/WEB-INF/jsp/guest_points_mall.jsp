<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hotel Points Guest — Points Mall</title>
    <style>
        body { font-family: Georgia, serif; background: #FAF5EE; color: #5A4632; margin: 0; }
        .topbar { display: flex; justify-content: space-between; background: #FFFBF5;
            border-bottom: 1px solid #EAD9C2; padding: 12px 20px; }
        .layout { display: flex; min-height: calc(100vh - 52px); }
        .sidebar { width: 170px; background: #FFFBF5; border-right: 1px solid #EAD9C2; padding: 14px 0; }
        .nav-item { display: block; padding: 13px 18px; color: #96785A; text-decoration: none; }
        .nav-item.active { color: #C08B5C; font-weight: bold; background: #FAF5EE;
            border-left: 3px solid #C08B5C; }
        .content { flex: 1; padding: 20px 24px; }
        .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
        h2 { font-size: 17px; margin: 0; }
        .balance { color: #96785A; } .balance strong { color: #C08B5C; font-size: 20px; }
        table { width: 100%; border-collapse: collapse; background: #FFFBF5; border: 1px solid #EAD9C2; }
        th { background: #F5EBDD; color: #96785A; text-align: left; padding: 10px; font-size: 12px; }
        td { padding: 10px; border-top: 1px solid #F5EBDD; font-size: 13px; }
        .btn { display: inline-block; padding: 6px 14px; border-radius: 8px; border: 1px solid #C08B5C;
            background: #C08B5C; color: #FFFBF5; text-decoration: none; font-size: 12px; }
        .empty { text-align: center; padding: 30px; color: #96785A; }
        @media (max-width: 700px) { .layout { display: block; } .sidebar { width: auto; display: flex; }
            .nav-item { padding: 12px; } .content { padding: 16px; } table { overflow-x: auto; display: block; } }
    </style>
</head>
<body>
<header class="topbar"><strong>🏷️ Hotel Points Guest</strong><span>👤 Guest: ${uid}</span></header>
<div class="layout">
    <nav class="sidebar">
        <a class="nav-item active" href="${pageContext.request.contextPath}/guest/${uid}/points-mall">Points Mall</a>
        <a class="nav-item" href="${pageContext.request.contextPath}/guest/${uid}/my-points">My Points</a>
        <form action="${pageContext.request.contextPath}/logout" method="post" style="margin:0;"><button type="submit" class="nav-item" style="color:#A05B3C;background:none;border:none;font:inherit;width:100%;text-align:left;cursor:pointer;">Sign out</button></form>
    </nav>
    <main class="content">
        <c:if test="${not empty errorMessage}"><div style="background:#fdecea;color:#b71c1c;border:1px solid #f5c6cb;padding:10px 14px;border-radius:6px;margin-bottom:14px;"><c:out value="${errorMessage}"/></div></c:if>
        <div class="header"><h2>Points Mall</h2>
            <span class="balance">Available Points:
                <strong>${mall.currentPoint}</strong></span></div>
        <c:choose>
            <c:when test="${empty mall.products}">
                <div class="empty">No rewards are available.</div>
            </c:when>
            <c:otherwise>
                <table>
                    <thead><tr><th>PID</th><th>Product</th><th>Points Required</th><th>Action</th></tr></thead>
                    <tbody>
                    <c:forEach items="${mall.products}" var="product">
                        <tr>
                            <td><c:out value="${product.pid}"/></td>
                            <td><c:out value="${product.name}"/></td>
                            <td>${product.totalPointsRequired} pts</td>
                            <td><a class="btn" href="${pageContext.request.contextPath}/guest/${uid}/confirm-payment/${product.pid}">
                                Redeem</a></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </main>
</div>
</body>
</html>
