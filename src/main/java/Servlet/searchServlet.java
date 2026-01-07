package Servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Model.VideoBean;
import Repository.SearchRepository;
import Repository.SearchRepository.SearchResult;
import Repository.VideoRepository;

/**
 * Servlet implementation class searchServlet
 */
@WebServlet("/search")
public class searchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	// Pagination constants
    private static final int RESULTS_PER_PAGE = 10;
    private static final int CHANNELS_PER_PAGE = 8;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public searchServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		 String query = request.getParameter("q");
	        String filter = request.getParameter("filter");
	        String sort = request.getParameter("sort");
	        String pageParam = request.getParameter("page");
	        
	        int page = 1;
	        if (pageParam != null && !pageParam.trim().isEmpty()) {
	            try {
	                page = Integer.parseInt(pageParam);
	                if (page < 1) page = 1;
	            } catch (NumberFormatException e) {
	                page = 1;
	            }
	        }
	        
	        if (query != null && !query.trim().isEmpty()) {
	            query = query.trim();
	            
	            // Set common attributes
	            request.setAttribute("searchQuery", query);
	            request.setAttribute("currentFilter", filter != null ? filter : "all");
	            request.setAttribute("currentSort", sort != null ? sort : "relevance");
	            request.setAttribute("currentPage", page);
	            
	            try {
	                if ("channels".equals(filter)) {
	                    // Search channels only with pagination
	                    int offset = (page - 1) * CHANNELS_PER_PAGE;
	                    SearchResult searchResult = SearchRepository.advancedSearch(query, "channels", sort, CHANNELS_PER_PAGE, offset);
	                    int totalChannels = SearchRepository.countSearchResults(query, "channels");
	                    int totalPages = (int) Math.ceil((double) totalChannels / CHANNELS_PER_PAGE);
	                    
	                    request.setAttribute("channels", searchResult.getChannels());
	                    request.setAttribute("totalPages", totalPages);
	                    request.setAttribute("totalResults", totalChannels);
	                    request.setAttribute("showChannelsOnly", true);
	                    
	                } else if ("videos".equals(filter)) {
	                    // Search videos only with pagination
	                    int offset = (page - 1) * RESULTS_PER_PAGE;
	                    SearchResult searchResult = SearchRepository.advancedSearch(query, "videos", sort, RESULTS_PER_PAGE, offset);
	                    int totalVideos = SearchRepository.countSearchResults(query, "videos");
	                    int totalPages = (int) Math.ceil((double) totalVideos / RESULTS_PER_PAGE);
	                    
	                    request.setAttribute("searchResults", searchResult.getVideos());
	                    request.setAttribute("totalPages", totalPages);
	                    request.setAttribute("totalResults", totalVideos);
	                    request.setAttribute("showVideosOnly", true);
	                    
	                } else {
	                    // Search both (default - with limits)
	                    SearchResult searchResult = SearchRepository.searchAll(query);
	                    
	                    // Set attributes
	                    request.setAttribute("searchResults", searchResult.getVideos());
	                    request.setAttribute("channels", searchResult.getChannels());
	                    
	                    // Calculate statistics
	                    request.setAttribute("totalVideos", searchResult.getVideos().size());
	                    request.setAttribute("totalChannels", searchResult.getChannels().size());
	                    request.setAttribute("totalResults", searchResult.getTotalResults());
	                    
	                    // Check if no results
	                    if (searchResult.getTotalResults() == 0) {
	                        request.setAttribute("noResults", true);
	                        
	                        // Get trending searches for suggestions
	                        List<String> trendingSearches = SearchRepository.getTrendingSearches(5);
	                        request.setAttribute("trendingSearches", trendingSearches);
	                    }
	                }
	                
	                
	                
	            } catch (ClassNotFoundException e) {
	                e.printStackTrace();
	                request.setAttribute("error", "Database connection error");
	                request.getRequestDispatcher("/search-results.jsp").forward(request, response);
	            } catch (Exception e) {
	                e.printStackTrace();
	                request.setAttribute("error", "An error occurred during search");
	                request.getRequestDispatcher("/search-results.jsp").forward(request, response);
	            }
	            
	         // Forward to JSP
                request.getRequestDispatcher("/search-results.jsp").forward(request, response);
	        } else {
	            // If no query, redirect to home
	            response.sendRedirect(request.getContextPath() + "/home");
	        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
