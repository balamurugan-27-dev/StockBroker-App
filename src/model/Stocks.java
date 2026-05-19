package model;

public class Stocks {
	private int stockId;
	private String companyName;
	private String symbol;
	private double price;
	
	
	public Stocks( int id,String name,String sym,double pri) {
		
		stockId=id;
		companyName=name;
		symbol=sym;
		price=pri;
		
	}
	public int getStockId() {
		return stockId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public String getSymbol() {
		return symbol;
	}
	public double getPrice() {
		return price;
	} 	
}
