<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Insert title here</title>
</head>
<body>
	<!-- 1. HEADER (Top Navigation Bar) -->
	<header class="yt-header bg-white shadow-sm border-bottom py-2 px-3">
		<div class="container-fluid d-flex align-items-center justify-content-between h-100">
			<!-- Left Section: Menu & Logo -->
			<div class="d-flex align-items-center">
				<!-- Menu Icon (Hamburger) - Toggles Sidebar -->
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
				<!-- YouTube Logo -->
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
			
			<!-- Right Section: Actions & Sign In Button -->
			<div class="d-flex align-items-center">
				<!-- Apps Icon 
				<button class="btn p-2 rounded-circle me-2 d-none d-sm-block"
					title="YouTube Apps">
					<svg width="24" height="24" viewBox="0 0 24 24" fill="none"
						stroke="currentColor" stroke-width="2" stroke-linecap="round"
						stroke-linejoin="round">
						<rect x="3" y="3" width="7" height="7"></rect>
						<rect x="14" y="3" width="7" height="7"></rect>
						<rect x="3" y="14" width="7" height="7"></rect>
						<rect x="14" y="14" width="7" height="7"></rect></svg>
				</button>-->
				<!-- Sign In Button -->
				<form action="login" method="get">
					<button type="submit" name="sign in"
						class="btn btn-outline-primary rounded-pill d-flex align-items-center py-1 px-2"
						title="Sign In">
						<svg width="18" height="18" viewBox="0 0 24 24" fill="none"
							stroke="currentColor" stroke-width="2" stroke-linecap="round"
							stroke-linejoin="round" class="me-1">
						<path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"></path>
						<circle cx="12" cy="7" r="4"></circle></svg>
						<a href="login">SIGN IN</a>
					</button>
				</form>
			</div>
		</div>
	</header>
</body>
</html>