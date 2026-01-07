<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Login - YouTube Style</title>
<!-- Bootstrap 5 CSS -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
<!-- Font Awesome for icons -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<!-- Google Fonts - Roboto (YouTube uses Roboto) -->
<link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
<link rel="stylesheet" href="loginstyle.css">
</head>
<body>
//
    <div class="login-container">
        <div class="card login-card">
            <!-- Login Header -->
            <div class="login-header">
            	<a href="${pageContext.request.contextPath}/home" class="text-decoration-none">
					<h1 class="login-title" >
						
						<i class="fab fa-youtube" ></i> YouTube
					</h1>
				</a>
                <p class="login-subtitle">Sign in to continue</p>
            </div>
            
            <!-- Display server-side error messages -->
            <c:if test="${not empty errorMessage}">
                <div class="server-error show">
                    <i class="fas fa-exclamation-circle me-2"></i>${errorMessage}
                </div>
            </c:if>
            
            <!-- Login Form -->
            <form id="loginForm" action="login" method="post">
                <!-- Email Input -->
                <div class="mb-3">
                    <label for="email" class="form-label">Email</label>
                    <input type="email" class="form-control-yt" 
                           id="email" name="email" 
                           placeholder="Enter your email" 
                           value="${param.email}" 
                           required>
                    <div class="error-message" id="emailError">
                        Please enter a valid email address.
                    </div>
                </div>
                
                <!-- Password Input -->
                <div class="mb-3">
                    <label for="password" class="form-label">Password</label>
                    <div class="password-wrapper">
                        <input type="password" class="form-control-yt" 
                               id="password" name="password" 
                               placeholder="Enter your password" 
                               required>
                        <button type="button" class="password-toggle" id="togglePassword">
                            <i class="far fa-eye"></i>
                        </button>
                    </div>
                    <div class="error-message" id="passwordError">
                        Please enter your password.
                    </div>
                </div>
                
                
                
                <!-- Submit Button -->
                <button type="submit" class="btn btn-yt-primary" id="loginBtn">
                    Sign In
                </button>
            </form>
            
            <!-- Sign Up Link -->
            <form action="signup" method="get">
				<div class="signup-link">
					Don't have an account? <a href="signup">Sign up here</a>
				</div>
			</form>
        </div>
    </div>

    <!-- Bootstrap JS Bundle with Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
    
    <script src="loginscript.js" defer>
 // Pre-fill email if there's a session error (for better UX)
    <c:if test="${not empty param.email}">
        emailInput.value = "${param.email}";
    </c:if>
    </script>
</body>
</html>