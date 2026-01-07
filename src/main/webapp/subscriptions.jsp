<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Your Subscriptions - YouTube Clone</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="style.css">
    <style>
        .channel-card {
            transition: all 0.3s;
            border: 1px solid #ddd;
        }
        .channel-card:hover {
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            transform: translateY(-2px);
        }
        .channel-avatar {
            width: 80px;
            height: 80px;
            object-fit: cover;
        }
        /* Make main content start after sidebar */
        .main-content-wrapper {
            margin-left: 240px;
            flex: 1;
        }
        /* Responsive */
        @media (max-width: 768px) {
            .main-content-wrapper {
                margin-left: 0;
            }
        }
    </style>
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

    <!-- MAIN LAYOUT: Sidebar + Content -->
    <div class="d-flex">
        <!-- sidebar -->
        <c:import var="sidebar" url="sidebar.jsp" />
        <c:out value="${sidebar}" escapeXml="false" />

        <!-- MAIN CONTENT -->
        
        <main class="yt-feed-container container-fluid p-3" id="feedContainer">
            <div class="container">
                <!-- Page Header -->
                <div class="mb-4">
                    <h1 class="mb-2">Subscriptions</h1>
                    <p class="text-muted">
                        <c:choose>
                            <c:when test="${not empty subscriptionCount && subscriptionCount > 0}">
                                You're subscribed to ${subscriptionCount} channel(s)
                            </c:when>
                            <c:otherwise>
                                You're not subscribed to any channels yet
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>
                
                <!-- Subscriptions Grid -->
                <c:choose>
                    <c:when test="${not empty subscribedChannels && !subscribedChannels.isEmpty()}">
                        <div class="row">
                            <c:forEach var="channel" items="${subscribedChannels}">
                                <div class="col-md-3 col-sm-6 mb-4">
                                    <div class="card channel-card h-100">
                                        <div class="card-body text-center">
                                            <!-- Channel Avatar -->
                                            <a href="channel?channelId=${channel.channelId}" class="text-decoration-none text-dark">
                                                <c:choose>
                                                    <c:when test="${channel.hasImage()}">
                                                        <img src="data:image/jpeg;base64,${channel.channelImgBase64}"
                                                             class="rounded-circle channel-avatar mb-3"
                                                             alt="${channel.channelName}">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="rounded-circle channel-avatar mb-3 mx-auto d-flex align-items-center justify-content-center bg-secondary">
                                                            <span class="h4 text-white mb-0">
                                                                ${channel.channelName.substring(0, 1).toUpperCase()}
                                                            </span>
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                                
                                                <!-- Channel Name -->
                                                <h5 class="card-title mb-1">${channel.channelName}</h5>
                                                
                                                <!-- Subscriber Count -->
                                                <p class="text-muted small mb-2">
                                                    <c:choose>
                                                        <c:when test="${channel.subscriberCount > 0}">
                                                            ${channel.subscriberCount} subscribers
                                                        </c:when>
                                                        <c:otherwise>
                                                            No subscribers yet
                                                        </c:otherwise>
                                                    </c:choose>
                                                </p>
                                            </a>
                                            
                                            <!-- Unsubscribe Form -->
                                            <form action="subscriptions" method="post" class="mt-3">
                                                <input type="hidden" name="action" value="unsubscribe">
                                                <input type="hidden" name="channelId" value="${channel.channelId}">
                                                <button type="submit" class="btn btn-danger btn-sm w-100" 
                                                        onclick="return confirm('Are you sure you want to unsubscribe from ${channel.channelName}?')">
                                                    <i class="fas fa-bell-slash me-1"></i> Unsubscribe
                                                </button>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    
                    <c:otherwise>
                        <!-- Empty State -->
                        <div class="text-center py-5">
                            <div class="mb-4">
                                <i class="fas fa-users fa-4x text-muted"></i>
                            </div>
                            <h3 class="mb-3">No subscriptions yet</h3>
                            <p class="text-muted mb-4">
                                Subscribe to your favorite channels to see them here.
                            </p>
                            <a href="home" class="btn btn-primary">
                                <i class="fas fa-home me-2"></i>Browse Videos
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
                
                <!-- Error Message (if any) -->
                <c:if test="${not empty param.error}">
                    <div class="alert alert-danger alert-dismissible fade show mt-3" role="alert">
                        There was an error processing your request. Please try again.
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
            </div>
        </main>
    </div>
	
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="script.js" defer></script>
</body>
</html>