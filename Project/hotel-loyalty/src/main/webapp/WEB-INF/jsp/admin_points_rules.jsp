<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" href="data:,">
    <title>Hotel Points Admin — Points Rules</title>
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
        .content { flex: 1; padding: 20px 24px; }
        .panel-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
        .panel-header h2 { font-size: 16px; margin: 0; }
        table { width: 100%; border-collapse: collapse; background: #FFFBF5; border: 1px solid #EAD9C2; border-radius: 10px; overflow: hidden; }
        th { background: #F5EBDD; color: #96785A; font-size: 12px; padding: 10px 12px; text-align: left; }
        td { padding: 10px 12px; font-size: 13px; border-top: 1px solid #F5EBDD; }
        tr:hover td { background: #FAF6EC; }
        .btn { padding: 6px 14px; border-radius: 8px; font-size: 12px; cursor: pointer; border: 1px solid #D9C4A8; background: #FFF8EF; color: #8B6F47; font-family: inherit; }
        .btn:hover { background: #F5EBDD; }
        .btn.primary { background: #C08B5C; color: #FFFBF5; border-color: #C08B5C; }
        .btn.primary:hover { background: #A87848; }
        .btn.small { padding: 4px 10px; font-size: 11px; }
        .modal-overlay { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(90,70,50,0.3); justify-content: center; align-items: center; }
        .modal-overlay.show { display: flex; }
        .modal { background: #FFFBF5; border: 1px solid #EAD9C2; border-radius: 14px; width: 460px; box-shadow: 0 8px 30px rgba(180,140,100,0.2); }
        .modal-header { padding: 14px 18px; border-bottom: 1px solid #F0E4D3; font-size: 15px; font-weight: bold; }
        .modal-body { padding: 16px 18px; }
        .modal-footer { padding: 12px 18px; border-top: 1px solid #F0E4D3; display: flex; justify-content: flex-end; gap: 10px; }
        .form-group { margin-bottom: 13px; }
        .form-group label { display: block; font-size: 12px; color: #96785A; margin-bottom: 4px; }
        .form-group input { width: 100%; box-sizing: border-box; padding: 8px 10px; border: 1px solid #EAD9C2; border-radius: 8px; background: #FAF5EE; font-size: 13px; color: #5A4632; font-family: inherit; outline: none; }
        .form-group .hint { font-size: 11px; color: #A98B6E; margin-top: 3px; }
        .radio-group { display: flex; gap: 18px; font-size: 13px; margin-top: 2px; }
        .radio-group label { display: flex; align-items: center; gap: 5px; cursor: pointer; color: #5A4632; }
        .radio-group input { accent-color: #C08B5C; }
        .msg { font-size: 12px; margin-top: 8px; padding: 6px 12px; border-radius: 6px; display: none; }
        .msg.error { color: #B0614A; background: #F5E0D8; }
        .msg.ok { color: #5A7A4A; background: #E8F0E5; }
        .empty { text-align: center; padding: 40px; color: #999; }
        .footer { margin-top: 16px; color: #888; font-size: 13px; }
        .muted { color: #A98B6E; }
        /* responsive */
        @media (max-width: 768px) {
            .layout { flex-direction: column; }
            .sidebar { width: 100%; border-right: none; border-bottom: 1px solid #EAD9C2; display: flex; padding: 0; overflow-x: auto; }
            .nav-item { border-left: none; border-bottom: 3px solid transparent; padding: 10px 14px; white-space: nowrap; }
            .nav-item.active { border-left-color: transparent; border-bottom-color: #C08B5C; }
            .content { padding: 14px 12px; }
            table { display: block; overflow-x: auto; }
            .modal { width: calc(100vw - 24px); }
            .panel-header { flex-wrap: wrap; gap: 8px; }
            .radio-group { flex-direction: column; gap: 6px; }
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
        <a class="nav-item active" href="/admin/rules">Points Rules</a>
        <a class="nav-item" href="/admin/points">Points Adjustment</a>
        <form action="/logout" method="post" style="margin:0;"><button type="submit" class="nav-item" style="color:#A05B3C;background:none;border:none;font-family:inherit;width:100%;text-align:left;cursor:pointer;">Sign out</button></form>
    </div>
    <div class="content">

    <div class="panel-header">
        <h2>Points Rule Management</h2>
        <button class="btn primary" onclick="openCreateModal()">+ Add Rule</button>
    </div>

    <div id="page-msg" class="msg"></div>

    <c:choose>
        <c:when test="${empty rules}">
            <div class="empty">No rules yet. Click "Add Rule" to create one.</div>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                <tr>
                    <th>PRID</th><th>Name</th><th>Type</th><th>Threshold</th><th>Points Ratio</th><th>Description</th><th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${rules}" var="r">
                    <tr data-prid="${fn:escapeXml(r.prid)}" data-name="${fn:escapeXml(r.name)}"
                        data-type="${r.type}" data-threshold="${r.threshold}"
                        data-ratio="${r.pointsRatio}"
                        data-start="${r.startTime}" data-end="${r.endTime}"
                        data-desc="${fn:escapeXml(r.description)}">
                        <td><c:out value="${r.prid}"/></td>
                        <td><c:out value="${r.name}"/></td>
                        <td><c:choose>
                            <c:when test="${r.type == 1}">By Level</c:when>
                            <c:when test="${r.type == 2}">By Period</c:when>
                            <c:otherwise>Unknown</c:otherwise>
                        </c:choose></td>
                        <td><c:choose>
                            <c:when test="${r.type == 1}"><span class="muted">—</span></c:when>
                            <c:when test="${empty r.threshold}"><span class="muted">No threshold</span></c:when>
                            <c:otherwise>&yen;${r.threshold}</c:otherwise>
                        </c:choose></td>
                        <td>${r.pointsRatio}%</td>
                        <td><c:out value="${r.description}"/></td>
                        <td>
                            <button class="btn small" onclick="openEditModal(this)">Edit</button>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>

    <div class="footer">
        ${fn:length(rules)} rules in total &nbsp;|&nbsp;
        <a href="/demo/products">Points Mall</a>
    </div>
    </div>
</div>

<!-- Modal: Add / Edit Rule -->
<div class="modal-overlay" id="modal-rule">
    <div class="modal">
        <div class="modal-header" id="modal-title">Add Rule</div>
        <div class="modal-body">
            <div class="form-group">
                <label>Rule Type*</label>
                <div class="radio-group">
                    <label><input type="radio" name="rtype" value="1" checked onchange="toggleTypeFields()"> By Level (type=1)</label>
                    <label><input type="radio" name="rtype" value="2" onchange="toggleTypeFields()"> By Period (type=2)</label>
                </div>
            </div>
            <div class="form-group"><label>Rule Name*</label><input type="text" id="f-name"></div>
            <div class="form-group" id="fg-threshold" style="display:none">
                <label>Min Order Amount Threshold</label><input type="number" id="f-threshold" min="0" placeholder="optional, e.g. 100">
                <div class="hint">Order must reach this amount to apply; leave empty = no threshold</div>
            </div>
            <div class="form-group"><label>Points Ratio* (%)</label><input type="number" id="f-pointsRatio" min="1" placeholder="e.g. 150 = 150%"><div class="hint">order amount × ratio% = points</div></div>
            <div class="form-group" id="fg-start" style="display:none">
                <label>Effective Start Time</label><input type="datetime-local" id="f-startTime">
                <div class="hint">Required for type=2</div>
            </div>
            <div class="form-group" id="fg-end" style="display:none">
                <label>Effective End Time</label><input type="datetime-local" id="f-endTime">
                <div class="hint">Required for type=2</div>
            </div>
            <div class="form-group"><label>Description</label><input type="text" id="f-description" placeholder="optional"></div>
            <div id="modal-msg" class="msg"></div>
        </div>
        <div class="modal-footer">
            <button class="btn" onclick="closeModal()">Cancel</button>
            <button class="btn primary" onclick="submitRule()">Save</button>
        </div>
    </div>
</div>

<script>
    /** null = create mode; non-null prid = edit mode (UC4 add / modify). */
    let editPrid = null;

    function showMsg(id, text, ok) {
        const el = document.getElementById(id);
        el.textContent = text;
        el.className = 'msg ' + (ok ? 'ok' : 'error');
        el.style.display = 'block';
    }

    /** Show/hide threshold + time fields based on selected rule type. */
    function toggleTypeFields() {
        const type = document.querySelector('input[name="rtype"]:checked').value;
        const isPeriod = type === '2';
        document.getElementById('fg-threshold').style.display = isPeriod ? 'block' : 'none';
        document.getElementById('fg-start').style.display = isPeriod ? 'block' : 'none';
        document.getElementById('fg-end').style.display = isPeriod ? 'block' : 'none';
        if (!isPeriod) {
            document.getElementById('f-threshold').value = '';
            document.getElementById('f-startTime').value = '';
            document.getElementById('f-endTime').value = '';
        }
    }

    /** Convert "2026-01-01T00:00:00" or "2026-01-01 00:00:00" to datetime-local value "2026-01-01T00:00". */
    function toDateTimeLocal(s) {
        if (!s) return '';
        return s.replace(' ', 'T').substring(0, 16);
    }

    /** Convert datetime-local value "2026-01-01T00:00" to ISO "2026-01-01T00:00:00". */
    function toIso(s) {
        if (!s) return null;
        return s.length === 16 ? s + ':00' : s;
    }

    function clearForm() {
        document.getElementById('f-name').value = '';
        document.getElementById('f-threshold').value = '';
        document.getElementById('f-pointsRatio').value = '';
        document.getElementById('f-startTime').value = '';
        document.getElementById('f-endTime').value = '';
        document.getElementById('f-description').value = '';
    }

    /** UC4 step 2: open the create modal (empty form). */
    function openCreateModal() {
        editPrid = null;
        document.getElementById('modal-title').textContent = 'Add Rule';
        document.querySelector('input[name="rtype"][value="1"]').checked = true;
        toggleTypeFields();
        clearForm();
        document.getElementById('modal-msg').style.display = 'none';
        document.getElementById('modal-rule').classList.add('show');
    }

    /** UC4 step 2: open the edit modal, pre-filled from the row's data attributes. */
    function openEditModal(btn) {
        const d = btn.closest('tr').dataset;
        editPrid = d.prid;
        document.getElementById('modal-title').textContent = 'Edit Rule — ' + d.prid;
        document.querySelector('input[name="rtype"][value="' + d.type + '"]').checked = true;
        toggleTypeFields();
        document.getElementById('f-name').value = d.name;
        document.getElementById('f-threshold').value = d.threshold || '';
        document.getElementById('f-pointsRatio').value = d.ratio;
        document.getElementById('f-startTime').value = toDateTimeLocal(d.start);
        document.getElementById('f-endTime').value = toDateTimeLocal(d.end);
        document.getElementById('f-description').value = d.desc;
        document.getElementById('modal-msg').style.display = 'none';
        document.getElementById('modal-rule').classList.add('show');
    }

    function closeModal() {
        document.getElementById('modal-rule').classList.remove('show');
    }

    /** Submit create (POST) or update (PUT) to the REST API. */
    function submitRule() {
        const name = document.getElementById('f-name').value.trim();
        const type = parseInt(document.querySelector('input[name="rtype"]:checked').value);
        const ratio = parseInt(document.getElementById('f-pointsRatio').value);
        const description = document.getElementById('f-description').value.trim();

        if (!name) { showMsg('modal-msg', 'Rule name is required', false); return; }
        if (isNaN(ratio) || ratio < 1) { showMsg('modal-msg', 'Points ratio must be a positive integer (e.g. 150 = 150%)', false); return; }

        const body = { name: name, type: type, pointsRatio: ratio, description: description || null };

        if (type === 2) {
            const threshold = document.getElementById('f-threshold').value;
            const start = document.getElementById('f-startTime').value;
            const end = document.getElementById('f-endTime').value;
            if (threshold) {
                const t = parseInt(threshold);
                if (isNaN(t) || t < 0) { showMsg('modal-msg', 'Threshold must be a non-negative integer', false); return; }
                body.threshold = t;
            }
            if (start && end && start >= end) { showMsg('modal-msg', 'Start time must be before end time', false); return; }
            body.startTime = toIso(start);
            body.endTime = toIso(end);
        }

        let url = '/api/admin/rules';
        let method = 'POST';
        if (editPrid) {
            url = '/api/admin/rules/' + encodeURIComponent(editPrid);
            method = 'PUT';
        }
        fetch(url, { method: method, headers: {'Content-Type': 'application/json'}, body: JSON.stringify(body) })
            .then(r => r.json().catch(() => { throw new Error('non-json response'); }))
            .then(result => {
                if (result.code === 0) { closeModal(); location.reload(); }
                else { showMsg('modal-msg', result.message, false); }
            })
            .catch(() => showMsg('modal-msg', 'Request failed, please retry', false));
    }
</script>
</body>
</html>