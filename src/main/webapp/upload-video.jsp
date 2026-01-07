<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Upload Video</title>
<!-- Bootstrap CSS (Ensure you have this or similar linked) -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<!-- FontAwesome for icons -->
<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
<link rel="stylesheet" href="style.css">
<link rel="stylesheet" href="upload-videostyle.css">

</head>
<body class="bg-light">

    <!-- header -->
	<c:choose>
		<%-- CASE 1: User is Logged In (sessionScope.currentUser is NOT null) --%>
		<c:when test="${not empty sessionScope.currentUser}">
			<c:import var="userheader" url="headerforuser.jsp" />
			<c:out value="${userheader}" escapeXml="false" />
		</c:when>

		<%-- CASE 2: User is a Guest (sessionScope.currentUser IS null) --%>
		<c:otherwise>
			<c:import var="guestheader" url="headerforguest.jsp" />
			<c:out value="${guestheader}" escapeXml="false" />
		</c:otherwise>
	</c:choose>

	<div class="d-flex pt-5">

		<!-- sidebar -->
		<c:import var="sidebar" url="sidebar.jsp" />
		<c:out value="${sidebar}" escapeXml="false" />

		<!-- 3. MAIN CONTENT AREA -->
		
		<main class="yt-feed-container container-fluid p-9" id="feedContainer">
		
		<div class="container mt-5 mb-5">
		<div class="row justify-content-center">
			<div class="col-lg-8">
				<!-- Main Card -->
				<div class="card shadow-sm border-0">
					<div class="card-header bg-white border-bottom py-3">
						<h4 class="mb-0 fw-bold">Upload Video</h4>
					</div>

					<div class="card-body p-4">
						<!-- Form Start -->
						<!-- IMPORTANT: enctype="multipart/form-data" is required for file uploads -->
						<form action="uploadvideo" method="post"
							enctype="multipart/form-data">

							<!-- Video File -->
							<div class="mb-4">
								<label for="videoFile" class="form-label fw-bold"> <i
									class="fas fa-video me-2"></i>Video File *
								</label> <input type="file" class="form-control" id="videoFile"
									name="videoFile" accept="video/*" required>
								<div class="form-text">Required. Max 1GB. MP4, AVI, MOV,
									WMV, FLV, MKV</div>
							</div>

							<hr>

							<!-- Title -->
							<div class="mb-3">
								<label for="title" class="form-label fw-bold"> <i
									class="fas fa-heading me-2"></i>Title *
								</label> <input type="text" class="form-control" id="title" name="title"
									placeholder="Enter video title" required maxlength="100">
							</div>

							<!-- Description -->
							<div class="mb-3">
								<label for="description" class="form-label fw-bold"> <i
									class="fas fa-align-left me-2"></i>Description
								</label>
								<textarea class="form-control" id="description"
									name="description" rows="4"
									placeholder="Enter video description" maxlength="5000"></textarea>
							</div>

							<!-- Thumbnail -->
							<div class="mb-3">
								<label for="thumbnail" class="form-label fw-bold"> <i
									class="fas fa-image me-2"></i>Thumbnail
								</label> <input type="file" class="form-control" id="thumbnail"
									name="thumbnail" accept="image/*">
								<div class="form-text">Optional. JPG, PNG, GIF</div>
							</div>

							<!-- Visibility -->
							<div class="mb-4">
								<label class="form-label fw-bold"> <i
									class="fas fa-eye me-2"></i>Visibility
								</label> <select class="form-select" name="visibility">
									<option value="public">Public - Anyone can watch</option>
									<option value="private">Private - Only you</option>
								</select>
							</div>

							<!-- Buttons -->
							<div class="row mt-4">
								<div class="col-md-6">
									<a href="home" class="btn btn-outline-secondary w-100">
										<i class="fas fa-times me-2"></i>Cancel
									</a>
								</div>
								<div class="col-md-6">
									<button type="submit" class="btn btn-primary w-100">
										<i class="fas fa-upload me-2"></i>Upload Video
									</button>
								</div>
							</div>

						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
		</main>
	</div>
	
    

    <!-- Bootstrap JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Simple Script for UI interactivity -->
    <script>
        function handleFileSelect(input) {
            const file = input.files[0];
            if (file) {
                document.getElementById('uploadIconContainer').classList.add('d-none');
                document.getElementById('fileSelectedContainer').classList.remove('d-none');
                document.getElementById('selectedFileName').textContent = file.name;
            }
        }

        function resetVideoInput() {
            const input = document.getElementById('videoFile');
            input.value = ''; // clear value
            document.getElementById('uploadIconContainer').classList.remove('d-none');
            document.getElementById('fileSelectedContainer').classList.add('d-none');
        }
    </script>
    <script type="text/javascript" src="sidebarscript.js"></script>
</body>
</html>