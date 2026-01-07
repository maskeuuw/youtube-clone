document.addEventListener('DOMContentLoaded', () => {
    	const videoGrid = document.getElementById('video-grid');
    	if (videoGrid) {
    		videoData.forEach(video => {
    			videoGrid.appendChild(createVideoCard(video));
    		});
    	}

    	// --- Sidebar Toggle Logic ---
    	const sidebar = document.getElementById('sidebarCollapse');
    	const feedContainer = document.getElementById('feedContainer');
    	const lgBreakpoint = 992; // Bootstrap lg breakpoint

    	if (sidebar && feedContainer) {
    		// Initial check: if desktop, start open and apply content offset
    		if (window.innerWidth >= lgBreakpoint) {
    			sidebar.classList.add('show');
    			feedContainer.classList.add('sidebar-open');
    		}

    		// Event listener to control main content margin on desktop
    		sidebar.addEventListener('show.bs.collapse', () => {
    			if (window.innerWidth >= lgBreakpoint) {
    				feedContainer.classList.add('sidebar-open');
    			}
    		});

    		sidebar.addEventListener('hide.bs.collapse', () => {
    			if (window.innerWidth >= lgBreakpoint) {
    				feedContainer.classList.remove('sidebar-open');
    			}
    		});

    		// Handle resize events to maintain correct desktop state
    		let isDesktop = window.innerWidth >= lgBreakpoint;
    		window.addEventListener('resize', () => {
    			const newIsDesktop = window.innerWidth >= lgBreakpoint;
    			if (newIsDesktop !== isDesktop) {
    				if (newIsDesktop) {
    					// Transitioning to Desktop: Ensure sidebar is open and margin is set
    					sidebar.classList.add('show');
    					feedContainer.classList.add('sidebar-open');
    				} else {
    					// Transitioning to Mobile: Ensure sidebar is closed and margin is removed
    					sidebar.classList.remove('show');
    					feedContainer.classList.remove('sidebar-open');
    				}
    				isDesktop = newIsDesktop;
    			}
    		});
    	}
    });