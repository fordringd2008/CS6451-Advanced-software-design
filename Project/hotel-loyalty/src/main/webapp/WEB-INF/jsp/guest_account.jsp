<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hotel Points Guest — Account</title>
    <style>
        body { font-family: Georgia, serif; background: #FAF5EE; color: #5A4632; margin: 0; }
        .topbar { display: flex; justify-content: space-between; background: #FFFBF5;
            border-bottom: 1px solid #EAD9C2; padding: 12px 20px; }
        .layout { display: flex; min-height: calc(100vh - 52px); }
        .sidebar { width: 170px; background: #FFFBF5; border-right: 1px solid #EAD9C2; padding: 14px 0; }
        .nav-item { display: block; padding: 13px 18px; color: #96785A; text-decoration: none; }
        .nav-item.active { color: #C08B5C; font-weight: bold; background: #FAF5EE;
            border-left: 3px solid #C08B5C; }
        .content { flex: 1; padding: 20px 24px; } h2 { font-size: 17px; margin: 0 0 16px; }
        .profile-card { background: #FFFBF5; border: 1px solid #EAD9C2; border-radius: 10px;
            padding: 16px; max-width: 520px; }
        .profile-card h3 { font-size: 13px; color: #C08B5C; margin: 0 0 12px; }
        .profile-row { display: flex; justify-content: space-between; font-size: 13px;
            padding: 8px 0; border-bottom: 1px solid #F5EBDD; }
        .profile-row:last-child { border-bottom: 0; }
        .profile-row span:first-child { color: #96785A; }
        @media (max-width: 700px) { .layout { display: block; } .sidebar { width: auto; display: flex; }
            .nav-item { padding: 12px; } .content { padding: 16px; } }
    </style>
</head>
<body>
<header class="topbar"><strong>🏷️ Hotel Points Guest</strong><span>👤 <c:out value="${account.name}"/> [<c:out value="${account.roleLabel}"/>]</span></header>
<div class="layout">
    <nav class="sidebar">
        <a class="nav-item" href="${pageContext.request.contextPath}/guest/${uid}/points-mall">Points Mall</a>
        <a class="nav-item" href="${pageContext.request.contextPath}/guest/${uid}/my-points">My Points</a>
        <a class="nav-item active" href="${pageContext.request.contextPath}/guest/${uid}/account">Account</a>
        <form action="${pageContext.request.contextPath}/logout" method="post" style="margin:0;" onsubmit="return confirm('Are you sure you want to sign out?');"><button type="submit" class="nav-item" style="color:#A05B3C;background:none;border:none;font:inherit;width:100%;text-align:left;cursor:pointer;">Sign out</button></form>
    </nav>
    <main class="content">
        <h2>Account</h2>
        <div class="profile-card">
            <h3>Account Details</h3>
            <div class="profile-row"><span>User ID</span><strong><c:out value="${account.uid}"/></strong></div>
            <div class="profile-row"><span>Name</span><strong><c:out value="${account.name}"/></strong></div>
            <div class="profile-row"><span>Role</span><strong><c:out value="${account.roleLabel}"/></strong></div>
            <div class="profile-row"><span>Status</span><strong><c:out value="${account.statusLabel}"/></strong></div>
            <div class="profile-row"><span>Member Level</span><strong><c:out value="${account.levelLabel}"/></strong></div>
            <div class="profile-row"><span>Current Points</span><strong>${account.currentPoint} pts</strong></div>
            <div class="profile-row"><span>Registered</span><strong><c:out value="${account.registerTimeDisplay}"/></strong></div>
        </div>
    </main>
</div>
</body>
</html>