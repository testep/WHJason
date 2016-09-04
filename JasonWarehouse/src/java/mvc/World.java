package mvc;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class World  {

	private Scene stage ;
	public double HEIGHT = 700;
	public double WIDTH  = 500;
	public int    PADDER = 200;				// Value to pad the screen
	
	public static double    BlockH  = 35;	   // Size of the grid cell
	public static double    BlockW  = 35;	   
	
	public World(){
		super();
	}
	
	public World(double height, double width) {
		super();
		
		HEIGHT = height;
		WIDTH = width;
		
	}
	public World(String title ,double hEIGHT, double width) {
		super();
		HEIGHT = hEIGHT;
		WIDTH = width;
	}
	
	public void setScene(Parent S){

		stage = new Scene(S,HEIGHT+PADDER + 450,WIDTH+PADDER);
		stage.getStylesheets().add("mvc/styles.css");
		
	}
	
	public Scene getScene(){
		return stage;
	}
	
	
	public double getBlockH() {
		return BlockH;
	}
	public void setBlockH(int bLOCK) {
		BlockH = bLOCK;
	}
	
	public double getBlockW() {
		return BlockW;
	}
	public void setBlockW(int bLOCK) {
		BlockW = bLOCK;
	}
}
