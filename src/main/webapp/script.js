const videoData = [
	{ title: "Building a Website with Bootstrap 5: A Complete Guide for Beginners", channel: "Web Master", views: "450K", timeAgo: "2 days ago", duration: "12:30", thumbnail: "Bootstrap+Tutorial" },
	{ title: "Review of the Newest Smartphone: Is It Worth the Upgrade?", channel: "Tech Reviewer", views: "1.8M", timeAgo: "1 week ago", duration: "25:15", thumbnail: "Phone+Review" },
	{ title: "10 Simple Home Decorating Ideas on a Budget", channel: "Design Ideas", views: "88K", timeAgo: "4 hours ago", duration: "8:09", thumbnail: "Home+Decor" },
	{ title: "Easy Chicken Curry Recipe That Takes 20 Minutes", channel: "Quick Meals", views: "3.2M", timeAgo: "3 months ago", duration: "6:55", thumbnail: "Cooking+Recipe" },
	{ title: "The Physics of Black Holes Explained Simply", channel: "Cosmos Simplified", views: "750K", timeAgo: "5 days ago", duration: "15:40", thumbnail: "Physics+Science" },
	{ title: "LIVE: 24/7 Relaxing Ambient Study Music", channel: "Focus Sounds", views: "15M", timeAgo: "Live", duration: "LIVE", thumbnail: "Study+Music" },
	{ title: "Understanding JavaScript Asynchronous Programming", channel: "JS Academy", views: "912K", timeAgo: "1 month ago", duration: "48:00", thumbnail: "JavaScript+Code" },
	{ title: "Top PC Games You Need to Play in 2024", channel: "Pro Gamer", views: "2.1M", timeAgo: "1 day ago", duration: "10:11", thumbnail: "Top+Games" },
];

function createVideoCard(video) {
	const { title, channel, views, timeAgo, duration, thumbnail } = video;

	const colDiv = document.createElement('div');
	colDiv.className = 'col';
	colDiv.innerHTML = `
                <div class="video-card cursor-pointer">
                    <!-- Thumbnail -->
                    <div class="video-card-img-container">
                        <img class="video-card-img" 
                             src="https://placehold.co/480x270/FF0000/FFFFFF?text=${thumbnail}" 
                             alt="Video thumbnail: ${title}" 
                             onerror="this.src='https://placehold.co/480x270/CCCCCC/333333?text=Video+Not+Found';">
                        <div class="duration-overlay">${duration}</div>
                    </div>
                    
                    <!-- Video Info -->
                    <div class="d-flex align-items-start">
                        <div class="channel-avatar flex-shrink-0"></div>
                        <div class="text-content">
                            <h2 class="video-title mb-1" title="${title}">${title}</h2>
                            <p class="video-meta mb-0">${channel}</p>
                            <p class="video-meta mb-0">${views} views &bull; ${timeAgo}</p>
                        </div>
                    </div>
                </div>
            `;
	return colDiv;
}

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

