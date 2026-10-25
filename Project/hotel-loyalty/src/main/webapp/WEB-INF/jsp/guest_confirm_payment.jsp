<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hotel Points Guest — Confirm Payment</title>
    <style>
        body { font-family: Georgia, serif; background: #FAF5EE; color: #5A4632; margin: 0; }
        .topbar { display: flex; justify-content: space-between; background: #FFFBF5;
            border-bottom: 1px solid #EAD9C2; padding: 12px 20px; }
        .layout { display: flex; min-height: calc(100vh - 52px); }
        .sidebar { width: 170px; background: #FFFBF5; border-right: 1px solid #EAD9C2; padding: 14px 0; }
        .nav-item { display: block; padding: 13px 18px; color: #96785A; text-decoration: none; }
        .content { flex: 1; padding: 20px 24px; max-width: 700px; }
        h2 { font-size: 17px; margin: 0 0 16px; } h3 { font-size: 13px; color: #C08B5C; }
        .card { background: #FFFBF5; border: 1px solid #EAD9C2; border-radius: 10px; padding: 16px; margin-bottom: 14px; }
        .row { display: flex; justify-content: space-between; padding: 8px 0; font-size: 13px; }
        .row span:first-child { color: #96785A; } label { display: block; padding: 8px 0; font-size: 13px; }
        input[type=number] { padding: 6px; border: 1px solid #EAD9C2; border-radius: 6px; width: 120px; }
        .btn { padding: 8px 16px; border-radius: 8px; border: 1px solid #C08B5C; background: #C08B5C;
            color: #FFFBF5; cursor: pointer; font-family: inherit; } .cancel { color: #8B6F47;
            background: #FFF8EF; text-decoration: none; margin-left: 8px; }
        @media (max-width: 700px) { .layout { display: block; } .sidebar { width: auto; display: flex; }
            .nav-item { padding: 12px; } .content { padding: 16px; } }
    </style>
</head>
<body>
<header class="topbar"><strong>🏷️ Hotel Points Guest</strong><span>👤 Guest: ${uid}</span></header>
<div class="layout">
    <nav class="sidebar">
        <a class="nav-item" href="${pageContext.request.contextPath}/guest/${uid}/points-mall">Points Mall</a>
        <a class="nav-item" href="${pageContext.request.contextPath}/guest/${uid}/my-points">My Points</a>
        <a class="nav-item" href="${pageContext.request.contextPath}/guest/${uid}/account">Account</a>
        <form action="${pageContext.request.contextPath}/logout" method="post" style="margin:0;"><button type="submit" class="nav-item" style="color:#A05B3C;background:none;border:none;font:inherit;width:100%;text-align:left;cursor:pointer;">Sign out</button></form>
    </nav>
    <main class="content">
        <h2>Confirm Payment</h2>
        <div class="card"><h3>Product</h3><div class="row"><span>Name</span><strong><c:out value="${payment.name}"/></strong></div>
            <div class="row"><span>Total Points</span><strong>${payment.totalPointsRequired} pts</strong></div>
            <div class="row"><span>Minimum Points</span><strong>${payment.minPointsRequired} pts</strong></div>
            <div class="row"><span>Available Balance</span><strong>${payment.availableBalance} pts</strong></div></div>
        <form class="card" method="post" action="${pageContext.request.contextPath}/guest/${uid}/redemptions">
            <input type="hidden" name="pid" value="${payment.pid}">
            <h3>Choose Payment Method</h3>
            <c:forEach items="${payment.paymentMethods}" var="method" varStatus="status">
                <label><input type="radio" name="pmid" value="${method.pmid}" ${status.first ? 'checked' : ''}>
                    <c:out value="${method.name}"/> (Type ${status.index + 1})</label>
            </c:forEach>
            <label>Points to use <input type="number" name="pointsUsed" min="${payment.minPointsRequired}"
                max="${payment.availableBalance}" value="${payment.totalPointsRequired}" required></label>
            <label>Cash supplement <input type="number" name="cashUsed" min="0" step="0.01"></label>
            <label>Voucher supplement <input type="number" name="voucherUsed" min="0" step="0.01"></label>
            <button class="btn" type="submit">Confirm Payment</button>
            <a class="btn cancel" href="${pageContext.request.contextPath}/guest/${uid}/points-mall">Cancel</a>
        </form>
    </main>
</div>
</body>
</html>
