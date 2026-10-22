<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>积分商城 — 商品列表</title>
    <style>
        body { font-family: 'Segoe UI', sans-serif; background: #f5f5f5; margin: 0; padding: 20px; }
        .container { max-width: 900px; margin: 0 auto; }
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
    </style>
</head>
<body>
<div class="container">
    <h1>Hotel Loyalty — 积分商城商品 <span class="badge">JSP Demo</span></h1>

    <c:choose>
        <c:when test="${empty products}">
            <div class="empty">暂无商品，请先在管理端添加（Swagger UI: POST /api/admin/products）</div>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                <tr>
                    <th>PID</th>
                    <th>商品名称</th>
                    <th>所需积分</th>
                    <th>最低积分</th>
                    <th>库存（剩/总）</th>
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
                                    <span class="stock-out">售罄</span>
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
        共 ${fn:length(products)} 件商品 &nbsp;|&nbsp;
        <a href="/swagger-ui.html">Swagger UI</a> &nbsp;|&nbsp;
        <a href="/h2-console">H2 Console</a>
    </div>
</div>
</body>
</html>