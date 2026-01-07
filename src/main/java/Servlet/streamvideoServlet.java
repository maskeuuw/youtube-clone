package Servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Model.VideoBean;
import Repository.VideoRepository;

/**
 * Servlet implementation class streamvideoServlet
 */
@WebServlet("/streamvideo")
public class streamvideoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	VideoRepository videoRepository = new VideoRepository();
	private static final Logger LOGGER = Logger.getLogger(uploadvideoServlet.class.getName());

	// Buffer size for reading and writing data chunks
	private static final int BUFFER_SIZE = 1024 * 128;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public streamvideoServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */


	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String videoIdParam = request.getParameter("videoId");
		if (videoIdParam == null || videoIdParam.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Video ID is required");
			return;
		}

		File videoFile = null;
		String mimeType = "video/mp4";

		try {
			int videoId = Integer.parseInt(videoIdParam);
			VideoBean video = videoRepository.getVideoById(videoId);

			if (video == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Video not found in DB");
				return;
			}

			String filePath = video.getVideoUrl();
			videoFile = new File(filePath);

			LOGGER.info("StreamServlet attempting to read (BYTE-RANGE MODE): " + videoFile.getAbsolutePath());

			if (!videoFile.exists()) {
				LOGGER.severe("ERROR: File does not exist on disk! Path: " + videoFile.getAbsolutePath());
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Video file does not exist on server disk");
				return;
			}

			// --- 1. GET FILE METADATA AND MIME TYPE ---
			long fileLength = videoFile.length();
			String detectedMimeType = getServletContext().getMimeType(videoFile.getName());
			if (detectedMimeType != null) {
				mimeType = detectedMimeType;
			}

			// --- 2. HANDLE RANGE HEADER ---
			String range = request.getHeader("Range");
			long startByte = 0;
			long endByte = fileLength - 1;
			long contentLength = fileLength;
			int responseStatus = HttpServletResponse.SC_OK; // Default to 200 OK

			// Only proceed with partial content if a Range header is present
			if (range != null) {
				// Example: bytes=0-1023
				range = range.replaceAll("bytes=", "");
				String[] rangeParts = range.split("-");

				try {
					// Extract start byte
					startByte = Long.parseLong(rangeParts[0].trim());

					// Extract end byte if present
					if (rangeParts.length > 1 && !rangeParts[1].isEmpty()) {
						endByte = Long.parseLong(rangeParts[1].trim());
					}
				} catch (NumberFormatException e) {
					// Ignore range header if format is invalid (fall through to 200 OK)
					LOGGER.warning("Invalid Range header format: " + request.getHeader("Range"));
				}

				// If start byte is requested, we will deliver partial content (206)
				if (startByte < fileLength) {

					// Correct end byte if it exceeds file length
					if (endByte >= fileLength) {
						endByte = fileLength - 1;
					}

					// Set status to 206 Partial Content
					responseStatus = HttpServletResponse.SC_PARTIAL_CONTENT;

					// Calculate the actual content length for the partial response
					contentLength = endByte - startByte + 1;

					// Set the Content-Range header: bytes 0-1023/100000
					response.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + fileLength);
				}
			}

			// --- 3. SET MANDATORY RESPONSE HEADERS ---
			response.setStatus(responseStatus);
			response.setContentType(mimeType);
			response.setHeader("Content-Length", String.valueOf(contentLength));
			response.setHeader("Accept-Ranges", "bytes"); // Crucial for seeking/resuming

			// --- 4. STREAM CONTENT ---
			try (RandomAccessFile fileAccess = new RandomAccessFile(videoFile, "r");
					OutputStream out = response.getOutputStream()) {

				byte[] buffer = new byte[BUFFER_SIZE];
				int bytesRead;

				// Seek to the start position requested by the browser
				fileAccess.seek(startByte);

				long bytesRemaining = contentLength;

				while (bytesRemaining > 0 && (bytesRead = fileAccess.read(buffer, 0,
						(int) Math.min(BUFFER_SIZE, bytesRemaining))) != -1) {
					out.write(buffer, 0, bytesRead);
					bytesRemaining -= bytesRead;
				}

				// Ensure all buffered output is sent before closing
				out.flush();

			} catch (IOException e) {
				// Catches ClientAbortException (or its cause, "An established connection was
				// aborted")
				String errorMessage = e.getMessage() != null ? e.getMessage() : e.toString();

				if (errorMessage.contains("ClientAbortException") || errorMessage.contains("Connection reset")
						|| errorMessage.contains("aborted by the software")) {
					// This is expected when the client stops streaming (e.g., stops the video,
					// closes the tab, or seeks)
					LOGGER.log(Level.INFO, "Client disconnected gracefully during streaming (Handled).", e);
				} else {
					// Log other severe IO errors
					LOGGER.log(Level.SEVERE, "Severe IO Error streaming video for ID: " + videoIdParam, e);
					if (!response.isCommitted()) {
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error streaming video");
					}
				}
			}

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "General Error streaming video for ID: " + videoIdParam, e);

			if (!response.isCommitted()) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "General error streaming video");
			}
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
