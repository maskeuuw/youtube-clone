package Servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import JDBCconnection.DBconnection;
import Repository.VideoRepository;

/**
 * Servlet implementation class thumbnailServlet
 */
@WebServlet("/thumbnail")
public class thumbnailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	VideoRepository  videoRepository = new VideoRepository();   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public thumbnailServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
String videoIdParam = request.getParameter("videoId");
        
        if (videoIdParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Video ID required");
            return;
        }
        
        try {
            int videoId = Integer.parseInt(videoIdParam);
            
            String sql = "SELECT video_thumbnails FROM video WHERE video_id = ?";
            
            try (Connection conn = DBconnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setInt(1, videoId);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    InputStream thumbnailStream = rs.getBinaryStream("video_thumbnails");
                    
                    if (thumbnailStream != null) {
                        // Set content type
                        response.setContentType("image/jpeg"); // or image/png
                        
                        // Copy thumbnail stream to response
                        OutputStream out = response.getOutputStream();
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        
                        while ((bytesRead = thumbnailStream.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                        
                        thumbnailStream.close();
                        out.flush();
                        return;
                    }
                }
            }
            
            // If no thumbnail found, send default image or 404
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Thumbnail not found");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading thumbnail");
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
