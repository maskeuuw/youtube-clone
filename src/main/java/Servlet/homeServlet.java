package Servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Model.VideoBean;
import Repository.VideoRepository;

/**
 * Servlet implementation class homeServlet
 */
@WebServlet("/home")
public class homeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private VideoRepository videoRepository = new VideoRepository();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public homeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
System.out.println("DEBUG: HomeServlet called");
        
        try {
            // Get all videos
            List<VideoBean> videos = videoRepository.getAllVideos();
            
            // Set videos in request
            request.setAttribute("videos", videos);
            
            System.out.println("DEBUG: Forwarding to index.jsp with " + videos.size() + " videos");
            
            // Forward to index.jsp
            request.getRequestDispatcher("index.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.out.println("ERROR in HomeServlet: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("error.jsp?message=Error loading videos");
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
