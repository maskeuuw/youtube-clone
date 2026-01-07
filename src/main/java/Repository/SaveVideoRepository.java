package Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Model.VideoBean;
import JDBCconnection.DBconnection;

public class SaveVideoRepository {
    
    // Get all saved videos for a user (Watch Later list)
    public List<VideoBean> getSavedVideos(int userId) throws ClassNotFoundException {
        List<VideoBean> savedVideos = new ArrayList<>();
        
        String query = """
            SELECT v.video_id, v.channel_id, v.video_name, v.video_desc, 
                   v.view_count, v.video_url, v.video_created_at,
                   v.video_thumbnails, v.video_visibility,
                   c.channel_name, c.channel_img
            FROM savevideo sv
            JOIN video v ON sv.video_id = v.video_id
            JOIN channel c ON v.channel_id = c.channel_id
            WHERE sv.user_id = ?
            ORDER BY sv.save_video_id DESC
            """;
            
        System.out.println("DEBUG: Executing query for saved videos for user: " + userId);
            
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                VideoBean video = new VideoBean();
                
                // Map database fields to VideoBean properties
                video.setVideoId(rs.getInt("video_id"));
                video.setChannelId(rs.getInt("channel_id"));
                video.setVideoName(rs.getString("video_name"));
                video.setVideoDesc(rs.getString("video_desc"));
                
                // Get view_count (matches your VideoBean's viewCount)
                video.setViewCount(rs.getLong("view_count"));
                
                // Get video_created_at (upload date)
                Timestamp uploadTimestamp = rs.getTimestamp("video_created_at");
                video.setUploadDate(uploadTimestamp);
                
                video.setVideoUrl(rs.getString("video_url"));
                
                // Get video thumbnail from blob
                Blob thumbnailBlob = rs.getBlob("video_thumbnails");
                if (thumbnailBlob != null) {
                    byte[] thumbnailBytes = thumbnailBlob.getBytes(1, (int) thumbnailBlob.length());
//                    String base64Thumbnail = java.util.Base64.getEncoder().encodeToString(thumbnailBytes);
//                    video.setVideoThumbnail("data:image/jpeg;base64," + base64Thumbnail);
                    video.setVideoThumbnail(thumbnailBytes);
                }
                
                video.setVideoVisibility(rs.getString("video_visibility"));
                video.setChannelName(rs.getString("channel_name"));
                
                // Get channel image blob and convert to base64
                Blob channelImgBlob = rs.getBlob("channel_img");
                if (channelImgBlob != null) {
                    byte[] imageBytes = channelImgBlob.getBytes(1, (int) channelImgBlob.length());
                    String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);
                    video.setChannelImgBase64(base64Image);
                }
                
                savedVideos.add(video);
                System.out.println("DEBUG: Added video to watch later: " + video.getVideoName());
            }
            
            System.out.println("DEBUG: Total saved videos found: " + savedVideos.size());
            
        } catch (SQLException e) {
            System.out.println("ERROR in getSavedVideos: " + e.getMessage());
            e.printStackTrace();
        }
        return savedVideos;
    }
    
    // Save a video for later (Watch Later)
    public boolean saveVideoForLater(int userId, int videoId) throws ClassNotFoundException {
        String query = "INSERT INTO savevideo (user_id, video_id) VALUES (?, ?)";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, videoId);
            int rowsInserted = pstmt.executeUpdate();
            
            System.out.println("DEBUG: Saved video " + videoId + " for user " + userId + 
                              ", rows inserted: " + rowsInserted);
            
            return rowsInserted > 0;
            
        } catch (SQLException e) {
            // Check if it's a duplicate entry error
            if (e.getErrorCode() == 1062) { // MySQL duplicate entry error code
                System.out.println("DEBUG: Video already saved for user");
                return false;
            }
            System.out.println("ERROR in saveVideoForLater: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Remove a saved video
    public boolean removeSavedVideo(int userId, int videoId) throws ClassNotFoundException {
        String query = "DELETE FROM savevideo WHERE user_id = ? AND video_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, videoId);
            int rowsDeleted = pstmt.executeUpdate();
            
            System.out.println("DEBUG: Removed video " + videoId + " for user " + userId + 
                              ", rows deleted: " + rowsDeleted);
            
            return rowsDeleted > 0;
            
        } catch (SQLException e) {
            System.out.println("ERROR in removeSavedVideo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Check if a video is saved by user
    public boolean isVideoSaved(int userId, int videoId) throws ClassNotFoundException {
        String query = "SELECT 1 FROM savevideo WHERE user_id = ? AND video_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, videoId);
            ResultSet rs = pstmt.executeQuery();
            
            boolean isSaved = rs.next();
            System.out.println("DEBUG: Video " + videoId + " saved by user " + userId + ": " + isSaved);
            
            return isSaved;
            
        } catch (SQLException e) {
            System.out.println("ERROR in isVideoSaved: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Get count of saved videos for a user
    public int getSavedVideoCount(int userId) throws ClassNotFoundException {
        String query = "SELECT COUNT(*) as count FROM savevideo WHERE user_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("DEBUG: User " + userId + " has " + count + " saved videos");
                return count;
            }
            
        } catch (SQLException e) {
            System.out.println("ERROR in getSavedVideoCount: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    // Remove all saved videos for a user (Clear Watch Later)
    public boolean clearWatchLater(int userId) throws ClassNotFoundException {
        String query = "DELETE FROM savevideo WHERE user_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            int rowsDeleted = pstmt.executeUpdate();
            
            System.out.println("DEBUG: Cleared all saved videos for user " + userId + 
                              ", rows deleted: " + rowsDeleted);
            
            return rowsDeleted > 0;
            
        } catch (SQLException e) {
            System.out.println("ERROR in clearWatchLater: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}