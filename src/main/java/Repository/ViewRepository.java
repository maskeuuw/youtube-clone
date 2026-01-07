package Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import JDBCconnection.DBconnection;

public class ViewRepository {
    
    // Check if user has already viewed this video today
    public static boolean hasViewedToday(int userId, int videoId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT 1 FROM video_views WHERE user_id = ? AND video_id = ? AND view_date = CURDATE()";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, videoId);
            ResultSet rs = ps.executeQuery();
            
            return rs.next();
        }
    }
    
    // Record a view (only once per user per day)
    public static boolean recordView(int userId, int videoId) throws SQLException, ClassNotFoundException {
        // Check if already viewed today
        if (hasViewedToday(userId, videoId)) {
            return false;
        }
        
        String sql = "INSERT INTO video_views (user_id, video_id, view_date, video_viewscol) VALUES (?, ?, CURDATE(), NOW())";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, videoId);
            
            int rowsInserted = ps.executeUpdate();
            return rowsInserted > 0;
            
        } catch (SQLException e) {
            // Handle duplicate entry (race condition)
            if (e.getErrorCode() == 1062) { // MySQL duplicate entry
                return false;
            }
            throw e;
        }
    }
    
    // Record anonymous view (no user ID)
    public static boolean recordAnonymousView(int videoId) throws SQLException, ClassNotFoundException {
        // Anonymous views are always counted (no user tracking)
        // You could add IP tracking here if needed
        String sql = "INSERT INTO video_views (video_id, view_date, video_viewscol) VALUES (?, CURDATE(), NOW())";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, videoId);
            
            int rowsInserted = ps.executeUpdate();
            return rowsInserted > 0;
            
        } catch (SQLException e) {
            // Log error but don't fail
            System.err.println("Error recording anonymous view: " + e.getMessage());
            return false;
        }
    }
    
    // Get total unique views (distinct users)
    public static int getUniqueViewCount(int videoId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(DISTINCT user_id) as unique_views FROM video_views WHERE video_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, videoId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("unique_views");
            }
            return 0;
        }
    }
    
    // Get total views (including anonymous)
    public static int getTotalViewCount(int videoId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) as total_views FROM video_views WHERE video_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, videoId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total_views");
            }
            return 0;
        }
    }
    
    // Get today's views
    public static int getTodayViewCount(int videoId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) as today_views FROM video_views WHERE video_id = ? AND view_date = CURDATE()";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, videoId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("today_views");
            }
            return 0;
        }
    }
    
    // Get view history for a user
    public static int getUserViewCountForVideo(int userId, int videoId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) as user_views FROM video_views WHERE user_id = ? AND video_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, videoId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("user_views");
            }
            return 0;
        }
    }
    
    // Clear old views (optional: for cleanup)
    public static int clearOldViews(int daysToKeep) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM video_views WHERE view_date < DATE_SUB(CURDATE(), INTERVAL ? DAY)";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, daysToKeep);
            return ps.executeUpdate();
        }
    }
}