<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Sign Up - YouTube</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
<link rel="stylesheet" href="signupstyle.css">

</head>
<body>
    <div class="signup-container">
        <div class="card signup-card">
            <div class="signup-header">
            	<a href="${pageContext.request.contextPath}/home" class="text-decoration-none">
                <h1 class="signup-title">
						<i class="fab fa-youtube"></i> YouTube
					</h1>
				</a>
                <p class="signup-subtitle">Create your YouTube account</p>
            </div>
            
            <c:if test="${not empty errorMessage}">
                <div class="server-error">
                    <i class="fas fa-exclamation-circle me-2"></i>${errorMessage}
                </div>
            </c:if>
            
            <c:if test="${not empty successMessage}">
                <div class="server-success">
                    <i class="fas fa-check-circle me-2"></i>${successMessage}
                </div>
            </c:if>
            
            <form id="signupForm" action="signup" method="post">
                
                <div class="mb-3">
                    <div class="field-group">
                        <label for="firstName" class="form-label">Name</label>
                        <input type="text" class="form-control-yt" 
                               id="name" name="name" 
                               placeholder="name" 
                               value="${param.name}" 
                               required>
                    </div>
    
                </div>
                
                <div class="mb-3">
                    <label for="email" class="form-label">Email</label>
                    <input type="email" class="form-control-yt" 
                           id="email" name="email" 
                           placeholder="Enter your email" 
                           value="${param.email}" 
                           required>
                    </div>
                
                <div class="mb-3">
                    <label for="password" class="form-label">Password</label>
                    <div class="password-wrapper">
                        <input type="password" class="form-control-yt" 
                               id="password" name="password" 
                               placeholder="Create a password" 
                               required
                               minlength="8">
                        <button type="button" class="password-toggle" id="togglePassword">
                            <i class="far fa-eye"></i>
                        </button>
                    </div>
                    
                    <div class="password-requirements">
                        Use 8 or more characters with a mix of letters, numbers & symbols.
                    </div>
                </div>
                
                <div class="mb-4">
                    <label for="confirmPassword" class="form-label">Confirm Password</label>
                    <div class="password-wrapper">
                        <input type="password" class="form-control-yt" 
                               id="confirmPassword" name="confirmPassword" 
                               placeholder="Confirm your password" 
                               required
                               minlength="8">
                        <button type="button" class="password-toggle" id="toggleConfirmPassword">
                            <i class="far fa-eye"></i>
                        </button>
                    </div>
                </div>
                
                <button type="submit" class="btn btn-yt-primary">
                    Create Account
                </button>
            </form>
            
            <form action="login" method="get">
				<div class="login-link">
					Already have an account? <a href="login">Sign in here</a>
				</div>
			</form>
        </div>
    </div>

    <script src="signupscript.js" defer></script>
</body>
</html>