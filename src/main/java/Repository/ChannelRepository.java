package Repository;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Model.ChannelBean;
import JDBCconnection.DBconnection;

public class ChannelRepository {

	public static int createChannel(int loggedInUserId,String channelName,String description) {
		if (userHasChannel(loggedInUserId)) {
	        System.err.println("DEBUG: User " + loggedInUserId + " already has a channel");
	        return -1; // Or return existing channel ID
	    }
		String sql = "INSERT INTO Channel (user_id, channel_name, channel_desc) VALUES (?, ?, ?)";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Set the values for the prepared statement
            ps.setInt(1, loggedInUserId);
            ps.setString(2, channelName);
            ps.setString(3, description);
            
            // Execute the update (insert)
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the generated channel ID
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int channelId = generatedKeys.getInt(1);
                        System.out.println("DEBUG: Successfully created channel with ID: " + channelId);
                        return channelId; // Return the new channel ID
                    }
                }
            }
            System.err.println("DEBUG: Failed to create channel or get generated keys");
            return -1; // Return -1 if failed
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error creating channel: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
	}

	public static boolean doesChannelExistForUser(int userId, String channelName) {
	    String sql = "SELECT COUNT(*) FROM Channel WHERE user_id = ? AND channel_name = ?";
	    
	    try (Connection conn = DBconnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setInt(1, userId);
	        ps.setString(2, channelName);
	        
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            return rs.getInt(1) > 0;
	        }
	        
	    } catch (SQLException | ClassNotFoundException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	public static boolean userHasChannel(int userId) {
	    String sql = "SELECT COUNT(*) as count FROM Channel WHERE user_id = ?";
	    
	    try (Connection conn = DBconnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setInt(1, userId);
	        ResultSet rs = ps.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getInt("count") > 0;
	        }
	        
	    } catch (SQLException | ClassNotFoundException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	// In ChannelRepository.java, add this method:
	public static int getExistingChannelId(int userId) {
	    String sql = "SELECT channel_id FROM Channel WHERE user_id = ?";
	    
	    try (Connection conn = DBconnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setInt(1, userId);
	        ResultSet rs = ps.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getInt("channel_id");
	        }
	        
	    } catch (SQLException | ClassNotFoundException e) {
	        e.printStackTrace();
	    }
	    return -1; // Return -1 if no channel found
	}
//	public static ChannelBean getChannelById(int channelId) {
//		String sql = "SELECT * FROM channel WHERE channel_id = ?";
//        ChannelBean channel = null;
//        
//        try (Connection conn = DBconnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            
//            ps.setInt(1, channelId);
//            ResultSet rs = ps.executeQuery();
//            
//            if (rs.next()) {
//                channel = new ChannelBean();
//                channel.setChannelId(rs.getInt("channel_id"));
//                channel.setUserId(rs.getInt("user_id"));
//                channel.setChannelName(rs.getString("channel_name"));
//                channel.setChannelDesc(rs.getString("channel_desc"));
//                channel.setSubscriber_count(rs.getInt("subscriber_count"));
//            }
//            
//        } catch (ClassNotFoundException | SQLException e) {
//            e.printStackTrace();
//        }
//        return channel;
//		
//	}
	
	// Get channel by user ID (for checking if it's the owner's channel)
    public static ChannelBean getChannelByUserId(int userId) {
        String sql = "SELECT * FROM channel WHERE user_id = ?";
        ChannelBean channel = null;
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                channel = new ChannelBean();
                channel.setChannelId(rs.getInt("channel_id"));
                channel.setUserId(rs.getInt("user_id"));
                channel.setChannelName(rs.getString("channel_name"));
                channel.setChannelDesc(rs.getString("channel_desc"));
                channel.setSubscriber_count(rs.getInt("subscriber_count"));
            }
            
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return channel;
    }
    
    public static int getSubscriberCount(int channelId) {
        String sql = "SELECT COUNT(*) as count FROM subscribe WHERE subscribe_channel_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
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

    // Also update the getChannelById method to calculate subscriber count:
    public static ChannelBean getChannelById(int channelId) {
        String sql = "SELECT c.* FROM channel c WHERE c.channel_id = ?";
        ChannelBean channel = null;
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, channelId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                channel = new ChannelBean();
                channel.setChannelId(rs.getInt("channel_id"));
                channel.setUserId(rs.getInt("user_id"));
                channel.setChannelName(rs.getString("channel_name"));
                channel.setChannelDesc(rs.getString("channel_desc"));
                
                // Get subscriber count from subscribe table
                int subscriberCount = getSubscriberCount(channelId);
                channel.setSubscriber_count(subscriberCount);
            }
            
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return channel;
    }
    
    public static boolean isUserSubscribed(int userId, int channelId) {
        String sql = "SELECT COUNT(*) as count FROM subscribe WHERE subscribe_user_id = ? AND subscribe_channel_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, channelId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean subscribeToChannel(int userId, int channelId) {
        String sql = "INSERT INTO subscribe (subscribe_user_id, subscribe_channel_id) VALUES (?, ?)";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, channelId);
            int rows = ps.executeUpdate();
            return rows > 0;
            
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean unsubscribeFromChannel(int userId, int channelId) {
        String sql = "DELETE FROM subscribe WHERE subscribe_user_id = ? AND subscribe_channel_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, channelId);
            int rows = ps.executeUpdate();
            return rows > 0;
            
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
 // Add these methods to your ChannelRepository class

//    public static boolean updateChannel(ChannelBean channel) {
//        String sql = "UPDATE channel SET channel_name = ?, channel_desc = ? WHERE channel_id = ?";
//        
//        try (Connection conn = DBconnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            
//            ps.setString(1, channel.getChannelName());
//            ps.setString(2, channel.getChannelDesc());
//            ps.setInt(3, channel.getChannelId());
//            
//            int rowsAffected = ps.executeUpdate();
//            return rowsAffected > 0;
//            
//        } catch (ClassNotFoundException | SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
    
    public static boolean updateChannel(ChannelBean channel) {
        System.out.println("=== updateChannel START ===");
        System.out.println("Channel ID: " + channel.getChannelId());
        System.out.println("New Name: " + channel.getChannelName());
        
        String sql = "UPDATE channel SET channel_name = ?, channel_desc = ? WHERE channel_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, channel.getChannelName());
            ps.setString(2, channel.getChannelDesc());
            ps.setInt(3, channel.getChannelId());
            
            System.out.println("Executing update...");
            int rowsAffected = ps.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            
            boolean success = rowsAffected > 0;
            System.out.println("Update " + (success ? "SUCCESSFUL" : "FAILED"));
            System.out.println("=== updateChannel END ===");
            
            return success;
            
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error in updateChannel: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

// // FIX THIS METHOD in ChannelRepository.java
//    public static boolean updateChannelWithImage(ChannelBean channel, InputStream imageStream) {
//        String sql = "UPDATE channel SET channel_name = ?, channel_desc = ?, channel_img = ? WHERE channel_id = ?";
//        
//        try (Connection conn = DBconnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            
//            ps.setString(1, channel.getChannelName());
//            ps.setString(2, channel.getChannelDesc());
//            
//            if (imageStream != null) {
//                // Read all bytes from InputStream
//                byte[] bytes = imageStream.readAllBytes();
//                if (bytes.length > 0) {
//                    ps.setBytes(3, bytes);
//                } else {
//                    ps.setNull(3, java.sql.Types.BLOB);
//                }
//            } else {
//                ps.setNull(3, java.sql.Types.BLOB);
//            }
//            
//            // ALWAYS set the channelId (moved outside the if block)
//            ps.setInt(4, channel.getChannelId());
//            
//            int rowsAffected = ps.executeUpdate();
//            return rowsAffected > 0;
//            
//        } catch (ClassNotFoundException | SQLException e) {
//            e.printStackTrace();
//            return false;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
    
    public static boolean updateChannelWithImage(ChannelBean channel, InputStream imageStream) {
        System.out.println("=== updateChannelWithImage START ===");
        System.out.println("Channel ID: " + channel.getChannelId());
        System.out.println("Channel Name: " + channel.getChannelName());
        
        String sql = "UPDATE channel SET channel_name = ?, channel_desc = ?, channel_img = ? WHERE channel_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Set channel name and description
            ps.setString(1, channel.getChannelName());
            ps.setString(2, channel.getChannelDesc());
            System.out.println("Set name and description");
            
            if (imageStream != null) {
                try {
                    // Read all bytes from the InputStream
                    byte[] imageBytes = imageStream.readAllBytes();
                    System.out.println("Image size: " + imageBytes.length + " bytes");
                    
                    if (imageBytes.length > 0) {
                        // Use setBytes for BLOB/MEDIUMBLOB
                        ps.setBytes(3, imageBytes);
                        System.out.println("Image bytes set in prepared statement");
                    } else {
                        ps.setNull(3, java.sql.Types.BLOB);
                        System.out.println("Empty image, setting to null");
                    }
                } catch (IOException e) {
                    System.out.println("IOException reading image: " + e.getMessage());
                    e.printStackTrace();
                    ps.setNull(3, java.sql.Types.BLOB);
                }
            } else {
                ps.setNull(3, java.sql.Types.BLOB);
                System.out.println("No image stream, setting to null");
            }
            
            // Set channel ID
            ps.setInt(4, channel.getChannelId());
            System.out.println("Set channel ID: " + channel.getChannelId());
            
            // Execute update
            int rowsAffected = ps.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            
            boolean success = rowsAffected > 0;
            System.out.println("Update " + (success ? "SUCCESSFUL" : "FAILED"));
            System.out.println("=== updateChannelWithImage END ===");
            
            return success;
            
        } catch (SQLException e) {
            System.out.println("SQLException in updateChannelWithImage:");
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.out.println("General Exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static ChannelBean getChannelByIdWithImage(int channelId) {
        System.out.println("=== getChannelByIdWithImage called for ID: " + channelId + " ===");
        
        String sql = "SELECT c.* FROM channel c WHERE c.channel_id = ?";
        ChannelBean channel = null;
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            System.out.println("Database connection established");
            ps.setInt(1, channelId);
            System.out.println("PreparedStatement set with channelId: " + channelId);
            
            ResultSet rs = ps.executeQuery();
            System.out.println("Query executed");
            
            if (rs.next()) {
                System.out.println("ResultSet has data!");
                channel = new ChannelBean();
                channel.setChannelId(rs.getInt("channel_id"));
                channel.setUserId(rs.getInt("user_id"));
                channel.setChannelName(rs.getString("channel_name"));
                channel.setChannelDesc(rs.getString("channel_desc"));
                
                System.out.println("Channel data loaded:");
                System.out.println("  channel_id: " + channel.getChannelId());
                System.out.println("  user_id: " + channel.getUserId());
                System.out.println("  channel_name: " + channel.getChannelName());
                
                // Get image BLOB
                byte[] imageBytes = rs.getBytes("channel_img");
                channel.setChannelImg(imageBytes);
                System.out.println("Image bytes: " + (imageBytes != null ? imageBytes.length + " bytes" : "null"));
                
                // Get subscriber count
                int subscriberCount = getSubscriberCount(channelId);
                channel.setSubscriber_count(subscriberCount);
                System.out.println("Subscriber count: " + subscriberCount);
            } else {
                System.out.println("No channel found with ID: " + channelId);
            }
            
            rs.close();
            
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: ClassNotFoundException - " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("ERROR: SQLException - " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== getChannelByIdWithImage finished ===");
        return channel;
    }
}
