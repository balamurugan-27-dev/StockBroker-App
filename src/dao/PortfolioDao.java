package dao;

import model.User;

import java.sql.*;

public class PortfolioDao {

	public boolean existsStock(int userId, int stockId, Connection con) throws SQLException {
		
		String query= "select Exists(select 1 from portfolio where user_id=? and stock_id=?) from portfolio";
		PreparedStatement pst=con.prepareStatement(query);
		pst.setInt(1, userId);
		pst.setInt(2,stockId);
		ResultSet rs=pst.executeQuery();
		if(rs.next()) {
			if(rs.getInt(1)==1) {
			
			rs.close();
			pst.close();
			return true;
			}
		}
		rs.close();
		pst.close();
		return false;
		
				
		
	}
	
	public int getQty(int userId, int stockId,Connection con) throws SQLException {
			
			String query="select quantity from portfolio where stock_id=? and user_id =?";
			PreparedStatement pst=con.prepareStatement(query);
			pst.setInt(1,stockId);
			pst.setInt(2, userId);
			ResultSet rs=pst.executeQuery();
			if(rs.next()) {
				int qty=rs.getInt(1);
				rs.close();
				pst.close();
				if(qty!=0) {
					return qty;
				}
			}
			System.out.println("no stock in your portfolio");
			return 0;
		}
	
	public double getAvg(int userId,int stockId,Connection con) throws SQLException {
		String query="select avg_buying_price from portfolio where user_id=? and stock_id=?";
		PreparedStatement pst=con.prepareStatement(query);
		pst.setInt(1,userId);
		pst.setInt(2, stockId);
		
		ResultSet rs=pst.executeQuery();
		
		rs.next();
		
		double avg=rs.getDouble(1);
		rs.close();
		pst.close();
		
		return avg;
		
		
	}
	

	public boolean updateOldPort(int stockId, int qty, double stPrice, User loginUser, Connection con) throws SQLException {
		String query="select quantity,avg_buying_price from portfolio where stock_id=? and user_id=? ";
		PreparedStatement pst=con.prepareStatement(query);
		pst.setInt(1, stockId);
		pst.setInt(2, loginUser.getId());
		ResultSet rs=pst.executeQuery(); // get old values for calculate average
	
		rs.next();
		int old_qty =rs.getInt(1);
		double old_price=rs.getDouble(2);
		int new_qty=old_qty+qty;
		double current_price=((old_qty*old_price)+(qty*stPrice))/ new_qty;
		
		rs.close();
		pst.close();
		String query2="update portfolio set quantity=?,avg_buying_price=? where user_id=? and stock_id=?";
		PreparedStatement pst2=con.prepareStatement(query2);
		pst2.setInt(1, new_qty);
		pst2.setDouble(2, current_price);
		pst2.setInt(3, loginUser.getId());
		pst2.setInt(4,stockId);
		boolean update= (pst2.executeUpdate()==1)? true :false;
		
		pst2.close();
		
		return update;
		
	
		
	}

	public boolean addPort(int stockId, int qty, double totalPrice,User loginUser ,Connection con) throws SQLException {
		
		StocksDao stockDao=new StocksDao();
		String stockName=stockDao.getStockName(stockId, con);
		
		double avg_price=totalPrice/qty;
		
		String query1="insert into portfolio(user_id,user_name,quantity,avg_buying_price,stock_id,stock_name)  values(?,?,?,?,?,?)";
		PreparedStatement pst=con.prepareStatement(query1);
		pst.setInt(1,loginUser.getId());
		pst.setString(2, loginUser.getName());
		pst.setInt(3, qty);
		pst.setDouble(4,avg_price);
		pst.setInt(5,stockId);
		pst.setString(6, stockName);
		
		boolean add=(pst.executeUpdate()==1)? true:false;
		pst.close();
		return add;
		
	}

	
	public boolean reduceStockQty(int stockId, int setQty, User loginUser, Connection con) throws SQLException {
		
		String query="update portfolio set quantity =? where stock_id=? and user_id=? ";
		PreparedStatement pst=con.prepareStatement(query);
		pst.setInt(1, setQty);
		pst.setInt(2, stockId);
		pst.setInt(3, loginUser.getId());
		int update=pst.executeUpdate();
		pst.close();
		return(update==1)?true:false;
	}

	public boolean removeStock(int stockId, User loginUser, Connection con) throws SQLException {
		
		String query="delete from portfolio where user_id=? and stock_id =? ";
		PreparedStatement pst=con.prepareStatement(query);
		pst.setInt(1, loginUser.getId());
		pst.setInt(2,stockId);
		int update=pst.executeUpdate();
		pst.close();
		return (update==1)?true:false;
	}

	public void viewPortfolio(int userId,Connection con) throws SQLException {
		
		String Query="select pofo_id , user_id , user_name, stock_id , stock_name , quantity ,avg_buying_price from  portfolio where user_id="+ userId;
		
		Statement st=con.createStatement();
		ResultSet rs=st.executeQuery(Query);
		
		System.out.printf("%-10s%-10s%-10s %-10s%-10s %-10s%-10s\n \n","portId"," user_id ","user_name" ,"stock_id","stock_name"," quantity","avg_buying_price");
		while(rs.next()) {
			System.out.printf("%-10d %-10d%-10s %-10d%-10s  %-10d %-10.2f\n",rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getInt(4),rs.getString(5),rs.getInt(6),rs.getDouble(7));
		}
		rs.close();
		st.close();
	}

}
