package service;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import dao.*;
import main.Main;
import dbconnection.DbConnection;
import model.Stocks;
import model.User;

public class Service {
	
	
	public void register() throws SQLException  { // user registration
		
		Connection con=DbConnection.getConnection();
		Scanner sc=Main.sc;
		
		String name;
		String email;
		String password;
		UserDao userDao=new UserDao();
		
		System.out.println("===============================");
		System.out.println("Enter Name ");
		name=sc.nextLine();
		System.out.println("Enter Email");
		email=sc.nextLine();
		System.out.println("Enter your Password");
		password= sc.nextLine();
		
		userDao.insert(name,email,password,con);
		
		
		con.close();
		
		
	}

	public User login() throws SQLException { // user login
		
		Connection con=DbConnection.getConnection();
		Scanner sc=Main.sc;
		
		String email;
		String password;
		UserDao userDao=new UserDao();
		System.out.println(" Enter Your Email");
		email=sc.nextLine();
		System.out.println(" Enter Password");
		password=sc.nextLine();
		
		User user= userDao.login(email,password,con);
		con.close();
		
		return user;
		
		
	}
	
	public void addBalance(User loginUser) throws SQLException {
		Connection con=DbConnection.getConnection();
		Scanner sc=Main.sc;
		System.out.println("Enter Amout You Want To Add In Waller");
		double amount=sc.nextDouble();
		UserDao userDao=new UserDao();
		
		if(userDao.updateWallet(loginUser.getId(), loginUser.getBalance()+amount, con)) {
			System.out.println("Amount Add Successfull");
			loginUser.setBalance(loginUser.getBalance()+amount);
		}
		con.close();
	}
	

	public List<Stocks> viewStocks() throws SQLException {   // view all stocks from  stocks table
		
		
		Connection con=DbConnection.getConnection();
		
		
		List<Stocks>st= StocksDao.viewStocks(con);
		con.close();
		return st;
		
	}
	
	

	public void buyStocks(User loginUser) throws SQLException {     // buy stock for user who login

		Connection con=DbConnection.getConnection();
		Scanner sc=Main.sc;
		
		try {
			int stockId;
			int qty;
			
			while(true) {
				try {
				  System.out.println("Enter Stock Id");
				  stockId=sc.nextInt();
				  System.out.println("Enter quantity");
					qty=sc.nextInt();
					
					if(stockId<=0||qty<=0) {
						System.out.println(" Don not enter 0 and negative value");
						continue;
					}
					
					
				  break;
				}
				catch(InputMismatchException e) {
					
					System.out.println("please enter number only in ID an QTY ");
					sc.nextLine();
				}
			
			}
			con.setAutoCommit(false);
			
			StocksDao stocksDao=new StocksDao();
			UserDao userDao=new UserDao();
			PortfolioDao portDao=new PortfolioDao();
			TransactionDao tranDao=new TransactionDao();
			
			boolean stAvailable=stocksDao.stockAvailable(stockId, con);
			
			
			if(!stAvailable) {
				System.out.println("this stock not listed please buy available stocks");
				return;
			}
			
			
			String stockName=stocksDao.getStockName(stockId, con);
			double stPrice=stocksDao.getStockPrice(stockId,con);
			double totalPrice=qty*stPrice;
			
		
			
			
			if(totalPrice<=loginUser.getBalance()) {
				
				boolean wallet=userDao.updateWallet(loginUser.getId(),loginUser.getBalance()-totalPrice,con);
				
				if(!wallet) {
					con.rollback();
					System.out.println("wallet update failed");
					return;
				}
				System.out.println("wallet update");
				
				
				
				boolean exists=portDao.existsStock(loginUser.getId(),stockId,con);
				
				boolean update =false;
				
				if(exists) {
					update=portDao.updateOldPort(stockId,qty,stPrice,loginUser,con);
					System.out.println("update stock success fully");
				}
				
				else {
					
					update=portDao.addPort( stockId, qty, totalPrice,loginUser,con);
					System.out.println("new stock buy successfully");
				}
				if(!update) {
					System.out.println("stock buying failed");
					con.rollback();
					return;
					
				}
				boolean tran;
				tran=tranDao.addTransaction(loginUser.getId(),loginUser.getName(),  stockId,stockName,"BUY ", qty, totalPrice, con);
				
				if(!tran) {
					System.out.println("Transaction Store Failed");
					con.rollback();
					return;
					
				}
				
				con.commit();
				loginUser.setBalance(loginUser.getBalance()-totalPrice);
				
				
				
			}
			
		else {
			
			System.out.println("Not Enough Balance ");
			return;
		}
			
			}
		catch(SQLException s) {
			con.rollback();
			s.printStackTrace();
		}
		finally {
			con.setAutoCommit(true);
			con.close();
		}
		
	}

	public void sellStock(User loginUser) throws SQLException {
		
		Connection con=DbConnection.getConnection();
		Scanner sc=Main.sc;
		try {
		
			int stockId;
			int sellQty;
			while(true) {
				try {
					System.out.println("Enter Your Stock Id Which You Want To Sell");
					 stockId=sc.nextInt();
					
					System.out.println("Enter Quantity ");
					 sellQty=sc.nextInt();
					 
					 if(stockId<=0||sellQty<=0) {
						 System.out.println(" Do not enter 0 or negative value");
						continue;
					 }
					 
					 break;
				}
				catch(InputMismatchException e) {
					
					System.out.println("please enter number only in ID an QTY ");
					sc.nextLine();
				}
			}
			
			
			PortfolioDao pfDao=new PortfolioDao();
			StocksDao stockDao=new StocksDao();
			UserDao userDao=new UserDao();
			TransactionDao traDao=new TransactionDao()	;
			
			
			
			
			String stockName=stockDao.getStockName(stockId, con); 
			double currentPrice=stockDao.getStockPrice(stockId, con);
			double sellingValue=sellQty*currentPrice;
			
			boolean stockExists=pfDao.existsStock(loginUser.getId(), stockId, con);
			int availableQty=pfDao.getQty(loginUser.getId(),stockId,con);
			
			if(stockExists && availableQty>=sellQty) {               //check the stack available in portfolio and enough quantity
				 
				double avgBuyPrice=pfDao.getAvg(loginUser.getId(), stockId, con); 
				con.setAutoCommit(false);
				boolean portUpdate=false;
				boolean wallet=userDao.updateWallet(loginUser.getId(),  loginUser.getBalance()+sellingValue,con);  // update balance in user table
				if(!wallet) {
					System.out.println("wallet update failed  roll backed");
					con.rollback();
					return;
				}
				System.out.println("wallet updated");
				
				
				if(availableQty>sellQty) {                                                     // if stock more than selling qty reduce stock from portfolio 
					
					portUpdate=pfDao.reduceStockQty(stockId,availableQty-sellQty,loginUser,con);
				
				}
				else {
					portUpdate=pfDao.removeStock(stockId,loginUser,con );                       // if equal qty need to sell remove the row from portfolio
				}
				
				if(!portUpdate) {
					con.rollback();
					System.out.println("selling failed due to error");
					return;
				}
				
				boolean tran=traDao.addTransaction(loginUser.getId(),loginUser.getName(),stockId,stockName,"SELL",sellQty,sellingValue,con);       //transaction
				if(!tran) {
					con.rollback();
					 System.out.println("Transaction failed");
					   return;
				}
				
				con.commit();
				loginUser.setBalance(loginUser.getBalance()+sellingValue);
				                // show profit or loss amount
				double pl=(currentPrice-avgBuyPrice)*sellQty;
				if(pl>=0) {
				System.out.println("profit: +₹"+String.format("%.2f", pl));
				}
				else {
					System.out.println("Loss: -₹"+String.format("%.2f", pl));
				}
				
				}
		else {
			System.out.println("stock not available or not enough quantity in your account");
			}
		}
		
		catch(SQLException e) {
			con.rollback();
			System.out.println("error occureed while selling tock");
		}
		finally {
		con.setAutoCommit(true);	
		con.close();
		}
		
	}

	public void viewPortfolio(User loginUser) throws SQLException {
		Connection con=DbConnection.getConnection();
		PortfolioDao portDao=new PortfolioDao();
		portDao.viewPortfolio(loginUser.getId(),con);
		
		con.close();
		
	}

	public void viewTransaction(User loginUser) throws SQLException {
		
		Connection con =DbConnection.getConnection();
		try {
			TransactionDao tranDao=new TransactionDao();
			tranDao.viewTransasction(loginUser.getId(),con);
			
		}
		
		catch(SQLException s){
			
			System.out.println("error ocuured in view transaction");
			s.printStackTrace();
		}
		
		finally {
			con.close();
		}
	}
	
	

	
	
	
	
	
	
	
	
	

}
