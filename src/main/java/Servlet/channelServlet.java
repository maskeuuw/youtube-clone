package Servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Model.ChannelBean;
import Model.UserBean;
import Model.VideoBean;
import Repository.ChannelRepository;
import Repository.VideoRepository;

/**
 * Servlet implementation class channelheaderServlet
 */
@WebServlet("/channel")
public class channelServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public channelServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//		String channelIdParam = request.getParameter("channelId");
//
//		if (channelIdParam == null || channelIdParam.isEmpty()) {
//			// Redirect to home or show error
//			response.sendRedirect("home.jsp");
//			return;
//		}
//
//		try {
//			int channelId = Integer.parseInt(channelIdParam);
//			ChannelBean channel = ChannelRepository.getChannelById(channelId);
//
//			if (channel == null) {
//				// Channel not found
//				response.sendRedirect("home.jsp");
//				return;
//			}
//
//			// Add these to your ChannelServlet after getting the channel
//			List<VideoBean> videos = VideoRepository.getVideosByChannelId(channelId);
//			int videoCount = VideoRepository.getVideoCountByChannelId(channelId);
//
//			// Add to request attributes
//			request.setAttribute("videos", videos);
//			request.setAttribute("videoCount", videoCount);
//
//			// Get current user from session
//			HttpSession session = request.getSession();
//			UserBean currentUser = (UserBean) session.getAttribute("currentUser");
//
//			// Check if current user is the channel owner
//			boolean isOwner = false;
//			if (currentUser != null) {
//				isOwner = currentUser.getUserId() == channel.getUserId();
//			}
//
//			// Set attributes for JSP
//			request.setAttribute("channel", channel);
//			request.setAttribute("isOwner", isOwner);
//
//			// Forward to JSP
//			request.getRequestDispatcher("user-channel.jsp").forward(request, response);
//
//		} catch (NumberFormatException e) {
//			response.sendRedirect("home.jsp");
//		}
		String channelIdParam = request.getParameter("channelId");
		
		// Get tab parameter (home, videos, about)
        String tab = request.getParameter("tab");
        if (tab == null || tab.isEmpty()) {
            tab = "home"; // Default tab
        }

		if (channelIdParam == null || channelIdParam.isEmpty()) {
			response.sendRedirect("index.jsp?error=Channel ID is required");
			return;
		}

		try {
			int channelId = Integer.parseInt(channelIdParam);
			
//			ChannelBean channel = ChannelRepository.getChannelById(channelId);
			ChannelBean channel = ChannelRepository.getChannelByIdWithImage(channelId);
			if (channel == null) {
				response.sendRedirect("index.jsp?error=Channel not found");
				return;
			}

			// Get videos and statistics
			
			List<VideoBean> videos = VideoRepository.getVideosByChannelId(channelId);
			int videoCount = VideoRepository.getVideoCountByChannelId(channelId);
			long totalViewCount = VideoRepository.getTotalViewsByChannelId(channelId);
	        
			// Get current user from session
			HttpSession session = request.getSession();
			UserBean currentUser = (UserBean) session.getAttribute("currentUser");

			// Check if current user is the channel owner
			boolean isOwner = false;
			if (currentUser != null) {
				isOwner = currentUser.getUserId() == channel.getUserId();
			}
			
			System.out.println("DEBUG: Channel ID: " + channelId + ", Current User ID: " + 
                    (currentUser != null ? currentUser.getUserId() : "null") + 
                    ", Is Owner: " + isOwner);
			
			if (isOwner) {
	            // Owner: Get ALL videos (including private)
	            videos = VideoRepository.getAllVideosByChannelId(channelId);
	            videoCount = videos.size(); // Or use VideoRepository.getVideoCountByChannelId(channelId)
	            
	            // Calculate total views from all videos (including private)
	            totalViewCount = 0;
	            for (VideoBean video : videos) {
	                totalViewCount += video.getViewCount();
	            }
	        } else {
	            // Non-owner: Get only PUBLIC videos
	            videos = VideoRepository.getVideosByChannelId(channelId);
	            videoCount = VideoRepository.getVideoCountByChannelId(channelId);
	            totalViewCount = VideoRepository.getTotalViewsByChannelId(channelId);
	        }
	        
	        // Get subscriber count (same for everyone)
	        int subscriberCount = ChannelRepository.getSubscriberCount(channelId);
			
			// Check if current user is subscribed
	        boolean isSubscribed = false;
	        if (currentUser != null) {
	            isSubscribed = ChannelRepository.isUserSubscribed(currentUser.getUserId(), channelId);
	        }
	        
	        VideoBean featuredVideo = null;
            if (!videos.isEmpty()) {
                featuredVideo = videos.get(0);
            }
            
			// Set attributes for JSP
            request.setAttribute("channel", channel);
            request.setAttribute("tab", tab);
            request.setAttribute("videoCount", videoCount);
            request.setAttribute("totalViewCount", totalViewCount);
            request.setAttribute("subscriberCount", subscriberCount);
            request.setAttribute("videos", videos);
            request.setAttribute("isOwner", isOwner);
            request.setAttribute("isSubscribed", isSubscribed);
            request.setAttribute("featuredVideo", featuredVideo);
            request.setAttribute("currentUser", currentUser);

			// Forward to JSP
			request.getRequestDispatcher("user-channel.jsp").forward(request, response);

		} catch (NumberFormatException e) {
			response.sendRedirect("index.jsp?error=Invalid channel ID");
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("index.jsp?error=Server error");
		}
	

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//		// 1. Get the requested channel ID (e.g., from the URL parameter)
//		String channelId = request.getParameter("id"); 
//
//		// 2. Fetch the Channel Model (data)
//		// This is where your actual database call would happen.
//		ChannelRepository channelData = ChannelRepository.getChannelById(channelId); 
//
//		// 3. Set the data into the Request Scope for the JSP to access
//		if (channelData != null) {
//		    request.setAttribute("channel", channelData); // <-- This is the key attribute
//		    request.setAttribute("mainContentPage", "channel"); // To load the correct JSP content
//		}
//
//		// 4. Forward the request to your main JSP template (e.g., /mainLayout.jsp)
//		request.getRequestDispatcher("/mainLayout.jsp").forward(request, response);
	}

}
