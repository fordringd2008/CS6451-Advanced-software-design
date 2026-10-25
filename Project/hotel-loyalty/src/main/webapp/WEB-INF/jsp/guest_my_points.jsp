<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hotel Points Guest — My Points</title>
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
        .summary { display: flex; gap: 14px; margin-bottom: 16px; }
        .card { flex: 1; padding: 14px 16px; background: #FFFBF5; border: 1px solid #EAD9C2; border-radius: 10px; }
        .label { color: #96785A; font-size: 12px; display: block; } .value { font-size: 20px; font-weight: bold; }
        table { width: 100%; border-collapse: collapse; background: #FFFBF5; border: 1px solid #EAD9C2; }
        th { background: #F5EBDD; color: #96785A; text-align: left; padding: 10px; font-size: 12px; }
        td { padding: 10px; border-top: 1px solid #F5EBDD; font-size: 13px; }
        .success { background: #E7F0DF; color: #5A7A4A; border-radius: 8px; padding: 10px; margin-bottom: 14px; }
        .empty { text-align: center; padding: 25px; color: #96785A; }
        .earn { color: #5A7A4A; font-weight: 600; }
        .redeem { color: #B0614A; font-weight: 600; }
        .tabs { display: flex; margin-top: 16px; border-bottom: 1px solid #EAD9C2; }
        .tab { flex: 1; text-align: center; padding: 11px 0; font-size: 13px;
            color: #96785A; text-decoration: none; border-bottom: 2px solid transparent;
            cursor: pointer; }
        .tab.active { color: #C08B5C; font-weight: bold; border-bottom-color: #C08B5C; }
        .tab:hover:not(.active) { color: #C08B5C; }
        @media (max-width: 700px) { .layout { display: block; } .sidebar { width: auto; display: flex; }
            .nav-item { padding: 12px; } .content { padding: 16px; } .summary { display: block; }
            .card { margin-bottom: 12px; } table { overflow-x: auto; display: block; } }
    </style>
</head>
<body>
<header class="topbar"><strong>🏷️ Hotel Points Guest</strong><span>👤 <c:out value="${points.name}"/> [Guest]</span></header>
<div class="layout">
    <nav class="sidebar">
        <a class="nav-item" href="${pageContext.request.contextPath}/guest/${uid}/points-mall">Points Mall</a>
        <a class="nav-item active" href="${pageContext.request.contextPath}/guest/${uid}/my-points">My Points</a>
        <a class="nav-item" href="${pageContext.request.contextPath}/guest/${uid}/account">Account</a>
        <form action="${pageContext.request.contextPath}/logout" method="post" style="margin:0;" onsubmit="return confirm('Are you sure you want to sign out?');"><button type="submit" class="nav-item" style="color:#A05B3C;background:none;border:none;font:inherit;width:100%;text-align:left;cursor:pointer;">Sign out</button></form>
    </nav>
    <main class="content">
        <c:if test="${not empty successMessage}"><div class="success"><c:out value="${successMessage}"/></div></c:if>
        <h2>My Points</h2>
        <div class="summary">
            <div class="card"><span class="label">Member</span><span class="value"><c:out value="${points.name}"/></span></div>
            <div class="card"><span class="label">Level</span><span class="value"><c:out value="${points.levelLabel}"/></span></div>
            <div class="card"><span class="label">Available Points</span><span class="value">${points.currentPoint} pts</span></div>
        </div>
        <p>Registered: ${points.registerTime}</p>
        <div class="tabs">
            <a class="tab" data-tab="history" href="javascript:void(0)" onclick="showTab('history')">Points History</a>
            <a class="tab active" data-tab="redemptions" href="javascript:void(0)" onclick="showTab('redemptions')">My Redemptions</a>
        </div>
        <section id="tab-history" style="display:none">
            <c:choose>
                <c:when test="${empty points.history}"><div class="empty">No points history yet.</div></c:when>
                <c:otherwise><table><thead><tr><th>Transaction</th><th>Type</th><th>Points Change</th><th>Related Order</th><th>Date</th></tr></thead>
                    <tbody><c:forEach items="${points.history}" var="txn"><tr>
                        <td><c:out value="${txn.ptid}"/></td>
                        <td><c:out value="${txn.type}"/></td>
                        <td><c:choose><c:when test="${txn.pointsChange >= 0}"><span class="earn">+${txn.pointsChange} pts</span></c:when><c:otherwise><span class="redeem">${txn.pointsChange} pts</span></c:otherwise></c:choose></td>
                        <td><c:choose><c:when test="${not empty txn.relatedOrderId}"><c:out value="${txn.relatedOrderId}"/></c:when><c:otherwise>—</c:otherwise></c:choose></td>
                        <td>${txn.gmtCreate}</td>
                    </tr></c:forEach></tbody></table></c:otherwise>
            </c:choose>
        </section>
        <section id="tab-redemptions">
            <c:choose>
                <c:when test="${empty points.redemptions}"><div class="empty">No redemptions yet.</div></c:when>
                <c:otherwise><table><thead><tr><th>Order</th><th>Product</th><th>Points Used</th><th>Status</th><th>Date</th></tr></thead>
                    <tbody><c:forEach items="${points.redemptions}" var="record"><tr>
                        <td><c:out value="${record.oid}"/></td><td><c:out value="${record.productName}"/></td>
                        <td>-${record.pointsUsed} pts</td><td><c:out value="${record.statusLabel}"/></td><td>${record.gmtCreate}</td>
                    </tr></c:forEach></tbody></table></c:otherwise>
            </c:choose>
        </section>
    </main>
</div>
<script>
    function showTab(name) {
        document.querySelectorAll('.tab').forEach(function (t) {
            t.classList.toggle('active', t.dataset.tab === name);
        });
        document.getElementById('tab-history').style.display = name === 'history' ? '' : 'none';
        document.getElementById('tab-redemptions').style.display = name === 'redemptions' ? '' : 'none';
    }
</script>
</body>
</html>
