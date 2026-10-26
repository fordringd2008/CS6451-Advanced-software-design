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
        .disabled-payment-method { color: #B8AA9A; cursor: not-allowed; }
        .disabled-payment-method input { cursor: not-allowed; }
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
        .form-error { display: none; color: #A05B3C; background: #FDECEA; border: 1px solid #F5C6CB;
            border-radius: 6px; padding: 10px 12px; margin: 10px 0; font-size: 13px; }
        .form-error.show { display: block; }
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
            <c:set var="fullPointsOnly" value="${payment.totalPointsRequired == payment.minPointsRequired}"/>
            <c:forEach items="${payment.paymentMethods}" var="method" varStatus="status">
                <label class="${fullPointsOnly && !status.first ? 'disabled-payment-method' : ''}">
                    <input type="radio" name="pmid" value="${method.pmid}"
                        ${status.first ? 'checked' : ''}
                        ${fullPointsOnly && !status.first ? 'disabled' : ''}>
                    <c:out value="${method.name}"/> (Type ${status.index + 1})</label>
            </c:forEach>
            <label>Points to use <input type="number" name="pointsUsed" min="${payment.minPointsRequired}"
                max="${payment.availableBalance}" value="${payment.totalPointsRequired}" required></label>
            <label id="cash-supplement">Cash supplement <input id="cash-used-display" type="number" min="0.01" step="0.01" disabled>
                <input type="hidden" name="cashUsed"></label>
            <label id="voucher-supplement">Voucher supplement <input id="voucher-used-display" type="number" min="0.01" step="0.01" disabled>
                <input type="hidden" name="voucherUsed"></label>
            <div id="payment-error" class="form-error" role="alert"></div>
            <button class="btn" type="button" id="confirm-payment">Confirm Payment</button>
            <a class="btn cancel" href="${pageContext.request.contextPath}/guest/${uid}/points-mall">Cancel</a>
        </form>
    </main>
</div>
<div class="modal-backdrop" id="payment-modal" role="dialog" aria-modal="true" aria-labelledby="payment-modal-title">
    <section class="modal">
        <div id="payment-form-state">
            <h3 id="payment-modal-title">Simulated Payment</h3>
            <p id="payment-instructions">Enter the information below to simulate the external payment.</p>
            <div id="cash-payment-fields">
                <label>Card or account number <input id="account-number" type="text" inputmode="numeric"
                    autocomplete="off"></label>
                <label>Account name <input id="account-name" type="text" autocomplete="name"></label>
                <label>Payment password <input id="payment-password" type="password" inputmode="numeric"
                    minlength="4" autocomplete="off"></label>
            </div>
            <div id="voucher-payment-fields">
                <label>Voucher ID <input id="voucher-id" type="text" autocomplete="off"></label>
            </div>
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
        var cashSupplement = document.getElementById('cash-supplement');
        var voucherSupplement = document.getElementById('voucher-supplement');
        var cashPaymentFields = document.getElementById('cash-payment-fields');
        var voucherPaymentFields = document.getElementById('voucher-payment-fields');
        var paymentInstructions = document.getElementById('payment-instructions');
        var pointsUsed = form.querySelector('input[name="pointsUsed"]');
        var cashUsedDisplay = document.getElementById('cash-used-display');
        var voucherUsedDisplay = document.getElementById('voucher-used-display');
        var cashUsedSubmit = form.querySelector('input[name="cashUsed"]');
        var voucherUsedSubmit = form.querySelector('input[name="voucherUsed"]');
        var paymentError = document.getElementById('payment-error');
        var paymentAttempt = 0;

        function selectedPaymentMethod() {
            return form.querySelector('input[name="pmid"]:checked').value;
        }

        function updatePaymentMethod() {
            var method = selectedPaymentMethod();
            var isCash = method === 'PM-2';
            var isVoucher = method === 'PM-3';
            cashSupplement.style.display = isCash ? 'block' : 'none';
            voucherSupplement.style.display = isVoucher ? 'block' : 'none';
            cashPaymentFields.style.display = isCash ? 'block' : 'none';
            voucherPaymentFields.style.display = isVoucher ? 'block' : 'none';
            paymentInstructions.textContent = isVoucher
                ? 'Enter a voucher ID to simulate voucher verification.'
                : 'Enter the information below to simulate the external payment.';
            updateSupplement();
        }

        function updateSupplement() {
            var method = selectedPaymentMethod();
            var points = Number(pointsUsed.value);
            var total = Number('${payment.totalPointsRequired}');
            var supplement = Number.isFinite(points) && points < total
                ? ((total - points) * 0.1).toFixed(2)
                : '';
            cashUsedDisplay.value = method === 'PM-2' ? supplement : '';
            voucherUsedDisplay.value = method === 'PM-3' ? supplement : '';
            cashUsedSubmit.value = method === 'PM-2' ? supplement : '';
            voucherUsedSubmit.value = method === 'PM-3' ? supplement : '';
        }

        function clearPaymentError() {
            paymentError.className = 'form-error';
            paymentError.textContent = '';
        }

        function openModal() {
            var method = selectedPaymentMethod();
            var points = Number(pointsUsed.value);
            var total = Number('${payment.totalPointsRequired}');
            var minimum = Number('${payment.minPointsRequired}');
            clearPaymentError();
            if (method !== 'PM-1' && points < minimum) {
                paymentError.className = 'form-error show';
                paymentError.textContent = 'Points to use must be at least ' + minimum
                    + ' for this payment method.';
                return;
            }
            if (method !== 'PM-1' && points >= total) {
                paymentError.className = 'form-error show';
                paymentError.textContent = 'Points must be less than the full product price for this payment method.';
                return;
            }
            if (method === 'PM-1') {
                form.submit();
                return;
            }
            paymentAttempt = 0;
            result.className = 'result';
            result.textContent = '';
            startButton.disabled = false;
            formState.style.display = 'block';
            status.classList.remove('show');
            updatePaymentMethod();
            modal.classList.add('open');
            document.getElementById(selectedPaymentMethod() === 'PM-3' ? 'voucher-id' : 'account-number').focus();
        }

        function closeModal() {
            modal.classList.remove('open');
        }

        document.getElementById('confirm-payment').addEventListener('click', openModal);
        form.querySelectorAll('input[name="pmid"]').forEach(function (radio) {
            radio.addEventListener('change', updatePaymentMethod);
        });
        pointsUsed.addEventListener('input', updateSupplement);
        pointsUsed.addEventListener('input', clearPaymentError);
        form.querySelectorAll('input[name="pmid"]').forEach(function (radio) {
            radio.addEventListener('change', clearPaymentError);
        });
        updatePaymentMethod();
        closeButton.addEventListener('click', closeModal);
        modal.addEventListener('click', function (event) {
            if (event.target === modal) closeModal();
        });

        startButton.addEventListener('click', function () {
            var method = selectedPaymentMethod();
            var accountNumber = document.getElementById('account-number');
            var accountName = document.getElementById('account-name');
            var password = document.getElementById('payment-password');
            var voucherId = document.getElementById('voucher-id');
            var invalidCashPayment = method === 'PM-2'
                && (!accountNumber.value.trim() || !accountName.value.trim() || password.value.length < 4);
            var invalidVoucherPayment = method === 'PM-3' && !voucherId.value.trim();
            if (invalidCashPayment || invalidVoucherPayment) {
                result.className = 'result failure show';
                result.textContent = method === 'PM-3'
                    ? 'Please enter a voucher ID.'
                    : 'Please complete all fields. The password must have at least 4 characters.';
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
                    ? (method === 'PM-3'
                        ? 'Voucher ID not found. Please try again.'
                        : 'Payment failed in the simulation. Please try again.')
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
