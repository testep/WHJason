package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import mvc.Agent;
import mvc.CommandParser;
import mvc.Item;
import mvc.Model;
import mvc.Shelf;
import mvc.SupplierManager;
import mvc.View;

public class Server extends UnicastRemoteObject implements FXServer{
	public static Model M;
	public static View V;
	public boolean result = false;
	public static String layoutFile = "";
	public static int LID ;
	public static DB db;
	public static boolean a = false;
	protected Server() throws RemoteException {
		super();

	}

	public boolean showStage() {
		System.out.println("Creating view..");
		a = true;
		return true;
	}
	
	@Override
	public boolean handshake() throws RemoteException {
		try{
			a = true;
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	@Override
	public void sendButton() throws RemoteException {
		
		V.drawControlPanel();
		
	}
	
	@Override
	public boolean setModel( int workers, int forks, int workerexit, int forkexit) throws RemoteException {
		while(layoutFile.equals("")){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Scanner in = null;
		try{
			in = new Scanner(new File(layoutFile));
			LID = in.nextInt();
			
			db =DB.getDB();
			db.createTable(LID);
			int width = in.nextInt(), height = in.nextInt();
			
			M = new Model(width,height);
			
			
			int SupX = in.nextInt(), SupY = in.nextInt() ,dirSup = in.nextInt();
			M.addAgent(new Agent(3,"supplier","supplier1",SupX,SupY));
			M.Supplier.SX = SupX;
			M.Supplier.SY = SupY;
			M.Supplier.DS = dirSup;
			
			int ExitX = in.nextInt(), ExitY = in.nextInt() , dirExit = in.nextInt();
			M.addAgent(new Agent(4,"exit","exit4",ExitX, ExitY));
			M.Supplier.EX = ExitX;
			M.Supplier.EY = ExitY;
			M.Supplier.DE = dirExit;
			
//			db.insert(new Item("Test1",200));
//			db.insert(new Item("Test2",700));
//			db.insert(new Item("Test3",400));
			// Test shelves
			int N = in.nextInt();
			for(int i = 0 ; i< N; i++){
				M.addObstacleArea(in.nextInt(),in.nextInt(),in.nextInt(),in.nextInt(),in.next());
			}
			
			int packStartX = in.nextInt(), packStartY = in.nextInt(), packdir = in.nextInt();

			for(int i = 0; i< workers ;i++){
				String name = "";
				if(workers==1) name = "human";
				else name = "human"+(i+1);
				Agent worker = new Agent(1,"human",name,packStartX+i,packStartY);
				M.addAgent(worker);

			}
			packStartY+=packdir;
			for(int i = 0; i<forks ;i++){
				String name = "";
				if(forks==1) name = "forklift";
				else name = "forklift"+(i+1);
				Agent worker = new Agent(1,"forklift",name,packStartX+i,packStartY);
				M.addAgent(worker);

			}
			
			int exStartX = in.nextInt(), exStartY = in.nextInt(), exdir = in.nextInt();
			for(int i = 0; i<workerexit ;i++){
				String name = "";
				if(workerexit==1) name = "humanExit";
				else name = "humanExit"+(i+1);
				Agent worker = new Agent(1,"human",name,exStartX+i*exdir,exStartY);
				M.addAgent(worker);
//				System.out.println(worker);
			}
			exStartY+=exdir;
			for(int i = 0; i<forkexit ;i++){
				String name = "";
				if(forkexit==1) name = "forkExit";
				else name = "forkExit"+(i+1);
				Agent worker = new Agent(1,"forklift",name,exStartX+i*exdir,exStartY);
				M.addAgent(worker);

			}
			
			
			V = new View(M);
		}
		catch(Exception e){
			e.printStackTrace();
			in.close();
			return false;
		}
		

		in.close();
		return true;
		
	}

	@Override
	public boolean move(String ag,String location) throws RemoteException {
		Agent act = M.AgList.get(ag);
		double time = act.moveManager.traverse(ag,location ) ;
		if(time<=0)return false;
		try {
			Thread.sleep((long) time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return act.moveManager.AtDest;
	}
	

	@Override
	public boolean getItem(String ag,String item) throws RemoteException {
		Agent act = M.AgList.get(ag);
		act.moveManager.carryItem(item);
		System.out.println(ag+": Obtained item");
		
		return true;
	}

	public boolean dropItem(String ag, String item) {
		Agent act = M.AgList.get(ag);
		act.moveManager.dropItem(item);
		System.out.println(ag+": Dropped item");
		
		return true;
	}

	@Override
	public int addItem(String weight, String item) {
		int queue = M.Supplier.addItem(Double.parseDouble(weight),item);
		return queue;
	}
	public String tryShelves(String item) {
		return M.Supplier.occupyShelf(item);
		
	}


	@Override
	public String invade(String item,String shelf) throws RemoteException {
		return SupplierManager.invade(item,shelf);
	}
	
	public boolean save(){
		PrintWriter state = null;
		try {
			state = new PrintWriter("savestates/state"+LID+"_"+Math.random());
			
			state.println(LID);
			
			for(Shelf sh : M.ShelfList){
				for(Item i : sh.list){
					state.println(sh +" " + i);
				}
			}
			state.println("S");
			for (Item enter: M.Supplier.ItemCollection){
				state.println(enter);
			}
			state.println("D");
			for (Item exit : M.Supplier.DispatchCollection){
				state.println(exit);
			}
			return true;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			state.close();
			return false;
		} finally{
			state.close();
		}
	}

	@Override
	public boolean putBack(String ag) throws RemoteException {
		M.AgList.get(ag).moveManager.putBack();
		return true;
	}

	@Override
	public boolean insert(String name, String weight,String loc) throws RemoteException {
		DB db = DB.getDB();
		name = CommandParser.filter(name);
		weight = CommandParser.filter(weight);
		Item i = new Item(name,Double.parseDouble(weight));
		i.location=loc;
		db.insert(i);
		return true;
	}
	
	
}
