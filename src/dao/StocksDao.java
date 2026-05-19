package dao;

import java.sql.*;
import java.util.*;
import model.*;

public class StocksDao {
	
 public static List<Stocks> viewStocks(Connection con) throws SQLException {
	 String query="select * from stocks";
	 Statement st= con.createStatement();
	 ResultSet rs=st.executeQuery(query);
	 List<Stocks> stockList=new  ArrayList<>();
	 while(rs.next()) {
		 Stocks stock= new Stocks(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getDouble(4));
		 stockList.add(stock);
		 
	 }
	
	rs.close();
	st.close();
	 return stockList;
 }
 
 
 public boolean stockAvailable(int stockId,Connection con) throws SQLException {
	 
	 String query="select exists(select 1 from stocks where stock_id="+stockId+ ")from stocks";
	 Statement st=con.createStatement();
	 ResultSet rs=st.executeQuery(query);
	 if(rs.next()) {
		 if(rs.getInt(1)==1) {
			 rs.close();
			 st.close();
			 return true;
		 }
	 }
	 rs.close();
	 st.close();
	 return false;
 }

 

 public double getStockPrice(int stockId,Connection con) throws SQLException {
	 
	 String query="select current_price from stocks where stock_id=?";
	 PreparedStatement pst= con.prepareStatement(query);
	 pst.setInt(1,stockId);
	 ResultSet rs =  pst.executeQuery();
	 if(rs.next()) {
		 double price= rs.getDouble(1);
		 
		 rs.close();
		 pst.close();
		 return price;
	 }
	 else {
	
		 rs.close();
		 pst.close();
		 System.out.println("stock Id is not available");
		 return  0;
	 }
	 
 }
 
 public String getStockName (int id ,Connection con) throws SQLException {
	 
	 String query="select company_name from stocks where stock_id=?";
	 PreparedStatement pst=con.prepareStatement(query);
	 pst.setInt(1,id);
	 ResultSet rs=pst.executeQuery();
	 
	 String name=(rs.next())?rs.getString(1): null;
	
	 rs.close();
	 pst.close();
	 return name;
	 }

 


}







