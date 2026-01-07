package Servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Model.VideoBean;
import Repository.VideoRepository;

/**
 * Servlet implementation class historyServlet
 */
@WebServlet("/history")
public class historyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private VideoRepository videoRepository = new VideoRepository();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public historyServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("DEBUG: HistoryServlet called");

        HttpSession session = request.getSession(false);

        // Check if user is logged in
        if (session == null || session.getAttribute("currentUser") == null) {
            System.out.println("DEBUG: User not logged in, redirecting to login");
            response.sendRedirect("login.jsp");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            System.out.println("DEBUG: User ID not found in session");
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            // Get watch history from user-specific cookie
            List<VideoBean> historyVideos = getHistoryFromCookie(request, userId);
            int historyCount = historyVideos != null ? historyVideos.size() : 0;

            System.out.println("DEBUG: Found " + historyCount + " videos in history for user " + userId);

            // Set attributes for JSP
            request.setAttribute("historyVideos", historyVideos);
            request.setAttribute("historyCount", historyCount);
            request.setAttribute("userId", userId);

            // Forward to history page
            request.getRequestDispatcher("history.jsp").forward(request, response);

        } catch (Exception e) {
            System.out.println("ERROR in HistoryServlet doGet: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("error.jsp?message=Error loading watch history");
        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
System.out.println("DEBUG: HistoryServlet POST called");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String action = request.getParameter("action");
        String videoIdStr = request.getParameter("videoId");

        System.out.println("DEBUG: User ID: " + userId + ", Action: " + action + ", VideoId: " + videoIdStr);

        if ("clear".equals(action)) {
            // Clear all history for this user
            clearHistoryCookie(response, userId);
            response.sendRedirect("history?success=cleared");
        } else if ("remove".equals(action) && videoIdStr != null) {
            try {
                int videoId = Integer.parseInt(videoIdStr);
                // Remove specific video from history
                removeFromHistoryCookie(request, response, userId, videoId);
                response.sendRedirect("history?success=removed");
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Invalid video ID: " + videoIdStr);
                response.sendRedirect("history?error=invalid_id");
            }
        } else {
            response.sendRedirect("history");
        }
	}

	// Get history from user-specific cookie
    private List<VideoBean> getHistoryFromCookie(HttpServletRequest request, int userId)
            throws ClassNotFoundException, SQLException {
        List<VideoBean> historyVideos = new ArrayList<>();
        String cookieName = "watch_history_" + userId;
        
        System.out.println("DEBUG: Looking for cookie: " + cookieName);
        
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    String historyValue = cookie.getValue();
                    System.out.println("DEBUG: Found history cookie: " + cookieName + " = " + historyValue);

                    if (historyValue != null && !historyValue.isEmpty()) {
                        // Parse video IDs from cookie (format: "1|2|3|4")
                        String[] videoIds = historyValue.split("\\|");

                        // Get video details for each ID
                        for (String videoIdStr : videoIds) {
                            try {
                                int videoId = Integer.parseInt(videoIdStr.trim());
                                VideoBean video = videoRepository.getVideoById(videoId);
                                if (video != null) {
                                    // Add only if not already in list (to prevent duplicates)
                                    if (!containsVideo(historyVideos, videoId)) {
                                        historyVideos.add(video);
                                    }
                                } else {
                                    System.out.println("DEBUG: Video not found for ID: " + videoId);
                                }
                            } catch (NumberFormatException e) {
                                // Skip invalid video IDs
                                System.out.println("DEBUG: Invalid video ID in cookie: " + videoIdStr);
                            }
                        }
                    }
                    break;
                }
            }
        } else {
            System.out.println("DEBUG: No cookies found for user " + userId);
        }

        return historyVideos;
    }

    // Check if list already contains video
    private boolean containsVideo(List<VideoBean> videos, int videoId) {
        for (VideoBean video : videos) {
            if (video.getVideoId() == videoId) {
                return true;
            }
        }
        return false;
    }

    // Remove specific video from user-specific history cookie
    private void removeFromHistoryCookie(HttpServletRequest request, HttpServletResponse response,
            int userId, int videoIdToRemove) {
        String cookieName = "watch_history_" + userId;
        StringBuilder newHistory = new StringBuilder();

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    String historyValue = cookie.getValue();

                    if (historyValue != null && !historyValue.isEmpty()) {
                        // Split by pipe separator
                        String[] videoIds = historyValue.split("\\|");

                        for (String videoIdStr : videoIds) {
                            try {
                                int videoId = Integer.parseInt(videoIdStr.trim());
                                if (videoId != videoIdToRemove) {
                                    if (newHistory.length() > 0) {
                                        newHistory.append("|");
                                    }
                                    newHistory.append(videoId);
                                }
                            } catch (NumberFormatException e) {
                                // Skip invalid IDs
                            }
                        }
                    }
                    break;
                }
            }
        }

        // Update cookie
        Cookie historyCookie = new Cookie(cookieName, newHistory.toString());
        historyCookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
        historyCookie.setPath("/");
        response.addCookie(historyCookie);

        System.out.println("DEBUG: Removed video " + videoIdToRemove + " from history for user " + userId);
        System.out.println("DEBUG: New cookie value: " + newHistory.toString());
    }

    // Clear all history for specific user
    private void clearHistoryCookie(HttpServletResponse response, int userId) {
        String cookieName = "watch_history_" + userId;
        Cookie historyCookie = new Cookie(cookieName, "");
        historyCookie.setMaxAge(0); // Delete cookie
        historyCookie.setPath("/");
        response.addCookie(historyCookie);

        System.out.println("DEBUG: Cleared all history for user " + userId);
    }
}
