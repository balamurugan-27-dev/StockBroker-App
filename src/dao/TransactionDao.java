package dao;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
public class TransactionDao {
	
	public boolean addTransaction(int userId,String userName, int stockId,String stockName, String traType, int qty, double value,Connection con) throws SQLException {
		
		String query ="insert into transaction (User_id,user_name,stock_id,stock_name,transaction_type,quantity,price) values (?,?,?,?,?,?,?)";
		PreparedStatement pst=con.prepareStatement(query);
		pst.setInt(1,userId);
		pst.setString(2,userName );
		pst.setInt(3, stockId);
		pst.setString(4,stockName );
		pst.setString(5,traType);
		pst.setInt(6,qty);
		pst.setDouble(7, value);
		
		int update=pst.executeUpdate();
		pst.close();
		return(update==1)?true :false;
		
	}

	public void viewTransasction(int userId, Connection con) throws SQLException {
		
		String query="select Transaction_id,user_id,user_name,stock_id,Stock_name,Transaction_type,Quantity,price,transaction_date from transaction where user_id=?";
		PreparedStatement pst= con.prepareStatement(query);
		pst.setInt(1, userId);
		
		ResultSet rs=pst.executeQuery();
		
		System.out.printf("%-10s %-10s %-10s  %-10s%-10s %-10s %-10s  %-10s  %-10s\n","Transaction_id","user_id","user_name","stock_id","Stock_name","Transaction_type","Quantity","price","transaction_date");
		
		while(rs.next()) {
			
			LocalDateTime lt=rs.getTimestamp(9).toLocalDateTime();
			DateTimeFormatter format=DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			
			String date=lt.format(format);
			
			System.out.printf("%-10d     %-10d   %-10s    %-10d %-10s %-10s   %-10d %-10.2f %-10s\n",rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getInt(4),rs.getString(5),rs.getString(6),rs.getInt(7),rs.getDouble(8),date);
		}
			
		rs.close();
		pst.close();
	}
	
}
