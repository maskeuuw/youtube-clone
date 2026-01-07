package Servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Model.ReactionBean;
import Model.UserBean;
import Model.VideoBean;
import Repository.ReactionRepository;
import Repository.UserRepository;

/**
 * Servlet implementation class likedvideosServlet
 */
@WebServlet("/liked-videos")
public class likedvideosServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public likedvideosServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String userName = (String) session.getAttribute("userName");
        Integer channelId = (Integer) session.getAttribute("channelId");
        
        // Check if user is logged in
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        try {
            // Get liked videos for the current user
            ReactionRepository reactionDAO = new ReactionRepository();
            List<VideoBean> likedVideos = reactionDAO.getLikedVideosByUserId(userId);
            
//            // Get user information for display
//            UserRepository userDAO = new UserRepository();
//            UserBean user = userDAO.getUserById(userId);
            
         // Set attributes for the JSP
            request.setAttribute("likedVideos", likedVideos);
            request.setAttribute("userName", userName); // Pass username to JSP
            request.setAttribute("channelId", channelId); // Pass channelId to JSP
            request.setAttribute("likedCount", likedVideos.size());
            
            
            // Forward to the JSP page
            request.getRequestDispatcher("liked-videos.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading liked videos");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
