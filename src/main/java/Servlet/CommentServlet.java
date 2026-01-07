package Servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Repository.CommentRepository;

/**
 * Servlet implementation class CommentServlet
 */
@WebServlet("/comment")
public class CommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	CommentRepository commentRepository = new CommentRepository();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CommentServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Redirect to video page
		String videoId = request.getParameter("videoId");
		if (videoId != null) {
			response.sendRedirect("videowatch?v=" + videoId);
		} else {
			response.sendRedirect("index.jsp");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		Integer userId = (Integer) session.getAttribute("userId");

		// Check if user is logged in
		if (userId == null) {
			response.sendRedirect("login.jsp");
			return;
		}

		String action = request.getParameter("action");
		String videoIdParam = request.getParameter("videoId");

		if (videoIdParam == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Video ID is required");
			return;
		}

		try {
			int videoId = Integer.parseInt(videoIdParam);

			switch (action) {
			case "add":
				addComment(request, response, userId, videoId);
				break;
			case "reply":
				addReply(request, response, userId, videoId);
				break;
			case "delete":
				deleteComment(request, response, userId);
				break;
			case "edit":
				editComment(request, response, userId);
				break;
			default:
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("videowatch?v=" + videoIdParam + "&error=Error processing comment");
		}
	}

	private void addComment(HttpServletRequest request, HttpServletResponse response, int userId, int videoId)
			throws Exception {

		String content = request.getParameter("content");

		if (content == null || content.trim().isEmpty()) {
			response.sendRedirect("videowatch?v=" + videoId + "&error=Comment cannot be empty");
			return;
		}

		boolean success = commentRepository.addComment(videoId, userId, content.trim(), null);

		if (success) {
			response.sendRedirect("videowatch?v=" + videoId);
		} else {
			response.sendRedirect("videowatch?v=" + videoId + "&error=Failed to add comment");
		}
	}

	private void addReply(HttpServletRequest request, HttpServletResponse response, int userId, int videoId)
			throws Exception {

		String content = request.getParameter("content");
		String parentIdParam = request.getParameter("parentId");

		if (content == null || content.trim().isEmpty()) {
			response.sendRedirect("videowatch?v=" + videoId + "&error=Reply cannot be empty");
			return;
		}

		if (parentIdParam == null) {
			response.sendRedirect("videowatch?v=" + videoId + "&error=Parent comment ID is required");
			return;
		}

		int parentId = Integer.parseInt(parentIdParam);
		boolean success = commentRepository.addComment(videoId, userId, content.trim(), parentId);

		if (success) {
			response.sendRedirect("videowatch?v=" + videoId);
		} else {
			response.sendRedirect("videowatch?v=" + videoId + "&error=Failed to add reply");
		}
	}

	private void deleteComment(HttpServletRequest request, HttpServletResponse response, int userId) throws Exception {
		
		 System.out.println("=== DELETE COMMENT METHOD DEBUG ===");
		
		String commentIdParam = request.getParameter("commentId");
		String videoIdParam = request.getParameter("videoId");

		System.out.println("DEBUG: commentIdParam = " + commentIdParam);
	    System.out.println("DEBUG: videoIdParam = " + videoIdParam);
	    System.out.println("DEBUG: userId = " + userId);
	    
		if (commentIdParam == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Comment ID is required");
			return;
		}

	    if (videoIdParam == null) {
	        System.out.println("ERROR: Video ID parameter is null");
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Video ID is required");
	        return;
	    }

	    try {
	        int commentId = Integer.parseInt(commentIdParam);
	        System.out.println("DEBUG: Parsed commentId = " + commentId);
	        
	        System.out.println("DEBUG: Calling commentRepository.deleteComment(" + commentId + ", " + userId + ")");
	        boolean success = commentRepository.deleteComment(commentId, userId);
	        System.out.println("DEBUG: Delete result = " + success);

	        if (success) {
	            System.out.println("DEBUG: Delete successful, redirecting to video");
	            response.sendRedirect("videowatch?v=" + videoIdParam);
	        } else {
	            System.out.println("DEBUG: Delete failed, redirecting with error");
	            response.sendRedirect("videowatch?v=" + videoIdParam + "&error=Failed to delete comment");
	        }
	        
	    } catch (NumberFormatException e) {
	        System.out.println("ERROR: Invalid commentId format: " + commentIdParam);
	        throw new Exception("Invalid comment ID format", e);
	    } catch (SQLException e) {
	        System.out.println("ERROR: SQL Exception in deleteComment: " + e.getMessage());
	        throw e;
	    } catch (ClassNotFoundException e) {
	        System.out.println("ERROR: ClassNotFoundException in deleteComment: " + e.getMessage());
	        throw e;
	    }
	}

	private void editComment(HttpServletRequest request, HttpServletResponse response, int userId) throws Exception {

		String commentIdParam = request.getParameter("commentId");
		String videoIdParam = request.getParameter("videoId");
		String newContent = request.getParameter("content");

		if (commentIdParam == null || newContent == null || newContent.trim().isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters");
			return;
		}

		int commentId = Integer.parseInt(commentIdParam);
		boolean success = commentRepository.updateComment(commentId, userId, newContent.trim());

		response.sendRedirect("videowatch?v=" + videoIdParam + (success ? "" : "&error=Failed to update comment"));
	}

}
