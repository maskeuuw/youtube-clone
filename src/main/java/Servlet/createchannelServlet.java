package Servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Model.ChannelBean;
import Repository.ChannelRepository;
import Repository.UserRepository;

/**
 * Servlet implementation class createchannelServlet
 */
@WebServlet("/createchannel")
public class createchannelServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	ChannelBean newrepo = new ChannelBean();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public createchannelServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 1. Get and Convert Input Data
		
		HttpSession session = request.getSession();
		Integer loggedInUserId = (Integer) session.getAttribute("userId");
		
		if (loggedInUserId == null) {
			// Redirect to login or show error
			response.sendRedirect("login.jsp");
			return;
		}
		
		 // Check if user already has a channel
        if (ChannelRepository.userHasChannel(loggedInUserId)) {
            ChannelBean existingChannel = ChannelRepository.getChannelByUserId(loggedInUserId);
            if (existingChannel != null) {
                // Redirect to existing channel
                response.sendRedirect("channel?channelId=" + existingChannel.getChannelId());
                return;
            }
        }
        
		String channelName = request.getParameter("channelName");
		String description = request.getParameter("description");

		// Basic input validation (optional but recommended)
		if (channelName == null || channelName.trim().isEmpty() || description == null
				|| description.trim().isEmpty()) {
			request.setAttribute("errorMessage", "Channel name and description cannot be empty.");
			request.getRequestDispatcher("/create-channel.jsp").forward(request, response);
			return;
		}
		
		// Use the DAO to Save Data and get the channel ID
        int channelId = ChannelRepository.createChannel(loggedInUserId, channelName, description);
        
        System.out.println("DEBUG: Created channel ID: " + channelId);
        
        // Handle Result and Redirect
        if (channelId > 0) {
            // Store channel ID in session for easy access
            session.setAttribute("channelId", channelId);
            
            // Redirect to the new channel page
            response.sendRedirect("channel?channelId=" + channelId);
        } else {
            // Check if channel name already exists
            boolean channelExists = ChannelRepository.doesChannelExistForUser(loggedInUserId, channelName);
            
            if (channelExists) {
                request.setAttribute("errorMessage", "You already have a channel with this name.");
            } else {
                request.setAttribute("errorMessage", "Failed to create channel. Please try again.");
            }
            request.getRequestDispatcher("/create-channel.jsp").forward(request, response);
        }
    }

}
