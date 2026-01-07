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
import Repository.SubscriptionRepository;

/**
 * Servlet implementation class subscriptionsServlet
 */
@WebServlet("/subscriptions")
public class subscriptionsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private SubscriptionRepository subscriptionRepository = new SubscriptionRepository();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public subscriptionsServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);

		// Check if user is logged in
		if (session == null || session.getAttribute("currentUser") == null) {
			response.sendRedirect("login.jsp");
			return;
		}

		Integer userId = (Integer) session.getAttribute("userId");

		if (userId == null) {
			response.sendRedirect("login.jsp");
			return;
		}

		try {
			// Fetch all subscribed channels
			List<ChannelBean> subscribedChannels = subscriptionRepository.getSubscribedChannels(userId);

			// Set attributes for JSP
			request.setAttribute("subscribedChannels", subscribedChannels);
			request.setAttribute("subscriptionCount", subscribedChannels != null ? subscribedChannels.size() : 0);

			// Forward to subscriptions page
			request.getRequestDispatcher("subscriptions.jsp").forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("error.jsp?message=Error loading subscriptions");
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);

		// Check if user is logged in
		if (session == null || session.getAttribute("currentUser") == null) {
			response.sendRedirect("login.jsp");
			return;
		}

		Integer userId = (Integer) session.getAttribute("userId");
		String action = request.getParameter("action");
		String channelIdStr = request.getParameter("channelId");

		if (userId == null || action == null || channelIdStr == null) {
			response.sendRedirect("subscriptions");
			return;
		}

		try {
			int channelId = Integer.parseInt(channelIdStr);
			boolean success = false;

			if ("unsubscribe".equals(action)) {
				success = subscriptionRepository.unsubscribeFromChannel(userId, channelId);
			}

			// Redirect back to subscriptions page
			response.sendRedirect("subscriptions");

		} catch (NumberFormatException e) {
			response.sendRedirect("subscriptions");
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("subscriptions?error=true");
		}
	}

}
