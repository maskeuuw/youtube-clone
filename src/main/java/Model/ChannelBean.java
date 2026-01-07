package Model;

import java.util.Base64;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelBean {
	private int channelId;
	private int userId;
	private String channelName;
	private String channelDesc;
	private int subscriber_count;
	private byte[] channelImg;
	
	private String userName;     // <-- ADD
    private int videoCount; 
	
	// Add these getters for JSP compatibility
    public String getName() {
        return channelName;
    }
    
    public String getDescription() {
        return channelDesc;
    }
    
    public int getSubscriberCount() {
        return subscriber_count;
    }
    
 // // Get Base64 encoded image for display in JSP
    public String getChannelImgBase64() {
        if (channelImg != null && channelImg.length > 0) {
            return java.util.Base64.getEncoder().encodeToString(channelImg);
        }
        return null;
    }
    
    // Check if image exists
    public boolean hasImage() {
        return channelImg != null && channelImg.length > 0;
    }
    
    public boolean getHasImage() {
        return hasImage();
    }
    
    // For JSTL access
    public String getAvatarUrl() {
        if (hasImage()) {
            return "channel-image?id=" + channelId;
        }
        return null;
    }
    
 // Get Base64 image with proper error handling
    public String getBase64Image() {
        if (channelImg != null && channelImg.length > 1) { // At least 1KB
            try {
                // First, try to determine image type from bytes
                String mimeType = getImageMimeType();
                return Base64.getEncoder().encodeToString(channelImg);
            } catch (Exception e) {
                System.err.println("Error encoding image for channel: " + channelName);
                return null;
            }
        }
        return null;
    }
    
 // Add this to resolve the ${channel.channelImg.length} error safely
    public int getImageLength() {
        if (channelImg != null) {
            return channelImg.length;
        }
        return 0;
    }
    
 // Try to determine image type
    private String getImageMimeType() {
        if (channelImg == null || channelImg.length < 4) {
            return "image/jpeg"; // Default
        }
        
        // Check for JPEG
        if (channelImg[0] == (byte)0xFF && channelImg[1] == (byte)0xD8 && channelImg[2] == (byte)0xFF) {
            return "image/jpeg";
        }
        
        // Check for PNG
        if (channelImg[0] == (byte)0x89 && channelImg[1] == (byte)0x50 && 
            channelImg[2] == (byte)0x4E && channelImg[3] == (byte)0x47) {
            return "image/png";
        }
        
        // Check for GIF
        if (channelImg[0] == (byte)0x47 && channelImg[1] == (byte)0x49 && channelImg[2] == (byte)0x46) {
            return "image/gif";
        }
        
        // Default to JPEG
        return "image/jpeg";
    }
}
