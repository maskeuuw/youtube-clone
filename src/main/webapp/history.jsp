<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Watch History - YouTube Clone</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="style.css">
    <style>
        .history-container {
            margin-left: 240px;
            width: calc(100% - 240px);
            padding: 20px;
            transition: margin-left 0.3s ease;
        }
        @media (max-width: 768px) {
            .history-container {
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
        .history-table {
            background: white;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        .history-table th {
            background-color: #f8f9fa;
            border-bottom: 2px solid #dee2e6;
            font-weight: 600;
        }
        .history-table td {
            vertical-align: middle;
        }
        .video-thumbnail-table {
            width: 120px;
            height: 68px;
            object-fit: cover;
            border-radius: 4px;
        }
        .channel-avatar-table {
            width: 24px;
            height: 24px;
            object-fit: cover;
            margin-right: 8px;
        }
        .action-buttons {
            white-space: nowrap;
        }
        .remove-btn-table {
            background: none;
            border: none;
            color: #dc3545;
            cursor: pointer;
            padding: 4px 8px;
            border-radius: 4px;
            transition: background-color 0.2s;
        }
        .remove-btn-table:hover {
            background-color: #f8d7da;
        }
        .video-title-link {
            color: #0d6efd;
            text-decoration: none;
            font-weight: 500;
        }
        .video-title-link:hover {
            text-decoration: underline;
        }
        .history-header {
            border-bottom: 1px solid #e0e0e0;
            padding-bottom: 15px;
            margin-bottom: 20px;
        }
        .table-responsive {
            border-radius: 8px;
            overflow: hidden;
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
        <main class="history-container">
            <div class="container-fluid">
                <!-- Page Header -->
                <div class="history-header">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h1 class="mb-2">Watch History</h1>
							<p class="text-muted mb-0">
								<c:choose>
									<c:when test="${not empty historyCount && historyCount > 0}">
                        ${historyCount} video(s) watched by 
                        <c:if test="${not empty sessionScope.userName}">
                            ${sessionScope.userName}
                        </c:if>
									</c:when>
									<c:otherwise>
                        No watch history for 
                        <c:if test="${not empty sessionScope.userName}">
                            ${sessionScope.userName}
                        </c:if>
									</c:otherwise>
								</c:choose>
							</p>
						</div>
                        
                        <c:if test="${not empty historyCount && historyCount > 0}">
                            <div class="d-flex gap-2">
                                <form action="history" method="post" onsubmit="return confirm('Clear all watch history? This cannot be undone.')">
                                    <input type="hidden" name="action" value="clear">
                                    <button type="submit" class="btn btn-outline-danger">
                                        <i class="fas fa-trash me-2"></i>Clear all
                                    </button>
                                </form>
                                <a href="home" class="btn btn-primary">
                                    <i class="fas fa-home me-2"></i>Watch More
                                </a>
                            </div>
                        </c:if>
                    </div>
                </div>
                
                <!-- History Table -->
                <c:choose>
                    <c:when test="${not empty historyVideos && !historyVideos.isEmpty()}">
                        <div class="table-responsive">
                            <table class="table history-table table-hover">
                                <thead>
                                    <tr>
                                        <th width="140">Video</th>
                                        <th>Title & Channel</th>
                                        <th width="120">Views</th>
                                        <th width="120">Date</th>
                                        <th width="100">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="video" items="${historyVideos}" varStatus="status">
                                        <tr>
                                            <!-- Thumbnail -->
                                            <td>
                                                <a href="${video.videoWebUrl}" class="d-block">
                                                    <c:choose>
                                                        <c:when test="${not empty video.videoThumbnailUrl}">
                                                            <img src="${video.videoThumbnailUrl}" 
                                                                 alt="${video.videoName}" 
                                                                 class="video-thumbnail-table">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="video-thumbnail-table bg-secondary d-flex align-items-center justify-content-center">
                                                                <i class="fas fa-video text-white"></i>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </a>
                                            </td>
                                            
                                            <!-- Title & Channel -->
                                            <td>
                                                <div class="d-flex flex-column">
                                                    <a href="${video.videoWebUrl}" class="video-title-link mb-1">
                                                        ${video.videoName}
                                                    </a>
                                                    <div class="d-flex align-items-center">
                                                        <c:choose>
                                                            <c:when test="${video.hasChannelImg()}">
                                                                <img src="${video.channelImgUrl}" 
                                                                     class="rounded-circle channel-avatar-table" 
                                                                     alt="${video.channelName}">
                                                            </c:when>
                                                            <c:otherwise>
                                                                <div class="rounded-circle channel-avatar-table bg-secondary d-flex align-items-center justify-content-center">
                                                                    <span class="text-white small">
                                                                        ${video.channelName != null ? video.channelName.substring(0, 1).toUpperCase() : 'C'}
                                                                    </span>
                                                                </div>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <span class="text-muted small">${video.channelName}</span>
                                                    </div>
                                                </div>
                                            </td>
                                            
                                            <!-- Views -->
                                            <td class="text-muted">
                                                ${video.getFormattedViews()} views
                                            </td>
                                            
                                            <!-- Date -->
                                            <td class="text-muted">
                                                ${video.getFormattedDate()}
                                            </td>
                                            
                                            <!-- Actions -->
                                            <td class="action-buttons">
                                                <div class="d-flex gap-2">
                                                    <!-- Watch Again -->
                                                    <a href="${video.videoWebUrl}" class="btn btn-sm btn-outline-primary" 
                                                       title="Watch Again">
                                                        <i class="fas fa-play"></i>
                                                    </a>
                                                    
                                                    <!-- Remove from History -->
                                                    <form action="history" method="post" class="d-inline">
                                                        <input type="hidden" name="action" value="remove">
                                                        <input type="hidden" name="videoId" value="${video.videoId}">
                                                        <button type="submit" class="btn btn-sm btn-outline-danger" 
                                                                onclick="return confirm('Remove this video from history?')"
                                                                title="Remove from History">
                                                            <i class="fas fa-times"></i>
                                                        </button>
                                                    </form>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        
                        <!-- Table Footer
                        <div class="d-flex justify-content-between align-items-center mt-3">
                            <div class="text-muted small">
                                Showing ${historyCount} video(s) in your history
                            </div>
                            <div>
                                <form action="history" method="post" onsubmit="return confirm('Clear all watch history? This cannot be undone.')">
                                    <input type="hidden" name="action" value="clear">
                                    <button type="submit" class="btn btn-outline-danger btn-sm">
                                        <i class="fas fa-trash me-1"></i>Clear All History
                                    </button>
                                </form>
                            </div>
                        </div> -->
                    </c:when>
                    
                    <c:otherwise>
                        <!-- Empty State -->
                        <div class="empty-state">
                            <div class="empty-state-icon">
                                <i class="far fa-clock"></i>
                            </div>
                            <h3 class="mb-3">No watch history yet</h3>
                            <p class="text-muted mb-4">
                                Videos you watch will appear here in a table format.<br>
                                Start watching videos to build your history.
                            </p>
                            <div class="d-flex justify-content-center gap-3">
                                <a href="home" class="btn btn-primary">
                                    <i class="fas fa-home me-2"></i>Browse Videos
                                </a>
                               <!-- <a href="trending" class="btn btn-outline-secondary">
                                    <i class="fas fa-fire me-2"></i>Trending Videos
                                </a> --> 
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
                
                <!-- Messages -->
                <c:if test="${not empty param.error}">
                    <div class="alert alert-danger alert-dismissible fade show mt-3" role="alert">
                        <c:choose>
                            <c:when test="${param.error == 'invalid_id'}">
                                Invalid video ID. Please try again.
                            </c:when>
                            <c:otherwise>
                                There was an error processing your request. Please try again.
                            </c:otherwise>
                        </c:choose>
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                
                <c:if test="${not empty param.success}">
                    <div class="alert alert-success alert-dismissible fade show mt-3" role="alert">
                        <c:choose>
                            <c:when test="${param.success == 'removed'}">
                                <i class="fas fa-check-circle me-2"></i>Video removed from history successfully.
                            </c:when>
                            <c:when test="${param.success == 'cleared'}">
                                <i class="fas fa-check-circle me-2"></i>History cleared successfully.
                            </c:when>
                        </c:choose>
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
            </div>
        </main>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- JavaScript for better UX -->
    <script>
    document.addEventListener('DOMContentLoaded', function() {
        // Auto-dismiss alerts after 5 seconds
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(alert => {
            setTimeout(() => {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }, 5000);
        });
        
        // Add confirmation for all remove buttons
        const removeButtons = document.querySelectorAll('.btn-outline-danger[type="submit"]');
        removeButtons.forEach(button => {
            button.addEventListener('click', function(e) {
                const form = this.closest('form');
                const action = form.querySelector('input[name="action"]').value;
                
                let message = '';
                if (action === 'remove') {
                    message = 'Remove this video from your watch history?';
                } else if (action === 'clear') {
                    message = 'Clear all watch history? This cannot be undone.';
                }
                
                if (message && !confirm(message)) {
                    e.preventDefault();
                }
            });
        });
        
        // Add hover effect to table rows
        const tableRows = document.querySelectorAll('.history-table tbody tr');
        tableRows.forEach(row => {
            row.addEventListener('mouseenter', function() {
                this.style.backgroundColor = '#f8f9fa';
            });
            row.addEventListener('mouseleave', function() {
                this.style.backgroundColor = '';
            });
        });
    });
    </script>
</body>
</html>