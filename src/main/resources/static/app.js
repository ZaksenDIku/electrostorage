const api = '/api';
let allComponents = [];
let currentOrderId = null;

document.querySelectorAll('nav button').forEach(button =>
    button.addEventListener('click', () => showPage(button.dataset.page)));
document.querySelector('#back-to-orders').addEventListener('click', () => showPage('orders'));

async function request(url, options = {}) {
    const response = await fetch(url, { headers: {'Content-Type': 'application/json'}, ...options });
    if (!response.ok) {
        const body = await response.json().catch(() => ({}));
        throw new Error(body.error || 'Der opstod en fejl');
    }
    return response.status === 204 ? null : response.json();
}

function message(text, error = false) {
    const box = document.querySelector('#message');
    box.textContent = text; box.hidden = false; box.classList.toggle('error', error);
    setTimeout(() => box.hidden = true, 3500);
}

async function showPage(id) {
    document.querySelectorAll('.page').forEach(page => page.hidden = page.id !== id);
    try {
        if (id === 'components') await loadComponents();
        if (id === 'orders') await loadOrders();
        if (id === 'inventory') await loadInventory();
        if (id === 'assemblies') await loadAssemblies();
    } catch (error) { message(error.message, true); }
}

async function loadSuppliers() {
    const suppliers = await request(`${api}/suppliers`);
    const options = suppliers.map(s => `<option value="${s.id}">${s.name}</option>`).join('');
    document.querySelector('#component-supplier').innerHTML = options;
    document.querySelector('#order-supplier').innerHTML = options;
}

async function loadComponents() {
    allComponents = await request(`${api}/components`);
    const rows = allComponents.map(c => `<tr class="${c.discontinued ? 'discontinued' : ''}">
        <td>${c.internalNumber}</td><td>${c.name}</td><td>${c.externalNumber || '-'}</td>
        <td>${c.supplier?.name || 'Samles'}</td><td>${c.discontinued ? 'Ja' : 'Nej'}</td>
        <td>${!c.discontinued && c.orderable ? `<button class="danger" onclick="discontinue(${c.internalNumber})">Markér udgået</button>` : ''}</td>
    </tr>`).join('');
    document.querySelector('#component-list').innerHTML = `<table><thead><tr><th>Nr.</th><th>Navn</th><th>Varenr.</th><th>Leverandør</th><th>Udgået</th><th></th></tr></thead><tbody>${rows}</tbody></table>`;
}

document.querySelector('#component-form').addEventListener('submit', async event => {
    event.preventDefault();
    const data = Object.fromEntries(new FormData(event.target));
    data.internalNumber = Number(data.internalNumber); data.supplierId = Number(data.supplierId);
    try {
        await request(`${api}/components`, {method: 'POST', body: JSON.stringify(data)});
        event.target.reset(); await loadComponents(); message('Komponenten blev tilføjet');
    } catch (error) { message(error.message, true); }
});

async function discontinue(number) {
    if (!confirm('Er du sikker på, at komponenten skal markeres som udgået?')) return;
    try { await request(`${api}/components/${number}/discontinue`, {method: 'PATCH'}); await loadComponents(); message('Komponenten er markeret som udgået'); }
    catch (error) { message(error.message, true); }
}

async function loadOrders() {
    const orders = await request(`${api}/orders`);
    document.querySelector('#order-list').innerHTML = orders.map(order => `<div class="card">
        <h3>Ordre ${order.id} – ${order.supplier.name}</h3>
        <p>Status: ${order.sent ? 'Sendt ' + order.sentDate : 'Under oprettelse'} · ${order.lines.length} varelinjer</p>
        ${!order.sent ? `<button onclick="openOrder(${order.id})">Åbn og tilføj varer</button>` : ''}
        ${order.sent ? `<button onclick="receiveOrder(${order.id})">Markér modtaget</button>` : ''}
    </div>`).join('') || '<p>Der er ingen åbne ordrer.</p>';
}

document.querySelector('#order-form').addEventListener('submit', async event => {
    event.preventDefault();
    const supplierId = Number(new FormData(event.target).get('supplierId'));
    try { const order = await request(`${api}/orders`, {method:'POST', body:JSON.stringify({supplierId})}); message('Ordren blev oprettet'); await openOrder(order.id); }
    catch (error) { message(error.message, true); }
});

async function openOrder(id) {
    currentOrderId = id;
    try {
        const order = await request(`${api}/orders/${id}`);
        document.querySelectorAll('.page').forEach(page => page.hidden = page.id !== 'order-detail');
        document.querySelector('#order-number').textContent = order.id;
        document.querySelector('#order-info').innerHTML = `<div class="card"><p><b>Leverandør:</b> ${order.supplier.name}</p>
            <p><b>Varer:</b></p><ul>${order.lines.map(line => `<li>${line.quantity} stk. ${line.component.name}</li>`).join('') || '<li>Ingen varer endnu</li>'}</ul></div>`;
        const choices = allComponents.filter(c => c.orderable && !c.discontinued && c.supplier?.id === order.supplier.id);
        document.querySelector('#line-component').innerHTML = choices.map(c => `<option value="${c.internalNumber}">${c.name}</option>`).join('');
        document.querySelector('#line-form').hidden = order.sent;
        document.querySelector('#send-form').hidden = order.sent;
    } catch (error) { message(error.message, true); }
}

document.querySelector('#line-form').addEventListener('submit', async event => {
    event.preventDefault(); const data = Object.fromEntries(new FormData(event.target));
    data.componentNumber = Number(data.componentNumber); data.quantity = Number(data.quantity);
    try { await request(`${api}/orders/${currentOrderId}/lines`, {method:'POST', body:JSON.stringify(data)}); await openOrder(currentOrderId); message('Varen blev tilføjet'); }
    catch (error) { message(error.message, true); }
});

document.querySelector('#send-form').addEventListener('submit', async event => {
    event.preventDefault(); const data = Object.fromEntries(new FormData(event.target));
    try { await request(`${api}/orders/${currentOrderId}/send`, {method:'PATCH', body:JSON.stringify(data)}); message('Ordren er sendt'); await showPage('orders'); }
    catch (error) { message(error.message, true); }
});

async function receiveOrder(id) {
    if (!confirm('Er ordren modtaget?')) return;
    try { await request(`${api}/orders/${id}/receive`, {method:'PATCH'}); await loadOrders(); message('Ordren er markeret som modtaget'); }
    catch (error) { message(error.message, true); }
}

async function loadInventory() {
    const items = await request(`${api}/inventory`);
    document.querySelector('#inventory-list').innerHTML = items.map(item => `<div class="card">
        <h3>${item.component.name}</h3><p>Modtaget i alt: ${item.receivedQuantity}</p>
        <p>Seneste optælling: ${item.latestCount ? `${item.latestCount.quantity} stk. af ${item.latestCount.countedBy}` : 'Ikke optalt'}</p>
        <form onsubmit="submitCount(event, ${item.component.internalNumber})"><input name="countedBy" placeholder="Dit navn" required>
        <input name="quantity" type="number" min="0" placeholder="Antal" required><button>Gem optælling</button></form>
    </div>`).join('') || '<p>Der er endnu ikke modtaget varer.</p>';
}

async function submitCount(event, number) {
    event.preventDefault(); const data = Object.fromEntries(new FormData(event.target)); data.quantity = Number(data.quantity);
    try { await request(`${api}/inventory/${number}/counts`, {method:'POST', body:JSON.stringify(data)}); await loadInventory(); message('Optællingen blev gemt'); }
    catch (error) { message(error.message, true); }
}

async function loadAssemblies() {
    const assemblies = await request(`${api}/assemblies`);
    document.querySelector('#assembly-list').innerHTML = assemblies.map(a => `<div class="card"><h3>${a.resultComponent.name}</h3>
        <ul>${a.lines.map(line => `<li>${line.quantity} stk. ${line.component.name}</li>`).join('')}</ul></div>`).join('');
}

loadSuppliers().then(loadComponents).catch(error => message(error.message, true));
