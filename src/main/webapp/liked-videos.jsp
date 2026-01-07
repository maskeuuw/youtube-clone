<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Liked Videos - YouTube Clone</title>
<!-- Bootstrap 5 CSS -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
<!-- Font Awesome -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="stylesheet" href="style.css">
<style>
    body {
        background-color: #f9f9f9;
        font-family: 'Roboto', Arial, sans-serif;
        margin: 0;
        padding: 0;
        padding-top: 56px; /* Add padding for fixed header */
    }
    
    .main-container {
        margin-left: 240px;
        padding: 20px;
        background-color: white;
        min-height: calc(100vh - 56px); /* Subtract header height */
        margin-top: 0; /* Remove any top margin */
    }
    
    /* Fixed Header Styles */
    .header-fixed {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        z-index: 1030;
        height: 56px;
    }
    
    /* Adjust sidebar for header */
    .yt-sidebar {
        position: fixed;
        top: 56px; /* Start below header */
        left: 0;
        bottom: 0;
        z-index: 1020;
        width: 240px;
        overflow-y: auto;
    }
    
    .page-header {
        display: flex;
        align-items: center;
        padding-bottom: 20px;
        border-bottom: 1px solid #e0e0e0;
        margin-bottom: 30px;
        margin-top: 0;
    }
    
    .header-icon {
        width: 40px;
        height: 40px;
        color: #ff0000;
        margin-right: 16px;
    }
    
    .header-title {
        font-size: 24px;
        font-weight: 500;
        color: #0f0f0f;
        margin: 0;
    }
    
    .header-subtitle {
        color: #606060;
        font-size: 14px;
        margin: 4px 0 0 0;
    }
    
    .liked-count {
        background-color: #ff0000;
        color: white;
        padding: 2px 8px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: 500;
        margin-right: 8px;
    }
    
    /* Table-like List Styles */
    .video-table {
        width: 100%;
        border-collapse: collapse;
        background-color: white;
        box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        border-radius: 8px;
        overflow: hidden;
    }
    
    .table-header {
        background-color: #f8f9fa;
        border-bottom: 2px solid #dee2e6;
    }
    
    .table-header th {
        padding: 16px;
        text-align: left;
        font-weight: 600;
        color: #495057;
        font-size: 14px;
        text-transform: uppercase;
        letter-spacing: 0.5px;
    }
    
    .video-table-row {
        border-bottom: 1px solid #e9ecef;
        transition: background-color 0.2s;
    }
    
    .video-table-row:hover {
        background-color: #f8f9fa;
    }
    
    .video-table-row td {
        padding: 16px;
        vertical-align: top;
    }
    
    /* Thumbnail Column */
    .thumbnail-cell {
        width: 160px;
        padding-right: 0;
    }
    
    .video-thumbnail {
        width: 160px;
        height: 90px;
        border-radius: 4px;
        overflow: hidden;
        background-color: #000;
    }
    
    .video-thumbnail img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.3s;
    }
    
    .video-thumbnail:hover img {
        transform: scale(1.05);
    }
    
    /* Index Column */
    .index-cell {
        width: 60px;
        text-align: center;
        font-weight: 500;
        color: #6c757d;
        font-size: 14px;
    }
    
    /* Video Info Column */
    .video-info-cell {
        min-width: 300px;
    }
    
    .video-name {
        font-size: 16px;
        font-weight: 500;
        color: #0f0f0f;
        line-height: 1.4;
        margin-bottom: 8px;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
    }
    
    .video-name a {
        color: inherit;
        text-decoration: none;
    }
    
    .video-name a:hover {
        color: #065fd4;
    }
    
    .channel-name {
        font-size: 14px;
        color: #606060;
        margin-bottom: 8px;
        display: flex;
        align-items: center;
        gap: 8px;
    }
    
    .channel-name a {
        color: inherit;
        text-decoration: none;
    }
    
    .channel-name a:hover {
        color: #065fd4;
    }
    
    .channel-icon {
        width: 24px;
        height: 24px;
        border-radius: 50%;
        overflow: hidden;
    }
    
    .channel-icon img {
        width: 100%;
        height: 100%;
        object-fit: cover;
    }
    
    /* Description Column */
    .description-cell {
        min-width: 250px;
        max-width: 350px;
    }
    
    .video-description {
        font-size: 14px;
        color: #606060;
        line-height: 1.5;
        display: -webkit-box;
        -webkit-line-clamp: 3;
        -webkit-box-orient: vertical;
        overflow: hidden;
        margin: 0;
    }
    
    /* Stats Column */
    .stats-cell {
        width: 150px;
    }
    
    .video-stats {
        display: flex;
        flex-direction: column;
        gap: 4px;
    }
    
    .stat-item {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 13px;
        color: #606060;
    }
    
    .stat-icon {
        width: 14px;
        height: 14px;
        color: #606060;
    }
    
    .views-count {
        font-weight: 500;
    }
    
    .upload-date {
        font-size: 12px;
        color: #909090;
    }
    
    /* Actions Column */
    .actions-cell {
        width: 100px;
        text-align: center;
    }
    
    .action-buttons {
        display: flex;
        flex-direction: column;
        gap: 8px;
    }
    
    .watch-btn {
        background-color: #ff0000;
        color: white;
        border: none;
        padding: 6px 12px;
        border-radius: 4px;
        font-size: 13px;
        font-weight: 500;
        cursor: pointer;
        transition: background-color 0.2s;
        text-decoration: none;
        display: inline-block;
        text-align: center;
    }
    
    .watch-btn:hover {
        background-color: #cc0000;
        color: white;
    }
    
    .remove-btn {
        background: none;
        border: 1px solid #dc3545;
        color: #dc3545;
        padding: 6px 12px;
        border-radius: 4px;
        font-size: 13px;
        cursor: pointer;
        transition: all 0.2s;
    }
    
    .remove-btn:hover {
        background-color: #dc3545;
        color: white;
    }
    
    /* Empty State */
    .empty-state {
        text-align: center;
        padding: 80px 20px;
        max-width: 500px;
        margin: 0 auto;
    }
    
    .empty-icon {
        width: 80px;
        height: 80px;
        color: #d3d3d3;
        margin: 0 auto 24px;
    }
    
    .empty-title {
        font-size: 20px;
        font-weight: 500;
        color: #606060;
        margin-bottom: 12px;
    }
    
    .empty-description {
        color: #909090;
        margin-bottom: 24px;
        line-height: 1.5;
    }
    
    .explore-btn {
        background-color: #ff0000;
        color: white;
        border: none;
        padding: 10px 24px;
        border-radius: 20px;
        font-weight: 500;
        text-decoration: none;
        display: inline-block;
    }
    
    .explore-btn:hover {
        background-color: #cc0000;
        color: white;
    }
    
    /* Responsive */
    @media (max-width: 1200px) {
        .main-container {
            margin-left: 72px;
        }
        
        .yt-sidebar {
            width: 72px;
        }
        
        .description-cell {
            display: none;
        }
    }
    
    @media (max-width: 992px) {
        .stats-cell {
            display: none;
        }
        
        .video-thumbnail {
            width: 120px;
            height: 68px;
        }
    }
    
    @media (max-width: 768px) {
        body {
            padding-top: 0;
        }
        
        .main-container {
            margin-left: 0;
            padding: 16px;
            min-height: 100vh;
        }
        
        .yt-sidebar {
            display: none;
        }
        
        .table-header {
            display: none;
        }
        
        .video-table-row {
            display: flex;
            flex-direction: column;
            margin-bottom: 16px;
            border: 1px solid #e9ecef;
            border-radius: 8px;
            padding: 16px;
        }
        
        .video-table-row td {
            display: block;
            padding: 8px 0;
            width: 100% !important;
        }
        
        .index-cell {
            display: none;
        }
        
        .thumbnail-cell {
            order: 1;
        }
        
        .video-info-cell {
            order: 2;
        }
        
        .description-cell {
            order: 3;
            display: block;
        }
        
        .stats-cell {
            order: 4;
            display: block;
        }
        
        .actions-cell {
            order: 5;
            text-align: left;
        }
        
        .action-buttons {
            flex-direction: row;
            gap: 12px;
        }
    }
</style>
</head>
<body>
    <!-- header -->
    <div class="header-fixed">
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
    </div>

    <!-- sidebar -->
    <c:import var="sidebar" url="sidebar.jsp" />
    <c:out value="${sidebar}" escapeXml="false" />
    
    <!-- Main Content -->
    <main class="main-container">
        <!-- Page Header -->
        <div class="page-header">
            <div class="header-icon">
                <svg width="40" height="40" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M1 21h4V9H1v12zm22-11c0-1.1-.9-2-2-2h-6.31l.95-4.57.03-.32c0-.41-.17-.79-.44-1.06L14.17 1 7.59 7.59C7.22 7.95 7 8.45 7 9v10c0 1.1.9 2 2 2h9c.83 0 1.54-.5 1.84-1.22l3.02-7.05c.09-.23.14-.47.14-.73v-2z"/>
                </svg>
            </div>
            <div>
                <h1 class="header-title">Liked Videos</h1>
                <p class="header-subtitle">
                    <span class="liked-count">${likedCount} videos</span>
                    Your liked videos collection
                </p>
            </div>
        </div>
        
        <!-- Video Table -->
        <div class="video-table-container">
            <c:choose>
                <c:when test="${not empty likedVideos && likedCount > 0}">
                    <table class="video-table">
                        <thead class="table-header">
                            <tr>
                                <th class="index-cell">#</th>
                                <th class="thumbnail-cell">Thumbnail</th>
                                <th class="video-info-cell">Video Information</th>
                                <th class="description-cell">Description</th>
                                <th class="stats-cell">Statistics</th>
                                <th class="actions-cell">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="video" items="${likedVideos}" varStatus="status">
                                <tr class="video-table-row" id="video-row-${video.videoId}">
                                    <!-- Index Column -->
                                    <td class="index-cell">${status.index + 1}</td>
                                    
                                    <!-- Thumbnail Column -->
                                    <td class="thumbnail-cell">
                                        <a href="videowatch?v=${video.videoId}" class="video-thumbnail">
                                            <c:choose>
                                                <c:when test="${not empty video.base64Thumbnail}">
                                                    <img src="data:image/jpeg;base64,${video.base64Thumbnail}" 
                                                         alt="${video.videoName}"
                                                         onerror="this.src='https://via.placeholder.com/160x90?text=No+Thumbnail'">
                                                </c:when>
                                                <c:otherwise>
                                                    <img src="https://via.placeholder.com/160x90?text=No+Thumbnail" 
                                                         alt="${video.videoName}">
                                                </c:otherwise>
                                            </c:choose>
                                        </a>
                                    </td>
                                    
                                    <!-- Video Info Column -->
                                    <td class="video-info-cell">
                                        <h3 class="video-name">
                                            <a href="videowatch?v=${video.videoId}">${video.videoName}</a>
                                        </h3>
                                        <div class="channel-name">
                                            <c:if test="${not empty video.channelImg}">
                                                <a href="channel?channelId=${video.channelId}" class="channel-icon">
                                                    <img src="data:image/jpeg;base64,${video.channelImg}" 
                                                         alt="${video.channelName}"
                                                         onerror="this.src='https://via.placeholder.com/24?text=CH'">
                                                </a>
                                            </c:if>
                                            <a href="channel?channelId=${video.channelId}">${video.channelName}</a>
                                        </div>
                                    </td>
                                    
                                    <!-- Description Column -->
                                    <td class="description-cell">
                                        <p class="video-description">
                                            <c:choose>
                                                <c:when test="${not empty video.videoDesc && video.videoDesc.length() > 0}">
                                                    ${video.videoDesc}
                                                </c:when>
                                                <c:otherwise>
                                                    No description available
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                    </td>
                                    
                                    <!-- Stats Column -->
                                    <td class="stats-cell">
                                        <div class="video-stats">
                                            <div class="stat-item">
                                                <svg class="stat-icon" viewBox="0 0 24 24" fill="currentColor">
                                                    <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
                                                </svg>
                                                <span class="views-count">${video.formattedViews} views</span>
                                            </div>
                                            <div class="stat-item">
                                                <svg class="stat-icon" viewBox="0 0 24 24" fill="currentColor">
                                                    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z"/>
                                                </svg>
                                                <span class="upload-date">${video.formattedDate}</span>
                                            </div>
                                            <c:if test="${video.likeCount > 0}">
                                                <div class="stat-item">
                                                    <svg class="stat-icon" viewBox="0 0 24 24" fill="currentColor">
                                                        <path d="M1 21h4V9H1v12zm22-11c0-1.1-.9-2-2-2h-6.31l.95-4.57.03-.32c0-.41-.17-.79-.44-1.06L14.17 1 7.59 7.59C7.22 7.95 7 8.45 7 9v10c0 1.1.9 2 2 2h9c.83 0 1.54-.5 1.84-1.22l3.02-7.05c.09-.23.14-.47.14-.73v-2z"/>
                                                    </svg>
                                                    <span>${video.formattedLikes} likes</span>
                                                </div>
                                            </c:if>
                                        </div>
                                    </td>
                                    
                                    <!-- Actions Column -->
                                    <td class="actions-cell">
                                        <div class="action-buttons">
                                            <a href="videowatch?v=${video.videoId}" class="watch-btn">
                                                <i class="fas fa-play"></i> Watch
                                            </a>
                                            <button type="button" class="remove-btn" onclick="removeLike(${video.videoId})">
                                                <i class="fas fa-times"></i> Remove
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                
                <c:otherwise>
                    <!-- Empty State -->
                    <div class="empty-state">
                        <div class="empty-icon">
                            <svg width="80" height="80" viewBox="0 0 24 24" fill="currentColor">
                                <path d="M1 21h4V9H1v12zm22-11c0-1.1-.9-2-2-2h-6.31l.95-4.57.03-.32c0-.41-.17-.79-.44-1.06L14.17 1 7.59 7.59C7.22 7.95 7 8.45 7 9v10c0 1.1.9 2 2 2h9c.83 0 1.54-.5 1.84-1.22l3.02-7.05c.09-.23.14-.47.14-.73v-2z"/>
                            </svg>
                        </div>
                        <h3 class="empty-title">No liked videos yet</h3>
                        <p class="empty-description">
                            Videos you like will appear here. Start exploring and like videos you enjoy watching.
                        </p>
                        <a href="home" class="explore-btn">Explore Videos</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>
    
    <!-- Bootstrap JS Bundle with Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script type="text/javascript" src="script.js" defer></script>
    
</body>
</html>