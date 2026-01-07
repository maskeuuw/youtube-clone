<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="Model.VideoBean"%>
<%@ page import="Model.CommentBean"%>
<%@ page import="Model.ChannelBean"%>
<%@ page import="Repository.ChannelRepository"%>
<%@ page import="java.util.List"%>

<%-- CRITICAL: Add this Java code to get attributes from servlet --%>
<%
// Get attributes from servlet
VideoBean video = (VideoBean) request.getAttribute("video");
String formattedViews = (String) request.getAttribute("formattedViews");
String formattedDate = (String) request.getAttribute("formattedDate");
String formattedLikes = (String) request.getAttribute("formattedLikes");
String videoStreamUrl = (String) request.getAttribute("videoStreamUrl");
List<CommentBean> comments = (List<CommentBean>) request.getAttribute("comments");
Integer commentCount = (Integer) request.getAttribute("commentCount");

if (commentCount == null)
	commentCount = 0;

// Debug: Check if we're getting the data
System.out.println("=== JSP DEBUG ===");
System.out.println("Video: " + video);
System.out.println("Comments: " + comments);
System.out.println("Comment Count: " + commentCount);

// Debug: Check if we're getting the data
System.out.println("=== JSP DEBUG ===");
System.out.println("Video: " + video);
System.out.println("Formatted Views: " + formattedViews);
System.out.println("Formatted Date: " + formattedDate);
System.out.println("Stream URL: " + videoStreamUrl);

// If video is null, redirect to home
if (video == null) {
	System.out.println("ERROR: Video is null, redirecting to index.jsp");
	response.sendRedirect("index.jsp");
	return;
}

// If videoStreamUrl is null, create it
if (videoStreamUrl == null) {
	videoStreamUrl = request.getContextPath() + "/streamvideo?videoId=" + video.getVideoId();
	System.out.println("Created stream URL: " + videoStreamUrl);
}

// Get user ID from session
Integer userId = (Integer) session.getAttribute("userId");
boolean isLoggedIn = userId != null;
String userName = (String) session.getAttribute("userName");

// Determine button states
boolean isLiked = "Like".equals(video.getUserReaction());
boolean isDisliked = "Dislike".equals(video.getUserReaction());

System.out.println("=== EXTENDED DEBUG ===");
System.out.println("User ID from session: " + userId);
System.out.println("Is Logged In: " + isLoggedIn);
System.out.println("Video Like Count: " + video.getLikeCount());
System.out.println("Video Dislike Count: " + video.getDislikeCount());
System.out.println("User Reaction: " + video.getUserReaction());
System.out.println("Is Liked: " + isLiked);
System.out.println("Is Disliked: " + isDisliked);
System.out.println("Formatted Likes: " + formattedLikes);
System.out.println("=== END EXTENDED DEBUG ===");

System.out.println("=== END DEBUG ===");

// Handle null formattedLikes
if (formattedLikes == null && video != null) {
	formattedLikes = video.getFormattedLikes();
}

// Fallback if still null
if (formattedLikes == null) {
	formattedLikes = "0";
}
%>
<%
System.out.println("=== DEBUG INFO ===");
System.out.println("Video Object: " + video);
if (video != null) {
	System.out.println("Video ID: " + video.getVideoId());
	System.out.println("Video Name: " + video.getVideoName());
	System.out.println("Video URL from DB: " + video.getVideoUrl());
}
System.out.println("Formatted Views: " + formattedViews);
System.out.println("Formatted Date: " + formattedDate);
System.out.println("Stream URL: " + videoStreamUrl);
System.out.println("=== END DEBUG ===");
%>
<%
// Get channel details
ChannelBean channel = null;
int channelId = 0;
int channelSubscriberCount = 0;
boolean isSubscribed = false;
boolean isChannelOwner = false;

if (video != null) {
    channelId = video.getChannelId();
    if (channelId > 0) {
        try {
            // Get channel details
            channel = ChannelRepository.getChannelById(channelId);
            if (channel != null) {
                // Get subscriber count
                channelSubscriberCount = ChannelRepository.getSubscriberCount(channelId);
                
                // Check if current user is subscribed to this channel
                if (isLoggedIn && userId != null) {
                    isSubscribed = ChannelRepository.isUserSubscribed(userId, channelId);
                }
                
                // Check if current user is the channel owner
                if (isLoggedIn && userId != null) {
                    isChannelOwner = (userId == channel.getUserId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading channel details: " + e.getMessage());
        }
    }
}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title><%=video.getVideoName()%> - Video Player</title>
<!-- Bootstrap CSS -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css"
	rel="stylesheet">
<!-- Font Awesome for icons -->
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="stylesheet" href="style.css">
<link rel="stylesheet" href="video-watchstyle.css">
</head>
<body>
	<!-- header -->
	<c:choose>
		<%-- CASE 1: User is Logged In (sessionScope.currentUser is NOT null) --%>
		<c:when test="${not empty sessionScope.currentUser}">
			<c:import var="userheader" url="headerforuser.jsp" />
			<c:out value="${userheader}" escapeXml="false" />
		</c:when>

		<%-- CASE 2: User is a Guest (sessionScope.currentUser IS null) --%>
		<c:otherwise>
			<c:import var="guestheader" url="headerforguest.jsp" />
			<c:out value="${guestheader}" escapeXml="false" />
		</c:otherwise>
	</c:choose>

	<!-- 2. MAIN LAYOUT: Sidebar (Guide) + Content Feed -->
	<div class="d-flex pt-5">
		<!-- sidebar -->
		<c:import var="sidebar" url="sidebar.jsp" />
		<c:out value="${sidebar}" escapeXml="false" />

		<!-- 3. MAIN CONTENT AREA -->
		<main class="yt-feed-container container-fluid p-9" id="feedContainer">
			<div class="container mt-4">
				<!-- Video Player -->
				<div class="video-container">
					<video class="video-player" controls id="mainVideoPlayer"
						<%String thumbnailUrl = video.getVideoThumbnailUrl();
							if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {%>
							poster="<%=thumbnailUrl%>" <%}%>>
						<!-- Use the streaming URL -->
						<source src="<%=videoStreamUrl%>" type="video/mp4">
						Your browser does not support HTML5 video.
					</video>
				</div>

				<!-- Video Title -->
				<h1 class="video-title"><%=video.getVideoName()%></h1>
				
				<!-- Channel Info and Subscribe Section -->
				<div class="channel-info-section mb-3">
					<div class="d-flex align-items-center">
						<!-- Channel Avatar -->
						<div class="channel-avatar me-3">
							<a href="channel?channelId=<%=channelId%>"
								class="text-decoration-none">
								<div
									class="rounded-circle bg-secondary d-flex align-items-center justify-content-center"
									style="width: 50px; height: 50px;">
									<i class="fas fa-user text-white"></i>
								</div>
							</a>
						</div>

						<!-- Channel Name and Stats -->
						<div class="flex-grow-1">
							<a href="channel?channelId=<%=channelId%>"
								class="text-decoration-none">
								<h5 class="mb-0 fw-bold" style="color: #0d6efd;">
									<%=channel != null ? channel.getChannelName() : "Channel"%>
								</h5>
							</a>
							<p class="mb-0 text-muted small">
								<%=channelSubscriberCount%>
								subscribers
							</p>
						</div>

						<!-- Subscribe Button -->
						<div class="subscribe-button-container">
							<%
							if (isLoggedIn && !isChannelOwner) {
								if (isSubscribed) {
							%>
							<!-- Already Subscribed - Show Unsubscribe -->
							<form action="subscribe" method="post" class="mb-0">
								<input type="hidden" name="channelId" value="<%=channelId%>">
								<input type="hidden" name="action" value="unsubscribe">
								<input type="hidden" name="videoId"
									value="<%=video.getVideoId()%>">
								<button type="submit" class="btn btn-secondary">
									<i class="fas fa-check"></i> Subscribed
								</button>
							</form>
							<%
							} else {
							%>
							<!-- Not Subscribed - Show Subscribe -->
							<form action="subscribe" method="post" class="mb-0">
								<input type="hidden" name="channelId" value="<%=channelId%>">
								<input type="hidden" name="action" value="subscribe"> <input
									type="hidden" name="videoId" value="<%=video.getVideoId()%>">
								<button type="submit" class="btn btn-danger">
									<i class="fas fa-bell"></i> Subscribe
								</button>
							</form>
							<%
							}
							} else if (isChannelOwner) {
							%>
							<!-- Channel Owner -->
							<button class="btn btn-outline-secondary" disabled>
								<i class="fas fa-user"></i> Your Channel
							</button>
							<%
							} else {
							%>
							<!-- Guest User -->
							<button class="btn btn-danger"
								onclick="location.href='login.jsp'">
								<i class="fas fa-bell"></i> Subscribe
							</button>
							<%
							}
							%>
						</div>
					</div>
				</div>

				<!-- Video Info Bar -->
				<div class="video-info-bar">
					<div
						class="d-flex flex-wrap justify-content-between align-items-center">
						<div class="video-stats mb-2 mb-md-0">
							<span><%=formattedViews%> views</span> <span class="mx-2">•</span>
							<span><%=formattedDate%></span>
						</div>


						<!-- Like/Dislike Buttons with Forms -->
						<div class="action-buttons">
							<%
							if (isLoggedIn) {
							%>
							<!-- Like Button -->
							<form action="react" method="post" style="display: inline;">
								<input type="hidden" name="videoId"
									value="<%=video.getVideoId()%>">
								<%
								if (isLiked) {
								%>
								<input type="hidden" name="action" value="remove">
								<button type="submit" class="action-button like-button active">
									<i class="fas fa-thumbs-up"></i> <span><%=formattedLikes%></span>
								</button>
								<%
								} else {
								%>
								<input type="hidden" name="action" value="like">
								<button type="submit" class="action-button like-button">
									<i class="fas fa-thumbs-up"></i> <span><%=formattedLikes%></span>
								</button>
								<%
								}
								%>
							</form>

							<!-- Dislike Button -->
							<form action="react" method="post" style="display: inline;">
								<input type="hidden" name="videoId"
									value="<%=video.getVideoId()%>">
								<%
								if (isDisliked) {
								%>
								<input type="hidden" name="action" value="remove">
								<button type="submit"
									class="action-button dislike-button active">
									<i class="fas fa-thumbs-down"></i> <span>Dislike</span>
								</button>
								<%
								} else {
								%>
								<input type="hidden" name="action" value="dislike">
								<button type="submit" class="action-button dislike-button">
									<i class="fas fa-thumbs-down"></i> <span>Dislike</span>
								</button>
								<%
								}
								%>
							</form>
							<%
							} else {
							%>
							<!-- Show buttons for non-logged in users (disabled) -->
							<button class="action-button like-button" disabled
								onclick="alert('Please login to like videos')">
								<i class="fas fa-thumbs-up"></i> <span><%=formattedLikes%></span>
							</button>
							<button class="action-button dislike-button" disabled
								onclick="alert('Please login to dislike videos')">
								<i class="fas fa-thumbs-down"></i> <span>Dislike</span>
							</button>
							<%
							}
							%>

							<!-- Save to Watch Later Button -->
							<%
							// Check if video is already saved
							boolean isVideoSaved = false;
							if (isLoggedIn && userId != null) {
								Repository.SaveVideoRepository saveVideoRepo = new Repository.SaveVideoRepository();
								isVideoSaved = saveVideoRepo.isVideoSaved(userId, video.getVideoId());
							}
							%>
							<form action="watch-later" method="post" style="display: inline;">
								<input type="hidden" name="videoId"
									value="<%=video.getVideoId()%>">
								<%
								if (isVideoSaved) {
								%>
								<input type="hidden" name="action" value="remove">
								<button type="submit" class="action-button save-button active"
									title="Remove from Watch Later">
									<i class="fas fa-clock"></i> <span>Saved</span>
								</button>
								<%
								} else {
								%>
								<input type="hidden" name="action" value="save">
								<button type="submit" class="action-button save-button"
									title="Save to Watch Later">
									<i class="far fa-clock"></i> <span>Save</span>
								</button>
								<%
								}
								%>
							</form>

							<!-- Share Button -->
							<button class="action-button" onclick="shareVideo()">
								<i class="fas fa-share"></i> <span>Share</span>
							</button>
						</div>
					</div>

				</div>

				<!-- Video Description -->
				<div class="video-description">
					<%=video.getVideoDesc() != null ? video.getVideoDesc().replace("\n", "<br>") : "No description available."%>
				</div>
			</div>

			<!-- Comments Section -->
			<div class="comments-section">
				<div class="comments-header">
					Comments <span class="comment-count"><%=commentCount%></span>
				</div>

				<!-- Add Comment Form -->
				<%
				if (isLoggedIn) {
				%>
				<form action="${pageContext.request.contextPath}/comment" method="post" class="comment-input-area">
					<div class="user-avatar">
						<i class="fas fa-user"></i>
					</div>
					<input type="hidden" name="action" value="add"> <input
						type="hidden" name="videoId" value="<%=video.getVideoId()%>">
					<input type="text" name="content" class="comment-input"
						placeholder="Add a public comment..." required>
					<button type="submit" class="btn btn-primary btn-sm ms-2">Comment</button>
				</form>
				<%
				} else {
				%>
				<div class="comment-input-area">
					<div class="user-avatar">
						<i class="fas fa-user"></i>
					</div>
					<input type="text" class="comment-input"
						placeholder="Please login to comment..."
						onclick="location.href='login.jsp'" readonly>
				</div>
				<%
				}
				%>

				<!-- Comments List -->
				<%
				if (comments != null && !comments.isEmpty()) {
					for (CommentBean comment : comments) {
				%>
				<!-- Comment -->
				<div class="comment" id="comment-<%=comment.getCommentId()%>">
					<div class="user-avatar">
						<i class="fas fa-user"></i>
					</div>
					<div class="comment-content">
						<div class="comment-author">
							<%=comment.getUserName()%>
							<span class="comment-time"><%=comment.getFormattedTime()%></span>
						</div>

						<!-- Comment Text Display -->
						<div class="comment-text-display"
							id="comment-text-<%=comment.getCommentId()%>">
							<%=comment.getContents()%>
						</div>

						<!-- Edit Comment Form (hidden by default) -->
						<%
						if (isLoggedIn && userId == comment.getUserId()) {
						%>
						<form action="${pageContext.request.contextPath}/comment" method="post" class="edit-comment-form"
							id="edit-form-<%=comment.getCommentId()%>">
							<input type="hidden" name="action" value="edit"> <input
								type="hidden" name="videoId" value="<%=video.getVideoId()%>">
							<input type="hidden" name="commentId"
								value="<%=comment.getCommentId()%>"> <input type="text"
								name="content" class="form-control"
								value="<%=comment.getContents()%>" required>
							<div class="mt-2">
								<button type="submit" class="btn btn-primary btn-sm">Update</button>
								<button type="button" class="btn btn-secondary btn-sm"
									onclick="cancelEdit(<%=comment.getCommentId()%>)">
									Cancel</button>
							</div>
						</form>
						<%
						}
						%>

						<!-- Comment Actions -->
						<div class="comment-actions">
							<%
							if (isLoggedIn) {
								String userCommentReaction = comment.getUserReaction();
								boolean commentLiked = "Like".equals(userCommentReaction);
								boolean commentDisliked = "Dislike".equals(userCommentReaction);
							%>
							<!-- Like Button -->
							<form action="${pageContext.request.contextPath}/commentreaction" method="post"
								style="display: inline;">
								<input type="hidden" name="commentId"
									value="<%=comment.getCommentId()%>"> <input
									type="hidden" name="videoId" value="<%=video.getVideoId()%>">
								<%
								if (commentLiked) {
								%>
								<input type="hidden" name="action" value="remove">
								<button type="submit" class="comment-action like active">
									<i class="fas fa-thumbs-up"></i> <span><%=comment.getLikeCount()%></span>
								</button>
								<%
								} else {
								%>
								<input type="hidden" name="action" value="like">
								<button type="submit" class="comment-action like">
									<i class="fas fa-thumbs-up"></i> <span><%=comment.getLikeCount()%></span>
								</button>
								<%
								}
								%>
							</form>

							<!-- Dislike Button -->
							<form action="${pageContext.request.contextPath}/commentreaction" method="post"
								style="display: inline;">
								<input type="hidden" name="commentId"
									value="<%=comment.getCommentId()%>"> <input
									type="hidden" name="videoId" value="<%=video.getVideoId()%>">
								<%
								if (commentDisliked) {
								%>
								<input type="hidden" name="action" value="remove">
								<button type="submit" class="comment-action dislike active">
									<i class="fas fa-thumbs-down"></i> <span><%=comment.getDislikeCount()%></span>
								</button>
								<%
								} else {
								%>
								<input type="hidden" name="action" value="dislike">
								<button type="submit" class="comment-action dislike">
									<i class="fas fa-thumbs-down"></i> <span><%=comment.getDislikeCount()%></span>
								</button>
								<%
								}
								%>
							</form>
							<%
							} else {
							%>
							<!-- Show buttons for non-logged in users (disabled) -->
							<button class="comment-action" disabled>
								<i class="fas fa-thumbs-up"></i> <span><%=comment.getLikeCount()%></span>
							</button>
							<button class="comment-action" disabled>
								<i class="fas fa-thumbs-down"></i> <span><%=comment.getDislikeCount()%></span>
							</button>
							<%
							}
							%>

							<!-- Reply Button -->
							<%
							if (isLoggedIn) {
							%>
							<button class="reply-button"
								onclick="showReplyForm(<%=comment.getCommentId()%>)">
								Reply</button>
							<%
							}
							%>

							<!-- Edit/Delete buttons for comment owner -->
							<%
							if (isLoggedIn && userId == comment.getUserId()) {
							%>
							<button class="comment-action"
								onclick="showEditForm(<%=comment.getCommentId()%>)">
								Edit</button>
							<form action="${pageContext.request.contextPath}/comment" method="post" style="display: inline;">
								<input type="hidden" name="action" value="delete"> <input
									type="hidden" name="videoId" value="<%=video.getVideoId()%>">
								<input type="hidden" name="commentId"
									value="<%=comment.getCommentId()%>">
								<button type="submit" class="comment-action text-danger"
									onclick="return confirm('Delete this comment?')">
									Delete</button>
							</form>
							<%
							}
							%>
						</div>

						<!-- Reply Form (hidden by default) -->
						<%
						if (isLoggedIn) {
						%>
						<form action="comment" method="post" class="reply-form"
							id="reply-form-<%=comment.getCommentId()%>">
							<div class="user-avatar small">
								<i class="fas fa-user"></i>
							</div>
							<input type="hidden" name="action" value="reply"> <input
								type="hidden" name="videoId" value="<%=video.getVideoId()%>">
							<input type="hidden" name="parentId"
								value="<%=comment.getCommentId()%>"> <input type="text"
								name="content" class="comment-input"
								placeholder="Write a reply..." required>
							<button type="submit" class="btn btn-primary btn-sm ms-2">Reply</button>
							<button type="button" class="btn btn-secondary btn-sm ms-1"
								onclick="hideReplyForm(<%=comment.getCommentId()%>)">
								Cancel</button>
						</form>
						<%
						}
						%>

						<!-- Replies -->
						<%
						if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
						%>
						<div class="reply-container">
							<%
							for (CommentBean reply : comment.getReplies()) {
							%>
							<div class="comment reply-comment">
								<div class="user-avatar small">
									<i class="fas fa-user"></i>
								</div>
								<div class="comment-content">
									<div class="comment-author">
										<%=reply.getUserName()%>
										<span class="comment-time"><%=reply.getFormattedTime()%></span>
									</div>
									<div class="comment-text">
										<%=reply.getContents()%>
									</div>
									<!-- Reply actions (optional) -->
								</div>
							</div>
							<%
							}
							%>
						</div>
						<%
						}
						%>
					</div>
				</div>
				<%
				}
				} else {
				%>
				<div class="no-comments text-center py-4 text-muted">No
					comments yet. Be the first to comment!</div>
				<%
				}
				%>
			</div>
		</main>
	</div>

	<!-- Bootstrap JS Bundle -->
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

	<!-- Simple JavaScript for comment functionality -->
	<script>
	function showReplyForm(commentId) {
        // Hide all other reply forms
        document.querySelectorAll('.reply-form').forEach(form => {
            form.classList.remove('active');
        });
        // Show this reply form
        document.getElementById('reply-form-' + commentId).classList.add('active');
    }
    
    function hideReplyForm(commentId) {
        document.getElementById('reply-form-' + commentId).classList.remove('active');
    }
    
    function showEditForm(commentId) {
        // Hide comment text
        document.getElementById('comment-text-' + commentId).style.display = 'none';
        // Show edit form
        document.getElementById('edit-form-' + commentId).classList.add('active');
    }
    
    function cancelEdit(commentId) {
        // Show comment text
        document.getElementById('comment-text-' + commentId).style.display = 'block';
        // Hide edit form
        document.getElementById('edit-form-' + commentId).classList.remove('active');
    }
    
    document.addEventListener('DOMContentLoaded', () => {
    	const videoGrid = document.getElementById('video-grid');
    	if (videoGrid) {
    		videoData.forEach(video => {
    			videoGrid.appendChild(createVideoCard(video));
    		});
    	}

    	// --- Sidebar Toggle Logic ---
    	const sidebar = document.getElementById('sidebarCollapse');
    	const feedContainer = document.getElementById('feedContainer');
    	const lgBreakpoint = 992; // Bootstrap lg breakpoint

    	if (sidebar && feedContainer) {
    		// Initial check: if desktop, start open and apply content offset
    		if (window.innerWidth >= lgBreakpoint) {
    			sidebar.classList.add('show');
    			feedContainer.classList.add('sidebar-open');
    		}

    		// Event listener to control main content margin on desktop
    		sidebar.addEventListener('show.bs.collapse', () => {
    			if (window.innerWidth >= lgBreakpoint) {
    				feedContainer.classList.add('sidebar-open');
    			}
    		});

    		sidebar.addEventListener('hide.bs.collapse', () => {
    			if (window.innerWidth >= lgBreakpoint) {
    				feedContainer.classList.remove('sidebar-open');
    			}
    		});

    		// Handle resize events to maintain correct desktop state
    		let isDesktop = window.innerWidth >= lgBreakpoint;
    		window.addEventListener('resize', () => {
    			const newIsDesktop = window.innerWidth >= lgBreakpoint;
    			if (newIsDesktop !== isDesktop) {
    				if (newIsDesktop) {
    					// Transitioning to Desktop: Ensure sidebar is open and margin is set
    					sidebar.classList.add('show');
    					feedContainer.classList.add('sidebar-open');
    				} else {
    					// Transitioning to Mobile: Ensure sidebar is closed and margin is removed
    					sidebar.classList.remove('show');
    					feedContainer.classList.remove('sidebar-open');
    				}
    				isDesktop = newIsDesktop;
    			}
    		});
    	}
    });
    </script>
</body>
</html>