package Servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Model.UserBean;
import Model.VideoBean;
import Repository.AdminRepository;

/**
 * Servlet implementation class AdminServlet
 */
@WebServlet("/admindashboard")
public class AdminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	AdminRepository adminrepo = new AdminRepository();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AdminServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 1. Check if session exists
        HttpSession session = request.getSession(false);
        
        // 2. Get the role from the session
        String role = (session != null) ? (String) session.getAttribute("userRole") : null;

        // 3. Security Check: Redirect if not an Admin
        if (role == null || !role.equalsIgnoreCase("Admin")) {
            // It's better to redirect back to login if they aren't authorized
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 1. Get the data using the DAO
        List<UserBean> userList;
        List<VideoBean> videoList;
		try {
			userList = adminrepo.getAllUsers();
			videoList = adminrepo.getAllVideos();
			
			// 2. Attach data to the request
	        request.setAttribute("userList", userList);
	        request.setAttribute("videoList", videoList);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
                
        // 4. Success: Forward to the Admin Dashboard JSP
        // Ensure you have a file named admin_dashboard.jsp in your WebContent/WEB-INF or root
        request.getRequestDispatcher("/admindashboard.jsp").forward(request, response);
    
	}

	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Usually, doPost is used for actions (like deleting a user)
        // You can call doGet here to apply the same security check
        doGet(request, response);
	}

}
