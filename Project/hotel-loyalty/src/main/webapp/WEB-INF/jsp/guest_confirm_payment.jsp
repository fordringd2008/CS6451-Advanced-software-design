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
        .modal-backdrop { display: none; position: fixed; inset: 0; background: rgba(90, 70, 50, .42);
            align-items: center; justify-content: center; padding: 16px; z-index: 10; }
        .modal-backdrop.open { display: flex; }
        .modal { width: min(420px, 100%); background: #FFFBF5; border: 1px solid #EAD9C2;
            border-radius: 10px; padding: 22px; box-shadow: 0 12px 32px rgba(90, 70, 50, .2); }
        .modal h3 { margin-top: 0; }
        .modal input { box-sizing: border-box; width: 100%; padding: 9px; border: 1px solid #EAD9C2;
            border-radius: 6px; font: inherit; }
        .modal label { padding: 7px 0; }
        .modal-actions { display: flex; gap: 8px; margin-top: 14px; }
        .modal-actions .btn { flex: 1; }
        .status { display: none; text-align: center; padding: 20px 0 8px; }
        .status.show { display: block; }
        .spinner { width: 25px; height: 25px; margin: 0 auto 12px; border: 3px solid #EAD9C2;
            border-top-color: #C08B5C; border-radius: 50%; animation: spin .8s linear infinite; }
        .result { display: none; padding: 12px; border-radius: 6px; margin-top: 12px; }
        .result.show { display: block; }
        .result.success { color: #276749; background: #E8F5E9; border: 1px solid #A5D6A7; }
        .result.failure { color: #A05B3C; background: #FDECEA; border: 1px solid #F5C6CB; }
        @keyframes spin { to { transform: rotate(360deg); } }
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
        <form action="${pageContext.request.contextPath}/logout" method="post" style="margin:0;" onsubmit="return confirm('Are you sure you want to sign out?');"><button type="submit" class="nav-item" style="color:#A05B3C;background:none;border:none;font:inherit;width:100%;text-align:left;cursor:pointer;">Sign out</button></form>
    </nav>
    <main class="content">
        <h2>Confirm Payment</h2>
        <div class="card"><h3>Product</h3><div class="row"><span>Name</span><strong><c:out value="${payment.name}"/></strong></div>
            <div class="row"><span>Total Points</span><strong>${payment.totalPointsRequired} pts</strong></div>
            <div class="row"><span>Minimum Points</span><strong>${payment.minPointsRequired} pts</strong></div>
            <div class="row"><span>Available Balance</span><strong>${payment.availableBalance} pts</strong></div></div>
        <form id="redemption-form" class="card" method="post" action="${pageContext.request.contextPath}/guest/${uid}/redemptions">
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
            <button class="btn" type="button" id="confirm-payment">Confirm Payment</button>
            <a class="btn cancel" href="${pageContext.request.contextPath}/guest/${uid}/points-mall">Cancel</a>
        </form>
    </main>
</div>
<div class="modal-backdrop" id="payment-modal" role="dialog" aria-modal="true" aria-labelledby="payment-modal-title">
    <section class="modal">
        <div id="payment-form-state">
            <h3 id="payment-modal-title">Simulated Payment</h3>
            <p>Enter the information below to simulate the external payment.</p>
            <label>Card or account number <input id="account-number" type="text" inputmode="numeric"
                autocomplete="off" required></label>
            <label>Account name <input id="account-name" type="text" autocomplete="name" required></label>
            <label>Payment password <input id="payment-password" type="password" inputmode="numeric"
                minlength="4" autocomplete="off" required></label>
            <div class="modal-actions">
                <button class="btn" type="button" id="start-payment">Pay Now</button>
                <button class="btn cancel" type="button" id="close-payment">Cancel</button>
            </div>
            <div class="result" id="payment-result" role="status"></div>
        </div>
        <div class="status" id="payment-status" role="status" aria-live="polite">
            <div class="spinner"></div>
            <strong>Processing payment...</strong>
            <p>Please wait.</p>
        </div>
    </section>
</div>
<script>
    (function () {
        var modal = document.getElementById('payment-modal');
        var form = document.getElementById('redemption-form');
        var formState = document.getElementById('payment-form-state');
        var status = document.getElementById('payment-status');
        var result = document.getElementById('payment-result');
        var startButton = document.getElementById('start-payment');
        var closeButton = document.getElementById('close-payment');
        var paymentAttempt = 0;

        function openModal() {
            paymentAttempt = 0;
            result.className = 'result';
            result.textContent = '';
            startButton.disabled = false;
            formState.style.display = 'block';
            status.classList.remove('show');
            modal.classList.add('open');
            document.getElementById('account-number').focus();
        }

        function closeModal() {
            modal.classList.remove('open');
        }

        document.getElementById('confirm-payment').addEventListener('click', openModal);
        closeButton.addEventListener('click', closeModal);
        modal.addEventListener('click', function (event) {
            if (event.target === modal) closeModal();
        });

        startButton.addEventListener('click', function () {
            var accountNumber = document.getElementById('account-number');
            var accountName = document.getElementById('account-name');
            var password = document.getElementById('payment-password');
            if (!accountNumber.value.trim() || !accountName.value.trim() || password.value.length < 4) {
                result.className = 'result failure show';
                result.textContent = 'Please complete all fields. The password must have at least 4 characters.';
                return;
            }

            formState.style.display = 'none';
            status.classList.add('show');
            window.setTimeout(function () {
                paymentAttempt += 1;
                status.classList.remove('show');
                formState.style.display = 'block';
                result.className = paymentAttempt % 2 === 1 ? 'result failure show' : 'result success show';
                result.textContent = paymentAttempt % 2 === 1
                    ? 'Payment failed in the simulation. Please try again.'
                    : 'Payment successful. Confirming your redemption...';
                if (paymentAttempt % 2 === 0) {
                    startButton.disabled = true;
                    window.setTimeout(function () { form.submit(); }, 600);
                }
            }, 2000);
        });
    }());
</script>
</body>
</html>
