package Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import JDBCconnection.DBconnection;
import Model.UserBean;

public class UserRepository {
	public static int singUpUser(UserBean user) throws ClassNotFoundException {
		int row = 0;
		Connection con = DBconnection.getConnection();
		String sql = "insert into user(user_name,user_email,user_password) values(?,?)";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, user.getUserName());
			ps.setString(2, user.getEmail());
			ps.setString(3, user.getPassword());
			row = ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Signup error:" + e.getMessage());
			e.printStackTrace();
		}
		return row;

	}

	public void registerUser(UserBean newUser) {
		String sql = "INSERT INTO users (user_name, user_email, user_password) VALUES (?, ?, ?)";

		try (Connection con = DBconnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			// Set the parameters from the UserBean object
			ps.setString(1, newUser.getUserName());
			ps.setString(2, newUser.getEmail());
			ps.setString(3, newUser.getPassword()); // Storing PLAIN text as requested ⚠️

			// Execute the update
			int rows = ps.executeUpdate();

			if (rows == 0) {
				// Handle case where INSERT failed (though uncommon without an exception)
				System.err.println("User registration failed: No rows affected.");
			}

		} catch (SQLException e) {
			System.err.println("Database error during user registration: " + e.getMessage());
			e.printStackTrace();
			// In a real application, you might throw a custom exception here
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	public boolean isEmailRegistered(String email) {
		String sql = "SELECT COUNT(*) FROM users WHERE user_email = ?";

		try (Connection con = DBconnection.getConnection(); 
				PreparedStatement ps = con.prepareStatement(sql)) {

			// Set the email parameter
			ps.setString(1, email);

			// Execute the query
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					// If COUNT(*) is greater than 0, the email is registered
					return rs.getInt(1) > 0;
				}
			}
		} catch (SQLException e) {
			// Log the error (e.g., failed connection, bad query)
			System.err.println("Database error checking email registration: " + e.getMessage());
			// It's safer to return true on a database error to prevent new registrations
			// that might fail later, but false is often used to allow the flow to continue.
			return false;
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
			return false;
		}
		return false;
	}

//	public UserBean validateUser(String email, String password) {
//		String sql = "SELECT user_email, user_password FROM users WHERE user_email = ?";
//        UserBean user = null;
//        
//        try (Connection con = DBconnection.getConnection();
//             PreparedStatement ps = con.prepareStatement(sql)) {
//            
//            ps.setString(1, email);
//            
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    // User found by email
//                    
//                    String dbpassword = rs.getString("user_password");
//                    
//                    // ⚠️ WARNING: Comparing plain text passwords 
//                    if (dbpassword.equals(password)) {
//                        // Passwords match! Create the UserBean object
//                        user = new UserBean();
//                        user.setEmail(email);
//                        user.setPassword(dbpassword); // Or omit the password for security
//                    }
//                    // If passwords don't match, 'user' remains null
//                }
//            }
//        } catch (SQLException | ClassNotFoundException e) {
//            System.err.println("Login validation error: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return user;
//    }
		
	public UserBean validateUser(String email, String password) {
	    // 🛑 CRITICAL CHANGE: Use LEFT JOIN to get user details AND channel ID.
	    // Assuming your users table has 'id' (primary key) and 'user_name'.
	    // Assuming your channel table has 'channel_id' and 'user_id' (foreign key).
	    String sql = "SELECT u.user_id, u.user_name, u.user_email, u.user_password, u.user_role, c.channel_id " +
                "FROM users u " +
                "LEFT JOIN channel c ON u.user_id = c.user_id " + // Corrected column name
                "WHERE u.user_email = ?";
	    
	    UserBean user = null;
	    
	    try (Connection con = DBconnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	        
	        ps.setString(1, email);
	        
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                
	                String dbpassword = rs.getString("user_password");
	                
	                // ⚠️ WARNING: Comparing plain text passwords 
	                if (dbpassword.equals(password)) {
	                    
	                    // Fetch required data for the UserBean:
	                	int dbUserId = rs.getInt("user_id");
	                    String dbName = rs.getString("user_name");
	                    // rs.getObject handles NULL correctly for Integer types
	                    Integer dbChannelId = (Integer) rs.getObject("channel_id"); 
	                    
	                    // Create and populate the UserBean object
	                    user = new UserBean();
	                    user.setUserId(dbUserId);
	                    user.setUserName(dbName);               // <-- NEW: Set the user's name
	                    user.setEmail(email);
	                    user.setPassword(dbpassword);
	                    user.setUserRole(rs.getString("user_role"));
	                    user.setChannelId(dbChannelId);  
	                    // <-- NEW: Set the channel ID (will be NULL if no channel exists)
	                }
	            }
	        }
	    } catch (SQLException | ClassNotFoundException e) {
	        System.err.println("Login validation error: " + e.getMessage());
	        e.printStackTrace();
	    }
	    return user;
	}

	
}

