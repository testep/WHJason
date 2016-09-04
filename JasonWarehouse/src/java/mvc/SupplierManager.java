package mvc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import server.Server;

public class SupplierManager {
	public int SX , SY , EX , EY, DS,DE;
	public  ArrayList<Item> ItemCollection = new ArrayList<Item>();
	public  ArrayList<Item> DispatchCollection = new ArrayList<Item>();
	public  ArrayList<Item> Incoming = new ArrayList<Item>();
	public  ArrayList<Item> Queue1 = new ArrayList<Item>();
	public  ArrayList<Item> Queue2 = new ArrayList<Item>();
	public  ArrayList<Item> Queue3 = new ArrayList<Item>();
	public  ArrayList<Item> Queue4 = new ArrayList<Item>();
	
	public static HashMap<String,Item> itemDB = new HashMap<String,Item>();
	public static int[] space = new int[2];
	public static int pointer = 0;
	
	public SupplierManager() {
		super();
		ItemCollection = new ArrayList<Item>();
		DispatchCollection = new ArrayList<Item>();
		Incoming = new ArrayList<Item>();
		Queue1 = new ArrayList<Item>();
		Queue2 = new ArrayList<Item>();
		Queue3 = new ArrayList<Item>();
		Queue4 = new ArrayList<Item>();
		itemDB = new HashMap<String,Item>();
	}

	public int addItem(Double weight , String item) {
			int queue ;
			Item newItem =  Incoming.remove(0);
			newItem.figure.setFill(itemDB.get(newItem.name).figure.getFill());
//			System.out.println("SUPPLIER"+newItem.name);
//				newItem.setX(7 * Server.V.BlockW);
//				newItem.setY(Server.V.HEIGHT+30);
			
			queue = ItemCollection.size()%4;
			Platform.runLater(new Runnable(){
				public void run(){	
				newItem.statusName = new Text(newItem.name +":");
				newItem.statusLocation = new Text(newItem.location);
				newItem.setFill(newItem.figure.getFill());
				newItem.status.getChildren().addAll(newItem.statusName,newItem.statusLocation,newItem.statusIdle);
				newItem.status.setSpacing(3);
				Server.V.itemList.getChildren().add(newItem.status);
				newItem.aniText(30);
				
				
				SequentialTransition add = new SequentialTransition();
				
//				TranslateTransition horizontal = new TranslateTransition();
				newItem.setVisible(true);
				
//				horizontal.setByX((-4*Server.V.BlockW));
//				horizontal.setDuration(Duration.millis(1));
//				horizontal.setNode(newItem);
				
				TranslateTransition vert = new TranslateTransition();
				double vertical = -60*DS;//+ItemCollection.size()*(Server.V.BlockH)/5;
				vert.setByY(vertical);
				vert.setDuration(Duration.millis(1000));
				vert.setNode(newItem);
				newItem.vert = vertical;
				add.getChildren().addAll(vert);
				newItem.startTime();
				add.play();
				add.setOnFinished(e->{
					ItemCollection.add(newItem);
				});
			}
		});
		
	
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		return queue +1;
	}

	public void drawAllItems(){
		System.out.println("Queueing items...");
		Scanner in = null; 
		try {
//			Thread.sleep(500);
			
			in = new Scanner(new File("in/items.txt"));
			int N = in.nextInt();
			for (int i = 0; i < N ; i++) {
				double weight = in.nextDouble();
				String name  = in.next() ;
				double time=in.nextDouble();
//				System.out.println("NAME:"+name+" WEIGHT:"+weight+" TIME:"+time);
				Item item = new Item(name.trim(),weight);
				Incoming.add(item);
				itemDB.put(name, item);
				
			}
		} catch (Exception e) {

			e.printStackTrace();
		}finally {
			in.close();
		}
		Platform.runLater(new Runnable(){
			public void run(){
				for(Item i : Incoming){
					if(!itemDB.containsKey(i.name)){
							itemDB.put(i.name, i);
//							System.out.println("NAME OF ITEM " + itemDB.get(i.name).name);
						
					}else{
						i.setColor(itemDB.get(i.name).getColor());
					}
//					Text q= new Text("New Items");
//					q.setY(Server.V.HEIGHT + 20);
//					q.setX(Server.M.width/2 * Server.V.BlockH);
					i.setY(60*DS + (Server.M.height - SY)*Server.V.BlockH);
					i.setX(SX*Server.V.BlockW - Server.V.BlockW/2.0 );
					i.setVisible(false);
					Server.V.Field.getChildren().add(i);
				}
				
			}
		});
	}

	public String occupyShelf(String item) {
		item = CommandParser.filter(item);
		System.out.println("OCCUPY:"+item);
		Shelf picked = findVacant(item) , pickedUp = null, pickedDown = null;
		
		if(picked == null)return "";

		int x = picked.x, y = picked.y;
		Server.M.AgMap[x][y].occupiedFor = item;
		
		
//		System.out.println(red+" " +green+" " + blue);
		Server.V.shelves[picked.x][picked.y].setFill(((Color) itemDB.get(item).figure.getFill()).brighter());
		
		
		String toreturn = x+","+y;
		
		if(Server.M.AgMap[x][y+1] != null) {
			if(Server.M.AgMap[x][y+1].occupiedFor.equals("item") || !Server.M.AgMap[x][y+1].occupiedFor.equals("")) pickedUp = null;
			else {
				pickedUp = Server.M.AgMap[x][y+1];
				Server.V.shelves[pickedUp.x][pickedUp.y].setFill(((Color) itemDB.get(item).figure.getFill()).brighter());
				Server.M.AgMap[x][y+1].occupiedFor = item;
				toreturn+=","+(y+1);
			}
			
		}
		if(Server.M.AgMap[x][y-1] != null) {
			if(Server.M.AgMap[x][y-1].occupiedFor.equals("item") || !Server.M.AgMap[x][y-1].occupiedFor.equals("") ) pickedDown = null;
			else {
				pickedDown = Server.M.AgMap[x][y-1];
				Server.V.shelves[pickedDown.x][pickedDown.y].setFill(((Color) itemDB.get(item).figure.getFill()).brighter());
				Server.M.AgMap[x][y-1].occupiedFor = item;
				toreturn+=","+(y-1);
			}
		}
		
		return toreturn;
		
	}
	public static Shelf findVacant(String item){
		item = CommandParser.filter(item);
		int randomshelf = (int)((-1+Server.M.ShelfList.size())*(Math.random()));
//		System.out.println("size:"+ randomshelf);
		Shelf random = Server.M.ShelfList.get(randomshelf);
		int count = 0;
		while((!random.occupiedFor.equals(item) || random.occupiedFor.equals("") ) && count < 100){
			random = Server.M.ShelfList.get((int)((-1+Server.M.ShelfList.size())*(Math.random())));
			count ++;
		}
		if(count > 100) return null;
		return random;
	}

	public static String invade(String item, String shelf) {
		item = CommandParser.filter(item);
		int[] coo = CommandParser.shelfParser(shelf);
		item.trim();
		int x = coo[0] , y = coo[1]; 
		Shelf pickedUp = null, pickedDown=null;
		String toreturn = x+"";
		Color c = (Color) Server.V.shelves[x][y].getFill();
		
		if(Server.M.AgMap[x][y+1] != null) {
			if(Server.M.AgMap[x][y+1].occupiedFor.equals("")) {
				pickedUp = Server.M.AgMap[x][y+1];
				Server.V.shelves[pickedUp.x][pickedUp.y].setFill(((Color) Server.V.shelves[x][y].getFill()));
				Server.M.AgMap[x][y+1].occupiedFor = item;
				toreturn+=","+(y+1);
			}
			
		}
		if(Server.M.AgMap[x][y-1] != null) {
			if(Server.M.AgMap[x][y-1].occupiedFor.equals("")) {
				pickedDown = Server.M.AgMap[x][y-1];
				Server.V.shelves[pickedDown.x][pickedDown.y].setFill(((Color) Server.V.shelves[x][y].getFill()));
				Server.M.AgMap[x][y-1].occupiedFor = item;
				toreturn+=","+(y-1);
			}
			
		}
		return toreturn;
	}
	public  String randomItem(){
		System.out.println("Supplier:" + itemDB.size());
		int count = (int)(itemDB.size() * Math.random());
		System.out.println(itemDB.size());
		Item rand = null;
		int i = 0;
		for(Map.Entry<String, Item> a : itemDB.entrySet()){
			if(i++==count) rand = a.getValue(); 
			}
		return rand.name;
	}
}
