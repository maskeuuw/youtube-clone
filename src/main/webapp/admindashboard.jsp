<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Insert title here</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">
<style>
.sidebar {
	height: 100vh;
	background: #212529;
	color: white;
	padding-top: 20px;
}

.sidebar a {
	color: #adb5bd;
	text-decoration: none;
	display: block;
	padding: 10px 20px;
}

.sidebar a:hover {
	background: #343a40;
	color: white;
}

.stat-card {
	border-radius: 10px;
	border: none;
	transition: 0.3s;
}

.stat-card:hover {
	transform: translateY(-5px);
}
</style>
</head>
<body>

	<div class="container-fluid">
		<div class="row">
			<nav class="col-md-2 d-none d-md-block sidebar">
				<h4 class="text-center mb-4">YouTube Admin</h4>
				<a href="#">Overview</a> <a href="#users">Manage Users</a> <a
					href="#videos">Monitor Videos</a>
				<hr>
				<a href="logout" class="text-danger">Logout</a>
			</nav>

			<main class="col-md-10 ms-sm-auto px-md-4">
				<div
					class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
					<h1 class="h2">Admin Control Panel</h1>
				</div>

				<div class="row mb-4">
					<div class="col-md-4">
						<div class="card bg-primary text-white stat-card p-3">
							<h5>Total Users</h5>
							<h2>${userList.size()}</h2>
						</div>
					</div>
					<div class="col-md-4">
						<div class="card bg-success text-white stat-card p-3">
							<h5>Total Videos</h5>
							<h2>${videoList.size()}</h2>
						</div>
					</div>
					<div class="col-md-4">
						<div class="card bg-warning text-dark stat-card p-3">
							<h5>Pending Reports</h5>
							<h2>${reportCount != null ? reportCount : 0}</h2>
						</div>
					</div>
				</div>

				<h3 id="users" class="mt-5">Manage Users</h3>
				<div class="table-responsive bg-white p-3 shadow-sm rounded">
					<table class="table table-hover">
						<thead class="table-dark">
							<tr>
								<th>ID</th>
								<th>Username</th>
								<th>Email</th>
								<th>Role</th>
								<th>Status</th>
								<th>Action</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="user" items="${userList}">
								<tr>
									<td>${user.userId}</td>
									<td>${user.userName}</td>
									<td>${user.email}</td>
									<td><span class="badge bg-secondary">${user.userRole}</span></td>
									<td><span
										class="badge ${user.userStatus == 'active' ? 'bg-success' : 'bg-danger'}">
											${user.userStatus} </span>
									</td>
									<td>
										<c:choose>
											<c:when test="${user.userStatus == 'active'}">
												<form action="admindashboard" method="POST"
													style="display: inline;">
													<input type="hidden" name="action" value="ban"> <input
														type="hidden" name="userId" value="${user.userId}">
													<button type="submit"
														class="btn btn-sm btn-outline-danger text-dark">Ban
														User</button>
												</form>
											</c:when>
											<c:otherwise>
												<form action="admindashboard" method="POST"
													style="display: inline;">
													<input type="hidden" name="action" value="unban"> <input
														type="hidden" name="userId" value="${user.userId}">
													<button type="submit"
														class="btn btn-sm btn-outline-success text-dark">Unban</button>
												</form>
											</c:otherwise>
										</c:choose>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>

			</main>
		</div>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>