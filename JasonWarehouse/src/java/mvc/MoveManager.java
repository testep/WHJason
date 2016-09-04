package mvc;	

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import server.Server;

public class MoveManager {
	
	public boolean AtDest = false;
	public boolean finishedTraversal = false;
	public boolean[][] visited ;
	public double speed = 200;
	
	public Group node = null;
	public HBox status = null;
	public ImageView statusImage = null;
	public Text statusName = new Text();
	public Text statusText = new Text();
	public Text idleText = new Text();
	public SequentialTransition Ani = new SequentialTransition();
	public boolean carrying;
	public Item holding;
	public int x , y;
	public String type = "enter";
	public double time = 0;
	public String name;
	public double idle = 0;
	public double still = 0;
	public double total = 0;
	
	public MoveManager(int x, int y, String name) {

		this.x = x;
		this.y = y;
		if(name.contains("human")) speed = 70;
		else speed = 140;
		if(name.contains("Exit")) type = "exit";	
		else type = "enter";
		this.name = name;
		carrying = false;
		AtDest = false;
		visited = new boolean[(int) Server.M.width+1][(int) Server.M.height+1];
		resetVisited();
//		status.setFont(new Font("Times New Roman",10));
		
	}
	
	public Group getNode() {
		return node;
	}

	public void setNode(Group node) {
		this.node = node;
	}
	
	private void addMove(String path) {
		boolean anItem = this.holding != null ? true : false;
		time += path.length() * speed;
		idle += System.currentTimeMillis() - still;
		addTotal();
		for(int i = 0 ; i < path.length() ; i++){
			ParallelTransition a = new ParallelTransition();
			a.setOnFinished(e->{
				still = System.currentTimeMillis();
			});
			TranslateTransition human = new TranslateTransition();
			human.setInterpolator(Interpolator.LINEAR);
			human.setNode(node);
			human.setDuration(Duration.millis(speed));
			TranslateTransition item = null;
			if(anItem){
				item = new TranslateTransition();
				item.setNode(holding);
				item.setDuration(Duration.millis(speed));
				item.setInterpolator(Interpolator.LINEAR);
			}
			 
			switch (path.charAt(i)) {
			
			case 'u':
				human.setByY(0 - Server.V.BlockH); a.getChildren().add(human);
				if(anItem){
					item.setByY(0 - Server.V.BlockH); a.getChildren().add(item);
				}
				break;
			case 'd':
				human.setByY(Server.V.BlockH); a.getChildren().add(human);
				if(anItem){
					item.setByY(Server.V.BlockH); a.getChildren().add(item);
				}
				break;
			case 'l':
				human.setByX(0 - Server.V.BlockW);
				a.getChildren().add(human);
				if(anItem){
					item.setByX(0 - Server.V.BlockW); a.getChildren().add(item);
				}
				break;
			case 'r':
				human.setByX(Server.V.BlockW);a.getChildren().add(human);
				if(anItem){
					item.setByX(Server.V.BlockW); a.getChildren().add(item);
				}
				break;
			default:
				System.exit(1);
			}
			
			Ani.getChildren().add(a);

		}
		
	}

	private void addTotal() {
		total = idle + time;
		Server.V.addTime(total, idle);
	}

	public synchronized double traverse(String ag, String location) {
		Ani.getChildren().clear();
		if(location.equals("supplier")){
			if(Server.M.Supplier.ItemCollection.isEmpty())
				return -1;
		}
		finishedTraversal = false;
		AtDest = false;
		int[] coo = CommandParser.locationParser(ag,location);
		
		if(visited[coo[0]][coo[1]]){
			System.out.println("Space Occupied");
			return 0;
		}
		int Destx = coo[0], Desty = coo[1];
		Ani.setOnFinished(e->{
			still = System.currentTimeMillis();
//			addTotal();
			finishedTraversal = true;
		});
		
		AStar a = new AStar((int)(Server.M.height*Server.M.width));
		String path = a.path(ag,x,y, Destx, Desty);
//		System.out.println("Path of "+ ag+":"+path);
		statusText.setText(": moving to "+coo[0] + " " + coo[1]);
		idleText.setText(toPrint(idle));
		addMove(path);
		Ani.play();
		
		return path.length()*speed;
	}
	
	public void resetVisited(){
		for(int i = 1; i <= Server.M.width; i++){
			for(int j = 1; j <= Server.M.height; j++){
				visited[i][j] = false;
			}
		}
		for(Shelf sh : Server.M.ShelfList){
			visited[sh.x][sh.y] = true;
		}
	}
	private String toPrint(double Urate){
		if(Urate==0)return "0";
		Urate /= 1000;
		String rate = Urate +"";
//		System.out.println("RATE"+rate);
		int s = rate.indexOf(".");
		String per = rate.substring(0,s+2);
		return per;
	}
	
	public void carryItem(String item) {
		item = CommandParser.filter(item);
		idle += System.currentTimeMillis()-still;
		
		System.out.println(name+" obtaining item:"+item);
		
		if(type.equals("exit")){
			carrying = true;
			int coo[] = CommandParser.shelfParser(item);
			int row = coo[2];
			statusText.setText("carrying " + item);
			idleText.setText(toPrint(idle));
			TranslateTransition carry = new TranslateTransition();
			holding = Server.M.AgMap[coo[0]][coo[1]].list.remove(Server.M.AgMap[coo[0]][coo[1]].list.size()-1);
			carry.setNode(holding);
			holding.agent.setFill(Color.GOLD);
			holding.location = "Shelf " + coo[0] + " " + coo[1];
			holding.statusLocation.setText(holding.location );
			carry.setDuration(Duration.millis(500));
			carry.setByX(row*Server.V.BlockW);
			carry.play();
			carry.setOnFinished(e->{
				still = System.currentTimeMillis();
				statusText.setText("Idle");
				idleText.setText(toPrint(idle) );
				holding.statusLocation.setText(" carried by " + name);
			});

		}
		else{
			carrying = true;
			statusText.setText("carrying " +item +" " + toPrint(idle));
			idleText.setText(toPrint(idle));
			TranslateTransition carry = new TranslateTransition();
	//		holding = SupplierManager.ItemCollection.get(SupplierManager.space[reservation]);
			int queue = 0;
			while(Server.M.Supplier.ItemCollection.get(queue).obtained){
				queue++;
			}
			Server.M.Supplier.ItemCollection.get(queue).obtained = true;
			holding = Server.M.Supplier.ItemCollection.remove(queue);
			holding.stopTime();
			carry.setNode(holding);
			holding.agent.setFill(Color.GOLD);
			holding.location = name;
			carry.setDuration(Duration.millis(500));
			carry.setByY(-Server.V.BlockH * Server.M.Supplier.DS);
			carry.play();
			carry.setOnFinished(e->{
				still = System.currentTimeMillis();
				statusText.setText("Idle");
				idleText.setText(toPrint(idle) );
				holding.statusLocation.setText(" carried by " + name);
			});

		}
		try {
			Thread.sleep(500);
		} catch (Exception e) {

			e.printStackTrace();
		}
		time += 500;
		addTotal();
	}
	
	public void dropItem(String item) {
		item = CommandParser.filter(item);
		idle += System.currentTimeMillis() - still;
		addTotal();
		int coo[] = CommandParser.shelfParser(item);
		
		if(type.equals("exit")){
			TranslateTransition carry = new TranslateTransition();
			carry.setNode(holding);
			carry.setDuration(Duration.millis(1000));
			carry.setByY(-Server.V.BlockH*4);
			carry.play();
			
			
			FadeTransition remove = new FadeTransition();
			remove.setNode(holding);
			remove.setDuration(Duration.millis(1000));
			remove.setToValue(0);
			TranslateTransition Out = new TranslateTransition();
			Out.setNode(holding);
			Out.setDuration(Duration.millis(2000));
			Out.setByY(-Server.V.BlockH);
			ParallelTransition Remove = new ParallelTransition(Out,remove);
			
			carry.setOnFinished(e->{
					still = System.currentTimeMillis();
					Server.M.Supplier.DispatchCollection.add(holding);
					holding.toFront();
					statusText.setText("Idle");
					idleText.setText(toPrint(idle) );
					holding = null;
					carrying = false;
					Remove.play();
				});
		}
		else {
			
			int row = -1*coo[2]; 
			TranslateTransition carry = new TranslateTransition();
			carry.setNode(holding);
			
			statusText.setText(": dropping " + holding.name );
			idleText.setText(toPrint(idle));
			carry.setDuration(Duration.millis(500));
			carry.setByX(row * Server.V.BlockH + Server.M.AgMap[coo[0]][coo[1]].list.size()*Server.V.BlockH/6*(-row));
			carry.play();
			carry.setOnFinished(e->{
					still = System.currentTimeMillis();
					Server.M.AgMap[coo[0]][coo[1]].list.add(holding);
					holding.toFront();
					statusText.setText("Idle");
					idleText.setText(toPrint(idle) );
					holding = null;
					carrying = false;
		           
				});
		}
		try {
			
			Thread.sleep(500);
		} catch (Exception e) {

			e.printStackTrace();
		}
		time+=2000;
		addTotal();
	}
	public void updateLocation(int x , int y){

		this.x = x;
		this.y = y;
		
	}

	public void putBack() {
		if(holding!=null) {
			Server.M.Supplier.ItemCollection.add(holding);
			holding = null;
		}
		
	}
}
