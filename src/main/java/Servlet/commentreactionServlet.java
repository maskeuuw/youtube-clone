package Servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Repository.CommentRepository;

/**
 * Servlet implementation class commentreactionServlet
 */
@WebServlet("/commentreaction")
public class commentreactionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	CommentRepository commentRepository = new CommentRepository();   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public commentreactionServlet() {
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
System.out.println("DEBUG: CommentReactionServlet called");
		
		HttpSession session = request.getSession();
		Integer userId = (Integer) session.getAttribute("userId");
		
		// Check if user is logged in
		if (userId == null) {
			System.out.println("DEBUG: User not logged in");
			response.sendRedirect("login.jsp");
			return;
		}
		
		String commentIdParam = request.getParameter("commentId");
		String videoIdParam = request.getParameter("videoId");
		String action = request.getParameter("action");
		
		System.out.println("DEBUG: Comment ID: " + commentIdParam);
		System.out.println("DEBUG: Video ID: " + videoIdParam);
		System.out.println("DEBUG: Action: " + action);
		
		if (commentIdParam == null || videoIdParam == null || action == null) {
			System.out.println("DEBUG: Missing parameters");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		
		try {
			int commentId = Integer.parseInt(commentIdParam);
			int videoId = Integer.parseInt(videoIdParam);
			
			boolean success = false;
			
			switch (action) {
				case "like":
					success = commentRepository.addCommentReaction(userId, commentId, "Like");
					break;
				case "dislike":
					success = commentRepository.addCommentReaction(userId, commentId, "Dislike");
					break;
				case "remove":
					success = commentRepository.removeCommentReaction(userId, commentId);
					break;
				default:
					System.out.println("DEBUG: Invalid action: " + action);
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
					return;
			}
			
			if (success) {
				System.out.println("DEBUG: Comment reaction successful");
				response.sendRedirect("videowatch?v=" + videoId);
			} else {
				System.out.println("DEBUG: Comment reaction failed");
				response.sendRedirect("videowatch?v=" + videoId + "&error=Failed to update reaction");
			}
			
		} catch (Exception e) {
			System.out.println("ERROR in CommentReactionServlet: " + e.getMessage());
			e.printStackTrace();
			response.sendRedirect("videowatch?v=" + videoIdParam + "&error=Error updating reaction");
		}
	}

}
