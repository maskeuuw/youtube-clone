package Servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher; // Import RequestDispatcher

/**
 * The central servlet for routing all main application requests (Home, Channel).
 * Maps to the application root (/) and the channel view (/channel).
 */
@WebServlet("/front")
public class frontcontrollerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public frontcontrollerServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
        
        // 1. Get the requested path (e.g., "/" or "/channel")
	    String path = request.getServletPath(); 
        
        // request.getParameter("id") would be used if the URL was /channel?id=123
        String channelId = request.getParameter("id"); 

        // Variable to hold the name of the content page to load (used by index.jsp)
        String contentPage = "home"; 

        // --- Logic to determine content ---
        
        if (path.equals("/channel") && channelId != null) {
            
            // A. Load Channel Profile View: URL example: /channel?id=123
            
            // TODO: Fetch Channel data from DB using channelId and set it as an attribute
            // Example: Channel channel = ChannelService.getChannelById(channelId);
            // request.setAttribute("channelData", channel);
            
            contentPage = "channel";
            
        } 
        // The default case: path is "/" (or any other unhandled path, which defaults to home)
        
        request.setAttribute("mainContentPage", contentPage);
        
        // --- 2. Forward to the main layout file (index.jsp) ---
        // The index.jsp will use the 'mainContentPage' attribute to decide what dynamic content to include.
        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
        dispatcher.forward(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
		doGet(request, response);
	}

}