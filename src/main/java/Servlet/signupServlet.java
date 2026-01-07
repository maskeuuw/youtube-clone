package Servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Model.UserBean;
import Repository.UserRepository;

/**
 * Servlet implementation class signupServlet
 */
@WebServlet("/signup")
public class signupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    UserRepository repo=new UserRepository();   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public signupServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		RequestDispatcher dispatcher = request.getRequestDispatcher("/register.jsp");
        dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        
        String errorMessage = null;
        
        if (name == null || name.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                confirmPassword == null || confirmPassword.trim().isEmpty()) {
                
                errorMessage = "All fields are required. Please fill in all details.";
            }
        
     // Check 2: Password Match
        if (errorMessage == null && !password.equals(confirmPassword)) {
            errorMessage = "Passwords do not match. Please re-enter your passwords.";
        }
        
        if (errorMessage == null && password.length() < 8) {
            errorMessage = "Password must be at least 8 characters long.";
        }
        
        try {
            if (errorMessage == null && repo.isEmailRegistered(email)) { // Assume this method exists in your UserRepository
                errorMessage = "This email address is already registered. Try signing in.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "A database check error occurred.";
        }
        
        // 3. Handle Results (Forward or Redirect)
        if (errorMessage != null) {
            // A. FAILED: Set error message and forward back
            
            // Set error message for JSTL in JSP
            request.setAttribute("errorMessage", errorMessage); 
            
            // Forward back to the registration JSP to show the error and retain input values
            request.getRequestDispatcher("/register.jsp").forward(request, response); 
            
        } else {
            // B. SUCCESS: Hash password and save to DB
            try {
                
                // Create User object (Assuming you have this model)
                UserBean newUser = new UserBean(name, email, password);
                
                // Save to database
                repo.registerUser(newUser); // Assume this method exists in your UserRepository

                // Use the Post-Redirect-Get pattern to prevent re-submission
                request.getSession().setAttribute("successMessage", "Account created successfully! Please sign in.");
                
                // Redirect to the login page
                response.sendRedirect(request.getContextPath() + "/login.jsp"); 
                
            } catch (Exception e) {
                e.printStackTrace();
                // Handle database save failure
                request.setAttribute("errorMessage", "Error saving account data. Please try again later.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
            }
        }
    }
	

}
