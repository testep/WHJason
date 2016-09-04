package mvc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.Node;
import server.*;


public class Model extends World implements Serializable{
	
	public HashMap<String,Agent> AgList ;
	public ArrayList<Shelf> ShelfList;
	public static double height = 30;		// Height and width of the grid (in cells)
	public static double width  = 30;
	
	public Shelf[][] AgMap;
	public Agent AgHuman,AgHumanExit;
	public SupplierManager Supplier;
	public Agent AgFork,AgForkExit;
	
	public boolean runAnimations = true;
	public Model() {
		super(height * BlockH  , width * BlockW);
		AgList = new HashMap<String,Agent>();
	}
	
	/* Create a h by w field */
	public Model( int h, int w) {
		super(h * BlockH  , w * BlockW);	// Create 'World' with height and width
		height = h;
		width = w;
		AgList = new HashMap<String,Agent>();
		ShelfList = new ArrayList<Shelf>();
		AgMap = new Shelf[h+1][w+1];
		Supplier = new SupplierManager();
		
	}

	public Model(int w, int h, int ags) {
		super(height * BlockH  , width * BlockW);
		AgList = new HashMap<String,Agent>();
		
	}
	
	public void addAgent(Agent a){
		try{
			if(a.x > width || a.y > height || a.x<=0 || a.y <= 0)
				throw new Exception();
			AgList.put(a.name, a);


		}
		catch(Exception e){
			System.out.println(a.getType() + " has been initialized out of bounds.");
		}
		
	}
	
	public void addShelf(int dir,int x , int y,String name){
		try{
			if( x > width || y > height || x <= 0 || y <= 0)
				throw new Exception("Shelf out of bounds");
			ShelfList.add(new Shelf(dir,x,y,name));
			AgMap[x][y] = new Shelf(dir,x,y,name);

		}
		catch(Exception e){
			e.toString();
		}
		
	}
	
	public void addObstacleArea(int UX, int UY, int LX, int LY,String name){
		try{
			if(UX > width || UX <= 0 || UY <= 0 || LX <= 0 || LY <= 0 )
				throw new Exception("Obstacle out of bounds");
			if( LX > width ){
				LX = (int) (width - 6);
			}
			if(LY > height ){
				LY = (int) (height - 2);
			}
			if(UY>height) {
				UY = (int) (height - 2);
			}
			for(int i = LX; i <= LX + 1 ; i++){
				for(int j = UY ; j <= LY ; j ++){
					int dir = (i == LX) ? -1 : 1;
					ShelfList.add(new Shelf(dir,i,j,name));
					AgMap[i][j] = new Shelf(dir,i,j,name);
				}
			}
		}
		catch(Exception e){
			e.toString();
		}
		
	}
	
	public void addAgents(Agent...a){
		for(Agent ag : a){
			try{
				if(ag.x>width || ag.y > height || ag.x<=0 || ag.y <=0)
					throw new Exception();
				addAgent(ag);
			}
			catch(Exception e){
				System.out.println(ag.getType() + " has been initialized out of bounds.");
			}
		}
	}
	
	public Agent getAgent(int index){
		return AgList.get(index);
	}
}