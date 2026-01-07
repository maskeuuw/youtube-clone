<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
<!-- Load Bootstrap 5 CSS -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet"
	xintegrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
	crossorigin="anonymous">
<!-- Load Font Awesome for icons -->
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css"
	xintegrity="sha512-SnH5WK+bZxgPHs44uWIX+LLMDJq8p11/Hxux1W/K75w2xLz4e5eN4B7z7P2zT9hN2s5+Oq1t4g+p6h0yQ/Q=="
	crossorigin="anonymous" referrerpolicy="no-referrer" />
<!-- Load Roboto font (used in the provided styles) -->
<link
	href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap"
	rel="stylesheet">
<link rel="stylesheet" href="createchannelstyle.css">

</head>
<body>

	
	
	<div class="container py-5">
		<!-- Check if the user is logged in -->
		<!-- Check if the user is logged in -->
		<c:if test="${empty sessionScope.currentUser}">
			<div class="mx-auto yt-card shadow-lg" style="max-width: 450px;">
				<div class="card-header-content text-center">
					<h1 class="card-title">
						<i class="fa-brands fa-youtube"></i> Login Required
					</h1>
					<p class="card-subtitle">You need to login to create a channel.</p>
					<a href="login.jsp" class="btn btn-yt-primary mt-3">Login Now</a>
				</div>
			</div>
		</c:if>
		
		<!-- Channel Creation Card -->
		<div class="mx-auto yt-card shadow-lg" style="max-width: 450px;">
			<div class="card-header-content">
				<h1 class="card-title">
					<i class="fa-brands fa-youtube"></i> Start Your Journey
				</h1>
				<p class="card-subtitle">Create your channel to upload videos
					and connect with viewers.</p>
			</div>

			<!-- Display success or error messages -->
			<c:if test="${not empty requestScope.message}">
				<div
					class="
                            <c:if test="${requestScope.success == true}">server-success</c:if>
                            <c:if test="${requestScope.success == false}">server-error</c:if>">
					<c:out value="${requestScope.message}" />
				</div>
			</c:if>
			
			<form action="${pageContext.request.contextPath}/createchannel"
				method="POST">

				<!-- Hidden User ID field -->
				<input type="hidden" name="userId"
					value="${sessionScope.currentUser.userId}">

				<!-- Channel Name -->
				<div class="mb-4">
					<label for="channelName" class="form-label"> Channel Name </label>
					<input type="text" id="channelName" name="channelName"
						placeholder="e.g., The Gaming Den" required maxlength="50"
						class="form-control form-control-yt">
					<div class="form-text text-muted">This is the name viewers
						will see on your channel page.</div>
				</div>

				<!-- Channel Description -->
				<div class="mb-4">
					<label for="description" class="form-label"> Channel
						Description </label>
					<textarea id="description" name="description" rows="4"
						placeholder="Tell the world what your channel is about..."
						maxlength="500" class="form-control form-control-yt resize-none"></textarea>
					<div class="form-text text-muted">(Optional) A brief summary
						for the 'About' section. Max 500 characters.</div>
				</div>

				<!-- Submit Button -->
				<div class="d-grid">
					<button type="submit" class="btn btn-yt-primary">Create
						Channel</button>
				</div>
			</form>

		</div>

	</div>

	<!-- Load Bootstrap JS bundle -->
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
		xintegrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
		crossorigin="anonymous">
		
	</script>


</body>


</html>