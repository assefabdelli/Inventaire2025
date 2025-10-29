# Inventory VM Frontend

## Overview
A modern web interface for managing virtual machines, hardware, sites, and deployment tasks. Built with HTML5, CSS3, JavaScript, and Bootstrap 5.

## Features

### Dashboard
- Real-time statistics of hardware, VMs, sites, and active tasks
- Quick action buttons for easy navigation
- Recent deployment tasks overview

### Hardware Management
- **Animated flip cards** displaying server information
- Filter by type (Server, Storage, Network)
- Organized by site
- Add, edit, and delete hardware
- Display specifications: CPU, RAM, Storage, Serial Number, IP Address

### Virtual Machines
- Beautiful animated cards showing VM details
- Filter by status (Running, Stopped, Suspended)
- Grouped by host hardware
- Full CRUD operations
- Track vCPU, vRAM, disk size, and OS information

### Sites Management
- Manage data center locations
- Contact information
- Address and location details
- Link hardware to specific sites

### Deployment Tasks
- Create and track deployment tasks
- Assign tasks to users
- Link tasks to virtual machines
- Filter by status (Pending, In Progress, Completed, Failed)
- Schedule deployment dates

## Technologies Used

- **HTML5** - Structure
- **CSS3** - Modern styling with animations
- **JavaScript (ES6+)** - Interactive functionality
- **Bootstrap 5** - Responsive UI framework
- **Font Awesome** - Icons
- **Fetch API** - Backend communication

## File Structure

```
src/main/resources/static/
├── index.html                    # Dashboard
├── hardware.html                 # Hardware management
├── virtual-machines.html         # VM management
├── sites.html                    # Sites management
├── deployment-tasks.html         # Deployment tasks
├── css/
│   └── style.css                 # Main stylesheet with animations
├── js/
│   ├── api.js                    # API wrapper functions
│   ├── dashboard.js              # Dashboard logic
│   ├── hardware.js               # Hardware management
│   ├── virtual-machines.js       # VM management
│   ├── sites.js                  # Sites management
│   └── deployment-tasks.js       # Deployment tasks
└── images/                       # Static images and icons
```

## API Endpoints

The frontend communicates with the backend through REST APIs:

- **Hardware**: `/api/hardware`
- **Virtual Machines**: `/api/virtual-machines`
- **Sites**: `/api/sites`
- **Deployment Tasks**: `/api/deployment-tasks`
- **Users**: `/api/users`

## How to Run

1. **Start the Spring Boot backend**:
   ```bash
   mvn spring-boot:run
   ```

2. **Access the application**:
   Open your browser and navigate to:
   ```
   http://localhost:8080
   ```

3. **Navigate through the interface**:
   - Dashboard: Overview of all resources
   - Hardware: Manage physical servers and infrastructure
   - Virtual Machines: Manage VMs
   - Sites: Manage data center locations
   - Deployments: Track deployment tasks

## Features Highlight

### Animated Flip Cards
The hardware and VM pages feature beautiful 3D flip cards that show:
- **Front**: Basic information and status
- **Back**: Image and IP/hostname
- Smooth hover animations
- Responsive design

### Status Indicators
Color-coded status badges for quick identification:
- **Green**: Active/Running/Completed
- **Orange**: Maintenance/Suspended/In Progress
- **Red**: Inactive/Failed
- **Blue**: Pending

### Responsive Design
- Mobile-friendly navigation
- Adaptive layouts
- Touch-friendly interfaces
- Optimized for all screen sizes

## Customization

### Colors
Edit `css/style.css` to modify the color scheme:
- Primary color: `#007BFF`
- Success: `#18cd5e`
- Warning: `#ffbb66`
- Danger: `#ff2233`

### Images
Replace images in `static/images/` to customize:
- Server icons
- VM icons
- Logo (`ns.png`)
- Background images

## Browser Support

- Chrome (recommended)
- Firefox
- Edge
- Safari
- Opera

## Tips

1. **First Time Setup**:
   - Add sites first
   - Then add hardware linked to sites
   - Create VMs on hardware
   - Finally, create deployment tasks

2. **Data Persistence**:
   - All data is stored in MySQL database
   - Changes are reflected immediately
   - No page refresh needed for most operations

3. **Keyboard Shortcuts**:
   - ESC: Close modals
   - Enter: Submit forms (when focused)

## Troubleshooting

### API Connection Issues
- Ensure backend is running on port 8080
- Check browser console for errors
- Verify database connection

### Images Not Loading
- Check `static/images/` folder exists
- Verify image file names match references
- Clear browser cache

### Modal Not Opening
- Check Bootstrap JS is loaded
- Verify no JavaScript errors in console
- Try refreshing the page

## Future Enhancements

- User authentication and authorization
- Real-time notifications
- Export data to Excel/PDF
- Advanced search and filtering
- Dashboard charts and graphs
- Drag-and-drop task management
- Dark mode toggle

## Credits

Design inspired by modern infrastructure management tools with custom animations and interactions.

