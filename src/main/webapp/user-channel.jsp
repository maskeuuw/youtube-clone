<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>${channel.channelName}-YouTube Channel</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
<link rel="stylesheet" href="style.css">
<link rel="stylesheet" href="user-channelstyle.css">
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

	<!-- Main Layout -->
	<div class="d-flex pt-5">
		<!-- sidebar -->
		<c:import var="sidebar" url="sidebar.jsp" />
		<c:out value="${sidebar}" escapeXml="false" />

		<!-- MAIN CONTENT AREA -->
		<main class=" container-fluid yt-feed-container" id="feedContainer">

			<!-- Channel Header -->
			<header class="channel-header">
				<div class="container">
					<div class="row align-items-center">
						<div class="col-md-2 col-4">
							<div
								class="channel-avatar rounded-circle d-flex align-items-center justify-content-center"
								style="width: 100px; height: 100px;">
								<c:choose>
									<c:when test="${channel.hasImage()}">
										<!-- Display the uploaded image -->
										<img src="data:image/png;base64,${channel.channelImgBase64}"
											class="rounded-circle w-100 h-100 object-fit-cover"
											alt="${channel.channelName}">
									</c:when>
									<c:otherwise>
										<i class="bi bi-person-circle fs-1"></i>
									</c:otherwise>
								</c:choose>
							</div>
						</div>
						<div class="col-md-8 col-8">
							<h1 class="channel-name" style="font-size: 30px;">${channel.channelName}</h1>

							<div class="channel-stats text-secondary">
								<span><i class="bi bi-people-fill"></i>
									${subscriberCount} subscribers</span> <span><i
									class="bi bi-play-circle-fill"></i> ${videoCount} videos</span> <span><i
									class="bi bi-eye-fill"></i> ${totalViewCount} views</span>
							</div>

							<p class="mt-2 text-secondary">${channel.channelDesc}</p>
						</div>

						<div class="col-md-2 col-12 mt-md-0 mt-3">
							<c:choose>
								<c:when test="${isOwner}">
									<button class="btn btn-outline-primary w-100" disabled>
										<i class="bi bi-person-check-fill"></i> Your Channel
									</button>
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${isSubscribed}">
											<form action="subscribe" method="post" style="margin: 0;">
												<input type="hidden" name="channelId"
													value="${channel.channelId}"> <input type="hidden"
													name="action" value="unsubscribe"> <input
													type="hidden" name="redirectTo" value="channel">
												<button type="submit" class="btn btn-secondary w-100">
													<i class="bi bi-bell-fill"></i> Subscribed
												</button>
											</form>
										</c:when>
										<c:otherwise>
											<form action="subscribe" method="post" style="margin: 0;">
												<input type="hidden" name="channelId"
													value="${channel.channelId}"> <input type="hidden"
													name="action" value="subscribe"> <input
													type="hidden" name="redirectTo" value="channel">
												<button type="submit" class="subscribe-btn w-100">
													<i class="bi bi-bell"></i> Subscribe
												</button>
											</form>
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</div>
			</header>

			<!-- Channel Navigation (No JavaScript) -->
			<nav class="channel-nav">
				<div class="container">
					<ul class="nav nav-tabs border-0">
						<li class="nav-item"><a
							class="nav-link ${tab == 'home' ? 'active' : ''}"
							href="channel?channelId=${channel.channelId}&tab=home">HOME</a></li>
						<li class="nav-item"><a
							class="nav-link ${tab == 'videos' ? 'active' : ''}"
							href="channel?channelId=${channel.channelId}&tab=videos">VIDEOS</a>
						</li>
						<li class="nav-item"><a
							class="nav-link ${tab == 'about' ? 'active' : ''}"
							href="channel?channelId=${channel.channelId}&tab=about">ABOUT</a>
						</li>
					</ul>
				</div>
			</nav>

			<!-- Tab Content -->
			<div class="container my-4">
				<c:choose>
					<c:when test="${tab == 'videos'}">
						<!-- VIDEOS TAB -->
						<h3 class="mb-4">All Videos</h3>
						<c:choose>
							<c:when test="${not empty videos}">
								<div
									class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 g-4">
									<c:forEach var="video" items="${videos}">
										<div class="col">
											<div class="card video-card h-100 border-0">
												<!-- Video thumbnail with overlay for owner actions -->
												<div class="position-relative">
													<a href="videowatch?v=${video.videoId}"> <c:choose>
															<c:when test="${not empty video.videoThumbnailUrl}">
																<img src="${video.videoThumbnailUrl}"
																	class="card-img-top" alt="${video.videoName}"
																	style="height: 180px; object-fit: cover;">
															</c:when>
															<c:otherwise>
																<div
																	class="card-img-top bg-secondary d-flex align-items-center justify-content-center"
																	style="height: 180px;">
																	<i class="bi bi-camera-video fs-1 text-white"></i>
																</div>
															</c:otherwise>
														</c:choose>
													</a>

													<!-- Owner action buttons (only visible to channel owner) -->
													<c:if test="${isOwner}">
														<div class="position-absolute top-0 end-0 p-2">
															<div class="btn-group btn-group-sm">
																<!-- Edit Button -->
																<button type="button"
																	class="btn btn-outline-light bg-dark bg-opacity-75"
																	data-bs-toggle="modal"
																	data-bs-target="#editVideoModal${video.videoId}">
																	<i class="bi bi-pencil"></i>
																</button>

																<!-- Delete Button -->
																<button type="button"
																	class="btn btn-outline-light bg-dark bg-opacity-75"
																	onclick="confirmDelete(${video.videoId}, '${video.videoName}')">
																	<i class="bi bi-trash"></i>
																</button>
															</div>
														</div>
													</c:if>
												</div>

												<div class="card-body">
													<h6 class="card-title text-truncate">
														${video.videoName}
														<c:if test="${video.videoVisibility == 'private'}">
															<span class="badge"
																style="background-color: #ff4444; color: white; padding: 2px 6px; border-radius: 3px; font-size: 10px; margin-left: 5px;">
																<i class="bi bi-lock-fill"></i> PRIVATE
															</span>
														</c:if>
													</h6>
													<p class="card-text small text-muted mb-1">
														${video.formattedViews} views</p>
													<p class="card-text small text-muted">
														${video.formattedDate}</p>

													<!-- Additional owner info (optional) -->
													<c:if test="${isOwner}">
														<div class="mt-2 pt-2 border-top border-light">
															<small class="text-muted d-flex align-items-center">
																<i class="bi bi-bar-chart me-1"></i>
																${video.videoVisibility == 'private' ? 'Private' : 'Public'}
																video
															</small>
														</div>
													</c:if>
												</div>
											</div>
										</div>

										<!-- Edit Video Modal (only for owner) -->
										<c:if test="${isOwner}">
											<div class="modal fade" id="editVideoModal${video.videoId}"
												tabindex="-1"
												aria-labelledby="editVideoModalLabel${video.videoId}"
												aria-hidden="true">
												<div class="modal-dialog">
													<div class="modal-content">
														<div class="modal-header">
															<h5 class="modal-title"
																id="editVideoModalLabel${video.videoId}">Edit
																Video: ${video.videoName}</h5>
															<button type="button" class="btn-close"
																data-bs-dismiss="modal" aria-label="Close"></button>
														</div>
														<form action="editVideo" method="post"
															enctype="multipart/form-data">
															<div class="modal-body">
																<input type="hidden" name="videoId"
																	value="${video.videoId}"> <input type="hidden"
																	name="channelId" value="${channel.channelId}">
																<input type="hidden" name="redirectTo"
																	value="channel?channelId=${channel.channelId}&tab=videos">

																<div class="mb-3">
																	<label for="videoName${video.videoId}"
																		class="form-label">Video Title</label> <input
																		type="text" class="form-control"
																		id="videoName${video.videoId}" name="videoName"
																		value="${video.videoName}" required>
																</div>

																<div class="mb-3">
																	<label for="videoDesc${video.videoId}"
																		class="form-label">Description</label>
																	<textarea class="form-control"
																		id="videoDesc${video.videoId}" name="videoDesc"
																		rows="3">${video.videoDesc}</textarea>
																</div>

																<div class="mb-3">
																	<label for="videoVisibility${video.videoId}"
																		class="form-label">Visibility</label> <select
																		class="form-select"
																		id="videoVisibility${video.videoId}"
																		name="videoVisibility">
																		<option value="public"
																			${video.videoVisibility == 'public' ? 'selected' : ''}>Public</option>
																		<option value="private"
																			${video.videoVisibility == 'private' ? 'selected' : ''}>Private</option>
																	</select>
																</div>

																<div class="mb-3">
																	<label for="thumbnail${video.videoId}"
																		class="form-label">Thumbnail (Optional)</label> <input
																		type="file" class="form-control"
																		id="thumbnail${video.videoId}" name="thumbnail"
																		accept="image/*"> <small class="text-muted">Leave
																		empty to keep current thumbnail</small>
																</div>
															</div>
															<div class="modal-footer">
																<button type="button" class="btn btn-secondary"
																	data-bs-dismiss="modal">Cancel</button>
																<button type="submit" class="btn btn-primary">Save
																	Changes</button>
															</div>
														</form>
													</div>
												</div>
											</div>
										</c:if>
									</c:forEach>
								</div>
							</c:when>
							<c:otherwise>
								<div class="text-center py-5">
									<i class="bi bi-camera-video-off fs-1 text-muted"></i>
									<p class="text-muted mt-2">No videos uploaded yet</p>
									<c:if test="${isOwner}">
										<a href="upload-video.jsp" class="btn btn-primary mt-3"> <i
											class="bi bi-upload"></i> Upload Your First Video
										</a>
									</c:if>
								</div>
							</c:otherwise>
						</c:choose>
					</c:when>

					<c:when test="${tab == 'about'}">
						<!-- ABOUT TAB -->
						<div class="about-card p-4">
							<h3 style="color: var(- -primary-color);">About This Channel</h3>
							<p class="mb-4">${channel.channelDesc}</p>

							<div class="row">
								<div class="col-md-6">
									<h5>Channel Statistics</h5>
									<ul class="list-unstyled">
										<li><strong>Subscribers:</strong> ${subscriberCount}</li>
										<li><strong>Total Videos:</strong> ${videoCount}</li>
										<li><strong>Total Views:</strong> ${totalViewCount}</li>
									</ul>
								</div>
								<div class="col-md-6">
									<h5>Details</h5>
									<ul class="list-unstyled">
										<li><strong>Channel Name:</strong> ${channel.channelName}</li>
										<!--  <li><strong>Joined:</strong></li>	-->
									</ul>
								</div>
							</div>
						</div>
					</c:when>

					<c:otherwise>
						<!-- DEFAULT/HOME TAB -->
						<div class="home-tab">
							<div class="row">
								<div class="col-lg-8">
									<c:choose>
										<c:when test="${not empty featuredVideo}">
											<h3 class="mb-3">
												<i class="bi bi-lightning-fill"
													style="color: var(- -primary-color);"></i> Latest Video
											</h3>
											<div class="video-card mb-4">
												<div class="video-thumbnail"
													style="height: 400px; background-color: #ccc;">
													<a href="videowatch?v=${featuredVideo.videoId}"> <c:choose>
															<c:when
																test="${not empty featuredVideo.videoThumbnailUrl}">
																<img src="${featuredVideo.videoThumbnailUrl}"
																	alt="${featuredVideo.videoName}"
																	style="width: 100%; height: 100%; object-fit: cover;">
															</c:when>
															<c:otherwise>
																<div
																	class="w-100 h-100 d-flex align-items-center justify-content-center bg-dark">
																	<i class="bi bi-play-circle fs-1 text-white"></i>
																</div>
															</c:otherwise>
														</c:choose>
													</a>
												</div>
												<div class="p-3">
													<h4 class="video-title">${featuredVideo.videoName}
														<c:if test="${featuredVideo.videoVisibility == 'private'}">
															<span class="badge"
																style="background-color: #ff4444; color: white; padding: 2px 6px; border-radius: 3px; font-size: 12px; margin-left: 8px;">
																<i class="bi bi-lock-fill"></i> PRIVATE
															</span>
														</c:if>
													</h4>
													<p class="video-meta">${featuredVideo.formattedViews}
														views • ${featuredVideo.formattedDate}</p>
													<p class="text-secondary mt-2">${featuredVideo.videoDesc}</p>
													<a href="videowatch?v=${featuredVideo.videoId}"
														class="btn btn-primary mt-2"> <i
														class="bi bi-play-fill"></i> Watch Now
													</a>
												</div>
											</div>

											<c:if test="${videos.size() > 1}">
												<h4 class="mb-3">More Videos</h4>
												<div class="row row-cols-1 row-cols-md-2 g-4">
													<c:forEach var="video" items="${videos}" begin="1" end="4">
														<div class="col">
															<div class="card video-card h-100 border-0">
																<a href="videowatch?v=${video.videoId}"> <c:choose>
																		<c:when test="${not empty video.videoThumbnailUrl}">
																			<img src="${video.videoThumbnailUrl}"
																				class="card-img-top" alt="${video.videoName}"
																				style="height: 150px; object-fit: cover;">
																		</c:when>
																		<c:otherwise>
																			<div
																				class="card-img-top bg-secondary d-flex align-items-center justify-content-center"
																				style="height: 150px;">
																				<i class="bi bi-camera-video text-white"></i>
																			</div>
																		</c:otherwise>
																	</c:choose>
																</a>
																<!--<div class="card-body">
                                                                    <h6 class="card-title text-truncate">${video.videoName}</h6>
                                                                    <p class="card-text small text-muted mb-1">
                                                                        ${video.formattedViews} views
                                                                    </p>
                                                                    <p class="card-text small text-muted">
                                                                        ${video.formattedDate}
                                                                    </p>
                                                                </div>-->
																<div class="card-body">
																	<h6 class="card-title text-truncate">
																		${video.videoName}
																		<!-- ADD THIS LINE FOR PRIVATE VIDEOS -->
																		<c:if test="${video.videoVisibility == 'private'}">
																			<span class="badge"
																				style="background-color: #ff4444; color: white; padding: 2px 6px; border-radius: 3px; font-size: 10px; margin-left: 5px;">
																				<i class="bi bi-lock-fill"></i> PRIVATE
																			</span>
																		</c:if>
																	</h6>
																	<p class="card-text small text-muted mb-1">
																		${video.formattedViews} views</p>
																	<p class="card-text small text-muted">
																		${video.formattedDate}</p>
																</div>
															</div>
														</div>
													</c:forEach>
												</div>
											</c:if>
										</c:when>
										<c:otherwise>
											<div class="text-center py-5">
												<i class="bi bi-camera-video-off fs-1 text-muted"></i>
												<p class="text-muted mt-2">No videos uploaded yet</p>
												<c:if test="${isOwner}">
													<a href="upload-video.jsp" class="btn btn-primary mt-3">
														<i class="bi bi-upload"></i> Upload Your First Video
													</a>
												</c:if>
											</div>
										</c:otherwise>
									</c:choose>
								</div>

								<div class="col-lg-4">
									<!-- Channel Stats Card 
                                    <div class="sidebar-card p-3 mb-4">
                                        <h5 style="color: var(--primary-color);">Channel Statistics</h5>
                                        <div class="row g-2">
                                            <div class="col-6">
                                                <div class="stat-card">
                                                    <div style="font-size: 20px; font-weight: bold;">${subscriberCount}</div>
                                                    <div style="font-size: 12px;">Subscribers</div>
                                                </div>
                                            </div>
                                            <div class="col-6">
                                                <div class="stat-card">
                                                    <div style="font-size: 20px; font-weight: bold;">${videoCount}</div>
                                                    <div style="font-size: 12px;">Videos</div>
                                                </div>
                                            </div>
                                            <div class="col-6 mt-2">
                                                <div class="stat-card">
                                                    <div style="font-size: 20px; font-weight: bold;">${totalViewCount}</div>
                                                    <div style="font-size: 12px;">Total Views</div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>-->

									
									<c:if test="${isOwner}">
										<div class="sidebar-card p-3">
											<h5 style="color: var(- -primary-color);">Channel
												Management</h5>
											<a href="edit-channel?channelId=${channel.channelId}"
												class="btn btn-outline-primary w-100 mb-2"> <i
												class="bi bi-pencil-fill"></i> Edit Channel
											</a> <a href="upload-video.jsp" class="btn btn-primary w-100">
												<i class="bi bi-upload"></i> Upload Video
											</a>
										</div>
									</c:if>

									<!-- About Card 
                                    <div class="sidebar-card p-3 mt-3">
                                        <h5 style="color: var(--primary-color);">About Channel</h5>
                                        <p class="small">${channel.channelDesc}</p>
                                        <a href="channel?channelId=${channel.channelId}&tab=about" 
                                           class="btn btn-link p-0">See more about this channel</a>
                                    </div>-->
								</div>
							</div>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
		</main>
	</div>

	<!-- Bootstrap JS Bundle -->
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
	<script type="text/javascript" src="sidebarscript.js"></script>
	<script>
	// Delete confirmation function
	function confirmDelete(videoId, videoName) {
    if (confirm('Are you sure you want to delete "' + videoName + '"? This action cannot be undone.')) {
        // Create a form and submit it
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = 'deleteVideo';
        
        const videoIdInput = document.createElement('input');
        videoIdInput.type = 'hidden';
        videoIdInput.name = 'videoId';
        videoIdInput.value = videoId;
        
        const channelIdInput = document.createElement('input');
        channelIdInput.type = 'hidden';
        channelIdInput.name = 'channelId';
        channelIdInput.value = ${channel.channelId};
        
        const redirectInput = document.createElement('input');
        redirectInput.type = 'hidden';
        redirectInput.name = 'redirectTo';
        redirectInput.value = 'channel?channelId=${channel.channelId}&tab=videos';
        
        form.appendChild(videoIdInput);
        form.appendChild(channelIdInput);
        form.appendChild(redirectInput);
        
        document.body.appendChild(form);
        form.submit();
    }
}
</body>
</html>
