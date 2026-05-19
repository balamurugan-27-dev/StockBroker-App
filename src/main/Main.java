package main;
import java.sql.*;

import java.util.List;
import java.util.Scanner;

import service.Service;
import model.*;
public class Main {
	public  static Scanner sc=new Scanner(System.in);

	public static void main(String[] args) throws SQLException {
		
		System.out.println("===========================================================");
		System.out.printf(   "         WELCOME TO  BALAMURUGAN STOCK BROKER  \n");
		System.out.println("===========================================================");
		System.out.println(" Press 1 for 'REGISTER' \n press 2 for 'LogIn' \n press 3 for 'Exit' ");
		int input; 
		
		
		Service service=new Service();
		User loginUser;
		
		input=sc.nextInt();
		sc.nextLine();
		
		switch(input) {
		case 1: service.register();
		break;
		case 2: { loginUser=service.login();
				if(loginUser!=null) {
					userMenu(loginUser);
					}
				
				else {
				System.out.println("login failed please Enter Correct User Name And Password ");
					}
				}
		break;
		case 3: System.out.println("THANK YOU ");
		break;
		
		default: System.out.println("please enter valid input");
		      
		
		}
		
		
		
		
		
	}

	private static void userMenu(User loginUser) throws SQLException {
		
		boolean active =true;
		Service service =new Service();
		
		while(active) {
			
			System.out.println("========= "+loginUser.getName().toUpperCase() +"  User Menu"+"===========");
			System.out.println(" Wallet Balance : ₹"+loginUser.getBalance());
			System.out.println(" press 1 View All Stocks");
			System.out.println(" press 2 Buy Stacks");;
			System.out.println(" press 3 sell Stacks");
			System.out.println(" press 4 View Portfolio");
			System.out.println(" press 5 View Transaction History");
			System.out.println(" press 6 add amount in wallet");
			System.out.println(" press 7 Exit");
			
			int input =sc.nextInt();
			sc.nextLine();
			switch(input) {
				case 1: 
					{
						List<Stocks> stlist=service.viewStocks();
						if(stlist==null) {
							System.out.println("no stacks available");
						}
						for(Stocks l:stlist) {
							
							System.out.println(l.getStockId() +"  " +l.getCompanyName()+"  " + l.getSymbol()+"  "+ l.getPrice()+"\n");
							
						}
						
					}
				break;
				case 2:service.buyStocks(loginUser);
				break;
				case 3:service.sellStock(loginUser);
				break;
				case 4:service.viewPortfolio(loginUser );
				break;
				case 5:service.viewTransaction(loginUser);
				break;
				case 6:service.addBalance(loginUser);
				break;
				case 7: System.out.println("THANK YOU "+loginUser.getName().toUpperCase()); active=false;
				break;
				
				default:System.out.println("please enter valid input");
				
				}
			
		}
		
	}

}
