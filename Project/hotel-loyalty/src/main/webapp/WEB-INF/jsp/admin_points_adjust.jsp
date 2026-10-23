<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" href="data:,">
    <title>Hotel Points Admin — Points Adjustment</title>
    <style>
        body { font-family: Georgia, serif; background: #FAF5EE; color: #5A4632; margin: 0; }
        .topbar { display: flex; justify-content: space-between; align-items: center; background: #FFFBF5; border-bottom: 1px solid #EAD9C2; padding: 12px 20px; }
        .topbar .title { font-size: 17px; font-weight: bold; }
        .topbar .user { font-size: 13px; color: #96785A; }
        .layout { display: flex; min-height: calc(100vh - 52px); }
        .sidebar { width: 150px; background: #FFFBF5; border-right: 1px solid #EAD9C2; padding: 14px 0; flex-shrink: 0; }
        .nav-item { display: block; padding: 13px 18px; font-size: 14px; color: #96785A; cursor: pointer; border-left: 3px solid transparent; text-decoration: none; }
        .nav-item:hover { background: #FAF5EE; }
        .nav-item.active { color: #C08B5C; font-weight: bold; background: #FAF5EE; border-left-color: #C08B5C; }
        .nav-item.disabled { color: #C4B09A; cursor: not-allowed; }
        .nav-item.disabled:hover { background: transparent; }
        .content { flex: 1; padding: 20px 24px; }
        .panel-header { margin-bottom: 16px; }
        .panel-header h2 { font-size: 16px; margin: 0; }
        .btn { padding: 6px 14px; border-radius: 8px; font-size: 12px; cursor: pointer; border: 1px solid #D9C4A8; background: #FFF8EF; color: #8B6F47; font-family: inherit; }
        .btn:hover { background: #F5EBDD; }
        .btn.primary { background: #C08B5C; color: #FFFBF5; border-color: #C08B5C; }
        .btn.primary:hover { background: #A87848; }
        .step { border: 1px solid #EAD9C2; border-radius: 10px; background: #FFFBF5; padding: 16px; margin-bottom: 14px; }
        .step h3 { font-size: 13px; color: #C08B5C; margin: 0 0 12px 0; }
        .search-row { display: flex; gap: 10px; margin-bottom: 10px; }
        .search-row input { flex: 1; padding: 8px 12px; border: 1px solid #EAD9C2; border-radius: 8px; background: #FAF5EE; font-size: 13px; color: #5A4632; font-family: inherit; outline: none; }
        .form-group { margin-bottom: 13px; }
        .form-group label { display: block; font-size: 12px; color: #96785A; margin-bottom: 4px; }
        .form-group input { width: 100%; box-sizing: border-box; padding: 8px 10px; border: 1px solid #EAD9C2; border-radius: 8px; background: #FAF5EE; font-size: 13px; color: #5A4632; font-family: inherit; outline: none; }
        .form-group .hint { font-size: 11px; color: #A98B6E; margin-top: 3px; }
        .selected-user { font-size: 13px; color: #5A4632; background: #F3E8D7; padding: 8px 12px; border-radius: 8px; display: none; margin-bottom: 10px; }
        .warn { font-size: 12px; color: #B0614A; background: #F5E0D8; padding: 6px 12px; border-radius: 6px; display: none; margin-top: 8px; }
        .msg { font-size: 12px; margin-top: 8px; padding: 6px 12px; border-radius: 6px; display: none; }
        .msg.error { color: #B0614A; background: #F5E0D8; }
        .msg.ok { color: #5A7A4A; background: #E8F0E5; }
        .queue-status { border: 1px dashed #D9C4A8; border-radius: 10px; background: #FAF5EE; padding: 14px 16px; margin-top: 14px; display: none; }
        .queue-status .ok { color: #5A7A4A; font-weight: bold; }
        .flow { font-size: 12px; color: #96785A; margin-top: 6px; }
        .footer { margin-top: 16px; color: #888; font-size: 13px; }
        /* responsive */
        @media (max-width: 768px) {
            .layout { flex-direction: column; }
            .sidebar { width: 100%; border-right: none; border-bottom: 1px solid #EAD9C2; display: flex; padding: 0; overflow-x: auto; }
            .nav-item { border-left: none; border-bottom: 3px solid transparent; padding: 10px 14px; white-space: nowrap; }
            .nav-item.active { border-left-color: transparent; border-bottom-color: #C08B5C; }
            .content { padding: 14px 12px; }
            .search-row { flex-direction: column; }
            .search-row input { width: 100%; box-sizing: border-box; }
        }
    </style>
</head>
<body>
<div class="topbar">
    <span class="title">🏷️ Hotel Points Admin</span>
    <span class="user">👤 Manager</span>
</div>
<div class="layout">
    <div class="sidebar">
        <a class="nav-item" href="/admin/products">Products</a>
        <a class="nav-item" href="/admin/rules">Points Rules</a>
        <a class="nav-item active" href="/admin/points">Points Adjustment</a>
        <a class="nav-item" href="/logout" style="color:#A05B3C;">Sign out</a>
    </div>
    <div class="content">

        <div class="panel-header">
            <h2>Points Adjustment (Manual Earn)</h2>
        </div>

        <!-- UC5 step 1: select target user -->
        <div class="step">
            <h3>Step 1: Select Target User (UC5 Step 1)</h3>
            <div class="search-row">
                <input type="text" id="f-uid" placeholder="Enter target user UID">
                <button class="btn primary" onclick="verifyUser()">Verify</button>
            </div>
            <div id="selected-user" class="selected-user"></div>
            <div id="verify-warn" class="warn">⚠️ Cannot operate if user does not exist or status is abnormal (Inactive / Forbidden) (Precondition 2)</div>
            <div id="verify-msg" class="msg"></div>
        </div>

        <!-- UC5 step 2: fill in earn details -->
        <div class="step">
            <h3>Step 2: Fill in Earn Details (UC5 Step 2)</h3>
            <div class="form-group"><label>Points to Earn*</label><input type="number" id="f-points" min="1" placeholder="Positive integer"></div>
            <div class="form-group"><label>Reason</label><input type="text" id="f-reason" placeholder="Optional"><div class="hint">Recorded in audit log</div></div>
            <button class="btn primary" onclick="submitAdjustment()">Submit (UC5 Step 3)</button>
            <div id="adjust-msg" class="msg"></div>
        </div>

        <!-- UC5 steps 4-6: queue status -->
        <div id="queue-status" class="queue-status">
            <span class="ok">✅ <span id="queue-text"></span></span>
            <div class="flow">Status: Queued → Processing → ✅ Success / ⚠️ Retrying</div>
            <div class="flow">After processing, the user's currentPoint is updated and a PointsTransaction (positive record) is generated</div>
        </div>

        <div class="footer">
            <a href="/demo/products">Points Mall</a>
        </div>
    </div>
</div>

<script>
    /** null = not verified; non-null uid = verified (UC5 precondition 2 satisfied). */
    let verifiedUid = null;

    function showMsg(id, text, ok) {
        const el = document.getElementById(id);
        el.textContent = text;
        el.className = 'msg ' + (ok ? 'ok' : 'error');
        el.style.display = 'block';
    }

    function hideMsg(id) {
        document.getElementById(id).style.display = 'none';
    }

    /** UC5 step 1: verify the target user exists and has normal status. */
    function verifyUser() {
        const uid = document.getElementById('f-uid').value.trim();
        if (!uid) { showMsg('verify-msg', 'Please enter a user UID', false); return; }

        verifiedUid = null;
        document.getElementById('selected-user').style.display = 'none';
        hideMsg('verify-msg');

        fetch('/api/admin/points/user/' + encodeURIComponent(uid))
            .then(r => r.json().catch(() => { throw new Error('non-json response'); }))
            .then(result => {
                if (result.code === 0) {
                    const u = result.data;
                    const statusText = u.status === 0 ? 'Normal' : (u.status === 1 ? 'Inactive' : 'Forbidden');
                    const el = document.getElementById('selected-user');
                    el.textContent = '✅ UID ' + u.uid + ' → ' + u.name
                        + ' (L' + u.level + ', current ' + u.currentPoint + ' pts, status: ' + statusText + ')';
                    el.style.display = 'block';
                    if (u.status === 0) {
                        verifiedUid = u.uid;
                        showMsg('verify-msg', 'Verified. You can now fill in the earn details.', true);
                    } else {
                        showMsg('verify-msg', 'User status is abnormal (' + statusText + '), cannot operate (Precondition 2)', false);
                    }
                } else {
                    showMsg('verify-msg', result.message, false);
                }
            })
            .catch(() => showMsg('verify-msg', 'Request failed, please retry', false));
    }

    /** UC5 steps 2-3: submit the adjustment — server enqueues it (step 4). */
    function submitAdjustment() {
        if (!verifiedUid) { showMsg('adjust-msg', 'Please complete Step 1 first: verify the target user', false); return; }

        const points = parseInt(document.getElementById('f-points').value);
        if (isNaN(points) || points < 1) { showMsg('adjust-msg', 'Points to earn must be a positive integer', false); return; }

        const reason = document.getElementById('f-reason').value.trim();
        hideMsg('adjust-msg');

        const body = { uid: verifiedUid, points: points, reason: reason || null };
        fetch('/api/admin/points/adjustment', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        })
            .then(r => r.json().catch(() => { throw new Error('non-json response'); }))
            .then(result => {
                if (result.code === 0) {
                    document.getElementById('queue-text').textContent = result.data || 'Accepted, entered processing queue';
                    document.getElementById('queue-status').style.display = 'block';
                    showMsg('adjust-msg', 'Submitted successfully', true);
                    document.getElementById('f-points').value = '';
                    document.getElementById('f-reason').value = '';
                } else {
                    showMsg('adjust-msg', result.message, false);
                }
            })
            .catch(() => showMsg('adjust-msg', 'Request failed, please retry', false));
    }
</script>
</body>
</html>