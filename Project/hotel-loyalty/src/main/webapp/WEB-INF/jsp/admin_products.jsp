<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hotel Points Admin — Products</title>
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
        .btn.danger { color: #B0614A; border-color: #E5C4B8; }
        .btn.danger:hover { background: #F5E0D8; }
        .btn.small { padding: 4px 10px; font-size: 11px; }
        .modal-overlay { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(90,70,50,0.3); justify-content: center; align-items: center; }
        .modal-overlay.show { display: flex; }
        .modal { background: #FFFBF5; border: 1px solid #EAD9C2; border-radius: 14px; width: 430px; box-shadow: 0 8px 30px rgba(180,140,100,0.2); }
        .modal-header { padding: 14px 18px; border-bottom: 1px solid #F0E4D3; font-size: 15px; font-weight: bold; }
        .modal-body { padding: 16px 18px; }
        .modal-footer { padding: 12px 18px; border-top: 1px solid #F0E4D3; display: flex; justify-content: flex-end; gap: 10px; }
        .form-group { margin-bottom: 13px; }
        .form-group label { display: block; font-size: 12px; color: #96785A; margin-bottom: 4px; }
        .form-group input { width: 100%; box-sizing: border-box; padding: 8px 10px; border: 1px solid #EAD9C2; border-radius: 8px; background: #FAF5EE; font-size: 13px; color: #5A4632; font-family: inherit; outline: none; }
        .form-group .hint { font-size: 11px; color: #A98B6E; margin-top: 3px; }
        .msg { font-size: 12px; margin-top: 8px; padding: 6px 12px; border-radius: 6px; display: none; }
        .msg.error { color: #B0614A; background: #F5E0D8; }
        .msg.ok { color: #5A7A4A; background: #E8F0E5; }
        .empty { text-align: center; padding: 40px; color: #999; }
        .footer { margin-top: 16px; color: #888; font-size: 13px; }
        .stock-out { color: #B0614A; font-weight: bold; }
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
        <a class="nav-item active" href="/admin/products">Products</a>
        <a class="nav-item" href="/admin/rules">Points Rules</a>
        <a class="nav-item" href="/admin/points">Points Adjustment</a>
    </div>
    <div class="content">

    <div class="panel-header">
        <h2>Product Management</h2>
        <button class="btn primary" onclick="openCreateModal()">+ Add Product</button>
    </div>

    <div id="page-msg" class="msg"></div>

    <c:choose>
        <c:when test="${empty products}">
            <div class="empty">No products yet. Click "Add Product" to create one.</div>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                <tr>
                    <th>PID</th><th>Name</th><th>Points Required</th><th>Min Points</th><th>Stock (Left/Total)</th><th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${products}" var="p">
                    <tr data-pid="${fn:escapeXml(p.pid)}" data-name="${fn:escapeXml(p.name)}" data-image="${fn:escapeXml(p.imageUrl)}"
                        data-totalpts="${p.totalPointsRequired}" data-minpts="${p.minPointsRequired}"
                        data-totalstock="${p.totalStock}" data-remaining="${p.remainingStock}">
                        <td><c:out value="${p.pid}"/></td>
                        <td><c:out value="${p.name}"/></td>
                        <td>${p.totalPointsRequired}</td>
                        <td>${p.minPointsRequired}</td>
                        <td>
                            <c:choose>
                                <c:when test="${p.remainingStock > 0}">${p.remainingStock} / ${p.totalStock}</c:when>
                                <c:otherwise><span class="stock-out">Out of stock</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <button class="btn small" onclick="openEditModal(this)">Edit</button>
                            <button class="btn small danger" onclick="deleteProduct(this)">Delete</button>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>

    <div class="footer">
        ${fn:length(products)} products in total &nbsp;|&nbsp;
        <a href="/demo/products">Points Mall</a>
    </div>
    </div>
</div>

<!-- Modal: Add / Edit Product -->
<div class="modal-overlay" id="modal-product">
    <div class="modal">
        <div class="modal-header" id="modal-title">Add Product</div>
        <div class="modal-body">
            <div class="form-group"><label>Product Name*</label><input type="text" id="f-name"></div>
            <div class="form-group"><label>Image URL*</label><input type="text" id="f-imageUrl"></div>
            <div class="form-group"><label>Total Points Required*</label><input type="number" id="f-totalPointsRequired" min="1"></div>
            <div class="form-group"><label>Min Points Required*</label><input type="number" id="f-minPointsRequired" min="1"><div class="hint">Difference can be paid by cash/voucher</div></div>
            <div class="form-group"><label>Total Stock*</label><input type="number" id="f-totalStock" min="1"></div>
            <div class="form-group" id="fg-remainingStock" style="display:none">
                <label>Remaining Stock*</label><input type="number" id="f-remainingStock" min="0"><div class="hint">Can be corrected when editing</div>
            </div>
            <div id="modal-msg" class="msg"></div>
        </div>
        <div class="modal-footer">
            <button class="btn" onclick="closeModal()">Cancel</button>
            <button class="btn primary" onclick="submitProduct()">Save</button>
        </div>
    </div>
</div>

<script>
    /** null = create mode; non-null pid = edit mode (UC6 submitCreate / submitUpdate). */
    let editPid = null;

    function showMsg(id, text, ok) {
        const el = document.getElementById(id);
        el.textContent = text;
        el.className = 'msg ' + (ok ? 'ok' : 'error');
        el.style.display = 'block';
    }

    function clearForm() {
        ['f-name','f-imageUrl','f-totalPointsRequired','f-minPointsRequired','f-totalStock','f-remainingStock']
            .forEach(id => document.getElementById(id).value = '');
    }

    /** UC6 step 3: open the create modal (empty form, remainingStock hidden). */
    function openCreateModal() {
        editPid = null;
        document.getElementById('modal-title').textContent = 'Add Product';
        document.getElementById('fg-remainingStock').style.display = 'none';
        clearForm();
        document.getElementById('modal-msg').style.display = 'none';
        document.getElementById('modal-product').classList.add('show');
    }

    /** UC6 step 5: open the edit modal, pre-filled from the row's data attributes. */
    function openEditModal(btn) {
        const row = btn.closest('tr');
        const d = row.dataset;
        editPid = d.pid;
        document.getElementById('modal-title').textContent = 'Edit Product — ' + d.pid;
        document.getElementById('fg-remainingStock').style.display = 'block';
        document.getElementById('f-name').value = d.name;
        document.getElementById('f-imageUrl').value = d.image;
        document.getElementById('f-totalPointsRequired').value = d.totalpts;
        document.getElementById('f-minPointsRequired').value = d.minpts;
        document.getElementById('f-totalStock').value = d.totalstock;
        document.getElementById('f-remainingStock').value = d.remaining;
        document.getElementById('modal-msg').style.display = 'none';
        document.getElementById('modal-product').classList.add('show');
    }

    function closeModal() {
        document.getElementById('modal-product').classList.remove('show');
    }

    /** Submit create (POST) or update (PUT) to the REST API. */
    function submitProduct() {
        const name = document.getElementById('f-name').value.trim();
        const imageUrl = document.getElementById('f-imageUrl').value.trim();
        const totalPts = parseInt(document.getElementById('f-totalPointsRequired').value);
        const minPts = parseInt(document.getElementById('f-minPointsRequired').value);
        const totalStock = parseInt(document.getElementById('f-totalStock').value);

        if (!name || !imageUrl) { showMsg('modal-msg', 'Product name and image URL are required', false); return; }
        if (isNaN(totalPts) || isNaN(minPts) || isNaN(totalStock)) { showMsg('modal-msg', 'Points and stock must be numbers', false); return; }
        if (minPts > totalPts) { showMsg('modal-msg', 'Min points cannot exceed total points required', false); return; }

        const body = { name: name, imageUrl: imageUrl, totalPointsRequired: totalPts, minPointsRequired: minPts, totalStock: totalStock };
        let url = '/api/admin/products';
        let method = 'POST';
        if (editPid) {
            const remaining = parseInt(document.getElementById('f-remainingStock').value);
            if (isNaN(remaining)) { showMsg('modal-msg', 'Remaining stock must be a number', false); return; }
            if (remaining > totalStock) { showMsg('modal-msg', 'Remaining stock cannot exceed total stock', false); return; }
            url = '/api/admin/products/' + encodeURIComponent(editPid);
            method = 'PUT';
            body.remainingStock = remaining;
        }
        fetch(url, { method: method, headers: {'Content-Type': 'application/json'}, body: JSON.stringify(body) })
            .then(r => r.json().catch(() => { throw new Error('non-json response'); }))
            .then(result => {
                if (result.code === 0) { closeModal(); location.reload(); }
                else { showMsg('modal-msg', result.message, false); }
            })
            .catch(() => showMsg('modal-msg', 'Request failed, please retry', false));
    }

    /** UC6 step 6 + Extension 6a: delete; server returns 409 if pending orders exist. */
    function deleteProduct(btn) {
        const pid = btn.closest('tr').dataset.pid;
        if (!confirm('Confirm delete product ' + pid + '?')) return;
        fetch('/api/admin/products/' + encodeURIComponent(pid), { method: 'DELETE' })
            .then(r => r.json().catch(() => { throw new Error('non-json response'); }))
            .then(result => {
                if (result.code === 0) { location.reload(); }
                else { showMsg('page-msg', result.message, false); }
            })
            .catch(() => showMsg('page-msg', 'Request failed, please retry', false));
    }
</script>
</body>
</html>