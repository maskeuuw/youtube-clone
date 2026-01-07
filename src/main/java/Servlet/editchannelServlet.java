package Servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import Model.ChannelBean;
import Model.UserBean;
import Repository.ChannelRepository;

/**
 * Servlet implementation class editchannelServlet
 */
@WebServlet("/edit-channel")
@MultipartConfig(
	    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
	    maxFileSize = 1024 * 1024 * 10,      // 10MB
	    maxRequestSize = 1024 * 1024 * 50    // 50MB
	)
public class editchannelServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public editchannelServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
System.out.println("===== EditChannelServlet.doGet() START ====="); // Debug
        
        HttpSession session = request.getSession();
        UserBean currentUser = (UserBean) session.getAttribute("currentUser");
        
        // Check if user is logged in
        if (currentUser == null) {
            System.out.println("User not logged in, redirecting to login"); // Debug
            response.sendRedirect("login.jsp?error=Please login first");
            return;
        }
        
        System.out.println("Current User ID: " + currentUser.getUserId()); // Debug
        
        // Get channel ID from request parameter
        String channelIdParam = request.getParameter("channelId");
        System.out.println("Channel ID parameter: " + channelIdParam); // Debug
        
        if (channelIdParam == null || channelIdParam.isEmpty()) {
            System.out.println("No channelId parameter, trying to get user's channel"); // Debug
            // Try to get user's channel
            ChannelBean userChannel = ChannelRepository.getChannelByUserId(currentUser.getUserId());
            System.out.println("User channel from DB: " + (userChannel != null ? "Found" : "Not found")); // Debug
            
            if (userChannel != null) {
                System.out.println("Setting userChannel with ID: " + userChannel.getChannelId()); // Debug
                request.setAttribute("channel", userChannel);
            } else {
                System.out.println("Channel not found for user"); // Debug
                response.sendRedirect("index.jsp?error=Channel not found");
                return;
            }
        } else {
            try {
                int channelId = Integer.parseInt(channelIdParam);
                System.out.println("Parsed channelId: " + channelId); // Debug
                
                ChannelBean channel = ChannelRepository.getChannelByIdWithImage(channelId);
                System.out.println("Channel from DB: " + (channel != null ? "Found" : "Not found")); // Debug
                
                if (channel != null) {
                    System.out.println("Channel User ID: " + channel.getUserId()); // Debug
                    System.out.println("Current User ID: " + currentUser.getUserId()); // Debug
                    
                    // Check if user owns the channel
                    if (channel.getUserId() != currentUser.getUserId()) {
                        System.out.println("User doesn't own this channel, unauthorized"); // Debug
                        response.sendRedirect("index.jsp?error=Unauthorized access");
                        return;
                    }
                    
                    System.out.println("Setting channel attribute with ID: " + channel.getChannelId()); // Debug
                    request.setAttribute("channel", channel);
                } else {
                    System.out.println("Channel not found in database"); // Debug
                    response.sendRedirect("index.jsp?error=Channel not found");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid channel ID format: " + channelIdParam); // Debug
                e.printStackTrace();
                response.sendRedirect("index.jsp?error=Invalid channel ID");
                return;
            }
        }
        
        System.out.println("Forwarding to edit-channel.jsp"); // Debug
        System.out.println("===== EditChannelServlet.doGet() END ====="); // Debug
        request.getRequestDispatcher("edit-channel.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("===== EditChannelServlet.doPost() START ====="); // Debug
        
        HttpSession session = request.getSession();
        UserBean currentUser = (UserBean) session.getAttribute("currentUser");
        
        // Check if user is logged in
        if (currentUser == null) {
            System.out.println("User not logged in, redirecting to login"); // Debug
            response.sendRedirect("login.jsp?error=Please login first");
            return;
        }
        
        // Get form parameters
        String channelIdParam = request.getParameter("channelId");
        String channelName = request.getParameter("channelName");
        String channelDesc = request.getParameter("channelDesc");
        Part filePart = request.getPart("channelImage");
        
        System.out.println("Form Data - Channel ID: " + channelIdParam); // Debug
        System.out.println("Form Data - Channel Name: " + channelName); // Debug
        System.out.println("Form Data - File part exists: " + (filePart != null)); // Debug
        
        // Validate inputs
        if (channelIdParam == null || channelIdParam.isEmpty() || 
            channelName == null || channelName.trim().isEmpty()) {
            System.out.println("Validation failed - empty fields"); // Debug
            request.setAttribute("error", "Channel name is required");
            
            // Try to reload the channel for the form
            try {
                if (channelIdParam != null && !channelIdParam.isEmpty()) {
                    int channelId = Integer.parseInt(channelIdParam);
                    ChannelBean channel = ChannelRepository.getChannelByIdWithImage(channelId);
                    if (channel != null && channel.getUserId() == currentUser.getUserId()) {
                        request.setAttribute("channel", channel);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            request.getRequestDispatcher("edit-channel.jsp").forward(request, response);
            return;
        }
        
        try {
            int channelId = Integer.parseInt(channelIdParam);
            System.out.println("Parsed channelId for update: " + channelId); // Debug
            
            // Get channel and verify ownership
            ChannelBean channel = ChannelRepository.getChannelByIdWithImage(channelId);
            System.out.println("Channel retrieved for update: " + (channel != null ? "Found" : "Not found")); // Debug
            
            if (channel == null) {
                System.out.println("Channel not found in database"); // Debug
                response.sendRedirect("index.jsp?error=Channel not found");
                return;
            }
            
            if (channel.getUserId() != currentUser.getUserId()) {
                System.out.println("User doesn't own this channel"); // Debug
                System.out.println("Channel User ID: " + channel.getUserId()); // Debug
                System.out.println("Current User ID: " + currentUser.getUserId()); // Debug
                response.sendRedirect("index.jsp?error=Unauthorized access");
                return;
            }
            
            // Update channel information
            channel.setChannelName(channelName.trim());
            if (channelDesc != null) {
                channel.setChannelDesc(channelDesc.trim());
            }
            
            boolean updateSuccess;
            
            // Handle file upload if present
            if (filePart != null && filePart.getSize() > 0 && filePart.getSubmittedFileName() != null) {
                System.out.println("Processing image upload..."); // Debug
                System.out.println("File name: " + filePart.getSubmittedFileName()); // Debug
                System.out.println("File size: " + filePart.getSize()); // Debug
                
                // Validate file type
                String contentType = filePart.getContentType();
                System.out.println("Content type: " + contentType); // Debug
                
                if (contentType == null || !contentType.startsWith("image/")) {
                    System.out.println("Invalid file type"); // Debug
                    request.setAttribute("error", "Only image files are allowed");
                    request.setAttribute("channel", channel);
                    request.getRequestDispatcher("edit-channel.jsp").forward(request, response);
                    return;
                }
                
                // Validate file size (max 10MB)
                if (filePart.getSize() > 10 * 1024 * 1024) {
                    System.out.println("File too large"); // Debug
                    request.setAttribute("error", "File size must be less than 10MB");
                    request.setAttribute("channel", channel);
                    request.getRequestDispatcher("edit-channel.jsp").forward(request, response);
                    return;
                }
                
                // Store image as BLOB in database
                InputStream imageStream = filePart.getInputStream();
                updateSuccess = ChannelRepository.updateChannelWithImage(channel, imageStream);
                System.out.println("Update with image result: " + updateSuccess); // Debug
                
            } else {
                // Update without changing image
                System.out.println("Updating without image..."); // Debug
                updateSuccess = ChannelRepository.updateChannel(channel);
                System.out.println("Update without image result: " + updateSuccess); // Debug
            }
            
            if (updateSuccess) {
                System.out.println("Update successful, redirecting..."); // Debug
                // Redirect to channel page
                response.sendRedirect("channel?channelId=" + channelId + "&success=Channel updated successfully");
            } else {
                System.out.println("Update failed"); // Debug
                request.setAttribute("error", "Failed to update channel");
                request.setAttribute("channel", channel);
                request.getRequestDispatcher("edit-channel.jsp").forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid channel ID format in POST: " + channelIdParam); // Debug
            e.printStackTrace();
            request.setAttribute("error", "Invalid channel ID");
            request.getRequestDispatcher("edit-channel.jsp").forward(request, response);
        } catch (Exception e) {
            System.out.println("Exception in doPost: " + e.getMessage()); // Debug
            e.printStackTrace();
            request.setAttribute("error", "Server error: " + e.getMessage());
            request.getRequestDispatcher("edit-channel.jsp").forward(request, response);
        }
        
        System.out.println("===== EditChannelServlet.doPost() END ====="); // Debug
    }
    

//    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
//            throws ServletException, IOException {
//        
//        System.out.println("EditChannelServlet: doPost called"); // Debug
//        
//        HttpSession session = request.getSession();
//        UserBean currentUser = (UserBean) session.getAttribute("currentUser");
//        
//        // Check if user is logged in
//        if (currentUser == null) {
//            response.sendRedirect("login.jsp?error=Please login first");
//            return;
//        }
//        
//        // Get form parameters
//        String channelIdParam = request.getParameter("channelId");
//        String channelName = request.getParameter("channelName");
//        String channelDesc = request.getParameter("channelDesc");
//        Part filePart = request.getPart("channelImage");
//        
//        System.out.println("Channel ID: " + channelIdParam); // Debug
//        System.out.println("Channel Name: " + channelName); // Debug
//        System.out.println("File part: " + filePart); // Debug
//        
//        // Validate inputs
//        if (channelIdParam == null || channelIdParam.isEmpty() || 
//            channelName == null || channelName.trim().isEmpty()) {
//            request.setAttribute("error", "Channel name is required");
//            
//            // Get channel again to display in form
//            try {
//                int channelId = Integer.parseInt(channelIdParam);
//                ChannelBean channel = ChannelRepository.getChannelByIdWithImage(channelId);
//                request.setAttribute("channel", channel);
//            } catch (Exception e) {
//                // Ignore
//            }
//            
//            request.getRequestDispatcher("edit-channel.jsp").forward(request, response);
//            return;
//        }
//        
//        try {
//            int channelId = Integer.parseInt(channelIdParam);
//            
//            // Get channel and verify ownership
//            ChannelBean channel = ChannelRepository.getChannelByIdWithImage(channelId);
//            if (channel == null || channel.getUserId() != currentUser.getUserId()) {
//                response.sendRedirect("index.jsp?error=Unauthorized access");
//                return;
//            }
//            
//            // Update channel information
//            channel.setChannelName(channelName.trim());
//            if (channelDesc != null) {
//                channel.setChannelDesc(channelDesc.trim());
//            }
//            
//            boolean updateSuccess;
//            
//            // Handle file upload if present
//            if (filePart != null && filePart.getSize() > 0 && filePart.getSubmittedFileName() != null) {
//                System.out.println("Processing image upload..."); // Debug
//                
//                // Validate file type
//                String contentType = filePart.getContentType();
//                System.out.println("Content type: " + contentType); // Debug
//                
//                if (contentType == null || !contentType.startsWith("image/")) {
//                    request.setAttribute("error", "Only image files are allowed");
//                    request.setAttribute("channel", channel);
//                    request.getRequestDispatcher("edit-channel.jsp").forward(request, response);
//                    return;
//                }
//                
//                // Validate file size (max 10MB)
//                if (filePart.getSize() > 10 * 1024 * 1024) {
//                    request.setAttribute("error", "File size must be less than 10MB");
//                    request.setAttribute("channel", channel);
//                    request.getRequestDispatcher("edit-channel.jsp").forward(request, response);
//                    return;
//                }
//                
//                // Store image as BLOB in database
//                InputStream imageStream = filePart.getInputStream();
//                updateSuccess = ChannelRepository.updateChannelWithImage(channel, imageStream);
//                System.out.println("Update with image result: " + updateSuccess); // Debug
//                
//            } else {
//                // Update without changing image
//                System.out.println("Updating without image..."); // Debug
//                updateSuccess = ChannelRepository.updateChannel(channel);
//                System.out.println("Update without image result: " + updateSuccess); // Debug
//            }
//            
//            if (updateSuccess) {
//                System.out.println("Update successful, redirecting..."); // Debug
//                // Redirect to channel page
//                response.sendRedirect("channel?channelId=" + channelId + "&success=Channel updated successfully");
//            } else {
//                System.out.println("Update failed"); // Debug
//                request.setAttribute("error", "Failed to update channel");
//                request.setAttribute("channel", channel);
//                request.getRequestDispatcher("edit-channel.jsp").forward(request, response);
//            }
//            
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//            request.setAttribute("error", "Invalid channel ID");
//            request.getRequestDispatcher("edit-channel.jsp").forward(request, response);
//        } catch (Exception e) {
//            e.printStackTrace();
//            request.setAttribute("error", "Server error: " + e.getMessage());
//            request.getRequestDispatcher("edit-channel.jsp").forward(request, response);
//        }
//    }
		

}
