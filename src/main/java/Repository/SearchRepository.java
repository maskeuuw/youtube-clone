package Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Model.VideoBean;
import Model.ChannelBean;

import JDBCconnection.DBconnection;

public class SearchRepository {

    // =========================== SEARCH ALL ===========================

    public static SearchResult searchAll(String searchQuery) throws ClassNotFoundException {
        SearchResult result = new SearchResult();
        result.setVideos(searchVideos(searchQuery));
        result.setChannels(searchChannels(searchQuery));
        return result;
    }

    // =========================== SEARCH VIDEOS ===========================

    public static List<VideoBean> searchVideos(String searchQuery) throws ClassNotFoundException {
        List<VideoBean> videos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBconnection.getConnection();

            String sql = "SELECT v.*, c.channel_name, c.channel_img, u.user_name " +
                    "FROM video v " +
                    "JOIN channel c ON v.channel_id = c.channel_id " +
                    "JOIN users u ON c.user_id = u.user_id " +
                    "WHERE (v.video_name LIKE ? OR v.video_desc LIKE ?) " +
                    "AND v.video_visibility = 'public' " +
                    "ORDER BY v.view_count DESC, v.video_created_at DESC " +
                    "LIMIT 50";

            pstmt = conn.prepareStatement(sql);
            String pattern = "%" + searchQuery + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                videos.add(mapVideoFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error searching videos: " + e.getMessage());
        } 
        return videos;
    }

    // =========================== SEARCH CHANNELS ===========================

    public static List<ChannelBean> searchChannels(String searchQuery) throws ClassNotFoundException {
        List<ChannelBean> channels = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBconnection.getConnection();

            String sql = "SELECT c.*, u.user_name, " +
                    "(SELECT COUNT(*) FROM video v WHERE v.channel_id = c.channel_id AND v.video_visibility = 'public') AS video_count " +
                    "FROM channel c " +
                    "JOIN users u ON c.user_id = u.user_id " +
                    "WHERE c.channel_name LIKE ? " +
                    "ORDER BY c.subscriber_count DESC " +
                    "LIMIT 20";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + searchQuery + "%");

            rs = pstmt.executeQuery();

            while (rs.next()) {
                channels.add(mapChannelFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error searching channels: " + e.getMessage());
        } 

        return channels;
    }

    // =========================== ADVANCED SEARCH ===========================

    public static SearchResult advancedSearch(String searchQuery, String filterType, String sortBy, int limit, int offset) throws ClassNotFoundException {
        SearchResult result = new SearchResult();

        if ("videos".equalsIgnoreCase(filterType)) {
            result.setVideos(searchVideosPaginated(searchQuery, sortBy, limit, offset));
        } else if ("channels".equalsIgnoreCase(filterType)) {
            result.setChannels(searchChannelsPaginated(searchQuery, sortBy, limit, offset));
        } else {
            result.setVideos(searchVideos(searchQuery));
            result.setChannels(searchChannels(searchQuery));
        }

        return result;
    }

    private static List<VideoBean> searchVideosPaginated(String searchQuery, String sortBy, int limit, int offset) throws ClassNotFoundException {
        List<VideoBean> videos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBconnection.getConnection();
            String sql = buildVideoSearchQuery(sortBy, true);
            pstmt = conn.prepareStatement(sql);

            String pattern = "%" + searchQuery + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setInt(3, limit);
            pstmt.setInt(4, offset);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                videos.add(mapVideoFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } 
        return videos;
    }

    private static List<ChannelBean> searchChannelsPaginated(String searchQuery, String sortBy, int limit, int offset) throws ClassNotFoundException {
        List<ChannelBean> channels = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBconnection.getConnection();
            String sql = buildChannelSearchQuery(sortBy, true);

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + searchQuery + "%");
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                channels.add(mapChannelFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } 

        return channels;
    }

    // =========================== SUGGESTIONS ===========================

    public static List<String> getSearchSuggestions(String prefix) throws ClassNotFoundException {
        List<String> suggestions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBconnection.getConnection();

            String videoSql = "SELECT DISTINCT video_name FROM video WHERE video_name LIKE ? AND video_visibility='public' LIMIT 5";
            pstmt = conn.prepareStatement(videoSql);
            pstmt.setString(1, prefix + "%");
            rs = pstmt.executeQuery();

            while (rs.next()) suggestions.add(rs.getString(1));

            rs.close();
            pstmt.close();

            String channelSql = "SELECT DISTINCT channel_name FROM channel WHERE channel_name LIKE ? LIMIT 5";
            pstmt = conn.prepareStatement(channelSql);
            pstmt.setString(1, prefix + "%");
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString(1);
                if (!suggestions.contains(name)) suggestions.add(name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } 

        return suggestions;
    }

    // =========================== TRENDING ===========================

    public static List<String> getTrendingSearches(int limit) throws ClassNotFoundException {
        List<String> trending = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBconnection.getConnection();

            String sql = "SELECT video_name " +
                    "FROM video " +
                    "WHERE video_visibility='public' " +
                    "ORDER BY view_count DESC " +
                    "LIMIT ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);

            rs = pstmt.executeQuery();

            while (rs.next()) trending.add(rs.getString("video_name"));

        } catch (SQLException e) {
            e.printStackTrace();
        } 
        return trending;
    }

    // =========================== COUNT RESULTS ===========================

    public static int countSearchResults(String searchQuery, String filterType) throws ClassNotFoundException {
        int count = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBconnection.getConnection();
            String pattern = "%" + searchQuery + "%";

            String sql;

            if ("videos".equalsIgnoreCase(filterType)) {
                sql = "SELECT COUNT(*) FROM video WHERE (video_name LIKE ? OR video_desc LIKE ?) AND video_visibility='public'";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, pattern);
                pstmt.setString(2, pattern);

            } else if ("channels".equalsIgnoreCase(filterType)) {
                sql = "SELECT COUNT(*) FROM channel WHERE channel_name LIKE ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, pattern);

            } else {
                sql = "SELECT " +
                        "(SELECT COUNT(*) FROM video WHERE (video_name LIKE ? OR video_desc LIKE ?) AND video_visibility='public')" +
                        " + (SELECT COUNT(*) FROM channel WHERE channel_name LIKE ?) AS total";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, pattern);
                pstmt.setString(2, pattern);
                pstmt.setString(3, pattern);
            }

            rs = pstmt.executeQuery();
            if (rs.next()) count = rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        } 

        return count;
    }

    // =========================== HELPERS ===========================

    private static String buildVideoSearchQuery(String sortBy, boolean paginated) {
        StringBuilder sql = new StringBuilder(
                "SELECT v.*, c.channel_name,c.channel_img, u.user_name " +
                        "FROM video v " +
                        "JOIN channel c ON v.channel_id = c.channel_id " +
                        "JOIN users u ON c.user_id = u.user_id " +
                        "WHERE (v.video_name LIKE ? OR v.video_desc LIKE ?) " +
                        "AND v.video_visibility='public' "
        );

        if ("date".equalsIgnoreCase(sortBy)) sql.append("ORDER BY v.video_created_at DESC ");
        else if ("views".equalsIgnoreCase(sortBy)) sql.append("ORDER BY v.view_count DESC ");
        else sql.append("ORDER BY v.view_count DESC, v.video_created_at DESC ");

        if (paginated) sql.append("LIMIT ? OFFSET ?");

        return sql.toString();
    }

    private static String buildChannelSearchQuery(String sortBy, boolean paginated) {
        StringBuilder sql = new StringBuilder(
                "SELECT c.*, u.user_name, " +
                        "(SELECT COUNT(*) FROM video v WHERE v.channel_id=c.channel_id AND v.video_visibility='public') AS video_count " +
                        "FROM channel c " +
                        "JOIN users u ON c.user_id=u.user_id " +
                        "WHERE c.channel_name LIKE ? "
        );

        if ("name".equalsIgnoreCase(sortBy)) sql.append("ORDER BY c.channel_name ASC ");
        else if ("videos".equalsIgnoreCase(sortBy)) sql.append("ORDER BY video_count DESC ");
        else sql.append("ORDER BY c.subscriber_count DESC ");

        if (paginated) sql.append("LIMIT ? OFFSET ?");

        return sql.toString();
    }

    private static VideoBean mapVideoFromResultSet(ResultSet rs) throws SQLException {
        VideoBean video = new VideoBean();

        video.setVideoId(rs.getInt("video_id"));
        video.setChannelId(rs.getInt("channel_id"));
        video.setVideoName(rs.getString("video_name"));
        video.setVideoDesc(rs.getString("video_desc"));
        video.setViewCount(rs.getLong("view_count"));
        video.setVideoUrl(rs.getString("video_url"));
        video.setUploadDate(rs.getTimestamp("video_created_at"));
        video.setVideoVisibility(rs.getString("video_visibility"));

        // Fix thumbnail (DB → Base64 String)
        byte[] thumbnailBytes = rs.getBytes("video_thumbnails");
        if (thumbnailBytes != null && thumbnailBytes.length > 0) {
//            video.setVideoThumbnail("data:image/jpeg;base64," + 
//                java.util.Base64.getEncoder().encodeToString(thumbnailBytes));
        	video.setVideoThumbnail(thumbnailBytes);
        } 

        if (columnExists(rs, "channel_img")) {
            byte[] channelImgBytes = rs.getBytes("channel_img");
            if (channelImgBytes != null && channelImgBytes.length > 0) {
                // Convert to Base64 String and set to the bean's channelImg property
                String base64ChannelImg = java.util.Base64.getEncoder().encodeToString(channelImgBytes);
                video.setChannelImgBase64(base64ChannelImg); 
            }
        }
        
        // Joined fields
        if (columnExists(rs, "channel_name")) {
            video.setChannelName(rs.getString("channel_name"));
        }
//        if (columnExists(rs, "user_name")) {
//            video.setVideoName(rs.getString("user_name"));
//        }

        return video;
    }


    private static ChannelBean mapChannelFromResultSet(ResultSet rs) throws SQLException {
        ChannelBean channel = new ChannelBean();

        channel.setChannelId(rs.getInt("channel_id"));
        channel.setUserId(rs.getInt("user_id"));
        channel.setChannelName(rs.getString("channel_name"));
        channel.setChannelDesc(rs.getString("channel_desc"));
        channel.setSubscriber_count(rs.getInt("subscriber_count"));

//        // Load Byte Image
//        channel.setChannelImg(rs.getBytes("channel_img"));
        
     // Load Byte Image
        byte[] imgBytes = rs.getBytes("channel_img");
        System.out.println("DEBUG: Image bytes length: " + (imgBytes != null ? imgBytes.length : "null"));
        channel.setChannelImg(imgBytes);

        // Optional fields
        if (columnExists(rs, "user_name")) {
            channel.setUserName(rs.getString("user_name"));
        }
        if (columnExists(rs, "video_count")) {
            channel.setVideoCount(rs.getInt("video_count"));
        }

        return channel;
    }


    private static boolean columnExists(ResultSet rs, String column) {
        try {
            rs.findColumn(column);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    // =========================== INNER RESULT CLASS ===========================

    public static class SearchResult {
        private List<VideoBean> videos = new ArrayList<>();
        private List<ChannelBean> channels = new ArrayList<>();

        public List<VideoBean> getVideos() { return videos; }
        public void setVideos(List<VideoBean> videos) { this.videos = videos; }

        public List<ChannelBean> getChannels() { return channels; }
        public void setChannels(List<ChannelBean> channels) { this.channels = channels; }

        public int getTotalResults() { return videos.size() + channels.size(); }
        public boolean hasVideos() { return videos != null && !videos.isEmpty(); }
        public boolean hasChannels() { return channels != null && !channels.isEmpty(); }
    }
}
