package server;

import java.sql.*;

import mvc.CommandParser;
import mvc.Item;

public class DB {
	private static DB db;
	public String table;
	private DB() {
		   
	}
	private static Connection getDBConnection() throws Exception {
		Connection con = null;
		Class.forName("org.sqlite.JDBC");
		con = DriverManager.getConnection("jdbc:sqlite:DB.db");
		return con;
	}
	
	public static DB getDB(){
		if(db==null){
			db = new DB();
		}

		return db;
	}
	public void createTable(int id){
		 table = "items";
		 Statement stmt = null;
		 Connection con = null;
		 try{
			 con = DB.getDBConnection();
			 stmt = con.createStatement();
			 
		      String sql = "CREATE TABLE  " + table +
		                   "(ID INT PRIMARY KEY     NOT NULL," +
		                   " NAME           TEXT    NOT NULL, " + 
		                   " WEIGHT            INT     NOT NULL, " + 
		                   " location)"; 
		      stmt.executeUpdate(sql);
		      stmt.close();
		 }catch(Exception e){
			 try {
		    		String sql = "DELETE from "+table;
		    		stmt.executeUpdate(sql);
					stmt.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		 }
	}
	public Item getRandomItem(){
		Item i = null;
		Statement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		try{
			con = this.getDBConnection();
			 stmt = con.createStatement();
			 rs = stmt.executeQuery( "SELECT * FROM items ORDER BY RANDOM() LIMIT 1;" );
	      while ( rs.next() ) {
	         int id = rs.getInt("id");
	         String  name = rs.getString("name");
	         double weight = rs.getDouble("weight");  
	         i = new Item(name,weight);
	         i.id=id;
	         i.location = rs.getString("location");
	         rs.close();
		     stmt.close();
	         this.delete(id);
	         
	      }
	      
	      
	      
	      return i;
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public void insert(Item item) {
		Connection con = null;
		Statement stmt = null; 
		try{
			
			con = this.getDBConnection();
			 stmt = con.createStatement();
			 int id = item.id;
			 String name = item.name;
			 double weight = item.weight;
			 String sql = "INSERT INTO items(ID,NAME,WEIGHT,location) " +
	                   "VALUES ("+id+",'"+ name + "',"+ weight + ","+item.location+" );"; 
			 stmt.executeUpdate(sql);
		     
		      stmt.close();
		      System.out.println("Item added in DB");
			} catch (Exception e){
				e.printStackTrace();
			}
		
	}
	public void delete(int id) {
		Connection con = null;
		Statement stmt = null; 
		try{
			
			 con = this.getDBConnection();
			 stmt = con.createStatement();
			 String sql = "DELETE from items where ID="+id; 
			 stmt.executeUpdate(sql);
		     
		      stmt.close();
			} catch (Exception e){
				e.printStackTrace();
			}
	}
	
}
