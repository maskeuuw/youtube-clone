package Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import JDBCconnection.DBconnection;
import Model.VideoBean;

public class VideoRepository {

	public boolean saveUpload(int channelId, String videoName, String absoluteVideoFilePath, byte[] thumbnailData,
			String videoDesc, String videoVisibility) throws SQLException, ClassNotFoundException {

		final int initialViewCount = 0;

		// video_created_at is handled by the database (CURRENT_TIMESTAMP).
		String sql = "INSERT INTO Video (channel_id, video_name, video_desc, view_count, video_url, video_thumbnails, video_visibility) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = DBconnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			// Bind the Java variables to the SQL placeholders (?)
			ps.setInt(1, channelId); // 1: channel_id (Required FK)
			ps.setString(2, videoName); // 2: video_name (Title)
			ps.setString(3, videoDesc); // 3: video_desc (Description)
			ps.setInt(4, initialViewCount); // 4: view_count (Set to 0)
			ps.setString(5, absoluteVideoFilePath); // 5: video_url (The server path)
			// 💡 CRITICAL: Use setBytes() for BLOB data
			if (thumbnailData != null && thumbnailData.length > 0) {
				ps.setBytes(6, thumbnailData); // 6: video_thumbnails (BLOB data)
			} else {
				ps.setNull(6, java.sql.Types.BLOB); // Set to NULL if no thumbnail provided
			}

			ps.setString(7, videoVisibility); // 7: video_visibility (ENUM value)

			int rowsAffected = ps.executeUpdate();

			return rowsAffected > 0;

		} catch (SQLException e) {
			System.err.println("Database Error saving video metadata.");
			e.printStackTrace();
			throw e;
		}
	}

	public VideoBean getVideoById(int videoId) throws SQLException, ClassNotFoundException {
		String sql = "SELECT * FROM Video WHERE video_id = ? AND video_visibility = 'public'";

		try (Connection conn = DBconnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, videoId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				VideoBean video = new VideoBean();
				video.setVideoId(rs.getInt("video_id"));
				video.setChannelId(rs.getInt("channel_id"));
				video.setVideoName(rs.getString("video_name"));
				video.setVideoDesc(rs.getString("video_desc"));
				video.setViewCount(rs.getLong("view_count"));
				video.setVideoUrl(rs.getString("video_url"));
				video.setUploadDate(rs.getTimestamp("video_created_at"));
				video.setVideoVisibility(rs.getString("video_visibility"));

				// Check if thumbnail exists as BLOB
				byte[] thumbnailBytes = rs.getBytes("video_thumbnails");
				if (thumbnailBytes != null && thumbnailBytes.length > 0) {
//                    video.setVideoThumbnail("data:image/jpeg;base64," + 
//                        java.util.Base64.getEncoder().encodeToString(thumbnailBytes));
					video.setVideoThumbnail(thumbnailBytes);
				}
//                else {
//                    video.setVideoThumbnail(""); // Empty string for no thumbnail
//                }

				return video;
			}
		}
		return null;
	}

	public VideoBean getVideoById(int videoId, Integer userId) throws SQLException, ClassNotFoundException {
		String sql = "SELECT * FROM Video WHERE video_id = ? AND video_visibility = 'public'";

		try (Connection conn = DBconnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, videoId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				VideoBean video = new VideoBean();
				video.setVideoId(rs.getInt("video_id"));
				video.setChannelId(rs.getInt("channel_id"));
				video.setVideoName(rs.getString("video_name"));
				video.setVideoDesc(rs.getString("video_desc"));
				video.setViewCount(rs.getLong("view_count"));
				video.setVideoUrl(rs.getString("video_url"));
				video.setUploadDate(rs.getTimestamp("video_created_at"));
				video.setVideoVisibility(rs.getString("video_visibility"));

				// Check if thumbnail exists as BLOB
				byte[] thumbnailBytes = rs.getBytes("video_thumbnails");
				if (thumbnailBytes != null && thumbnailBytes.length > 0) {
//                    video.setVideoThumbnail("data:image/jpeg;base64," + 
//                        java.util.Base64.getEncoder().encodeToString(thumbnailBytes));
					video.setVideoThumbnail(thumbnailBytes);
				}
//                else {
//                    video.setVideoThumbnail(""); // Empty string for no thumbnail
//                }

				// Get reaction counts
				ReactionRepository reactionRepo = new ReactionRepository();
				int[] reactionCounts = reactionRepo.getReactionCounts(videoId);
				video.setLikeCount(reactionCounts[0]);
				video.setDislikeCount(reactionCounts[1]);

				// Get user's reaction if logged in
				if (userId != null) {
					String userReaction = reactionRepo.getUserReaction(userId, videoId);
					video.setUserReaction(userReaction);
				}

				return video;
			}
		}
		return null;
	}

	public void incrementViewCount(int videoId) throws SQLException, ClassNotFoundException {
		String sql = "UPDATE Video SET view_count = view_count + 1 WHERE video_id = ?";

		try (Connection conn = DBconnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, videoId);
			ps.executeUpdate();
		}
	}

	public List<VideoBean> getAllPublicVideos() throws SQLException, ClassNotFoundException {
		List<VideoBean> videos = new ArrayList<>();
		String sql = "SELECT * FROM Video WHERE video_visibility = 'public' ORDER BY video_created_at DESC";

		try (Connection conn = DBconnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				VideoBean video = new VideoBean();
				video.setVideoId(rs.getInt("video_id"));
				video.setVideoName(rs.getString("video_name"));
				video.setVideoDesc(rs.getString("video_desc"));
				video.setViewCount(rs.getLong("view_count"));
				video.setVideoUrl(rs.getString("video_url"));
				video.setUploadDate(rs.getTimestamp("video_created_at"));

				// Handle thumbnail
				byte[] thumbnailBytes = rs.getBytes("video_thumbnails");
				if (thumbnailBytes != null && thumbnailBytes.length > 0) {
//                    video.setVideoThumbnail("data:image/jpeg;base64," + 
//                        java.util.Base64.getEncoder().encodeToString(thumbnailBytes));
					video.setVideoThumbnail(thumbnailBytes);
				}
//                else {
//                    video.setVideoThumbnail("");
//                }

				videos.add(video);
			}
		}
		return videos;
	}


	// Get videos by user/channel
	public List<VideoBean> getVideosByUserId(int userId) throws SQLException, ClassNotFoundException {
		List<VideoBean> videos = new ArrayList<>();

		String sql = "SELECT v.*, c.channel_name " + "FROM video v "
				+ "LEFT JOIN channel c ON v.channel_id = c.channel_id " + "LEFT JOIN users u ON c.user_id = u.user_id "
				+ "WHERE u.user_id = ? " + "AND (v.video_visibility = 'public' OR v.video_visibility IS NULL) "
				+ "ORDER BY v.video_created_at DESC";

		try (Connection conn = DBconnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				VideoBean video = mapResultSetToVideo(rs);
				videos.add(video);
			}
		}

		return videos;
	}

	// Search videos
	public List<VideoBean> searchVideos(String query) throws SQLException, ClassNotFoundException {
		List<VideoBean> videos = new ArrayList<>();

		String sql = "SELECT v.*, c.channel_name " + "FROM video v "
				+ "LEFT JOIN channel c ON v.channel_id = c.channel_id "
				+ "WHERE (v.video_name LIKE ? OR v.video_desc LIKE ?) "
				+ "AND (v.video_visibility = 'public' OR v.video_visibility IS NULL) "
				+ "ORDER BY v.video_created_at DESC";

		try (Connection conn = DBconnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			String searchPattern = "%" + query + "%";
			ps.setString(1, searchPattern);
			ps.setString(2, searchPattern);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				VideoBean video = mapResultSetToVideo(rs);
				videos.add(video);
			}
		}

		return videos;
	}

	// Get video like/dislike counts
	public int[] getVideoReactionCounts(int videoId) throws SQLException, ClassNotFoundException {
		int[] counts = new int[2]; // [0] = likes, [1] = dislikes

		// Check if you have a video_reaction table
		String likeSql = "SELECT COUNT(*) as like_count FROM video_reaction WHERE video_id = ? AND type = 'Like'";
		String dislikeSql = "SELECT COUNT(*) as dislike_count FROM video_reaction WHERE video_id = ? AND type = 'Dislike'";

		try (Connection conn = DBconnection.getConnection()) {
			// Get like count
			try (PreparedStatement likePs = conn.prepareStatement(likeSql)) {
				likePs.setInt(1, videoId);
				ResultSet rs = likePs.executeQuery();
				if (rs.next()) {
					counts[0] = rs.getInt("like_count");
				}
			} catch (SQLException e) {
				System.out.println("DEBUG: No video_reaction table or error: " + e.getMessage());
			}

			// Get dislike count
			try (PreparedStatement dislikePs = conn.prepareStatement(dislikeSql)) {
				dislikePs.setInt(1, videoId);
				ResultSet rs = dislikePs.executeQuery();
				if (rs.next()) {
					counts[1] = rs.getInt("dislike_count");
				}
			} catch (SQLException e) {
				System.out.println("DEBUG: No video_reaction table or error: " + e.getMessage());
			}
		}

		return counts;
	}

	// Get user's reaction to video
	public String getUserVideoReaction(int userId, int videoId) throws SQLException, ClassNotFoundException {
		String sql = "SELECT type FROM video_reaction WHERE user_id = ? AND video_id = ?";

		try (Connection conn = DBconnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, userId);
			ps.setInt(2, videoId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getString("type");
			}
		} catch (SQLException e) {
			System.out.println("DEBUG: No video_reaction table or error: " + e.getMessage());
		}

		return null;
	}



	public static List<VideoBean> getAllVideosByChannelId(int channelId) throws SQLException, ClassNotFoundException {
		List<VideoBean> videos = new ArrayList<>();
		// No visibility filter here
		String sql = "SELECT * FROM Video WHERE channel_id = ? ORDER BY video_created_at DESC";

		try (Connection conn = DBconnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, channelId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				VideoBean video = new VideoBean();
				video.setVideoId(rs.getInt("video_id"));
				video.setChannelId(rs.getInt("channel_id"));
				video.setVideoName(rs.getString("video_name"));
				video.setVideoDesc(rs.getString("video_desc"));
				video.setViewCount(rs.getLong("view_count"));
				video.setVideoUrl(rs.getString("video_url"));
				video.setUploadDate(rs.getTimestamp("video_created_at"));
				video.setVideoVisibility(rs.getString("video_visibility"));

				// Handle BLOB thumbnail
				byte[] thumbnailBytes = rs.getBytes("video_thumbnails");
				if (thumbnailBytes != null && thumbnailBytes.length > 0) {
//                    video.setVideoThumbnail("data:image/jpeg;base64," +
//                        java.util.Base64.getEncoder().encodeToString(thumbnailBytes));
					video.setVideoThumbnail(thumbnailBytes);
				}
//                else {
//                    video.setVideoThumbnail("");
//                }
				videos.add(video);
			}
		}
		return videos;
	}

	public static List<VideoBean> getVideosByChannelId(int channelId) {
		List<VideoBean> videos = new ArrayList<>();
		String sql = "SELECT * FROM video WHERE channel_id = ? AND video_visibility = 'public' ORDER BY video_created_at DESC";

		try (Connection conn = DBconnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, channelId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				VideoBean video = new VideoBean();
				video.setVideoId(rs.getInt("video_id"));
				video.setChannelId(rs.getInt("channel_id"));
				video.setVideoName(rs.getString("video_name"));
				video.setVideoDesc(rs.getString("video_desc"));

				// FIX THIS LINE: Change channelId to rs.getLong("view_count")
				video.setViewCount(rs.getLong("view_count")); // FIXED!

				video.setVideoUrl(rs.getString("video_url"));
				video.setUploadDate(rs.getTimestamp("video_created_at"));
				video.setVideoVisibility(rs.getString("video_visibility"));

				// Also fix the thumbnail - if you store as BLOB
				byte[] thumbnailBytes = rs.getBytes("video_thumbnails");
				if (thumbnailBytes != null && thumbnailBytes.length > 0) {
//                    video.setVideoThumbnail("data:image/jpeg;base64," + 
//                        java.util.Base64.getEncoder().encodeToString(thumbnailBytes));
					video.setVideoThumbnail(thumbnailBytes);
				}
//                else {
//                    video.setVideoThumbnail(""); // Empty string for no thumbnail
//                }

				// In getVideosByChannelId() method, add debug:
				System.out.println("DEBUG: Retrieving video with ID: " + rs.getInt("video_id"));
				System.out.println("DEBUG: video_created_at value: " + rs.getTimestamp("video_created_at"));
				System.out.println("DEBUG: Was video_created_at null? " + rs.wasNull());

				Timestamp uploadTimestamp = rs.getTimestamp("video_created_at");
				video.setUploadDate(uploadTimestamp);
				System.out.println("DEBUG: Set upload date to: " + uploadTimestamp);

				videos.add(video);
			}

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return videos;
	}

	public static int getVideoCountByChannelId(int channelId) {
		String sql = "SELECT COUNT(*) as count FROM video WHERE channel_id = ? AND video_visibility = 'public'";

		try (Connection conn = DBconnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, channelId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt("count");
			}

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static long getTotalViewsByChannelId(int channelId) {
		String sql = "SELECT SUM(view_count) as total_views FROM video WHERE channel_id = ? AND video_visibility = 'public'";

		try (Connection conn = DBconnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, channelId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				long total = rs.getLong("total_views");
				return rs.wasNull() ? 0 : total;
			}

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// In VideoRepository.java, update the getAllVideos() method:

	public List<VideoBean> getAllVideos() throws SQLException, ClassNotFoundException {
		List<VideoBean> videos = new ArrayList<>();

		// CORRECT SQL query based on your tables
		String sql = "SELECT v.*, c.channel_name, c.channel_img " + "FROM video v "
				+ "LEFT JOIN channel c ON v.channel_id = c.channel_id "
				+ "WHERE v.video_visibility = 'public' OR v.video_visibility IS NULL "
				+ "ORDER BY v.video_created_at DESC";

		System.out.println("DEBUG: Getting videos with SQL: " + sql);

		try (Connection conn = DBconnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				VideoBean video = mapResultSetToVideo(rs);
				videos.add(video);

				// Debug output
				byte[] thumbnail = rs.getBytes("video_thumbnails");
				byte[] channelImg = rs.getBytes("channel_img");
				System.out.println("DEBUG: Video ID " + video.getVideoId() + " - Thumbnail: "
						+ (thumbnail != null ? thumbnail.length + " bytes" : "null") + " - Channel img: "
						+ (channelImg != null ? channelImg.length + " bytes" : "null"));
			}

			System.out.println("DEBUG: Total videos loaded: " + videos.size());
		}

		return videos;
	}

	// Update the mapResultSetToVideo method:

	private VideoBean mapResultSetToVideo(ResultSet rs) throws SQLException {
		VideoBean video = new VideoBean();

		// Map video table fields
		video.setVideoId(rs.getInt("video_id"));
		video.setChannelId(rs.getInt("channel_id"));
		video.setVideoName(rs.getString("video_name"));
		video.setVideoDesc(rs.getString("video_desc"));
		video.setViewCount(rs.getLong("view_count"));
		video.setVideoUrl(rs.getString("video_url"));
		video.setUploadDate(rs.getTimestamp("video_created_at"));
		video.setVideoVisibility(rs.getString("video_visibility"));

		// Map channel fields
		video.setChannelName(rs.getString("channel_name"));

		// Handle channel_img BLOB - convert to base64 for JSP
		byte[] channelImgBytes = rs.getBytes("channel_img");
		if (channelImgBytes != null && channelImgBytes.length > 0) {
			String base64ChannelImg = java.util.Base64.getEncoder().encodeToString(channelImgBytes);
			video.setChannelImgBase64(base64ChannelImg); // Store as base64 string
		}

		// Don't convert video_thumbnails here - let the servlet handle it
		// Just check if it exists for debugging
		byte[] thumbnailBytes = rs.getBytes("video_thumbnails");
		boolean hasThumbnail = (thumbnailBytes != null && thumbnailBytes.length > 0);

		return video;
	}

	/// New method: Increment view count only if it's a new view for the day
	public void incrementViewCountIfNew(int videoId, Integer userId) throws SQLException, ClassNotFoundException {
		if (userId == null) {
			// For anonymous users, always increment
			incrementViewCount(videoId);
			return;
		}

		// Check if user has already viewed today
		if (!ViewRepository.hasViewedToday(userId, videoId)) {
			// Record the view
			if (ViewRepository.recordView(userId, videoId)) {
				// Only increment if it's a new view for today
				incrementViewCount(videoId);
			}
		}

	}

	public static boolean updateVideo(int videoId, String videoName, String videoDesc, String videoVisibility,
			byte[] thumbnail) throws ClassNotFoundException {
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DBconnection.getConnection();
			String sql;

			if (thumbnail != null && thumbnail.length > 0) {
				sql = "UPDATE video SET video_name = ?, video_desc = ?, video_visibility = ?, video_thumbnails = ? WHERE video_id = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, videoName);
				pstmt.setString(2, videoDesc);
				pstmt.setString(3, videoVisibility);
				pstmt.setBytes(4, thumbnail);
				pstmt.setInt(5, videoId);
			} else {
				sql = "UPDATE video SET video_name = ?, video_desc = ?, video_visibility = ? WHERE video_id = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, videoName);
				pstmt.setString(2, videoDesc);
				pstmt.setString(3, videoVisibility);
				pstmt.setInt(4, videoId);
			}

			int rowsAffected = pstmt.executeUpdate();
			return rowsAffected > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
// Close resources
		}
	}

	public static boolean deleteVideo(int videoId) throws ClassNotFoundException {
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DBconnection.getConnection();
			String sql = "DELETE FROM video WHERE video_id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, videoId);

			int rowsAffected = pstmt.executeUpdate();
			return rowsAffected > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
// Close resources
		}
	}
}