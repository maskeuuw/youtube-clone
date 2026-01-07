package Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import JDBCconnection.DBconnection;
import Model.ChannelBean;


public class SubscriptionRepository {
    
    // Get all subscribed channels for a user
    public List<ChannelBean> getSubscribedChannels(int userId) throws ClassNotFoundException {
        List<ChannelBean> subscribedChannels = new ArrayList<>();
        String query = """
            SELECT c.channel_id, c.user_id, c.channel_name, 
                   c.channel_desc, c.subscriber_count, c.channel_img
            FROM subscribe s
            JOIN channel c ON s.subscribe_channel_id = c.channel_id
            WHERE s.subscribe_user_id = ?
            ORDER BY s.subscribe_id DESC
            """;
            
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ChannelBean channel = new ChannelBean();
                channel.setChannelId(rs.getInt("channel_id"));
                channel.setUserId(rs.getInt("user_id"));
                channel.setChannelName(rs.getString("channel_name"));
                channel.setChannelDesc(rs.getString("channel_desc"));
                channel.setSubscriber_count(rs.getInt("subscriber_count"));
                
                Blob imageBlob = rs.getBlob("channel_img");
                if (imageBlob != null) {
                    channel.setChannelImg(imageBlob.getBytes(1, (int) imageBlob.length()));
                }
                
                subscribedChannels.add(channel);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subscribedChannels;
    }
    
    // Check if user is subscribed to a channel
    public boolean isUserSubscribed(int userId, int channelId) throws ClassNotFoundException {
        String query = "SELECT 1 FROM subscribe WHERE subscribe_user_id = ? AND subscribe_channel_id = ?";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, channelId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Subscribe to a channel
    public boolean subscribeToChannel(int userId, int channelId) throws ClassNotFoundException {
        // Check if already subscribed
        if (isUserSubscribed(userId, channelId)) {
            return false;
        }
        
        String insertQuery = "INSERT INTO subscribe (subscribe_user_id, subscribe_channel_id) VALUES (?, ?)";
        String updateCountQuery = "UPDATE channel SET subscriber_count = COALESCE(subscriber_count, 0) + 1 WHERE channel_id = ?";
        
        Connection conn = null;
        try {
            conn = DBconnection.getConnection();
            conn.setAutoCommit(false);
            
            // Insert subscription
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, channelId);
                insertStmt.executeUpdate();
            }
            
            // Update subscriber count
            try (PreparedStatement updateStmt = conn.prepareStatement(updateCountQuery)) {
                updateStmt.setInt(1, channelId);
                updateStmt.executeUpdate();
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Unsubscribe from a channel
    public boolean unsubscribeFromChannel(int userId, int channelId) throws ClassNotFoundException {
        // Check if subscribed
        if (!isUserSubscribed(userId, channelId)) {
            return false;
        }
        
        String deleteQuery = "DELETE FROM subscribe WHERE subscribe_user_id = ? AND subscribe_channel_id = ?";
        String updateCountQuery = "UPDATE channel SET subscriber_count = GREATEST(COALESCE(subscriber_count, 0) - 1, 0) WHERE channel_id = ?";
        
        Connection conn = null;
        try {
            conn = DBconnection.getConnection();
            conn.setAutoCommit(false);
            
            // Delete subscription
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setInt(1, userId);
                deleteStmt.setInt(2, channelId);
                deleteStmt.executeUpdate();
            }
            
            // Update subscriber count
            try (PreparedStatement updateStmt = conn.prepareStatement(updateCountQuery)) {
                updateStmt.setInt(1, channelId);
                updateStmt.executeUpdate();
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}