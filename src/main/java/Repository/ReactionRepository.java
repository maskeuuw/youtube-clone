package Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import JDBCconnection.DBconnection;
import Model.VideoBean;

public class ReactionRepository {

	// Add or update user reaction
    public boolean addReaction(int userId, int videoId, String reactionType) 
            throws SQLException, ClassNotFoundException {
        
        // Check if user already reacted
        String checkSql = "SELECT react_id FROM React WHERE react_user_id = ? AND react_video_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            
            checkPs.setInt(1, userId);
            checkPs.setInt(2, videoId);
            ResultSet rs = checkPs.executeQuery();
            
            if (rs.next()) {
                // Update existing reaction
                String updateSql = "UPDATE React SET type = ? WHERE react_user_id = ? AND react_video_id = ?";
                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                    updatePs.setString(1, reactionType);
                    updatePs.setInt(2, userId);
                    updatePs.setInt(3, videoId);
                    return updatePs.executeUpdate() > 0;
                }
            } else {
                // Insert new reaction
                String insertSql = "INSERT INTO React (react_user_id, react_video_id, type) VALUES (?, ?, ?)";
                try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                    insertPs.setInt(1, userId);
                    insertPs.setInt(2, videoId);
                    insertPs.setString(3, reactionType);
                    return insertPs.executeUpdate() > 0;
                }
            }
        }
    }
    
    // Remove user reaction
    public boolean removeReaction(int userId, int videoId) 
            throws SQLException, ClassNotFoundException {
        
        String sql = "DELETE FROM React WHERE react_user_id = ? AND react_video_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, videoId);
            return ps.executeUpdate() > 0;
        }
    }
    
    // Get user's reaction to a video
    public String getUserReaction(int userId, int videoId) 
            throws SQLException, ClassNotFoundException {
        
        String sql = "SELECT type FROM React WHERE react_user_id = ? AND react_video_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, videoId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getString("type");
            }
        }
        return null;
    }
    
    // Get like and dislike counts for a video
    public int[] getReactionCounts(int videoId) throws SQLException, ClassNotFoundException {
        int[] counts = new int[2]; // [0] = likes, [1] = dislikes
        
        String likeSql = "SELECT COUNT(*) as like_count FROM React WHERE react_video_id = ? AND type = 'Like'";
        String dislikeSql = "SELECT COUNT(*) as dislike_count FROM React WHERE react_video_id = ? AND type = 'Dislike'";
        
        try (Connection conn = DBconnection.getConnection()) {
            // Get like count
            try (PreparedStatement likePs = conn.prepareStatement(likeSql)) {
                likePs.setInt(1, videoId);
                ResultSet rs = likePs.executeQuery();
                if (rs.next()) {
                    counts[0] = rs.getInt("like_count");
                }
            }
            
            // Get dislike count
            try (PreparedStatement dislikePs = conn.prepareStatement(dislikeSql)) {
                dislikePs.setInt(1, videoId);
                ResultSet rs = dislikePs.executeQuery();
                if (rs.next()) {
                    counts[1] = rs.getInt("dislike_count");
                }
            }
        }
        
        return counts;
    }
    
 // Get all liked videos for a user
    public List<VideoBean> getLikedVideosByUserId(int userId) throws ClassNotFoundException {
    	Connection conn = DBconnection.getConnection();
        List<VideoBean> likedVideos = new ArrayList<>();
        String sql = "SELECT v.*, c.channel_name, c.channel_img, " +
                    "(SELECT COUNT(*) FROM react WHERE react_video_id = v.video_id AND type = 'Like') as like_count, " +
                    "(SELECT COUNT(*) FROM react WHERE react_video_id = v.video_id AND type = 'Dislike') as dislike_count " +
                    "FROM video v " +
                    "INNER JOIN react r ON v.video_id = r.react_video_id " +
                    "INNER JOIN channel c ON v.channel_id = c.channel_id " +
                    "WHERE r.react_user_id = ? AND r.type = 'Like' " +
                    "ORDER BY r.react_id DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                VideoBean video = new VideoBean();
                video.setVideoId(rs.getInt("video_id"));
                video.setChannelId(rs.getInt("channel_id"));
                video.setVideoName(rs.getString("video_name"));
                video.setVideoDesc(rs.getString("video_desc"));
                video.setViewCount(rs.getLong("view_count"));
                video.setVideoUrl(rs.getString("video_url"));
                video.setVideoThumbnail(rs.getBytes("video_thumbnails"));
                video.setUploadDate(rs.getTimestamp("video_created_at"));
                video.setVideoVisibility(rs.getString("video_visibility"));
                video.setChannelName(rs.getString("channel_name"));
                video.setChannelImgBase64(rs.getString("channel_img"));
                video.setLikeCount(rs.getInt("like_count"));
                video.setDislikeCount(rs.getInt("dislike_count"));
                
                likedVideos.add(video);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return likedVideos;
    }
}
