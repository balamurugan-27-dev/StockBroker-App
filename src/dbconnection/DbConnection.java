package dbconnection;
import java.sql.*;
public class DbConnection {
	
	public static Connection getConnection() {
		
		String url="jdbc:mysql://localhost:3306/stockbroker";
		String name="root";
		String pass="root777";
		try {
		return DriverManager.getConnection(url,name,pass);
		}
		
		catch(SQLException e) {
			System.out.println("Connection failed ");
			return null;
		}
	}
		
	
}
