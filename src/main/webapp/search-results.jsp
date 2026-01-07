<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.VideoBean" %>
<%@ page import="Model.ChannelBean" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Search Results for "${searchQuery}"</title>
    
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="style.css">
    <style>
        body {
            background-color: #f8f9fa;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
        }
        
        /* Main Content */
        .main-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px 15px;
        }
        
        /* Search Info */
        .search-info {
            padding: 20px 0 15px 0;
            border-bottom: 1px solid #dee2e6;
            margin-bottom: 25px;
        }
        
        /* Results List */
        .results-list {
            list-style: none;
            padding: 0;
        }
        
        .result-item {
            padding: 20px 0;
            border-bottom: 1px solid #e9ecef;
        }
        
        .result-item:last-child {
            border-bottom: none;
        }
        
        /* Video Result - Entire row is clickable */
        .video-result-wrapper {
            position: relative;
        }
        
        .video-result-link {
            display: flex;
            gap: 20px;
            align-items: flex-start;
            text-decoration: none;
            color: inherit;
            padding: 10px;
            border-radius: 8px;
            transition: background-color 0.2s ease;
            cursor: pointer;
        }
        
        .video-result-link:hover {
            background-color: #f0f0f0;
        }
        
        .video-thumb {
            width: 360px;
            height: 202px;
            object-fit: cover;
            border-radius: 12px;
            background-color: #000;
            flex-shrink: 0;
        }
        
        .video-details {
            flex: 1;
            display: flex;
            flex-direction: column;
            gap: 8px;
        }
        
        .video-title {
            font-size: 18px;
            font-weight: 500;
            color: #0f0f0f;
            text-decoration: none;
            display: block;
            line-height: 1.4;
            margin-bottom: 4px;
        }
        
        .video-meta-container {
            display: flex;
            flex-direction: column;
            gap: 6px;
        }
        
        .video-meta {
            color: #606060;
            font-size: 14px;
            line-height: 1.4;
        }
        
        .video-description {
            color: #606060;
            font-size: 14px;
            margin-top: 8px;
            line-height: 1.5;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        
        /* Channel link inside video result - should NOT trigger video link */
        .channel-link-wrapper {
            z-index: 2;
            position: relative;
        }
        
        .channel-link {
            color: #606060;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            padding: 2px 6px;
            border-radius: 4px;
            transition: all 0.2s;
        }
        
        .channel-link:hover {
            color: #0d6efd;
            background-color: #e9ecef;
            text-decoration: underline;
        }
        
        /* Channel Result - Entire row is clickable */
        .channel-result-wrapper {
        	
            position: relative;
            
        }
        
        .channel-result-link {
            display: flex;
            align-items: center;
            gap: 24px;
            padding: 20px;
            background-color: #f8f9fa;
            border-radius: 12px;
            border: 1px solid #dee2e6;
            text-decoration: none;
            color: inherit;
            cursor: pointer;
            transition: all 0.2s ease;
        }
        
        .channel-result-link:hover {
            background-color: #f0f0f0;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        
        .channel-avatar {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            object-fit: cover;
            border: 3px solid #dee2e6;
            flex-shrink: 0;
        }
        
        .channel-details {
            flex: 1;
            display: flex;
            flex-direction: column;
            gap: 8px;
        }
        
        .channel-name {
            font-size: 20px;
            font-weight: 600;
            margin-bottom: 4px;
            color: #0f0f0f;
            text-decoration: none;
            display: block;
        }
        
        .channel-stats {
            color: #606060;
            font-size: 15px;
            line-height: 1.4;
        }
        
        .channel-description {
            color: #606060;
            font-size: 14px;
            line-height: 1.5;
            margin-top: 8px;
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        
        /* Filters */
        .filter-bar {
            display: flex;
            gap: 12px;
            margin-bottom: 25px;
            flex-wrap: wrap;
            padding-bottom: 15px;
            border-bottom: 1px solid #dee2e6;
        }
        
        .filter-btn {
            padding: 8px 20px;
            border: 1px solid #dee2e6;
            background: white;
            border-radius: 20px;
            color: #495057;
            text-decoration: none;
            font-size: 15px;
            font-weight: 500;
            transition: all 0.2s;
        }
        
        .filter-btn:hover {
            background-color: #f8f9fa;
            border-color: #adb5bd;
        }
        
        .filter-btn.active {
            background-color: #0d6efd;
            color: white;
            border-color: #0d6efd;
        }
        
        /* No Results */
        .no-results {
            text-align: center;
            padding: 80px 20px;
        }
        
        .no-results-icon {
            font-size: 64px;
            color: #adb5bd;
            margin-bottom: 20px;
        }
        
        /* Pagination */
        .pagination-container {
            margin-top: 40px;
            padding-top: 20px;
            border-top: 1px solid #dee2e6;
        }
        
        /* Responsive */
        @media (max-width: 992px) {
            .video-thumb {
                width: 300px;
                height: 169px;
            }
        }
        
        @media (max-width: 768px) {
            .video-result-link {
                flex-direction: column;
            }
            
            .video-thumb {
                width: 100%;
                height: 200px;
            }
            
            .channel-result-link {
                flex-direction: column;
                text-align: center;
                padding: 25px 20px;
            }
            
            .channel-avatar {
                width: 120px;
                height: 120px;
            }
            
            .channel-details {
                text-align: center;
            }
        }
        
        @media (max-width: 576px) {
            .video-thumb {
                height: 180px;
            }
            
            .channel-avatar {
                width: 100px;
                height: 100px;
            }
            
            .main-container {
                padding: 15px 10px;
            }
            
            .filter-bar {
                justify-content: center;
            }
        }
        
        /* Separator style */
        .meta-separator {
            margin: 0 6px;
            color: #606060;
        }
        
        /* Prevent default link styling for clickable rows */
        .clickable-row {
            cursor: pointer;
        }
    </style>
</head>
<body>
    <!-- Header -->
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
    
    <!-- Main Content -->
    <div class="main-container">
        <!-- Search Info -->
        <div class="search-info">
            <h4 class="mb-2 fw-semibold">Search Results for "${searchQuery}"</h4>
            <p class="text-muted mb-0">
                Found ${totalResults} results
                <c:if test="${not empty totalVideos and not empty totalChannels}">
                    (${totalVideos} videos, ${totalChannels} channels)
                </c:if>
            </p>
        </div>

        <!-- Filter Bar -->
        <div class="filter-bar">
            <a href="?q=${searchQuery}&filter=all" 
               class="filter-btn ${currentFilter == 'all' or empty currentFilter ? 'active' : ''}">
                <i class="fas fa-layer-group me-1"></i> All
            </a>
            <a href="?q=${searchQuery}&filter=videos" 
               class="filter-btn ${currentFilter == 'videos' ? 'active' : ''}">
                <i class="fas fa-play-circle me-1"></i> Videos
            </a>
            <a href="?q=${searchQuery}&filter=channels" 
               class="filter-btn ${currentFilter == 'channels' ? 'active' : ''}">
                <i class="fas fa-user-circle me-1"></i> Channels
            </a>
        </div>

        <!-- No Results -->
        <c:if test="${noResults}">
            <div class="no-results">
                <div class="no-results-icon">
                    <i class="fas fa-search"></i>
                </div>
                <h4 class="mb-3">No results found for "${searchQuery}"</h4>
                <p class="text-muted mb-4">Try different keywords or check your spelling</p>
                
                <c:if test="${not empty trendingSearches}">
                    <div class="mt-4">
                        <h5 class="mb-3">Try these trending searches:</h5>
                        <div class="d-flex flex-wrap justify-content-center gap-2">
                            <c:forEach var="trending" items="${trendingSearches}">
                                <a href="?q=${trending}" class="btn btn-outline-secondary btn-sm">
                                    ${trending}
                                </a>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>
            </div>
        </c:if>

        <!-- Results List -->
        <ul class="results-list">
            <!-- Video Results -->
            <c:if test="${(currentFilter == 'all' or currentFilter == 'videos' or empty currentFilter) and not empty searchResults}">
                <c:forEach var="video" items="${searchResults}">
                    <li class="result-item">
                        <div class="video-result-wrapper clickable-row" onclick="window.location='<c:url value='/videowatch?v=${video.videoId}'/>'">
                            <div class="video-result-link">
                                <!-- Thumbnail -->
                                <c:choose>
                                    <c:when test="${not empty video.videoThumbnail}">
                                        <img src="data:image/jpeg;base64,${video.videoThumbnail}" 
                                             alt="${video.videoName}" 
                                             class="video-thumb">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="video-thumb bg-secondary d-flex align-items-center justify-content-center">
                                            <i class="fas fa-play-circle text-white fs-1"></i>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                                
                                <!-- Video Details-->
                                <div class="video-details">
                                    <h3 class="video-title">${video.videoName}</h3>
                                    
                                    <div class="video-meta-container">
                                        <div class="video-meta">
                                            
                                            <span>${video.viewCount} views</span>
                                            <span class="meta-separator">•</span>
                                            
                                            <span>
                                                <c:choose>
                                                    <c:when test="${not empty video.uploadDate}">
                                                        ${video.uploadDate}
                                                    </c:when>
                                                    <c:otherwise>
                                                        Recently
                                                    </c:otherwise>
                                                </c:choose>
                                            </span>
                                        </div>

										<div class="video-details">
											<div class="video-meta channel-link-wrapper" style="display: flex; align-items: center;">
												<c:choose>
													<c:when test="${not empty video.channelImgUrl}">
														<img src="${video.channelImgUrl}" class="channel-avatar"
															style="width: 24px; height: 24px; border-radius: 50%; margin-right: 8px; flex-shrink: 0;" />

														<a
															href="<c:url value='/channel?channelId=${video.channelId}'/>"
															class="channel-link" onclick="event.stopPropagation()">
															${video.channelName} </a>
													</c:when>
													<c:otherwise>
														<div
															class="bg-secondary d-flex align-items-center justify-content-center"
															style="width: 24px; height: 24px; border-radius: 50%; margin-right: 8px; flex-shrink: 0;">
															<i class="fas fa-user-circle text-white fs-6"></i>
														</div>

														<a
															href="<c:url value='/channel?channelId=${video.channelId}'/>"
															class="channel-link" onclick="event.stopPropagation()">
															${video.channelName} </a>

													</c:otherwise>
												</c:choose>
											</div>
										</div>
									</div>
                                    
                                    <c:if test="${not empty video.videoDesc}">
                                        <p class="video-description">
                                            ${video.videoDesc}
                                        </p>
                                    </c:if>
                                </div>								
							</div> 
                        </div>
                    </li>
                </c:forEach>
            </c:if>

            <!-- Channel Results -->
            <c:if test="${(currentFilter == 'all' or currentFilter == 'channels' or empty currentFilter) and not empty channels}">
                <c:if test="${currentFilter == 'all' or empty currentFilter}">
                    <h5 class="mt-4 mb-3 fw-semibold">Channels</h5>
                </c:if>
                
                <c:forEach var="channel" items="${channels}">
                    <li class="result-item">
                        <div class="channel-result-wrapper clickable-row" onclick="window.location='<c:url value='/channel?channelId=${channel.channelId}'/>'">
							<div class="channel-result-link">
								<!-- Channel Avatar -->
								<c:choose>
									<c:when test="${not empty channel.avatarUrl}">
										<img src="data:image/png;base64,${channel.channelImgBase64}"
											alt="${channel.channelName}" class="channel-avatar">
									</c:when>
									<c:otherwise>
										<div
											class="channel-avatar bg-secondary d-flex align-items-center justify-content-center">
											<i class="fas fa-user-circle text-white fs-1"></i>
										</div>
									</c:otherwise>
								</c:choose>

								<!--<c:choose>
									<%-- Try channelImgBase64 first --%>
									<c:when test="${not empty channel.channelImgBase64}">
										<img src="data:image/*;base64,${channel.channelImgBase64}"
											alt="${channel.channelName}" class="channel-avatar">
									</c:when>

									<%-- Try base64Image second --%>
									<c:when test="${not empty channel.base64Image}">
										<img src="data:image/*;base64,${channel.base64Image}"
											alt="${channel.channelName}" class="channel-avatar">
									</c:when>

									<%-- Try to use channelImg directly --%>
									<c:when test="${channel.hasImage}">
										<img src="data:image/*;base64,${channel.base64Image}"
											alt="${channel.channelName}" class="channel-avatar">
									</c:when>

									<%-- Default avatar if no image --%>
									<c:otherwise>
										<div
											class="channel-avatar bg-secondary d-flex align-items-center justify-content-center">
											<i class="fas fa-user-circle text-white fs-1"></i>
										</div>
									</c:otherwise>
								</c:choose>-->

								


								<!-- Channel Details -->
                                <div class="channel-details">
                                    <h4 class="channel-name">${channel.channelName}</h4>
                                    
                                    <div class="channel-stats">
                                        <c:if test="${not empty channel.videoCount}">
                                            <span>${channel.videoCount} videos</span>
                                            <span class="meta-separator">•</span>
                                        </c:if>
                                        <span>
                                            <c:choose>
                                                <c:when test="${not empty channel.subscriber_count}">
                                                    ${channel.subscriber_count} subscribers
                                                </c:when>
                                                <c:otherwise>
                                                    0 subscribers
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                    </div>
                                    
                                    <c:if test="${not empty channel.channelDesc}">
                                        <p class="channel-description">
                                            ${channel.channelDesc}
                                        </p>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </li>
                </c:forEach>
            </c:if>
        </ul>

        <!-- Pagination -->
        <c:if test="${not empty totalPages and totalPages > 1}">
            <div class="pagination-container">
                <nav aria-label="Search results navigation">
                    <ul class="pagination justify-content-center">
                        <c:if test="${currentPage > 1}">
                            <li class="page-item">
                                <a class="page-link" 
                                   href="?q=${searchQuery}&filter=${currentFilter}&page=${currentPage - 1}"
                                   aria-label="Previous">
                                    <i class="fas fa-chevron-left"></i>
                                </a>
                            </li>
                        </c:if>
                        
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <c:if test="${(i >= currentPage - 2 and i <= currentPage + 2) or i == 1 or i == totalPages}">
                                <c:if test="${i == 1 and currentPage > 3}">
                                    <li class="page-item">
                                        <a class="page-link" 
                                           href="?q=${searchQuery}&filter=${currentFilter}&page=1">1</a>
                                    </li>
                                    <li class="page-item disabled">
                                        <span class="page-link">...</span>
                                    </li>
                                </c:if>
                                
                                <li class="page-item ${i == currentPage ? 'active' : ''}">
                                    <a class="page-link" 
                                       href="?q=${searchQuery}&filter=${currentFilter}&page=${i}">${i}</a>
                                </li>
                                
                                <c:if test="${i == totalPages - 1 and currentPage < totalPages - 2}">
                                    <li class="page-item disabled">
                                        <span class="page-link">...</span>
                                    </li>
                                </c:if>
                            </c:if>
                        </c:forEach>
                        
                        <c:if test="${currentPage < totalPages}">
                            <li class="page-item">
                                <a class="page-link" 
                                   href="?q=${searchQuery}&filter=${currentFilter}&page=${currentPage + 1}"
                                   aria-label="Next">
                                    <i class="fas fa-chevron-right"></i>
                                </a>
                            </li>
                        </c:if>
                    </ul>
                </nav>
            </div>
        </c:if>
    </div>

    <!-- Bootstrap JS Bundle with Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        // Format large numbers (1,000 => 1K, 1,000,000 => 1M)
        function formatNumber(num) {
            if (!num) return "0";
            
            if (num >= 1000000) {
                return (num / 1000000).toFixed(1).replace(/\.0$/, '') + 'M';
            }
            if (num >= 1000) {
                return (num / 1000).toFixed(1).replace(/\.0$/, '') + 'K';
            }
            return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
        }

        // Format numbers on page load
        document.addEventListener('DOMContentLoaded', function() {
            // Format video views
            document.querySelectorAll('.video-meta span:first-child').forEach(function(el) {
                const text = el.textContent;
                const match = text.match(/([\d,]+) views/);
                if (match) {
                    const num = parseInt(match[1].replace(/,/g, ''));
                    el.textContent = formatNumber(num) + ' views';
                }
            });

            // Format channel subscribers
            document.querySelectorAll('.channel-stats span:last-child').forEach(function(el) {
                const text = el.textContent;
                const match = text.match(/([\d,]+) subscribers/);
                if (match) {
                    const num = parseInt(match[1].replace(/,/g, ''));
                    el.textContent = formatNumber(num) + ' subscribers';
                }
            });

            // Format channel video counts
            document.querySelectorAll('.channel-stats span:first-child').forEach(function(el) {
                const text = el.textContent;
                const match = text.match(/([\d,]+) videos/);
                if (match) {
                    const num = parseInt(match[1].replace(/,/g, ''));
                    el.textContent = formatNumber(num) + ' videos';
                }
            });
            
            // Make entire video row clickable (except channel link)
            document.querySelectorAll('.clickable-row').forEach(function(row) {
                // Add pointer cursor
                row.style.cursor = 'pointer';
                
                // Optional: Add keyboard navigation support
                row.setAttribute('tabindex', '0');
                row.addEventListener('keypress', function(e) {
                    if (e.key === 'Enter' || e.key === ' ') {
                        e.preventDefault();
                        const url = this.getAttribute('onclick')?.match(/window\.location='([^']+)'/)?.[1];
                        if (url) window.location = url;
                    }
                });
            });
            
            // Format date to relative time (optional)
            document.querySelectorAll('.video-meta').forEach(function(meta) {
                const dateSpan = meta.querySelector('span:nth-child(3)');
                if (dateSpan && dateSpan.textContent.includes('Recently')) {
                    // You could implement relative time formatting here
                    // For now, we'll leave it as "Recently"
                }
            });
        });
    </script>
</body>
</html>