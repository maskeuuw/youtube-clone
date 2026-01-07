package Servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Model.UserBean;
import Repository.UserRepository;

/**
 * Servlet implementation class loginServlet
 */
@WebServlet("/login")
public class loginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	UserRepository repo=new UserRepository();    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public loginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 	HttpSession session = request.getSession();
	        Integer userId = (Integer) session.getAttribute("userId");
	        
	        // Check if user is logged in
	        if (userId == null) {
//	            response.sendRedirect("login.jsp");
	        	RequestDispatcher dispatcher = request.getRequestDispatcher("/login.jsp");
	            dispatcher.forward(request, response);
	            return;
	        }
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email=request.getParameter("email");
		String password=request.getParameter("password");
		
		String errorMessage = null;
		
		UserBean user = repo.validateUser(email, password);
		
		if (user != null) {
            // A. SUCCESS: Create a Session and Redirect
            
            // Get the current session (create one if none exists)
            HttpSession session = request.getSession();
            
            // Store the UserBean in the session. This keeps the user logged in.
            // You can access user data (like name) on any JSP using ${sessionScope.currentUser.name}
            session.setAttribute("currentUser", user); 
            
            session.setAttribute("userName", user.getUserName());
            
         // *** FIX: Store the primary key (ID) separately for access/security ***
            session.setAttribute("userId", user.getUserId());
            
            session.setAttribute("channelId", user.getChannelId());
            
            session.setAttribute("userRole", user.getUserRole());
            // Redirect the user to the home page (index.jsp)
//          response.sendRedirect(request.getContextPath() + "/home");
            
         // 3. ROLE-BASED REDIRECTION
            String role = user.getUserRole(); // This will be 'Admin' or 'User'
            
            if ("Admin".equalsIgnoreCase(role)) {
                // Send Admin to their dashboard
                response.sendRedirect(request.getContextPath() + "/admindashboard"); 
            } else {
                // Send regular User to home
                response.sendRedirect(request.getContextPath() + "/home");
            }
            
        } else {
            // B. FAILURE: Set error message and forward back to login page
            errorMessage = "Invalid email or password. Please try again.";
            
            request.setAttribute("errorMessage", errorMessage); 
            
            // Forward back to the login JSP to show the error
            request.getRequestDispatcher("/home").forward(request, response);
        }
        
    } 
	
}


