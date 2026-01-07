package Servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import Repository.VideoRepository;

/**
 * Servlet implementation class editvideoServlet
 */
@WebServlet("/editVideo")
@MultipartConfig(
	    maxFileSize = 1024 * 1024 * 5, // 5MB
	    maxRequestSize = 1024 * 1024 * 10 // 10MB
	)
public class editvideoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public editvideoServlet() {
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
		try {
            int videoId = Integer.parseInt(request.getParameter("videoId"));
            String videoName = request.getParameter("videoName");
            String videoDesc = request.getParameter("videoDesc");
            String videoVisibility = request.getParameter("videoVisibility");
            String redirectTo = request.getParameter("redirectTo");
            
            // Handle thumbnail upload
            Part thumbnailPart = request.getPart("thumbnail");
            byte[] thumbnailBytes = null;
            
            if (thumbnailPart != null && thumbnailPart.getSize() > 0) {
                InputStream is = thumbnailPart.getInputStream();
                thumbnailBytes = is.readAllBytes();
            }
            
            // Update video in database
            boolean success = VideoRepository.updateVideo(videoId, videoName, videoDesc, videoVisibility, thumbnailBytes);
            
            if (success) {
                response.sendRedirect(redirectTo);
            } else {
                response.sendRedirect("error.jsp?message=Failed to update video");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp?message=" + e.getMessage());
        }
    
	}

}
