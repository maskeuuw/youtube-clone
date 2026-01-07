package Servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Model.UserBean;
import Repository.ChannelRepository;

/**
 * Servlet implementation class subscribeServlet
 */
@WebServlet("/subscribe")
public class subscribeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public subscribeServlet() {
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
//	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		HttpSession session = request.getSession();
//        UserBean currentUser = (UserBean) session.getAttribute("currentUser");
//        
//        if (currentUser == null) {
//            response.sendRedirect("login.jsp?error=Please login to subscribe");
//            return;
//        }
//        
//        String channelIdParam = request.getParameter("channelId");
//        String action = request.getParameter("action");
//        
//        if (channelIdParam == null || action == null) {
//            response.sendRedirect("index.jsp");
//            return;
//        }
//        
//        try {
//            int channelId = Integer.parseInt(channelIdParam);
//            boolean success = false;
//            
//            if ("subscribe".equals(action)) {
//                success = ChannelRepository.subscribeToChannel(currentUser.getUserId(), channelId);
//            } else if ("unsubscribe".equals(action)) {
//                success = ChannelRepository.unsubscribeFromChannel(currentUser.getUserId(), channelId);
//            }
//            
//            if (success) {
//                response.sendRedirect("channel?channelId=" + channelId);
//            } else {
//                response.sendRedirect("channel?channelId=" + channelId + "&error=Subscription failed");
//            }
//            
//        } catch (NumberFormatException e) {
//            response.sendRedirect("index.jsp?error=Invalid channel ID");
//        }
//    
//	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserBean currentUser = (UserBean) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect("login.jsp?error=Please login to subscribe");
            return;
        }
        
        String channelIdParam = request.getParameter("channelId");
        String action = request.getParameter("action");
        String videoId = request.getParameter("videoId"); // Get videoId for redirect back to video
        String redirectTo = request.getParameter("redirectTo"); // Optional: specify where to redirect
        
        if (channelIdParam == null || action == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        try {
            int channelId = Integer.parseInt(channelIdParam);
            boolean success = false;
            
            if ("subscribe".equals(action)) {
                success = ChannelRepository.subscribeToChannel(currentUser.getUserId(), channelId);
            } else if ("unsubscribe".equals(action)) {
                success = ChannelRepository.unsubscribeFromChannel(currentUser.getUserId(), channelId);
            }
            
            // Determine where to redirect
            String redirectUrl;
            if (redirectTo != null && "channel".equals(redirectTo)) {
                // Redirect to channel page
                redirectUrl = "channel?channelId=" + channelId;
            } else if (videoId != null && !videoId.isEmpty()) {
                // Redirect back to the video page
                redirectUrl = "videowatch?v=" + videoId;
            } else {
                // Default: redirect to channel page
                redirectUrl = "channel?channelId=" + channelId;
            }
            
            // Add success/failure message
            if (success) {
                redirectUrl += "&success=" + ("subscribe".equals(action) ? "Subscribed successfully" : "Unsubscribed successfully");
            } else {
                redirectUrl += "&error=Subscription failed";
            }
            
            response.sendRedirect(redirectUrl);
            
        } catch (NumberFormatException e) {
            response.sendRedirect("index.jsp?error=Invalid channel ID");
        }
    }
}
