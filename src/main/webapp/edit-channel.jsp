<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="uft-8">
<title>Edit Channel - ${channel.channelName}</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
<link rel="stylesheet" href="style.css">
<link rel="stylesheet" href="edit-channelstyle.css">
</head>
<body>

	<%-- Add this at the very top of your JSP --%>
	<c:if test="${empty sessionScope.currentUser}">
		<c:redirect url="login.jsp?error=Please login first" />
	</c:if>
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
        <main class="container-fluid yt-feed-container" id="feedContainer">
            
            <!-- Edit Channel Header -->
            <div class="container my-4">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h1><i class="bi bi-pencil-square"></i> Edit Channel</h1>
                    <a href="channel?channelId=${channel.channelId}" class="btn btn-outline-secondary">
                        <i class="bi bi-arrow-left"></i> Back to Channel
                    </a>
                </div>
                
                <!-- Debug info (remove after testing)
                <div class="alert alert-info">
                    <strong>Debug Info:</strong><br>
                    Channel ID from object: ${channel.channelId}<br>
                    Channel ID from param: ${param.channelId}<br>
                    Channel Name: ${channel.channelName}<br>
                    User ID: ${channel.userId}
                </div> -->
                
                <!-- Success/Error Messages -->
                <c:if test="${not empty param.success}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        ${param.success}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                
                <c:if test="${not empty param.error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        ${param.error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                
                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        ${error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                
                <!-- Check if channel exists -->
                <c:choose>
                    <c:when test="${empty channel}">
                        <div class="alert alert-danger">
                            <h4>Channel Not Found</h4>
                            <p>The channel you're trying to edit doesn't exist or you don't have permission to edit it.</p>
                            <a href="index.jsp" class="btn btn-primary">Go to Home</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <!-- Edit Form -->
                        <div class="card">
                            <div class="card-body">
								<form action="edit-channel" method="post"
									enctype="multipart/form-data" class="form-container">
									<!-- IMPORTANT: Always use param.channelId since the object might be null -->
									<input type="hidden" name="channelId"
										value="${param.channelId}">

									<!-- Channel Image Upload -->
									<div class="mb-4 text-center">
										<h4 class="mb-3">Channel Picture</h4>
										<div class="mb-3">
											<div
												class="channel-avatar-edit rounded-circle d-flex align-items-center justify-content-center mx-auto bg-light mb-3"
												style="width: 150px; height: 150px;">
												<c:choose>
													<c:when test="${channel.hasImage()}">
														<img
															src="data:image/png;base64,${channel.channelImgBase64}"
															class="rounded-circle w-100 h-100 object-fit-cover"
															alt="Current Channel Image">
													</c:when>
													<c:otherwise>
														<i class="bi bi-person-circle fs-1 text-secondary"></i>
													</c:otherwise>
												</c:choose>
											</div>
											<p class="upload-hint">Current channel picture</p>
										</div>

										<div class="mb-3">
											<label for="channelImage" class="form-label">Upload
												New Picture</label> <input type="file" class="form-control"
												id="channelImage" name="channelImage" accept="image/*">
											<div class="form-text">Recommended: Square image, at
												least 800x800px. Max file size: 10MB</div>
										</div>
									</div>

									<hr class="my-4">

									<!-- Channel Information -->
									<div class="mb-4">
										<h4 class="mb-3">Channel Information</h4>

										<div class="mb-3">
											<label for="channelName" class="form-label">Channel
												Name *</label> <input type="text" class="form-control"
												id="channelName" name="channelName"
												value="${not empty channel.channelName ? channel.channelName : ''}"
												required maxlength="45">
											<div class="form-text">This will be displayed as your
												channel name</div>
										</div>

										<div class="mb-3">
											<label for="channelDesc" class="form-label">Channel
												Description</label>
											<textarea class="form-control" id="channelDesc"
												name="channelDesc" rows="5" maxlength="255">${not empty channel.channelDesc ? channel.channelDesc : ''}</textarea>
											<div class="form-text">Tell viewers about your channel.
												This will appear in the About section.</div>
										</div>
									</div>

									<!-- Statistics (Read-only)
									<div class="mb-4">
										<h4 class="mb-3">Channel Statistics</h4>
										<div class="row">
											<div class="col-md-4 mb-3">
												<div class="card bg-light">
													<div class="card-body text-center">
														<h5 class="card-title">Subscribers</h5>
														<p class="card-text display-6">${channel.subscriberCount}</p>
													</div>
												</div>
											</div>
											<div class="col-md-4 mb-3">
												<div class="card bg-light">
													<div class="card-body text-center">
														<h5 class="card-title">Videos</h5>
														<p class="card-text display-6">
															<c:choose>
																<c:when test="${not empty videoCount}">${videoCount}</c:when>
																<c:otherwise>0</c:otherwise>
															</c:choose>
														</p>
													</div>
												</div>
											</div>
											<div class="col-md-4 mb-3">
												<div class="card bg-light">
													<div class="card-body text-center">
														<h5 class="card-title">Channel ID</h5>
														<p class="card-text">${channel.channelId}</p>
													</div>
												</div>
											</div>
										</div>
									</div> -->

									<!-- Form Actions -->
									<div class="d-flex justify-content-between mt-4">
										<a href="channel?channelId=${channel.channelId}"
											class="btn btn-outline-secondary"> <i
											class="bi bi-x-circle"></i> Cancel
										</a>
										<button type="submit" class="btn btn-primary">
											<i class="bi bi-check-circle"></i> Save Changes
										</button>
									</div>
								</form>
							</div>
                        </div>
                    </c:otherwise>
                </c:choose>
                
                <!-- Danger Zone -->
                <div class="card border-danger mt-4">
                    <div class="card-header bg-danger text-white">
                        <i class="bi bi-exclamation-triangle"></i> Danger Zone
                    </div>
                    <div class="card-body">
                        <h5 class="card-title">Delete Channel</h5>
                        <p class="card-text">
                            Once you delete a channel, there is no going back. This will permanently delete:
                        </p>
                        <ul>
                            <li>All videos uploaded to this channel</li>
                            <li>All subscribers will be removed</li>
                            <li>Channel statistics will be lost</li>
                            <li>Comments on your videos will be deleted</li>
                        </ul>
                        
                        <button type="button" class="btn btn-danger" data-bs-toggle="modal" data-bs-target="#deleteModal">
                            <i class="bi bi-trash"></i> Delete Channel
                        </button>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <!-- Delete Confirmation Modal -->
    <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-danger text-white">
                    <h5 class="modal-title" id="deleteModalLabel">
                        <i class="bi bi-exclamation-triangle"></i> Confirm Channel Deletion
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p>Are you sure you want to delete your channel "<strong>${channel.channelName}</strong>"?</p>
                    <p class="text-danger"><strong>This action cannot be undone!</strong></p>
                    
                    <div class="alert alert-warning">
                        <i class="bi bi-info-circle"></i>
                        To confirm deletion, please type your channel name below:
                    </div>
                    
                    <form id="deleteForm" action="delete-channel" method="post">
                        <input type="hidden" name="channelId" value="${channel.channelId}">
                        <div class="mb-3">
                            <label for="confirmChannelName" class="form-label">Channel Name</label>
                            <input type="text" class="form-control" id="confirmChannelName" 
                                   placeholder="Enter channel name to confirm" required>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        <i class="bi bi-x-circle"></i> Cancel
                    </button>
                    <button type="button" class="btn btn-danger" id="confirmDeleteBtn" disabled>
                        <i class="bi bi-trash"></i> Delete Channel
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Simple form validation for delete confirmation 
    <script>
    document.addEventListener('DOMContentLoaded', function() {
        const confirmInput = document.getElementById('confirmChannelName');
        const confirmBtn = document.getElementById('confirmDeleteBtn');
        const deleteForm = document.getElementById('deleteForm');
        
        if (confirmInput && confirmBtn) {
            confirmInput.addEventListener('input', function() {
                confirmBtn.disabled = this.value !== '${channel.channelName}';
            });
            
            confirmBtn.addEventListener('click', function() {
                if (confirmInput.value === '${channel.channelName}') {
                    deleteForm.submit();
                }
            });
        }
    });
    </script>-->
    <script type="text/javascript" src="sidebarscript.js"></script>
</body>
</html>