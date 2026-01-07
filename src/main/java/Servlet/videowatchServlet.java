package Servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Model.CommentBean;
import Model.VideoBean;
import Repository.CommentRepository;
import Repository.VideoRepository;
import Repository.ViewRepository;

/**
 * Servlet implementation class videowatchServlet
 */
@WebServlet("/videowatch")
public class videowatchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	VideoRepository  videoRepository = new VideoRepository();
	
	private static final Logger LOGGER = Logger.getLogger(uploadvideoServlet.class.getName());
    /**
     * @see HttpServlet#HttpServlet()
     */
    public videowatchServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
//	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		String videoIdParam = request.getParameter("v");
//        if (videoIdParam == null || videoIdParam.isEmpty()) {
//            response.sendRedirect("index.jsp");
//            return;
//        }
//        
//        try {
//            int videoId = Integer.parseInt(videoIdParam);
//            VideoBean video = videoRepository.getVideoById(videoId);
//            
//            if (video == null) {
//                response.sendRedirect("index.jsp?error=Video not found or is private");
//                return;
//            }
//            
//            // Increment view count
//            videoRepository.incrementViewCount(videoId);
//            
//            // Format date
//            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
//            String formattedDate = dateFormat.format(video.getUploadDate());
//            
//            // Format view count
//            String formattedViews = formatViewCount(video.getViewCount());
//            
//         // NEW, SAFER CODE (Include the context path)
//            String videoStreamUrl = request.getContextPath() + "/streamvideo?videoId=" + videoId;
//            
//            // Set attributes for JSP
//            request.setAttribute("video", video);
//            request.setAttribute("formattedViews", formattedViews);
//            request.setAttribute("formattedDate", formattedDate);
//            request.setAttribute("videoStreamUrl", videoStreamUrl);
//            
//            // Forward to JSP
//            RequestDispatcher dispatcher = request.getRequestDispatcher("video-watch.jsp");
//            dispatcher.forward(request, response);
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.sendRedirect("index.jsp?error=Error loading video");
//        }
//    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	System.out.println("=== VIDEOWATCH SERVLET CALLED ===");
        System.out.println("Request URL: " + request.getRequestURL());
        System.out.println("Query String: " + request.getQueryString());
        System.out.println("Parameter 'v': " + request.getParameter("v"));
    	String videoIdParam = request.getParameter("v");
        int videoId = -1; // Default to invalid ID

        if (videoIdParam == null || videoIdParam.isEmpty()) {
            LOGGER.warning("Video watch request failed: Missing 'v' parameter.");
            response.sendRedirect("index.jsp");
            return;
        }
        
        try {
            // Attempt to parse the ID first
            videoId = Integer.parseInt(videoIdParam);
            // Get user ID from session (if logged in)
            HttpSession session = request.getSession();
            Integer userId = (Integer) session.getAttribute("userId");
            
            VideoBean video = videoRepository.getVideoById(videoId, userId);
            
            if (video == null) {
                LOGGER.info("Video watch request failed: Video ID " + videoId + " not found in repository.");
                response.sendRedirect("index.jsp?error=Video not found or is private");
                return;
            }
            
            // ========== UPDATED VIEW COUNT LOGIC ==========
            System.out.println("DEBUG: Processing view count for video " + videoId + ", user ID: " + userId);
            
            try {
                if (userId != null) {
                    // For logged-in users: track daily view
                    ViewRepository viewRepo = new ViewRepository();
                    boolean hasViewedToday = ViewRepository.hasViewedToday(userId, videoId);
                    
                    if (!hasViewedToday) {
                        // Record the view in video_views table
                        boolean viewRecorded = ViewRepository.recordView(userId, videoId);
                        
                        if (viewRecorded) {
                            // Only increment main view count if it's a new view for today
                            videoRepository.incrementViewCount(videoId);
                            System.out.println("DEBUG: ✅ New view counted for user " + userId + " on video " + videoId);
                            
                            // Update video's view count in the object
                            video.setViewCount(video.getViewCount() + 1);
                        } else {
                            System.out.println("DEBUG: ⚠️ View already recorded today for user " + userId);
                        }
                    } else {
                        System.out.println("DEBUG: ⚠️ User " + userId + " already viewed video " + videoId + " today");
                    }
                } else {
                    // For anonymous users: always increment view count
                    videoRepository.incrementViewCount(videoId);
                    video.setViewCount(video.getViewCount() + 1);
                    
                    // Also record anonymous view (optional)
                    try {
                        ViewRepository.recordAnonymousView(videoId);
                    } catch (Exception e) {
                        // Don't fail if anonymous view recording fails
                        System.out.println("DEBUG: Could not record anonymous view: " + e.getMessage());
                    }
                    
                    System.out.println("DEBUG: ✅ Anonymous view counted for video " + videoId);
                }
            } catch (Exception e) {
                System.err.println("ERROR in view tracking: " + e.getMessage());
                e.printStackTrace();
                // Fallback: always increment view count on error
                videoRepository.incrementViewCount(videoId);
                video.setViewCount(video.getViewCount() + 1);
            }
            // ==============================================
            
            // Format date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
//          String formattedDate = dateFormat.format(video.getUploadDate());
            String formattedDate;
            if (video.getUploadDate() != null) {
                formattedDate = dateFormat.format(video.getUploadDate());
            } else {
                formattedDate = "Recent";
                System.out.println("WARNING: Upload date is null for video " + videoId);
            }
            
            // Format view count
            String formattedViews = formatViewCount(video.getViewCount());
            
         // Format likes count - IMPORTANT!
            String formattedLikes = formatViewCount(video.getLikeCount());
            
         // CREATE STREAMING URL - THIS IS CRITICAL!
            String videoStreamUrl = request.getContextPath() + "/streamvideo?videoId=" + videoId;
            
//         // Load comments for this video
//            CommentRepository commentRepo = new CommentRepository();
//            List<CommentBean> comments = commentRepo.getCommentsByVideoId(videoId, userId);
//            int commentCount = commentRepo.getCommentCount(videoId);
            
         // Load comments - with error handling
            List<CommentBean> comments = new ArrayList<>();
            int commentCount = 0;
            
            try {
                CommentRepository commentRepo = new CommentRepository();
                comments = commentRepo.getCommentsByVideoId(videoId, userId);
                commentCount = commentRepo.getCommentCount(videoId);
                System.out.println("DEBUG: Comments loaded successfully: " + comments.size());
            } catch (Exception e) {
                System.out.println("WARNING: Could not load comments: " + e.getMessage());
                // Continue without comments - don't fail the whole page
                e.printStackTrace();
            }
            
         // Debug logging
            System.out.println("=== SERVLET DEBUG ===");
            System.out.println("Video ID: " + videoId);
            System.out.println("User ID: " + userId);
            System.out.println("Video Name: " + video.getVideoName());
            System.out.println("Like Count: " + video.getLikeCount());
            System.out.println("Dislike Count: " + video.getDislikeCount());
            System.out.println("User Reaction: " + video.getUserReaction());
            System.out.println("Formatted Views: " + formattedViews);
            System.out.println("Formatted Date: " + formattedDate);
            System.out.println("Formatted Likes: " + formattedLikes);
            System.out.println("Stream URL: " + videoStreamUrl);
            System.out.println("=== END DEBUG ===");
            
         // Debug logging
            System.out.println("=== SERVLET DEBUG ===");
            System.out.println("Video ID: " + videoId);
            System.out.println("Video Name: " + video.getVideoName());
            System.out.println("Video URL from DB: " + video.getVideoUrl());
            System.out.println("Formatted Views: " + formattedViews);
            System.out.println("Formatted Date: " + formattedDate);
            System.out.println("Stream URL: " + videoStreamUrl);
            System.out.println("=== END DEBUG ===");
             
         // Debug logging
            System.out.println("=== SERVLET DEBUG ===");
            System.out.println("Video ID: " + videoId);
            System.out.println("User ID: " + userId);
            System.out.println("Comment Count: " + commentCount);
            System.out.println("Comments loaded: " + comments.size());
            System.out.println("=== END DEBUG ===");
            
            // Set ALL attributes
            request.setAttribute("video", video);
            request.setAttribute("formattedViews", formattedViews);
            request.setAttribute("formattedDate", formattedDate);
            request.setAttribute("formattedLikes", formattedLikes);
            request.setAttribute("videoStreamUrl", videoStreamUrl); // THIS WAS MISSING!
            request.setAttribute("comments", comments); // Add comments
            request.setAttribute("commentCount", commentCount); // Add comment count            
            
         // Add to history cookie
            addToHistoryCookie(request, response, video.getVideoId());
            // Forward to JSP
            RequestDispatcher dispatcher = request.getRequestDispatcher("video-watch.jsp");
            dispatcher.forward(request, response);
            
            
        } catch (NumberFormatException e) {
             // Handle case where 'v' parameter is not an integer
             LOGGER.log(Level.SEVERE, "Invalid video ID format received: " + videoIdParam, e);
             response.sendRedirect("index.jsp?error=Invalid video ID format");
        } catch (Exception e) {
            // This catches all database exceptions (connection failures, SQL errors, etc.)
            LOGGER.log(Level.SEVERE, "Critical Error processing video watch request for ID: " + videoId + ". Check DB connection/Repository logic.", e);
            response.sendRedirect("index.jsp?error=Error loading video");
        }
    }
    private String formatViewCount(long count) {
        if (count >= 1000000) {
            return String.format("%.1fM", count / 1000000.0);
        } else if (count >= 1000) {
            return String.format("%.1fK", count / 1000.0);
        }
        return String.valueOf(count);
    }
	
    
//    private void addToHistoryCookie(HttpServletRequest request, HttpServletResponse response, int videoId) {
//        // Get existing cookie or create new one
//        String currentHistory = "";
//        
//        // Get existing cookies from request
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if ("watch_history".equals(cookie.getName())) {
//                    currentHistory = cookie.getValue();
//                    break;
//                }
//            }
//        }
//        
//        // Use pipe | as separator (comma causes issues with Tomcat)
//        String separator = "|";
//        
//        // Add new video ID to beginning (most recent first)
//        String newHistory;
//        if (currentHistory == null || currentHistory.isEmpty()) {
//            newHistory = String.valueOf(videoId);
//        } else {
//            // Remove existing occurrence of this video ID (if any)
//            String[] existingIds = currentHistory.split("\\|");
//            List<String> idList = new ArrayList<>();
//            idList.add(String.valueOf(videoId)); // Add new video first
//            
//            for (String id : existingIds) {
//                if (!id.trim().equals(String.valueOf(videoId)) && idList.size() < 50) {
//                    idList.add(id.trim());
//                }
//            }
//            
//            // Keep only last 50 videos
//            if (idList.size() > 50) {
//                idList = idList.subList(0, 50);
//            }
//            
//            newHistory = String.join(separator, idList);
//        }
//        
//        // Set cookie
//        Cookie historyCookie = new Cookie("watch_history", newHistory);
//        historyCookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
//        historyCookie.setPath("/");
//        response.addCookie(historyCookie);
//        
//        System.out.println("DEBUG: Updated history cookie with video: " + videoId);
//        System.out.println("DEBUG: New cookie value: " + newHistory);
//    }
    	
    private void addToHistoryCookie(HttpServletRequest request, HttpServletResponse response, int videoId) {
        // Get user ID from session
        HttpSession session = request.getSession(false);
        if (session == null) {
            return; // User not logged in
        }
        
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return; // User ID not found
        }
        
        // Create cookie name with user ID
        String cookieName = "watch_history_" + userId;
        
        // Get existing cookie or create new one
        String currentHistory = "";
        
        // Get existing cookies from request
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    currentHistory = cookie.getValue();
                    break;
                }
            }
        }
        
        // Use pipe | as separator
        String separator = "|";
        
        // Add new video ID to beginning (most recent first)
        String newHistory;
        if (currentHistory == null || currentHistory.isEmpty()) {
            newHistory = String.valueOf(videoId);
        } else {
            // Remove existing occurrence of this video ID (if any)
            String[] existingIds = currentHistory.split("\\|");
            List<String> idList = new ArrayList<>();
            idList.add(String.valueOf(videoId)); // Add new video first
            
            for (String id : existingIds) {
                if (!id.trim().equals(String.valueOf(videoId)) && idList.size() < 50) {
                    idList.add(id.trim());
                }
            }
            
            // Keep only last 50 videos
            if (idList.size() > 50) {
                idList = idList.subList(0, 50);
            }
            
            newHistory = String.join(separator, idList);
        }
        
        // Set cookie
        Cookie historyCookie = new Cookie(cookieName, newHistory);
        historyCookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
        historyCookie.setPath("/");
        response.addCookie(historyCookie);
        
        System.out.println("DEBUG: Updated history cookie for user " + userId + " with video: " + videoId);
        System.out.println("DEBUG: Cookie name: " + cookieName + ", Value: " + newHistory);
    }
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
