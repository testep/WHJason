package mvc;

import java.io.Serializable;
import java.util.ArrayList;

public class Shelf implements Serializable{
	public ArrayList<Item> list = new ArrayList<Item>();
	public int dir;
	public int x , y;
	public String name;
	public String occupiedFor = "";

	
	public Shelf(int dir, int x, int y,String name) {
		super();
		this.dir = dir;
		this.x = x;
		this.y = y;
		this.name = name;
	}
	
	public String toString(){
		return x + " " + y + " " + occupiedFor;
	}
	
	
}
