package Servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Model.VideoBean;
import Repository.SaveVideoRepository;

/**
 * Servlet implementation class watchlaterServlet
 */
@WebServlet("/watch-later")
public class watchlaterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private SaveVideoRepository saveVideoRepo = new SaveVideoRepository();   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public watchlaterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
        
        // Check if user is logged in
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        Integer userId = (Integer) session.getAttribute("userId");
        
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        try {
            // Get saved videos for the user
            List<VideoBean> savedVideos = saveVideoRepo.getSavedVideos(userId);
            int savedCount = savedVideos != null ? savedVideos.size() : 0;
            
            // Set attributes for JSP
            request.setAttribute("savedVideos", savedVideos);
            request.setAttribute("savedCount", savedCount);
            
            // Forward to watch-later page
            request.getRequestDispatcher("watch-later.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp?message=Error loading watch later list");
        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
HttpSession session = request.getSession(false);
        
        // Check if user is logged in
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        Integer userId = (Integer) session.getAttribute("userId");
        String action = request.getParameter("action");
        String videoIdStr = request.getParameter("videoId");
        
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        try {
            boolean success = false;
            
            if ("clear".equals(action)) {
                // Clear all saved videos
                success = saveVideoRepo.clearWatchLater(userId);
            } else if (videoIdStr != null) {
                int videoId = Integer.parseInt(videoIdStr);
                
                if ("save".equals(action)) {
                    success = saveVideoRepo.saveVideoForLater(userId, videoId);
                } else if ("remove".equals(action)) {
                    success = saveVideoRepo.removeSavedVideo(userId, videoId);
                }
            }
            
            // Redirect back to previous page or watch-later page
            String referer = request.getHeader("Referer");
            if (referer != null && !referer.isEmpty()) {
                response.sendRedirect(referer);
            } else {
                response.sendRedirect("watch-later");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect("home");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("watch-later?error=true");
        }
	}

}
