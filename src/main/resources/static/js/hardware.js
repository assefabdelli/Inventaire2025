// Hardware JavaScript

let hardwareList = [];
let sitesList = [];
let currentFilter = 'ALL';

// Load data on page load
document.addEventListener('DOMContentLoaded', () => {
    loadHardware();
    loadSitesForDropdown();
});

// Load all hardware
async function loadHardware() {
    try {
        hardwareList = await API.hardware.getAll();
        sitesList = await API.sites.getAll();
        renderHardware();
    } catch (error) {
        console.error('Error loading hardware:', error);
        showToast('Error loading hardware', 'danger');
    }
}

// Load sites for dropdown
async function loadSitesForDropdown() {
    try {
        const sites = await API.sites.getAll();
        const siteSelect = document.getElementById('siteId');
        const editSiteSelect = document.getElementById('editSiteId');
        
        sites.forEach(site => {
            const option = new Option(site.name, site.id);
            const editOption = new Option(site.name, site.id);
            siteSelect.add(option);
            editSiteSelect.add(editOption.cloneNode(true));
        });
    } catch (error) {
        console.error('Error loading sites:', error);
    }
}

// Filter hardware by type
function filterHardware(type) {
    currentFilter = type;
    
    // Update button states
    document.querySelectorAll('.btn-group button').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');
    
    renderHardware();
}

// Render hardware cards
function renderHardware() {
    const container = document.getElementById('hardwareContainer');
    
    // Filter hardware
    let filteredHardware = currentFilter === 'ALL' 
        ? hardwareList 
        : hardwareList.filter(h => h.type === currentFilter);
    
    if (filteredHardware.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-server"></i>
                <p>No hardware found</p>
            </div>
        `;
        return;
    }

    // Group by site
    const hardwareBySite = {};
    filteredHardware.forEach(hardware => {
        const site = sitesList.find(s => s.id === hardware.siteId);
        const siteName = site ? site.name : 'Unknown Site';
        
        if (!hardwareBySite[siteName]) {
            hardwareBySite[siteName] = [];
        }
        hardwareBySite[siteName].push(hardware);
    });

    let html = '';
    
    for (const [siteName, hardwareItems] of Object.entries(hardwareBySite)) {
        html += `
            <div class="sub-card mb-3" style="background-color: rgb(218, 234, 241);">
                <h6><i class="fas fa-building"></i> ${siteName}</h6>
                <div class="cards">
                    ${hardwareItems.map(hardware => createHardwareCard(hardware)).join('')}
                </div>
            </div>
        `;
    }

    container.innerHTML = html;
}

// Create hardware card
function createHardwareCard(hardware) {
    const statusClass = getStatusBadgeClass(hardware.status);
    const image = getTypeImage(hardware.type);
    
    return `
        <div class="card red">
            <div class="card" style="width: 85%; height: 85%;">
                <div class="content">
                    <div class="back">
                        <div class="back-content">
                            <img src="${image}" alt="${hardware.type}" width="50%" />
                            <strong>${hardware.ipAddress || 'No IP'}</strong>
                        </div>
                    </div>
                    <div class="front">
                        <div class="img">
                            <div class="circle"></div>
                            <div class="circle" id="right"></div>
                            <div class="circle" id="bottom"></div>
                        </div>
                        <div class="front-content">
                            <small class="badge">${hardware.name}</small>
                            <div class="description">
                                <div class="title">
                                    <div style="font-size: 8px; text-align: left;">
                                        <strong>Model:</strong> ${hardware.model}<br>
                                        <strong>CPU:</strong> ${hardware.cpuCores || 'N/A'} cores<br>
                                        <strong>RAM:</strong> ${hardware.ramGb || 'N/A'} GB<br>
                                        <strong>Storage:</strong> ${hardware.storageGb || 'N/A'} GB<br>
                                        <strong>SN:</strong> ${hardware.serialNumber}<br>
                                        <strong>Status:</strong> <span class="status-badge ${statusClass}">${hardware.status}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <button class="btn btn-sm btn-primary mt-2" onclick="editHardwareModal(${hardware.id})">
                <i class="fas fa-edit"></i> Edit
            </button>
        </div>
    `;
}

// Add hardware
async function addHardware() {
    const siteIdValue = document.getElementById('siteId').value;
    
    if (!siteIdValue || siteIdValue === '') {
        showToast('Please select a site', 'danger');
        return;
    }
    
    const data = {
        name: document.getElementById('name').value,
        type: document.getElementById('type').value,
        model: document.getElementById('model').value,
        serialNumber: document.getElementById('serialNumber').value,
        ipAddress: document.getElementById('ipAddress').value,
        cpuCores: parseInt(document.getElementById('cpuCores').value) || null,
        ramGb: parseInt(document.getElementById('ramGb').value) || null,
        storageGb: parseInt(document.getElementById('storageGb').value) || null,
        status: document.getElementById('status').value,
        purchaseDate: document.getElementById('purchaseDate').value,
        warrantyEndDate: document.getElementById('warrantyEndDate').value,
        siteId: parseInt(siteIdValue)
    };

    try {
        await API.hardware.create(data);
        showToast('Hardware added successfully', 'success');
        bootstrap.Modal.getInstance(document.getElementById('addHardwareModal')).hide();
        document.getElementById('addHardwareForm').reset();
        loadHardware();
    } catch (error) {
        console.error('Error adding hardware:', error);
        showToast('Error adding hardware: ' + (error.message || 'Unknown error'), 'danger');
    }
}

// Edit hardware modal
async function editHardwareModal(id) {
    try {
        const hardware = await API.hardware.getById(id);
        
        document.getElementById('editId').value = hardware.id;
        document.getElementById('editName').value = hardware.name;
        document.getElementById('editType').value = hardware.type;
        document.getElementById('editModel').value = hardware.model;
        document.getElementById('editSerialNumber').value = hardware.serialNumber;
        document.getElementById('editIpAddress').value = hardware.ipAddress || '';
        document.getElementById('editCpuCores').value = hardware.cpuCores || '';
        document.getElementById('editRamGb').value = hardware.ramGb || '';
        document.getElementById('editStorageGb').value = hardware.storageGb || '';
        document.getElementById('editStatus').value = hardware.status;
        document.getElementById('editPurchaseDate').value = hardware.purchaseDate || '';
        document.getElementById('editWarrantyEndDate').value = hardware.warrantyEndDate || '';
        document.getElementById('editSiteId').value = hardware.siteId;
        
        new bootstrap.Modal(document.getElementById('editHardwareModal')).show();
    } catch (error) {
        console.error('Error loading hardware details:', error);
        showToast('Error loading hardware details', 'danger');
    }
}

// Update hardware
async function updateHardware() {
    const id = document.getElementById('editId').value;
    const data = {
        name: document.getElementById('editName').value,
        type: document.getElementById('editType').value,
        model: document.getElementById('editModel').value,
        serialNumber: document.getElementById('editSerialNumber').value,
        ipAddress: document.getElementById('editIpAddress').value,
        cpuCores: parseInt(document.getElementById('editCpuCores').value) || null,
        ramGb: parseInt(document.getElementById('editRamGb').value) || null,
        storageGb: parseInt(document.getElementById('editStorageGb').value) || null,
        status: document.getElementById('editStatus').value,
        purchaseDate: document.getElementById('editPurchaseDate').value,
        warrantyEndDate: document.getElementById('editWarrantyEndDate').value,
        siteId: parseInt(document.getElementById('editSiteId').value)
    };

    try {
        await API.hardware.update(id, data);
        showToast('Hardware updated successfully', 'success');
        bootstrap.Modal.getInstance(document.getElementById('editHardwareModal')).hide();
        loadHardware();
    } catch (error) {
        console.error('Error updating hardware:', error);
        showToast('Error updating hardware', 'danger');
    }
}

// Delete hardware
async function deleteHardware() {
    if (!confirm('Are you sure you want to delete this hardware?')) {
        return;
    }

    const id = document.getElementById('editId').value;

    try {
        await API.hardware.delete(id);
        showToast('Hardware deleted successfully', 'success');
        bootstrap.Modal.getInstance(document.getElementById('editHardwareModal')).hide();
        loadHardware();
    } catch (error) {
        console.error('Error deleting hardware:', error);
        showToast('Error deleting hardware', 'danger');
    }
}

