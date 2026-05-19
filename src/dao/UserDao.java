package dao;
import java.sql.*;
import model.User;

public class UserDao {
	

	public void insert(String name, String email, String password,Connection con) throws SQLException  {
		
		
		String query="insert into user(name,email,password) values(?,?,?)";
		
		PreparedStatement pst=con.prepareStatement(query);
		pst.setString(1,name);
		pst.setString(2,email);
		pst.setString(3,password);
		
		if(pst.executeUpdate()==1) {
			System.out.println(" Registerd Successfull You Can Login");
			return;
		}
		
		System.out.println("registeration faile");
		
		
	}

	public User login(String email, String password,Connection con) throws SQLException {
		String query="select user_id,name,balance from user where email=? and password=?";
		PreparedStatement pst=con.prepareStatement(query);
		pst.setString(1, email);;
		pst.setString(2, password);
		
		ResultSet rs=pst.executeQuery();

		if(	rs.next()){
		User user=new User();
		user.setId(rs.getInt(1));
		user.setName(rs.getString(2));
		user.setBalance(rs.getDouble(3));
		rs.close();
		pst.close();
		return user;
		}
		rs.close();
		pst.close();
		return null;
	}
	

public boolean updateWallet(int userId, double balance, Connection con) throws SQLException{
	
	String query="update user set balance=? where user_id=?";
	
	try{
		
		PreparedStatement pst=con.prepareStatement(query);
		
	
		pst.setDouble(1, balance);
		pst.setInt(2, userId);
		
		boolean update= (pst.executeUpdate()>0)? true : false;
		pst.close();
		return update;	
	}
	
	finally {}
	
}

}














