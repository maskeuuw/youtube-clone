<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Dynamic Content View</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC"
	crossorigin="anonymous">
<link rel="stylesheet" href="style.css">

</head>
<body>

	<!-- header -->
	<c:choose>
        <%-- CASE 1: User is Logged In (sessionScope.currentUser is NOT null) --%>
        <c:when test="${not empty sessionScope.currentUser}">
            <c:import var="userheader" url="headerforuser.jsp"/>
            <c:out value="${userheader}" escapeXml="false"/>
        </c:when>

        <%-- CASE 2: User is a Guest (sessionScope.currentUser IS null) --%>
        <c:otherwise>
            <c:import var="guestheader" url="headerforguest.jsp"/>
            <c:out value="${guestheader}" escapeXml="false"/>
        </c:otherwise>
    </c:choose>
	
	<!-- 2. MAIN LAYOUT: Sidebar (Guide) + Content Feed -->
	<!-- Added inline style to ensure minimum screen height below fixed header -->
	<div class="d-flex pt-5" style="min-height: calc(100vh - 56px);">

		<!-- sidebar -->
		<c:import var="sidebar" url="sidebar.html"/>
		<c:out value="${sidebar}" escapeXml="false"/>	
		
		<!-- 3. MAIN CONTENT AREA -->
		<!-- IMPORTANT: We use 'container-fluid p-4 content-with-sidebar' to fix the layout issue with the fixed sidebar -->
		<main class="container-fluid p-4 content-with-sidebar" id="feedContainer">
			
            <c:choose>
                <%-- If the Java Controller set mainContentPage to 'channel' --%>
                <c:when test="${requestScope.mainContentPage == 'channel'}">
                    
                    <h1 class="mb-4">Channel Profile</h1>
                    
                    <%-- DYNAMIC CONTENT: Include the channel-specific content file --%>
                    <c:import var="userchannel" url="user-channel-contents.jsp" />
                    <c:out value="${userchannel}" escapeXml="false" />
                    
                </c:when>
                
                <%-- Default case (mainContentPage is 'home' or not set) --%>
                <c:otherwise>
                    
                    <h1 class="mb-4">Home Feed</h1>
                    <c:if test="${empty sessionScope.currentUser}">
                        <h2 class="text-muted">Log in to enjoy full features</h2>
                    </c:if>
                    
                    <div
                        class="row row-cols-1 row-cols-sm-2 row-cols-lg-3 row-cols-xl-4 g-4"
                        id="video-grid">
                        <!-- Placeholder video cards for the home feed -->
                        <div class="col"><div class="p-3 bg-white border rounded shadow-sm">Default Video Card 1</div></div>
                        <div class="col"><div class="p-3 bg-white border rounded shadow-sm">Default Video Card 2</div></div>
                        <div class="col"><div class="p-3 bg-white border rounded shadow-sm">Default Video Card 3</div></div>
                    </div>
                    
                </c:otherwise>
            </c:choose>

		</main>
	</div>
 
	<!-- Bootstrap JS Bundle -->
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
		crossorigin="anonymous"></script>

	<!-- JavaScript for Data Injection and Sidebar Logic -->
	<script src="script.js" defer></script> 
</body>
</html>