// Virtual Machines JavaScript

let vmList = [];
let hardwareList = [];
let currentFilter = 'ALL';

// Load data on page load
document.addEventListener('DOMContentLoaded', () => {
    loadVMs();
    loadHardwareForDropdown();
});

// Load all VMs
async function loadVMs() {
    try {
        vmList = await API.virtualMachines.getAll();
        hardwareList = await API.hardware.getAll();
        renderVMs();
    } catch (error) {
        console.error('Error loading VMs:', error);
        showToast('Error loading VMs', 'danger');
    }
}

// Load hardware for dropdown
async function loadHardwareForDropdown() {
    try {
        const hardware = await API.hardware.getAll();
        const hardwareSelect = document.getElementById('hardwareId');
        const editHardwareSelect = document.getElementById('editHardwareId');
        
        hardware.forEach(hw => {
            const option = new Option(`${hw.name} (${hw.type})`, hw.id);
            const editOption = new Option(`${hw.name} (${hw.type})`, hw.id);
            hardwareSelect.add(option);
            editHardwareSelect.add(editOption.cloneNode(true));
        });
    } catch (error) {
        console.error('Error loading hardware:', error);
    }
}

// Filter VMs by status
function filterVMs(status) {
    currentFilter = status;
    
    // Update button states
    document.querySelectorAll('.btn-group button').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');
    
    renderVMs();
}

// Render VM cards
function renderVMs() {
    const container = document.getElementById('vmContainer');
    
    // Filter VMs
    let filteredVMs = currentFilter === 'ALL' 
        ? vmList 
        : vmList.filter(vm => vm.status === currentFilter);
    
    if (filteredVMs.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-network-wired"></i>
                <p>No virtual machines found</p>
            </div>
        `;
        return;
    }

    // Group by hardware
    const vmsByHardware = {};
    filteredVMs.forEach(vm => {
        const hardware = hardwareList.find(h => h.id === vm.hardwareId);
        const hardwareName = hardware ? hardware.name : 'Unknown Hardware';
        
        if (!vmsByHardware[hardwareName]) {
            vmsByHardware[hardwareName] = [];
        }
        vmsByHardware[hardwareName].push(vm);
    });

    let html = '';
    
    for (const [hardwareName, vms] of Object.entries(vmsByHardware)) {
        html += `
            <div class="sub-card mb-3" style="background-color: rgb(218, 234, 241);">
                <h6><i class="fas fa-server"></i> ${hardwareName}</h6>
                <div class="cards">
                    ${vms.map(vm => createVMCard(vm)).join('')}
                </div>
            </div>
        `;
    }

    container.innerHTML = html;
}

// Create VM card
function createVMCard(vm) {
    const statusClass = getStatusBadgeClass(vm.status);
    
    return `
        <div class="card red">
            <div class="card" style="width: 85%; height: 85%;">
                <div class="content">
                    <div class="back">
                        <div class="back-content">
                            <img src="images/laptop-user_15594453.gif" alt="VM" width="50%" />
                            <strong>${vm.ipAddress || vm.hostname}</strong>
                        </div>
                    </div>
                    <div class="front">
                        <div class="img">
                            <div class="circle"></div>
                            <div class="circle" id="right"></div>
                            <div class="circle" id="bottom"></div>
                        </div>
                        <div class="front-content">
                            <small class="badge">${vm.name}</small>
                            <div class="description">
                                <div class="title">
                                    <div style="font-size: 8px; text-align: left;">
                                        <strong>Hostname:</strong> ${vm.hostname}<br>
                                        <strong>OS:</strong> ${vm.operatingSystem}<br>
                                        <strong>vCPU:</strong> ${vm.vcpu} cores<br>
                                        <strong>vRAM:</strong> ${vm.vram} GB<br>
                                        <strong>Disk:</strong> ${vm.diskSize} GB<br>
                                        <strong>Status:</strong> <span class="status-badge ${statusClass}">${vm.status}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <button class="btn btn-sm btn-success mt-2" onclick="editVMModal(${vm.id})">
                <i class="fas fa-edit"></i> Edit
            </button>
        </div>
    `;
}

// Add VM
async function addVM() {
    const hardwareIdValue = document.getElementById('hardwareId').value;
    
    if (!hardwareIdValue || hardwareIdValue === '') {
        showToast('Please select a hardware host', 'danger');
        return;
    }
    
    const data = {
        name: document.getElementById('name').value,
        hostname: document.getElementById('hostname').value,
        ipAddress: document.getElementById('ipAddress').value,
        operatingSystem: document.getElementById('operatingSystem').value,
        vcpu: parseInt(document.getElementById('vcpu').value),
        vram: parseInt(document.getElementById('vram').value),
        diskSize: parseInt(document.getElementById('diskSize').value),
        status: document.getElementById('status').value,
        hardwareId: parseInt(hardwareIdValue)
    };

    try {
        await API.virtualMachines.create(data);
        showToast('VM added successfully', 'success');
        bootstrap.Modal.getInstance(document.getElementById('addVMModal')).hide();
        document.getElementById('addVMForm').reset();
        loadVMs();
    } catch (error) {
        console.error('Error adding VM:', error);
        showToast('Error adding VM: ' + (error.message || 'Unknown error'), 'danger');
    }
}

// Edit VM modal
async function editVMModal(id) {
    try {
        const vm = await API.virtualMachines.getById(id);
        
        document.getElementById('editId').value = vm.id;
        document.getElementById('editName').value = vm.name;
        document.getElementById('editHostname').value = vm.hostname;
        document.getElementById('editIpAddress').value = vm.ipAddress || '';
        document.getElementById('editOperatingSystem').value = vm.operatingSystem;
        document.getElementById('editVcpu').value = vm.vcpu;
        document.getElementById('editVram').value = vm.vram;
        document.getElementById('editDiskSize').value = vm.diskSize;
        document.getElementById('editStatus').value = vm.status;
        document.getElementById('editHardwareId').value = vm.hardwareId;
        
        new bootstrap.Modal(document.getElementById('editVMModal')).show();
    } catch (error) {
        console.error('Error loading VM details:', error);
        showToast('Error loading VM details', 'danger');
    }
}

// Update VM
async function updateVM() {
    const id = document.getElementById('editId').value;
    const hardwareIdValue = document.getElementById('editHardwareId').value;
    
    if (!hardwareIdValue || hardwareIdValue === '') {
        showToast('Please select a hardware host', 'danger');
        return;
    }
    
    const data = {
        name: document.getElementById('editName').value,
        hostname: document.getElementById('editHostname').value,
        ipAddress: document.getElementById('editIpAddress').value,
        operatingSystem: document.getElementById('editOperatingSystem').value,
        vcpu: parseInt(document.getElementById('editVcpu').value),
        vram: parseInt(document.getElementById('editVram').value),
        diskSize: parseInt(document.getElementById('editDiskSize').value),
        status: document.getElementById('editStatus').value,
        hardwareId: parseInt(hardwareIdValue)
    };

    try {
        await API.virtualMachines.update(id, data);
        showToast('VM updated successfully', 'success');
        bootstrap.Modal.getInstance(document.getElementById('editVMModal')).hide();
        loadVMs();
    } catch (error) {
        console.error('Error updating VM:', error);
        showToast('Error updating VM: ' + (error.message || 'Unknown error'), 'danger');
    }
}

// Delete VM
async function deleteVM() {
    if (!confirm('Are you sure you want to delete this VM?')) {
        return;
    }

    const id = document.getElementById('editId').value;

    try {
        await API.virtualMachines.delete(id);
        showToast('VM deleted successfully', 'success');
        bootstrap.Modal.getInstance(document.getElementById('editVMModal')).hide();
        loadVMs();
    } catch (error) {
        console.error('Error deleting VM:', error);
        showToast('Error deleting VM', 'danger');
    }
}

