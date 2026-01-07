package Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import JDBCconnection.DBconnection;
import Model.UserBean;
import Model.VideoBean;

public class AdminRepository {
	
	public static List<UserBean> getAllUsers() throws ClassNotFoundException {
        List<UserBean> users = new ArrayList<>();
        String sql = "SELECT * FROM Users";
        
        try (Connection conn = DBconnection.getConnection();
        		Statement stmt = conn.createStatement();
        		ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                UserBean user = new UserBean();
                user.setUserId(rs.getInt("user_id"));
                user.setUserName(rs.getString("user_name"));
                user.setEmail(rs.getString("user_email"));
                user.setUserRole(rs.getString("user_role"));
                // Note: Add 'user_status' to your Bean if you haven't yet!
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // GET ALL VIDEOS
    public static List<VideoBean> getAllVideos() throws ClassNotFoundException {
        List<VideoBean> videos = new ArrayList<>();
        // Joining with Channel to get the channel name as required by your Bean
        String sql = "SELECT v.*, c.channel_name FROM Video v JOIN Channel c ON v.channel_id = c.channel_id";

        try (Connection conn = DBconnection.getConnection(); 
        		Statement stmt = conn.createStatement();
        		ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                VideoBean video = new VideoBean();
                video.setVideoId(rs.getInt("video_id"));
                video.setVideoName(rs.getString("video_name"));
                video.setChannelName(rs.getString("channel_name"));
                video.setViewCount(rs.getLong("view_count"));
                video.setUploadDate(rs.getTimestamp("video_created_at"));
                video.setVideoThumbnail(rs.getBytes("video_thumbnails"));
                videos.add(video);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return videos;
    }
}
