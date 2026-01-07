package Servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Repository.ReactionRepository;
import Repository.VideoRepository;

/**
 * Servlet implementation class ReactionServlet
 */
@WebServlet("/react")
public class ReactionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final ReactionRepository reactionRepository = new ReactionRepository();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReactionServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        
        // Check if user is logged in
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String videoIdParam = request.getParameter("videoId");
        String action = request.getParameter("action");
        
        if (videoIdParam == null || action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
            return;
        }
        
        try {
            int videoId = Integer.parseInt(videoIdParam);
            
            switch (action) {
                case "like":
                    reactionRepository.addReaction(userId, videoId, "Like");
                    break;
                case "dislike":
                    reactionRepository.addReaction(userId, videoId, "Dislike");
                    break;
                case "remove":
                    reactionRepository.removeReaction(userId, videoId);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
                    return;
            }
            
            // Redirect back to video page
            response.sendRedirect("videowatch?v=" + videoId);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing reaction");
        }
	}

}
