<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" href="data:,">
    <title>Points Mall — Product List</title>
    <style>
        body { font-family: 'Segoe UI', sans-serif; background: #f5f5f5; margin: 0; padding: 20px; }
        .container { width: 100%; box-sizing: border-box; }
        h1 { color: #1a3c5e; border-bottom: 3px solid #d4a017; padding-bottom: 8px; }
        .badge { background: #d4a017; color: #fff; padding: 2px 10px; border-radius: 12px; font-size: 13px; }
        table { width: 100%; border-collapse: collapse; background: #fff; box-shadow: 0 1px 3px rgba(0,0,0,.1); }
        th { background: #1a3c5e; color: #fff; padding: 10px 14px; text-align: left; }
        td { padding: 10px 14px; border-bottom: 1px solid #eee; }
        tr:hover td { background: #faf6ec; }
        .stock-ok { color: #2e7d32; font-weight: bold; }
        .stock-out { color: #c62828; font-weight: bold; }
        .empty { text-align: center; padding: 40px; color: #999; }
        .footer { margin-top: 16px; color: #888; font-size: 13px; }
        /* responsive */
        @media (max-width: 768px) {
            body { padding: 10px; }
            table { display: block; overflow-x: auto; }
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Hotel Loyalty — Points Mall Products <span class="badge">JSP Demo</span></h1>

    <c:choose>
        <c:when test="${empty products}">
            <div class="empty">No products yet. Please add them in the admin console first.</div>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                <tr>
                    <th>PID</th>
                    <th>Product Name</th>
                    <th>Points Required</th>
                    <th>Min Points</th>
                    <th>Stock (Left/Total)</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${products}" var="p">
                    <tr>
                        <td>${p.pid}</td>
                        <td><c:out value="${p.name}"/></td>
                        <td>${p.totalPointsRequired} pts</td>
                        <td>${p.minPointsRequired} pts</td>
                        <td>
                            <c:choose>
                                <c:when test="${p.remainingStock > 0}">
                                    <span class="stock-ok">${p.remainingStock} / ${p.totalStock}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="stock-out">Out of stock</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>

    <div class="footer">
        ${fn:length(products)} products in total &nbsp;|&nbsp;
        <a href="/h2-console">H2 Console</a>
    </div>
</div>
</body>
</html>