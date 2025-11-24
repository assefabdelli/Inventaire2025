// Sites JavaScript

let sitesList = [];

// Load data on page load
document.addEventListener('DOMContentLoaded', () => {
    // Check authentication
    if (!checkAuth()) return;
    
    // Update nav user info
    updateNavUserInfo();
    hideRestrictedNavItems();
    
    loadSites();
});

// Load all sites
async function loadSites() {
    try {
        sitesList = await API.sites.getAll();
        renderSites();
    } catch (error) {
        console.error('Error loading sites:', error);
        showToast('Error loading sites', 'danger');
    }
}

// Render sites
function renderSites() {
    const container = document.getElementById('sitesContainer');
    
    if (sitesList.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-building"></i>
                <p>No sites found</p>
            </div>
        `;
        return;
    }

    container.innerHTML = `
        <div class="row">
            ${sitesList.map(site => createSiteCard(site)).join('')}
        </div>
    `;
}

// Create site card
function createSiteCard(site) {
    return `
        <div class="col-md-6 col-lg-4 mb-3">
            <div class="sub-card h-100">
                <div class="d-flex justify-content-between align-items-start mb-3">
                    <h5><i class="fas fa-building text-primary"></i> ${site.name}</h5>
                    <span class="badge bg-primary">${site.code}</span>
                </div>
                <div class="text-start">
                    <p class="mb-2">
                        <strong><i class="fas fa-map-marker-alt"></i> Location:</strong><br>
                        ${site.address || 'N/A'}<br>
                        ${site.city || ''} ${site.country || ''}
                    </p>
                    <p class="mb-2">
                        <strong><i class="fas fa-user"></i> Contact:</strong><br>
                        ${site.contactPerson || 'N/A'}
                    </p>
                    ${site.contactEmail ? `
                        <p class="mb-2">
                            <strong><i class="fas fa-envelope"></i> Email:</strong><br>
                            <a href="mailto:${site.contactEmail}">${site.contactEmail}</a>
                        </p>
                    ` : ''}
                    ${site.contactPhone ? `
                        <p class="mb-2">
                            <strong><i class="fas fa-phone"></i> Phone:</strong><br>
                            ${site.contactPhone}
                        </p>
                    ` : ''}
                </div>
                <button class="btn btn-sm btn-primary mt-3 w-100" onclick="editSiteModal(${site.id})">
                    <i class="fas fa-edit"></i> Edit Site
                </button>
            </div>
        </div>
    `;
}

// Add site
async function addSite() {
    const data = {
        name: document.getElementById('name').value,
        code: document.getElementById('code').value,
        address: document.getElementById('address').value,
        city: document.getElementById('city').value,
        country: document.getElementById('country').value,
        contactPerson: document.getElementById('contactPerson').value,
        contactEmail: document.getElementById('contactEmail').value,
        contactPhone: document.getElementById('contactPhone').value
    };

    try {
        await API.sites.create(data);
        showToast('Site added successfully', 'success');
        bootstrap.Modal.getInstance(document.getElementById('addSiteModal')).hide();
        document.getElementById('addSiteForm').reset();
        loadSites();
    } catch (error) {
        console.error('Error adding site:', error);
        showToast('Error adding site', 'danger');
    }
}

// Edit site modal
async function editSiteModal(id) {
    try {
        const site = await API.sites.getById(id);
        
        document.getElementById('editId').value = site.id;
        document.getElementById('editName').value = site.name;
        document.getElementById('editCode').value = site.code;
        document.getElementById('editAddress').value = site.address || '';
        document.getElementById('editCity').value = site.city || '';
        document.getElementById('editCountry').value = site.country || '';
        document.getElementById('editContactPerson').value = site.contactPerson || '';
        document.getElementById('editContactEmail').value = site.contactEmail || '';
        document.getElementById('editContactPhone').value = site.contactPhone || '';
        
        new bootstrap.Modal(document.getElementById('editSiteModal')).show();
    } catch (error) {
        console.error('Error loading site details:', error);
        showToast('Error loading site details', 'danger');
    }
}

// Update site
async function updateSite() {
    const id = document.getElementById('editId').value;
    const data = {
        name: document.getElementById('editName').value,
        code: document.getElementById('editCode').value,
        address: document.getElementById('editAddress').value,
        city: document.getElementById('editCity').value,
        country: document.getElementById('editCountry').value,
        contactPerson: document.getElementById('editContactPerson').value,
        contactEmail: document.getElementById('editContactEmail').value,
        contactPhone: document.getElementById('editContactPhone').value
    };

    try {
        await API.sites.update(id, data);
        showToast('Site updated successfully', 'success');
        bootstrap.Modal.getInstance(document.getElementById('editSiteModal')).hide();
        loadSites();
    } catch (error) {
        console.error('Error updating site:', error);
        showToast('Error updating site', 'danger');
    }
}

// Delete site
async function deleteSite() {
    if (!confirm('Are you sure you want to delete this site? This may affect associated hardware.')) {
        return;
    }

    const id = document.getElementById('editId').value;

    try {
        await API.sites.delete(id);
        showToast('Site deleted successfully', 'success');
        bootstrap.Modal.getInstance(document.getElementById('editSiteModal')).hide();
        loadSites();
    } catch (error) {
        console.error('Error deleting site:', error);
        showToast('Error deleting site. It may be in use.', 'danger');
    }
}

