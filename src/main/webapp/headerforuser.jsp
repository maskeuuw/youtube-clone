<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>YouTube Header</title>
<link >
</head>
<body>
	<%
	// Check if user has a channel - FIXED VERSION
	boolean hasChannel = false;
	Integer userChannelId = null;
	
	if (session.getAttribute("currentUser") != null) {
		Model.UserBean currentUser = (Model.UserBean) session.getAttribute("currentUser");
		int userId = currentUser.getUserId();
		
		try {
			// Call static methods correctly (without creating instance)
			hasChannel = Repository.ChannelRepository.userHasChannel(userId);
			
			if (hasChannel) {
				// Call static method
				userChannelId = Repository.ChannelRepository.getExistingChannelId(userId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Store in page context for JSTL
		pageContext.setAttribute("hasChannel", hasChannel);
		pageContext.setAttribute("userChannelId", userChannelId);
	}
	%>
	
	<header class="yt-header bg-white shadow-sm border-bottom py-2 px-3">
		<div class="container-fluid d-flex align-items-center justify-content-between h-100">
			<div class="d-flex align-items-center">
				<button class="btn p-2 rounded-circle me-3" type="button"
					data-bs-toggle="collapse" data-bs-target="#sidebarCollapse"
					aria-expanded="false" aria-controls="sidebarCollapse"
					title="Toggle Sidebar">
					<svg width="24" height="24" viewBox="0 0 24 24" fill="none"
						stroke="currentColor" stroke-width="2" stroke-linecap="round"
						stroke-linejoin="round">
                    <line x1="3" y1="12" x2="21" y2="12"></line>
                    <line x1="3" y1="6" x2="21" y2="6"></line>
                    <line x1="3" y1="18" x2="21" y2="18"></line></svg>
				</button>
				<a href="${pageContext.request.contextPath}/home" class="text-decoration-none d-flex align-items-center">
					<!--  <span class="yt-logo me-1">&#9658;</span>--> 
					<span class="yt-logo-text text-dark fs-5">YouTube</span> 
					<!-- <sup class="text-muted ms-1" style="font-size: 10px;">Mock</sup>-->
				</a>
			</div>
			
			<div class="search-container d-none d-md-flex align-items-center">
                <form action="${pageContext.request.contextPath}/search" method="get" class="w-100 d-flex">
                    <div class="input-group search-input-group flex-grow-1">
                        <input type="text" name="q" placeholder="Search" 
                               class="form-control search-input" 
                               value="${param.q != null ? param.q : ''}"
                               aria-label="Search YouTube" required>
                        
                        <button class="btn search-btn" type="submit" title="Search">
                            <i class="fas fa-search"></i>
                        </button>
                    </div>
                </form>                          
            </div>
			
			<div class="d-flex align-items-center">
				
				<c:choose>
					<%-- Case 1: User has a channel. Show Upload Button. --%>
					<c:when test="${hasChannel}">
						<!-- Update 'upload-video.jsp' to your actual upload page -->
						<a href="uploadvideo" class="btn p-2 rounded-circle me-2 text-dark" title="Upload Video">
							<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
								<path d="M23 7l-7 5 7 5V7z"></path>
								<rect x="1" y="5" width="15" height="14" rx="2" ry="2"></rect>
								<line x1="12" y1="12" x2="12" y2="12"></line>
								<line x1="12" y1="8" x2="12" y2="16"></line>
								<line x1="8" y1="12" x2="16" y2="12"></line>
							</svg>
						</a>
					</c:when>
					
					<%-- Case 2: User has NO channel. Redirect to Create Channel. --%>
					<c:otherwise>
						<a href="createchannel.jsp" class="btn p-2 rounded-circle me-2 text-dark" title="Create Channel to Upload">
							<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
								<path d="M23 7l-7 5 7 5V7z"></path>
								<rect x="1" y="5" width="15" height="14" rx="2" ry="2"></rect>
								<line x1="12" y1="12" x2="12" y2="12"></line>
								<line x1="12" y1="8" x2="12" y2="16"></line>
								<line x1="8" y1="12" x2="16" y2="12"></line>
							</svg>
						</a>
					</c:otherwise>
				</c:choose>
				
				<div class="dropdown">
					<button class="btn btn-outline-secondary rounded-circle p-1"
						type="button" id="userProfileDropdown" data-bs-toggle="dropdown"
						aria-expanded="false" title="Account">

						<span
							class="d-inline-block text-center rounded-circle bg-primary text-white"
							style="width: 32px; height: 32px; line-height: 32px; font-size: 16px;">
							${sessionScope.currentUser.userName.substring(0, 1)} </span>
					</button>

					<ul class="dropdown-menu dropdown-menu-end"
						aria-labelledby="userProfileDropdown">
						<li><h6 class="dropdown-header">${sessionScope.currentUser.userName}</h6></li>
						<li><p class="dropdown-item-text text-muted">${sessionScope.currentUser.email}</p></li>
						<li><hr class="dropdown-divider"></li>

						<c:choose>
							<%-- Check if user has a channel --%>
							<c:when test="${hasChannel}">
								<!-- Has channel: Show Your Channel -->
								<li><a class="dropdown-item" href="channel?channelId=${userChannelId}"> 
									<i class="fas fa-user me-2"></i> Your Channel
								</a></li>
							</c:when>
							<c:otherwise>
								<!-- No channel: Show Create Your Channel -->
								<li><a class="dropdown-item" href="create-channel.jsp">
									<i class="fas fa-plus-circle me-2"></i> Create Your Channel
								</a></li>
							</c:otherwise>
						</c:choose>

						<li><a class="dropdown-item" href="settings.jsp"><i
								class="fas fa-cog me-2"></i> Settings</a></li>
						<li><hr class="dropdown-divider"></li>

						<li><a class="dropdown-item text-danger"
							href="${pageContext.request.contextPath}/logout"> <i
								class="fas fa-sign-out-alt me-2"></i> Sign Out
						</a></li>
					</ul>
				</div>
			</div>
		</div>
	</header>
</body>
</html>