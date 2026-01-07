package Model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Base64;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoBean {
	
	private int videoId;
    private int channelId;
    private String videoName;
    private String videoDesc;
    private long viewCount;
    private String videoUrl;
    private byte[] videoThumbnail;
    private Timestamp uploadDate;
    private String videoVisibility;
    private String channelName; // Add this for display
	
    private String duration;
//  private String channelProfile;
//    private String channelImg;
    // Add reaction fields
    private int likeCount;
    private int dislikeCount;
    private String userReaction; // "Like", "Dislike", or null
    
    private String channelImgBase64;
    private String videoThumbnailBase64;
//	private byte[] videoThumbnailBytes;
    
// // For backward compatibility - accepts either
//    public void setVideoThumbnail(Object thumbnail) {
//        if (thumbnail instanceof byte[]) {
//            this.videoThumbnailBytes = (byte[]) thumbnail;
//        } else if (thumbnail instanceof String) {
//            this.videoThumbnailBase64 = (String) thumbnail;
//        } else if (thumbnail == null) {
//            this.videoThumbnailBytes = null;
//            this.videoThumbnailBase64 = null;
//        }
//    }
    
 // Getter for base64 string - used in JSP
    public String getVideoThumbnail() {
        // Return base64 string for JSP
        if (videoThumbnailBase64 != null && !videoThumbnailBase64.isEmpty()) {
            return videoThumbnailBase64;
        }
        
        if (videoThumbnail != null && videoThumbnail.length > 0) {
            // Convert byte array to base64 string
            return Base64.getEncoder().encodeToString(videoThumbnail);
        }
        return null;
    }
    
 // Only ONE setVideoThumbnail method
    public void setVideoThumbnail(byte[] thumbnailBytes) {
        this.videoThumbnail = thumbnailBytes;
    }
    
 // Getter for JSP - returns base64 string
    public String getVideoThumbnailBase64() {
        if (videoThumbnail != null && videoThumbnail.length > 0) {
            return java.util.Base64.getEncoder().encodeToString(videoThumbnail);
        }
        return null;
    }
    
 // This is what your JSP is looking for
    public String getBase64Thumbnail() {
        if (videoThumbnail != null && videoThumbnail.length > 0) {
            return Base64.getEncoder().encodeToString(videoThumbnail);
        }
        return null;
    }
    
 // Getter for JSP - returns full data URL
    public String getVideoThumbnailUrl() {
        if (videoThumbnail != null && videoThumbnail.length > 0) {
            return "data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(videoThumbnail);
        }
        return null;
    }
    
    public String getVideoWebUrl() {
        return "videowatch?v=" + this.videoId;
    }
    
 // Helper methods for JSP
    public String getFormattedViews() {
        if (viewCount >= 1000000) {
            return String.format("%.1fM", viewCount / 1000000.0);
        } else if (viewCount >= 1000) {
            return String.format("%.1fK", viewCount / 1000.0);
        }
        return String.valueOf(viewCount);
    }
    
    public String getFormattedDate() {
        if (uploadDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
            return sdf.format(uploadDate);
        }
        return "Unknown date";
    }
    
    public String getFormattedLikes() {
        if (likeCount >= 1000000) {
            return String.format("%.1fM", likeCount / 1000000.0);
        } else if (likeCount >= 1000) {
            return String.format("%.1fK", likeCount / 1000.0);
        }
        return String.valueOf(likeCount);
    }
    
 // CRITICAL: Add these getters for JSP compatibility
    public String getTitle() {
        return videoName; // JSP expects getTitle() but we store as videoName
    }
    
    public int getViews() {
        return (int) viewCount; // JSP expects getV iews()
    }
    
    public byte[] getThumbnailUrl() {
        return videoThumbnail; // JSP expects getThumbnailUrl()
    }
    
//    public byte[] getThumbnailUrl() {
//        return videoThumbnail; // JSP expects getThumbnailUrl()
//    }
    
//    public String getUploadDate() {
//        return getFormattedDate(); // Return formatted date for display
//    }
 // Add a method to check if channel image exists
//    public boolean hasChannelImg() {
//        return channelImg != null && !channelImg.isEmpty();
//    }
    
    // Add a method to get channel image as base64 data URL
//    public String getChannelImgUrl() {
//        if (hasChannelImg()) {
//            return "data:image/jpeg;base64," + channelImg;
//        }
//        return null;
//    }
    
    
 // Method 1: Check existence
    public boolean hasChannelImg() {
        // Use the new field name
        return channelImgBase64 != null && !channelImgBase64.isEmpty(); 
    }

    // Method 2: Get full data URL
    public String getChannelImgUrl() {
        if (hasChannelImg()) {
            // Use the new field name
            return "data:image/jpeg;base64," + channelImgBase64; 
        }
        return null;
    }
//    public String getBase64Thumbnail() {
//        if (videoThumbnail != null) {
//            return Base64.getEncoder().encodeToString(videoThumbnail);
//        }
//        return null;
//    }
}

