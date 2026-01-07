package Servlet;

import java.io.IOException;
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

/**
 * Servlet implementation class channelimageServlet
 */
@WebServlet("/channel-image")
public class channelimageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public channelimageServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String channelIdParam = request.getParameter("id");
        if (channelIdParam == null || channelIdParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        try {
            int channelId = Integer.parseInt(channelIdParam);
            
            String sql = "SELECT channel_img FROM channel WHERE channel_id = ?";
            try (Connection conn = DBconnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setInt(1, channelId);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    byte[] imageBytes = rs.getBytes("channel_img");
                    
                    if (imageBytes != null && imageBytes.length > 0) {
                        response.setContentType("image/jpeg");
                        response.setContentLength(imageBytes.length);
                        
                        try (OutputStream out = response.getOutputStream()) {
                            out.write(imageBytes);
                            out.flush();
                        }
                    } else {
                        // Return default image or 404
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
