<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Watch Later - YouTube Clone</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="style.css">
    <style>
        .watch-later-container {
            margin-left: 240px;
            width: calc(100% - 240px);
            padding: 20px;
            transition: margin-left 0.3s ease;
        }
        @media (max-width: 768px) {
            .watch-later-container {
                margin-left: 0;
                width: 100%;
            }
        }
        .empty-state {
            padding: 80px 20px;
            text-align: center;
        }
        .empty-state-icon {
            font-size: 64px;
            color: #ccc;
            margin-bottom: 20px;
        }
        .remove-btn {
            position: absolute;
            top: 10px;
            right: 10px;
            background: rgba(0,0,0,0.7);
            color: white;
            border: none;
            border-radius: 50%;
            width: 32px;
            height: 32px;
            display: flex;
            align-items: center;
            justify-content: center;
            opacity: 0;
            transition: opacity 0.3s;
            z-index: 10;
        }
        .video-card:hover .remove-btn {
            opacity: 1;
        }
        .video-thumbnail {
            height: 180px;
            object-fit: cover;
            width: 100%;
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
        <main class="watch-later-container">
            <div class="container-fluid">
                <!-- Page Header -->
                <div class="mb-4">
                    <h1 class="mb-2">Watch Later</h1>
                    <p class="text-muted">
                        <c:choose>
                            <c:when test="${not empty savedCount && savedCount > 0}">
                                You have ${savedCount} video(s) saved to watch later
                            </c:when>
                            <c:otherwise>
                                Your watch later list is empty
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>
                
                <!-- Videos Grid -->
                <c:choose>
                    <c:when test="${not empty savedVideos && !savedVideos.isEmpty()}">
                        <div class="row">
                            <c:forEach var="video" items="${savedVideos}">
                                <div class="col-xl-3 col-lg-4 col-md-6 col-sm-6 mb-4">
                                    <div class="card h-100 position-relative video-card">
                                        <!-- Remove Button -->
                                        <form action="watch-later" method="post" class="remove-btn">
                                            <input type="hidden" name="action" value="remove">
                                            <input type="hidden" name="videoId" value="${video.videoId}">
                                            <button type="submit" class="btn btn-link p-0" 
                                                    onclick="return confirm('Remove from Watch Later?')"
                                                    title="Remove from Watch Later">
                                                <i class="fas fa-times"></i>
                                            </button>
                                        </form>
                                        
                                        <a href="${video.videoWebUrl}" class="text-decoration-none text-dark">
                                            <!-- Video Thumbnail -->
                                            <div class="position-relative">
                                                <c:choose>
                                                    <c:when test="${not empty video.videoThumbnailUrl}">
                                                        <img src="${video.videoThumbnailUrl}" 
                                                             alt="${video.videoName}" 
                                                             class="card-img-top video-thumbnail">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="video-thumbnail bg-secondary d-flex align-items-center justify-content-center">
                                                            <i class="fas fa-video fa-3x text-white"></i>
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            
                                            <div class="card-body">
                                                <!-- Video Title -->
                                                <h6 class="card-title mb-1" style="font-size: 0.95rem; line-height: 1.3;">
                                                    ${video.videoName}
                                                </h6>
                                                
                                                <!-- Channel Info -->
                                                <div class="d-flex align-items-center mb-2">
                                                    <c:choose>
                                                        <c:when test="${video.hasChannelImg()}">
                                                            <img src="${video.channelImgUrl}" 
                                                                 class="rounded-circle me-2" 
                                                                 width="24" 
                                                                 height="24" 
                                                                 alt="${video.channelName}"
                                                                 style="object-fit: cover;">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="rounded-circle me-2 bg-secondary d-flex align-items-center justify-content-center"
                                                                 style="width: 24px; height: 24px;">
                                                                <span class="text-white small">
                                                                    ${video.channelName.substring(0, 1).toUpperCase()}
                                                                </span>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <span class="text-muted small">${video.channelName}</span>
                                                </div>
                                                
                                                <!-- Video Stats -->
                                                <p class="card-text small text-muted mb-0">
                                                    ${video.getFormattedViews()} views • 
                                                    ${video.getFormattedDate()}
                                                </p>
                                                
                                                <!-- Watch Now Button -->
                                                <a href="${video.videoWebUrl}" class="btn btn-primary btn-sm w-100 mt-2">
                                                    <i class="fas fa-play me-1"></i> Watch Now
                                                </a>
                                            </div>
                                        </a>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        
                        <!-- Clear All Button -->
                        <div class="text-center mt-4">
                            <form action="watch-later" method="post" onsubmit="return confirm('Clear all videos from Watch Later?')">
                                <input type="hidden" name="action" value="clear">
                                <button type="submit" class="btn btn-outline-danger">
                                    <i class="fas fa-trash me-2"></i>Clear All
                                </button>
                            </form>
                        </div>
                    </c:when>
                    
                    <c:otherwise>
                        <!-- Empty State -->
                        <div class="empty-state">
                            <div class="empty-state-icon">
                                <i class="far fa-clock"></i>
                            </div>
                            <h3 class="mb-3">No videos in Watch Later</h3>
                            <p class="text-muted mb-4">
                                Save videos to watch later by clicking the "Watch Later" button<br>
                                under any video on YouTube.
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