package Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import JDBCconnection.DBconnection;
import Model.CommentBean;

public class CommentRepository {

	// Add a new comment
	public boolean addComment(int videoId, int userId, String content, Integer parentCommentId)
			throws SQLException, ClassNotFoundException {

		String sql = "INSERT INTO comment (comment_video_id, comment_user_id, parent_comment_id, contents) VALUES (?, ?, ?, ?)";

		try (Connection conn = DBconnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, videoId);
			ps.setInt(2, userId);

			if (parentCommentId != null) {
				ps.setInt(3, parentCommentId);
			} else {
				ps.setNull(3, java.sql.Types.INTEGER);
			}

			ps.setString(4, content);

			return ps.executeUpdate() > 0;
		}
	}

	// Get comments for a video (with nested replies)
	public List<CommentBean> getCommentsByVideoId(int videoId, Integer userId)
			throws SQLException, ClassNotFoundException {

		// Get all comments for this video with user info
		String sql = "SELECT c.*, u.user_name as username " + 
	             "FROM comment c " + 
	             "JOIN users u ON c.comment_user_id = u.user_id " + 
	             "WHERE c.comment_video_id = ? " + 
	             "ORDER BY c.comment_created_at ASC";
		
		System.out.println("DEBUG: Executing SQL: " + sql);
	    System.out.println("DEBUG: Video ID: " + videoId);

		List<CommentBean> allComments = new ArrayList<>();

		try (Connection conn = DBconnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, videoId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				CommentBean comment = new CommentBean();
				comment.setCommentId(rs.getInt("comment_id"));
				comment.setVideoId(rs.getInt("comment_video_id"));
				comment.setUserId(rs.getInt("comment_user_id"));
				comment.setUserName(rs.getString("username"));
				comment.setContents(rs.getString("contents"));
				comment.setCreatedAt(rs.getTimestamp("comment_created_at"));

				int parentId = rs.getInt("parent_comment_id");
				if (!rs.wasNull()) {
					comment.setParentCommentId(parentId);
				}

				// ========== CRITICAL: ADD REACTION DATA ==========
                // Get reaction counts for this comment
                int[] reactionCounts = getCommentReactionCounts(comment.getCommentId());
                comment.setLikeCount(reactionCounts[0]);
                comment.setDislikeCount(reactionCounts[1]);

                // Get user's reaction if logged in
                if (userId != null) {
                    String userReaction = getUserCommentReaction(userId, comment.getCommentId());
                    comment.setUserReaction(userReaction);
                }
                // ========== END CRITICAL ADDITION ==========
                System.out.println("DEBUG: Comment loaded - ID: " + comment.getCommentId() + 
                        ", User: " + comment.getUserName() + 
                        ", Content: " + comment.getContents() +
                        ", Likes: " + comment.getLikeCount() +
                        ", Dislikes: " + comment.getDislikeCount() +
                        ", User Reaction: " + comment.getUserReaction());
				
				allComments.add(comment);
			}
		}catch (SQLException e) {
	        System.out.println("ERROR in getCommentsByVideoId: " + e.getMessage());
	        e.printStackTrace();
	        throw e;
	    }


		// Organize comments into hierarchy
		return buildCommentHierarchy(allComments);
	}

//	// Get comments for a video (with nested replies)
//	public List<CommentBean> getCommentsByVideoId(int videoId, Integer userId)
//			throws SQLException, ClassNotFoundException {
//
//		// FIXED: Use correct table name 'users' and column 'user_name'
//		String sql = "SELECT c.*, u.user_name as username " + "FROM comment c "
//				+ "JOIN users u ON c.comment_user_id = u.user_id " + "WHERE c.comment_video_id = ? "
//				+ "ORDER BY c.comment_created_at ASC";
//
//		System.out.println("DEBUG: Executing SQL: " + sql);
//		System.out.println("DEBUG: Video ID: " + videoId);
//
//		List<CommentBean> allComments = new ArrayList<>();
//
//		try (Connection conn = DBconnection.getConnection()) {
//			System.out.println("DEBUG: Database connection successful");
//
//			try (PreparedStatement ps = conn.prepareStatement(sql)) {
//				ps.setInt(1, videoId);
//				ResultSet rs = ps.executeQuery();
//
//				System.out.println("DEBUG: Query executed");
//				int count = 0;
//
//				while (rs.next()) {
//					count++;
//					System.out.println("DEBUG: Found comment #" + count);
//
//					CommentBean comment = new CommentBean();
//					comment.setCommentId(rs.getInt("comment_id"));
//					comment.setVideoId(rs.getInt("comment_video_id"));
//					comment.setUserId(rs.getInt("comment_user_id"));
//
//					// Get username from users table
//					String userName = rs.getString("username");
//					if (userName != null && !userName.isEmpty()) {
//						comment.setUserName(userName);
//					} else {
//						comment.setUserName("User " + rs.getInt("comment_user_id"));
//					}
//
//					comment.setContents(rs.getString("contents"));
//					comment.setCreatedAt(rs.getTimestamp("comment_created_at"));
//
//					int parentId = rs.getInt("parent_comment_id");
//					if (!rs.wasNull()) {
//						comment.setParentCommentId(parentId);
//						System.out.println("DEBUG: Parent comment ID: " + parentId);
//					} else {
//						comment.setParentCommentId(0);
//					}
//
//					System.out.println("DEBUG: Comment loaded - ID: " + comment.getCommentId() + ", User: "
//							+ comment.getUserName() + ", Content: " + comment.getContents());
//
//					allComments.add(comment);
//				}
//
//				System.out.println("DEBUG: Total comments found: " + count);
//			}
//		} catch (SQLException e) {
//			System.out.println("ERROR in getCommentsByVideoId: " + e.getMessage());
//			e.printStackTrace();
//			throw e; // Re-throw to see the actual error
//		}
//
//		// Organize comments into hierarchy
//		List<CommentBean> organizedComments = buildCommentHierarchy(allComments);
//		System.out.println("DEBUG: Organized comments count: " + organizedComments.size());
//
//		return organizedComments;
//	}

	// Build comment hierarchy (parent comments with replies)
	private List<CommentBean> buildCommentHierarchy(List<CommentBean> allComments) {
		List<CommentBean> topLevelComments = new ArrayList<>();

		// Create a map for quick lookup
		java.util.Map<Integer, CommentBean> commentMap = new java.util.HashMap<>();

		// First pass: add all comments to map
		for (CommentBean comment : allComments) {
			commentMap.put(comment.getCommentId(), comment);
		}

		// Second pass: organize hierarchy
		for (CommentBean comment : allComments) {
			if (comment.getParentCommentId() == 0) {
				// Top-level comment
				topLevelComments.add(comment);
			} else {
				// Find parent and add as reply
				CommentBean parent = commentMap.get(comment.getParentCommentId());
				if (parent != null) {
					parent.addReply(comment);
				}
			}
		}

		return topLevelComments;
	}

	// Get comment count for a video
	public int getCommentCount(int videoId) throws SQLException, ClassNotFoundException {
		String sql = "SELECT COUNT(*) as comment_count FROM comment WHERE comment_video_id = ?";

	    try (Connection conn = DBconnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, videoId);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            int count = rs.getInt("comment_count");
	            System.out.println("DEBUG: Comment count for video " + videoId + ": " + count);
	            return count;
	        }
	    } catch (Exception e) {
	        System.out.println("ERROR getting comment count: " + e.getMessage());
	    }
	    return 0;
	}

	public boolean deleteComment(int commentId, int userId) throws SQLException, ClassNotFoundException {
	    System.out.println("=== COMMENT REPOSITORY DELETE DEBUG ===");
	    System.out.println("DEBUG: Deleting comment ID: " + commentId + " for user ID: " + userId);
	    
	    // SIMPLE VERSION - Let database handle cascading
	    String sql = "DELETE FROM comment WHERE comment_id = ? AND comment_user_id = ?";
	    
	    try (Connection conn = DBconnection.getConnection(); 
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setInt(1, commentId);
	        ps.setInt(2, userId);
	        
	        int rowsAffected = ps.executeUpdate();
	        System.out.println("DEBUG: Rows affected: " + rowsAffected);
	        
	        return rowsAffected > 0;
	        
	    } catch (SQLException e) {
	        System.out.println("SQL ERROR in deleteComment: " + e.getMessage());
	        System.out.println("SQL State: " + e.getSQLState());
	        System.out.println("Error Code: " + e.getErrorCode());
	        e.printStackTrace();
	        throw e;
	    }
	}

	// Update a comment
	public boolean updateComment(int commentId, int userId, String newContent)
			throws SQLException, ClassNotFoundException {

		String sql = "UPDATE comment SET contents = ? WHERE comment_id = ? AND comment_user_id = ?";

		try (Connection conn = DBconnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, newContent);
			ps.setInt(2, commentId);
			ps.setInt(3, userId);

			return ps.executeUpdate() > 0;
		}
	}

	// Comment reactions (like/dislike)
	public boolean addCommentReaction(int userId, int commentId, String reactionType)
			throws SQLException, ClassNotFoundException {

		System.out.println("DEBUG: Adding comment reaction - User: " + userId + ", Comment: " + commentId + ", Type: "
				+ reactionType);

		// Check if user already reacted to this comment
		String checkSql = "SELECT comment_reaction_id FROM comment_reaction WHERE user_id = ? AND comment_id = ?";

		try (Connection conn = DBconnection.getConnection();
				PreparedStatement checkPs = conn.prepareStatement(checkSql)) {

			checkPs.setInt(1, userId);
			checkPs.setInt(2, commentId);
			ResultSet rs = checkPs.executeQuery();

			if (rs.next()) {
				// Update existing reaction
				String updateSql = "UPDATE comment_reaction SET type = ?, reaction_created_at = CURRENT_TIMESTAMP "
						+ "WHERE user_id = ? AND comment_id = ?";
				try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
					updatePs.setString(1, reactionType);
					updatePs.setInt(2, userId);
					updatePs.setInt(3, commentId);
					boolean success = updatePs.executeUpdate() > 0;
					System.out.println("DEBUG: Updated existing reaction: " + success);
					return success;
				}
			} else {
				// Insert new reaction
				String insertSql = "INSERT INTO comment_reaction (comment_id, user_id, type) VALUES (?, ?, ?)";
				try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
					insertPs.setInt(1, commentId);
					insertPs.setInt(2, userId);
					insertPs.setString(3, reactionType);
					boolean success = insertPs.executeUpdate() > 0;
					System.out.println("DEBUG: Inserted new reaction: " + success);
					return success;
				}
			}
		}
	}

	// REMOVE COMMENT REACTION
	public boolean removeCommentReaction(int userId, int commentId) throws SQLException, ClassNotFoundException {

		System.out.println("DEBUG: Removing comment reaction - User: " + userId + ", Comment: " + commentId);

		String sql = "DELETE FROM comment_reaction WHERE user_id = ? AND comment_id = ?";

		try (Connection conn = DBconnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, userId);
			ps.setInt(2, commentId);
			boolean success = ps.executeUpdate() > 0;
			System.out.println("DEBUG: Reaction removed: " + success);
			return success;
		}
	}

	// GET COMMENT REACTION COUNTS - FIXED IMPLEMENTATION
	public int[] getCommentReactionCounts(int commentId) throws SQLException, ClassNotFoundException {
		int[] counts = new int[2]; // [0] = likes, [1] = dislikes

		System.out.println("DEBUG: Getting reaction counts for comment: " + commentId);

		String likeSql = "SELECT COUNT(*) as like_count FROM comment_reaction WHERE comment_id = ? AND type = 'Like'";
		String dislikeSql = "SELECT COUNT(*) as dislike_count FROM comment_reaction WHERE comment_id = ? AND type = 'Dislike'";

		try (Connection conn = DBconnection.getConnection()) {
			// Get like count
			try (PreparedStatement likePs = conn.prepareStatement(likeSql)) {
				likePs.setInt(1, commentId);
				ResultSet rs = likePs.executeQuery();
				if (rs.next()) {
					counts[0] = rs.getInt("like_count");
				}
			}

			// Get dislike count
			try (PreparedStatement dislikePs = conn.prepareStatement(dislikeSql)) {
				dislikePs.setInt(1, commentId);
				ResultSet rs = dislikePs.executeQuery();
				if (rs.next()) {
					counts[1] = rs.getInt("dislike_count");
				}
			}
		}

		System.out.println("DEBUG: Comment " + commentId + " - Likes: " + counts[0] + ", Dislikes: " + counts[1]);
		return counts;
	}

	// GET USER'S REACTION TO A COMMENT - FIXED IMPLEMENTATION
	public String getUserCommentReaction(int userId, int commentId) throws SQLException, ClassNotFoundException {

		System.out.println("DEBUG: Getting user reaction - User: " + userId + ", Comment: " + commentId);

		String sql = "SELECT type FROM comment_reaction WHERE user_id = ? AND comment_id = ?";

		try (Connection conn = DBconnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, userId);
			ps.setInt(2, commentId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				String reaction = rs.getString("type");
				System.out.println("DEBUG: User reaction found: " + reaction);
				return reaction;
			}
		}

		System.out.println("DEBUG: No user reaction found");
		return null;
	}

	// UPDATE getCommentsByVideoId to use the fixed reaction methods
//	public List<CommentBean> getCommentsByVideoId(int videoId, Integer userId)
//			throws SQLException, ClassNotFoundException {
//
//		String sql = "SELECT c.*, u.user_name as username " + "FROM comment c "
//				+ "JOIN users u ON c.comment_user_id = u.user_id " + "WHERE c.comment_video_id = ? "
//				+ "ORDER BY c.comment_created_at ASC";
//
//		System.out.println("DEBUG: Getting comments for video: " + videoId);
//
//		List<CommentBean> allComments = new ArrayList<>();
//
//		try (Connection conn = DBconnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
//
//			ps.setInt(1, videoId);
//			ResultSet rs = ps.executeQuery();
//
//			while (rs.next()) {
//				CommentBean comment = new CommentBean();
//				comment.setCommentId(rs.getInt("comment_id"));
//				comment.setVideoId(rs.getInt("comment_video_id"));
//				comment.setUserId(rs.getInt("comment_user_id"));
//
//				String userName = rs.getString("username");
//				if (userName != null && !userName.isEmpty()) {
//					comment.setUserName(userName);
//				} else {
//					comment.setUserName("User " + rs.getInt("comment_user_id"));
//				}
//
//				comment.setContents(rs.getString("contents"));
//				comment.setCreatedAt(rs.getTimestamp("comment_created_at"));
//
//				int parentId = rs.getInt("parent_comment_id");
//				if (!rs.wasNull()) {
//					comment.setParentCommentId(parentId);
//				} else {
//					comment.setParentCommentId(0);
//				}
//
//				// Get reaction counts for this comment
//				int[] reactionCounts = getCommentReactionCounts(comment.getCommentId());
//				comment.setLikeCount(reactionCounts[0]);
//				comment.setDislikeCount(reactionCounts[1]);
//
//				// Get user's reaction if logged in
//				if (userId != null) {
//					String userReaction = getUserCommentReaction(userId, comment.getCommentId());
//					comment.setUserReaction(userReaction);
//				}
//
//				allComments.add(comment);
//			}
//		}
//
//		// Organize comments into hierarchy
//		return buildCommentHierarchy(allComments);
//	}
	
//	public boolean deleteComment(int commentId, int userId) throws SQLException, ClassNotFoundException {
//	    System.out.println("=== COMMENT REPOSITORY DELETE DEBUG ===");
//	    System.out.println("DEBUG: Deleting comment ID: " + commentId + " for user ID: " + userId);
//	    
//	    Connection conn = null;
//	    try {
//	        conn = DBconnection.getConnection();
//	        conn.setAutoCommit(false); // Start transaction
//	        
//	        // 1. First, check if comment exists and belongs to user
//	        String checkSql = "SELECT comment_id FROM comment WHERE comment_id = ? AND comment_user_id = ?";
//	        System.out.println("DEBUG: Checking comment ownership...");
//	        try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
//	            checkPs.setInt(1, commentId);
//	            checkPs.setInt(2, userId);
//	            ResultSet rs = checkPs.executeQuery();
//	            
//	            if (!rs.next()) {
//	                System.out.println("DEBUG: Comment not found or user doesn't own it");
//	                conn.rollback();
//	                return false;
//	            }
//	            System.out.println("DEBUG: Comment found and belongs to user");
//	        }
//	        
//	        // 2. Check if comment has any replies
//	        System.out.println("DEBUG: Checking for replies...");
//	        String checkRepliesSql = "SELECT COUNT(*) as reply_count FROM comment WHERE parent_comment_id = ?";
//	        try (PreparedStatement checkRepliesPs = conn.prepareStatement(checkRepliesSql)) {
//	            checkRepliesPs.setInt(1, commentId);
//	            ResultSet rs = checkRepliesPs.executeQuery();
//	            if (rs.next() && rs.getInt("reply_count") > 0) {
//	                System.out.println("DEBUG: Comment has " + rs.getInt("reply_count") + " replies");
//	                
//	                // OPTION 1: Delete replies first (recommended for cascading delete)
//	                deleteReplies(conn, commentId);
//	                
//	                // OPTION 2: Or set parent_comment_id to NULL for replies
//	                // updateRepliesParentToNull(conn, commentId);
//	            }
//	        }
//	        
//	        // 3. Delete reactions FIRST (MUST be done before deleting comment due to FK constraint)
//	        System.out.println("DEBUG: Deleting comment reactions...");
//	        String deleteReactionsSql = "DELETE FROM comment_reaction WHERE comment_id = ?";
//	        try (PreparedStatement deleteReactionsPs = conn.prepareStatement(deleteReactionsSql)) {
//	            deleteReactionsPs.setInt(1, commentId);
//	            int reactionsDeleted = deleteReactionsPs.executeUpdate();
//	            System.out.println("DEBUG: Deleted " + reactionsDeleted + " reactions from comment_reaction table");
//	        } catch (SQLException e) {
//	            System.out.println("DEBUG: No reactions to delete or error deleting reactions: " + e.getMessage());
//	            // Continue anyway
//	        }
//	        
//	        // 4. Now delete the comment (after replies and reactions are handled)
//	        System.out.println("DEBUG: Deleting comment...");
//	        String deleteCommentSql = "DELETE FROM comment WHERE comment_id = ? AND comment_user_id = ?";
//	        try (PreparedStatement deleteCommentPs = conn.prepareStatement(deleteCommentSql)) {
//	            deleteCommentPs.setInt(1, commentId);
//	            deleteCommentPs.setInt(2, userId);
//	            int rowsAffected = deleteCommentPs.executeUpdate();
//	            System.out.println("DEBUG: Rows affected in comment table: " + rowsAffected);
//	            
//	            if (rowsAffected > 0) {
//	                conn.commit(); // Commit the transaction
//	                System.out.println("DEBUG: Successfully deleted comment " + commentId);
//	                return true;
//	            } else {
//	                conn.rollback(); // Rollback if no rows affected
//	                System.out.println("DEBUG: Failed to delete comment - no rows affected");
//	                return false;
//	            }
//	        }
//	        
//	    } catch (SQLException e) {
//	        if (conn != null) {
//	            try {
//	                conn.rollback();
//	            } catch (SQLException rollbackEx) {
//	                System.out.println("ERROR rolling back transaction: " + rollbackEx.getMessage());
//	            }
//	        }
//	        System.out.println("SQL ERROR in deleteComment: " + e.getMessage());
//	        System.out.println("SQL State: " + e.getSQLState());
//	        System.out.println("Error Code: " + e.getErrorCode());
//	        e.printStackTrace();
//	        throw e;
//	    } finally {
//	        if (conn != null) {
//	            try {
//	                conn.setAutoCommit(true);
//	                conn.close();
//	            } catch (SQLException e) {
//	                System.out.println("ERROR closing connection: " + e.getMessage());
//	            }
//	        }
//	    }
//	}
//
//	// Helper method to delete replies
//	private void deleteReplies(Connection conn, int parentCommentId) throws SQLException {
//	    System.out.println("DEBUG: Deleting replies for parent comment " + parentCommentId);
//	    
//	    // First, get all reply IDs
//	    List<Integer> replyIds = new ArrayList<>();
//	    String getRepliesSql = "SELECT comment_id FROM comment WHERE parent_comment_id = ?";
//	    try (PreparedStatement getRepliesPs = conn.prepareStatement(getRepliesSql)) {
//	        getRepliesPs.setInt(1, parentCommentId);
//	        ResultSet rs = getRepliesPs.executeQuery();
//	        while (rs.next()) {
//	            replyIds.add(rs.getInt("comment_id"));
//	        }
//	    }
//	    
//	    System.out.println("DEBUG: Found " + replyIds.size() + " replies to delete");
//	    
//	    // Delete each reply (including their reactions)
//	    for (int replyId : replyIds) {
//	        System.out.println("DEBUG: Deleting reply ID: " + replyId);
//	        
//	        // Delete reply reactions
//	        String deleteReplyReactionsSql = "DELETE FROM comment_reaction WHERE comment_id = ?";
//	        try (PreparedStatement deleteReactionsPs = conn.prepareStatement(deleteReplyReactionsSql)) {
//	            deleteReactionsPs.setInt(1, replyId);
//	            deleteReactionsPs.executeUpdate();
//	        }
//	        
//	        // Delete the reply
//	        String deleteReplySql = "DELETE FROM comment WHERE comment_id = ?";
//	        try (PreparedStatement deleteReplyPs = conn.prepareStatement(deleteReplySql)) {
//	            deleteReplyPs.setInt(1, replyId);
//	            deleteReplyPs.executeUpdate();
//	        }
//	    }
//	}
//
//	// Alternative: Update replies to have NULL parent instead of deleting them
//	private void updateRepliesParentToNull(Connection conn, int parentCommentId) throws SQLException {
//	    System.out.println("DEBUG: Setting parent_comment_id to NULL for replies of comment " + parentCommentId);
//	    
//	    String updateRepliesSql = "UPDATE comment SET parent_comment_id = NULL WHERE parent_comment_id = ?";
//	    try (PreparedStatement updateRepliesPs = conn.prepareStatement(updateRepliesSql)) {
//	        updateRepliesPs.setInt(1, parentCommentId);
//	        int updatedCount = updateRepliesPs.executeUpdate();
//	        System.out.println("DEBUG: Updated " + updatedCount + " replies to have NULL parent");
//	    }
//	}
}
