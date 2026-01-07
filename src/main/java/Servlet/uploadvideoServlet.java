package Servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

// Apache Commons FileUpload Imports (REQUIRED)
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import Model.VideoBean;
import Repository.VideoRepository; // Import the repository

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet implementation class uploadvideoServlet
 */
@WebServlet("/uploadvideo")
@MultipartConfig(
		fileSizeThreshold = 1024 * 1024, // 1MB: Size threshold after which the file will be temporarily stored												// on disk
		maxFileSize = 1024 * 1024 * 1024, // 1GB: Max size allowed for a single file
		maxRequestSize = 1024 * 1024 * 1025 // ~1GB: Max size allowed for the whole request (files + form data)
)
public class uploadvideoServlet extends HttpServlet {
	private final VideoRepository videoRepository = new VideoRepository();
	private static final Logger LOGGER = Logger.getLogger(uploadvideoServlet.class.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public uploadvideoServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Forward to the upload form JSP
		RequestDispatcher dispatcher = request.getRequestDispatcher("upload-video.jsp");
		dispatcher.forward(request, response);
	}

	private static final String UPLOAD_DIRECTORY = "D:/Youtubeprojectupload/videos/";

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
//	protected void doPost(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//
//		// --- 1. Get Text Fields ---
//		// These fields are retrieved using getParameter() as usual
//		String title = request.getParameter("title");
//		String description = request.getParameter("description");
//		String visibility = request.getParameter("visibility");
//
//		// Simple logging for text fields
//		System.out.println("Title: " + title);
//		System.out.println("Description: " + description);
//		System.out.println("Visibility: " + visibility);
//
//		// Create the directory if it doesn't exist
//		File uploadDir = new File(UPLOAD_DIRECTORY);
//		if (!uploadDir.exists()) {
//			uploadDir.mkdirs();
//		}
//
//		// --- 2. Handle File Uploads (Video and Thumbnail) ---
//		String videoFileName = null;
//		String thumbnailFileName = null;
//
//		try {
//			// Get the video file part by its 'name' attribute from the form: 'videoFile'
//			Part videoPart = request.getPart("videoFile");
//			videoFileName = getFileName(videoPart);
//
//			if (videoFileName != null && !videoFileName.isEmpty()) {
//				// Save the video file to the specified directory
//				videoPart.write(UPLOAD_DIRECTORY + videoFileName);
//				System.out.println("Video uploaded successfully: " + videoFileName);
//			}
//
//			// Get the thumbnail file part by its 'name' attribute from the form:
//			// 'thumbnail'
//			Part thumbnailPart = request.getPart("thumbnail");
//			thumbnailFileName = getFileName(thumbnailPart);
//
//			if (thumbnailFileName != null && !thumbnailFileName.isEmpty()) {
//				// Save the thumbnail file
//				thumbnailPart.write(UPLOAD_DIRECTORY + thumbnailFileName);
//				System.out.println("Thumbnail uploaded successfully: " + thumbnailFileName);
//			}
//
//			// --- 3. Success Response and Further Processing ---
//
//			// At this point, you would typically:
//			// a) Save the video metadata (title, description, file paths) to a database.
//			// b) Redirect the user to a success page or the video's detail page.
//
//			response.getWriter().println("Video Upload Successful!");
//			response.getWriter().println("Title: " + title);
//			response.getWriter().println("Video Path: " + UPLOAD_DIRECTORY + videoFileName);
//			response.getWriter().println("Thumbnail Path: " + UPLOAD_DIRECTORY + thumbnailFileName);
//
//			// Example of redirection:
//			// response.sendRedirect("success.jsp?video=" + videoFileName);
//
//		} catch (Exception ex) {
//			// Handle exceptions during file processing/saving
//			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
//					"Error during file upload: " + ex.getMessage());
//			ex.printStackTrace();
//		}
//
//	}
//
//	/**
//	 * Extracts the file name from the Content-Disposition header of the Part. This
//	 * is a utility method often needed when dealing with Part objects.
//	 */
//	private String getFileName(Part part) {
//		if (part == null) {
//			return null; // Handle cases where a part might be optional (like your thumbnail)
//		}
//		String contentDisp = part.getHeader("content-disposition");
//		for (String s : contentDisp.split(";")) {
//			if (s.trim().startsWith("filename")) {
//				// The substring removes 'filename=' and the surrounding quotes
//				return s.substring(s.indexOf("=") + 2, s.length() - 1);
//			}
//		}
//		return null;
//	}
	
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
		// --- 1. Variable Initialization and Form Field Retrieval ---
        String videoFilePath = null;
        byte[] thumbnailData = null; // 💡 CHANGED: Stores the raw thumbnail image bytes
        
        // Get non-file form parameters
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String visibility = request.getParameter("visibility");
        
        // --- 2. Channel ID Retrieval (MUST BE DONE SAFELY) ---
        int currentChannelId = -1; 
        Integer channelIdObject = (Integer) request.getSession().getAttribute("channelId");
        
        if (channelIdObject == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Channel ID not found in session. Please log in.");
            return;
        }
        currentChannelId = channelIdObject.intValue();
        
        // --- 3. Prepare Directory ---
        File uploadDir = new File(UPLOAD_DIRECTORY);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        try {
            // --- 4. Handle Video File Upload (Path is still saved to disk) ---
            Part videoPart = request.getPart("videoFile");
            String videoFileName = getFileName(videoPart);
            
            if (videoFileName != null && !videoFileName.isEmpty()) {
                videoFilePath = UPLOAD_DIRECTORY + videoFileName;
                videoPart.write(videoFilePath);
                System.out.println("Video file saved to: " + videoFilePath);
            }
            
            // --- 5. Handle Thumbnail File Upload (Data is read into memory) ---
            Part thumbnailPart = request.getPart("thumbnail"); 
            String thumbnailFileName = getFileName(thumbnailPart);
            
            if (thumbnailFileName != null && !thumbnailFileName.isEmpty()) {
                // 💡 CRITICAL: Read the bytes from the input stream
                try (InputStream inputStream = thumbnailPart.getInputStream()) {
                    thumbnailData = IOUtils.toByteArray(inputStream);
                }
                
                // We SKIP thumbnailPart.write() since we are storing the bytes in the DB
                System.out.println("Thumbnail bytes read successfully (" + thumbnailData.length + " bytes).");
            }

            // --- 6. Proceed to Database Save ---
            if (videoFilePath != null) {
                
                // 🔑 Call the repository with the new byte[] argument
                boolean dbSuccess = videoRepository.saveUpload(
                    currentChannelId,
                    title,
                    videoFilePath,
                    thumbnailData, // 💡 NEW: Passing the byte array
                    description,
                    visibility
                );
                
                // --- 7. Respond to the User ---
                if (dbSuccess) {
                    response.sendRedirect("channel?channelId=" + currentChannelId);
                } else {
                    response.getWriter().println("File saved, but failed to record path in database.");
                }
            } else {
                 response.getWriter().println("Error: No video file found in the request.");
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database error during upload process.", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + ex.getMessage());
        }
        catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Generic error during upload process.", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during file upload: " + ex.getMessage());
        }
    }

	/** Extracts the original file name from the Part header. */
	private String getFileName(Part part) {
		if (part == null)
			return null;
		String contentDisp = part.getHeader("content-disposition");
		for (String s : contentDisp.split(";")) {
			if (s.trim().startsWith("filename")) {
				return s.substring(s.indexOf("=") + 2, s.length() - 1);
			}
		}
		return null;
	}
}