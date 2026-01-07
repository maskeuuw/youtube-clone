<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<!-- LEFT SIDEBAR (The Guide) - Now Collapsible -->
	<aside class="yt-sidebar collapse" id="sidebarCollapse">
		<div class="pb-2 border-bottom">
			<a class="sidebar-item active text-decoration-none text-dark" href="home">
				<svg class="me-4" width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
					<path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z" />
				</svg> Home
			</a>
			
			
			<!-- Conditionally show Subscriptions only for logged-in users -->
			<%
				// Check if user is logged in based on your login servlet
				Object currentUser = session.getAttribute("currentUser");
				String userName = (String) session.getAttribute("userName");
				Integer userId = (Integer) session.getAttribute("userId");
				Integer channelId = (Integer) session.getAttribute("channelId");
				
				if (currentUser != null) {
			%>
				<a class="sidebar-item text-decoration-none text-dark" href="subscriptions">
					<svg class="me-4" width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
						<path d="M10 16.5l6-4.5-6-4.5v9zM19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V5h14v14z" />
					</svg> Subscriptions
				</a>
			<%
				}
			%>
		</div>

		
		<!-- PLAYLIST SECTION - Show only when logged in -->
		<%
			if (currentUser != null) {
		%>
			<div class="pb-2 border-bottom mt-2">
				<p class="sidebar-title text-muted fw-bold text-uppercase px-4 mb-1">Playlists</p>
				<a class="sidebar-item text-decoration-none text-dark" href="liked-videos">
					<svg class="me-4" width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
						<path d="M1 21h4V9H1v12zm22-11c0-1.1-.9-2-2-2h-6.31l.95-4.57.03-.32c0-.41-.17-.79-.44-1.06L14.17 1 7.59 7.59C7.22 7.95 7 8.45 7 9v10c0 1.1.9 2 2 2h9c.83 0 1.54-.5 1.84-1.22l3.02-7.05c.09-.23.14-.47.14-.73v-2z"/>
					</svg> Liked videos
				</a>
				<a class="sidebar-item text-decoration-none text-dark" href="watch-later">
					<svg class="me-4" width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
						<path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z"/>
					</svg> Watch later
				</a>
				<a class="sidebar-item text-decoration-none text-dark" href="history">
					<svg class="me-4" width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
						<path d="M13 3c-4.97 0-9 4.03-9 9H1l3.89 3.89.07.14L9 12H6c0-3.87 3.13-7 7-7s7 3.13 7 7-3.13 7-7 7c-1.93 0-3.68-.79-4.94-2.06l-1.42 1.42C8.27 19.99 10.51 21 13 21c4.97 0 9-4.03 9-9s-4.03-9-9-9zm-1 5v5l4.28 2.54.72-1.21-3.5-2.08V8H12z"/>
					</svg> History
				</a>
			</div>
		<%
			}
		%>
		
		
		
		<!-- USER SECTION -->
		<%
			if (currentUser != null) {
		%>
			<div class="pb-2 border-bottom mt-2">
				<p class="sidebar-title text-muted fw-bold text-uppercase px-4 mb-1">You</p>
				<a class="sidebar-item text-decoration-none text-dark" href="channel?channelId=<%= channelId %>">
					<svg class="me-4" width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
						<path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z" />
					</svg> 
					<%
						if (userName != null) {
							out.print("Hi, " + userName);
						} else {
							out.print("Your Channel");
						}
					%>
				</a>
				<a class="sidebar-item text-decoration-none text-dark" href="channel?channelId=<%= channelId %>&tab=videos">
					<svg class="me-4" width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
						<path d="M18 4l2 4h-3l-2-4h-2l2 4h-3l-2-4H8l2 4H7L5 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V4h-4z"/>
					</svg> Your videos
				</a>				
				<a class="sidebar-item text-decoration-none text-dark" href="logout">
					<svg class="me-4" width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
						<path d="M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.58L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z" />
					</svg> Logout
				</a>
			</div>
		<%
			} else {
		%>
			<div class="pb-2 border-bottom mt-2">
				<p class="sidebar-title text-muted fw-bold text-uppercase px-4 mb-1">Sign in to</p>
				<a class="sidebar-item text-decoration-none text-dark" href="login">
					<svg class="me-4" width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
						<path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z" />
					</svg> Sign In
				</a>
			</div>
		<%
			}
		%>
	</aside>
</body>
</html>