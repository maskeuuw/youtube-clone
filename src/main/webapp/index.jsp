<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>YouTube Clone - Home</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC"
	crossorigin="anonymous">
<link rel="stylesheet" href="style.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

	<!-- header -->
	<c:choose>
		<c:when test="${not empty sessionScope.currentUser}">
			<c:import var="userheader" url="headerforuser.jsp" />
			<c:out value="${userheader}" escapeXml="false" />
		</c:when>
		<c:otherwise>
			<c:import var="guestheader" url="headerforguest.jsp" />
			<c:out value="${guestheader}" escapeXml="false" />
		</c:otherwise>
	</c:choose>

	<!-- MAIN LAYOUT: Sidebar + Content Feed -->
	<div class="d-flex">

		<!-- sidebar -->
		<c:import var="sidebar" url="sidebar.jsp" />
		<c:out value="${sidebar}" escapeXml="false" />

		<!-- MAIN CONTENT -->
		<main class="yt-feed-container container-fluid p-3" id="feedContainer">
			
			<!-- TOPIC BAR -->
				<div class="topic-bar mb-4">
					<div class="topic-scroll-container">
						<div class="topic-items">
							<button class="topic-item active">All</button>
						</div>
					</div>
				</div>
			
			<!-- Video Grid -->
			<div class="video-grid container-fluid" id="videoGrid">

				
				<%-- Check if videos exist --%>
							<c:choose>
								<c:when test="${not empty videos}">
									<%-- Loop through videos --%>
									<c:forEach var="video" items="${videos}" varStatus="status">
										<a href="${video.videoWebUrl}" class="video-card">
											<div class="thumbnail-container">
												<%-- Thumbnail from servlet --%>
												<img src="thumbnail?videoId=${video.videoId}"
													alt="${video.videoName}" class="thumbnail" loading="lazy"
													onerror="this.style.display='none'; 
										          this.nextElementSibling.style.display='flex';">

												<%-- Fallback placeholder --%>
												<div class="thumbnail-placeholder" style="display: none;">
													<i class="fas fa-video"></i> <span>No thumbnail</span>
												</div>
											</div>

											<div class="video-details">
												<div class="channel-avatar">
													<%-- Channel image --%>
													<c:choose>
														<c:when test="${not empty video.channelImgBase64}">
															<%-- Channel image from database as base64 --%>
															<img src="data:image/jpeg;base64,${video.channelImgBase64}"
																alt="${video.channelName}"
																onerror="this.src='https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-4.0.3&auto=format&fit=crop&w=100&q=80'">
														</c:when>
														<c:otherwise>
															<%-- Default channel avatar --%>
															<img
																src="https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-4.0.3&auto=format&fit=crop&w=100&q=80"
																alt="${video.channelName}">
														</c:otherwise>
													</c:choose>
												</div>

												<div class="video-info">
													<h3 class="video-title">${video.videoName}</h3>
													<span class="channel-name"> ${video.channelName != null ? video.channelName : 'Unknown Channel'}
													</span>
													<p class="video-stats">
														<span>${video.getFormattedViews()} views</span> <span
															class="dot-separator">•</span> <span>${video.getFormattedDate()}</span>
													</p>
												</div>
											</div>
										</a>
									</c:forEach>
								</c:when>

								<c:otherwise>
									<%-- No videos found --%>
									<div class="no-videos">
										<i class="fas fa-video-slash fa-3x mb-3"></i>
										<h4>No videos available</h4>
										<p>Check back later for new content</p>
									</div>
								</c:otherwise>
							</c:choose>
						</div></main>
	</div>

	<!-- Bootstrap JS Bundle -->
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
		integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
		crossorigin="anonymous"></script>

	<!-- JavaScript for Data Injection and Sidebar Logic -->
	<script src="script.js" defer></script>
</body>
</html>