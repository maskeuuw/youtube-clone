package Model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentBean {
	
	private int commentId;
    private int videoId;
    private int userId;
    private String userName; // For display
    private String contents;
    private Timestamp createdAt;
    private int parentCommentId;
    private List<CommentBean> replies; // For nested replies
    private int likeCount;
    private int dislikeCount;
    private String userReaction; // User's reaction to this comment
    
    // Helper methods
    public String getFormattedTime() {
        if (createdAt != null) {
            long diff = System.currentTimeMillis() - createdAt.getTime();
            
            // Convert to appropriate time unit
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            long weeks = days / 7;
            long months = days / 30;
            long years = days / 365;
            
            if (years > 0) return years + " year" + (years > 1 ? "s" : "") + " ago";
            if (months > 0) return months + " month" + (months > 1 ? "s" : "") + " ago";
            if (weeks > 0) return weeks + " week" + (weeks > 1 ? "s" : "") + " ago";
            if (days > 0) return days + " day" + (days > 1 ? "s" : "") + " ago";
            if (hours > 0) return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
            if (minutes > 0) return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
            return "Just now";
        }
        return "";
    }
    
    // Initialize replies list
    public CommentBean() {
        this.replies = new ArrayList<>();
    }
    
    // Add reply
    public void addReply(CommentBean reply) {
        this.replies.add(reply);
    }
}
