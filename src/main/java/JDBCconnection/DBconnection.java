package JDBCconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {
	public static Connection getConnection() throws ClassNotFoundException {
		Connection con = null;
		Class.forName("com.mysql.cj.jdbc.Driver");
		try {
			con= DriverManager.getConnection("jdbc:mysql://localhost:3306/youtube" ,"root","moethu179916");
			System.out.println("Connecton found");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Connection not found");
			e.printStackTrace();
		}
		return con;
		
	}
}
