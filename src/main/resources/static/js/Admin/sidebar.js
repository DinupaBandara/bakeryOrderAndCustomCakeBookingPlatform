document.addEventListener("DOMContentLoaded", function() {
    const sidebar = document.getElementById('sidebar');
    const toggleIcon = document.getElementById('toggleIcon');
    const overlay = document.getElementById('sidebarOverlay');

    // 1. Check localStorage on page load
    const isCollapsed = localStorage.getItem('sidebar-collapsed') === 'true';

    // 2. Apply the saved state immediately
    if (isCollapsed) {
        sidebar.classList.add('collapsed');
        if (toggleIcon) toggleIcon.innerText = 'menu';
        // Ensure overlay is hidden if it was saved as collapsed
        if (overlay) overlay.style.display = 'none';
    } else {
        if (toggleIcon) toggleIcon.innerText = 'menu_open';
        // If expanded on mobile, show the overlay on load
        if (window.innerWidth <= 850 && overlay) {
            overlay.style.display = 'block';
        }
    }

    // 3. Define the toggle function
    window.toggleSidebar = function() {
        sidebar.classList.toggle('collapsed');
        const nowCollapsed = sidebar.classList.contains('collapsed');
        const isMobile = window.innerWidth <= 850;

        if (overlay && isMobile) {
            // Only show overlay if the sidebar is NOT collapsed
            overlay.style.display = nowCollapsed ? 'none' : 'block';
        }

        // Update Overlay (New mobile feature)
        if (overlay) {
            if (isMobile) {
                overlay.style.display = nowCollapsed ? 'none' : 'block';
            } else {
                overlay.style.display = 'none';
            }
        }

        // Save the current state (Your original feature)
        localStorage.setItem('sidebar-collapsed', nowCollapsed);
    };

    // 4. Optional: Close sidebar if window is resized above mobile break-point
    window.addEventListener('resize', function() {
        if (window.innerWidth > 850 && overlay) {
            overlay.style.display = 'none';
        } else if (window.innerWidth <= 850 && !sidebar.classList.contains('collapsed') && overlay) {
            overlay.style.display = 'block';
        }
    });
});