package mvc;

import java.io.Serializable;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import server.Server;

public class Item extends Group implements Serializable{
	public static int ID = 0;
	public int id;
	public String name;
	public double weight;
	public String location;
	public boolean obtained = false;
	public double time;
	public double timeafter;
	public Rectangle figure = new Rectangle();			// The item image
	public Rectangle agent = new Rectangle();			// The agent carrying this item
	public HBox status=new HBox();
	public Text statusName=new Text();
	public Text statusLocation=new Text();
	public Text statusIdle=new Text();
	public double vert = 0;
	
	public Item(String name,double w) {
		super();
		this.id = ID++;
		this.weight = w;
		this.name = name;
		this.location = "supplier";
		figure.setHeight(View.BlockH - 15);
		figure.setWidth(View.BlockW/3);
		int red = (int)(Math.random()*255);
		int green= (int)(Math.random()*255);
		int blue = (int)(Math.random()*255);
		figure.setFill(Color.rgb(red, green, blue));
		figure.toFront();
		agent.toFront();
		agent.setHeight(View.BlockH/8);
		agent.setWidth(View.BlockW/4);
		agent.setFill(Color.rgb(red, green, blue));
		this.getChildren().addAll(figure,agent);

	}
	
	
	
	@Override
	public String toString() {
		return id + " " + name + " " +weight + " " + time;
	}



	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public boolean isObtained() {
		return obtained;
	}
	public void setObtained(boolean obtained) {
		this.obtained = obtained;
	}
	public void toFront() {
		this.figure.toFront();
		this.agent.toFront();
	}
	
	public void setX(double x){
		this.agent.setX(x);
		this.figure.setX(x);
	}
	
	public void setY(double x){
		this.agent.setY(x+View.BlockH/4);
		this.figure.setY(x);
	}
	
	public void startTime(){
		time = (double)System.currentTimeMillis();
	}
	public void stopTime(){
		long t = System.currentTimeMillis();
		time = (double)(t - time)/1000;
		System.out.printf("Time in supply area:%.3f sec\n",time);
		statusIdle.setText(time+" s");
		Server.V.addSum((double)time);
	}



	public void setColor(Color C) {
		figure.setFill(C);
		agent.setFill(C);
			
	}
	public Color getColor() {
		return (Color) figure.getFill();
		
	}



	public void aniText(double d) {
		
//		status.setVisible(true);
		status.setTranslateX(d);
		ParallelTransition p = new ParallelTransition();
		TranslateTransition t = new TranslateTransition(Duration.millis(1000));
		t.setNode(status);
		t.setByX(-d);
		FadeTransition f = new FadeTransition(Duration.millis(1000));
		f.setNode(status);
		f.setFromValue(0);
		f.setToValue(1);
		p.getChildren().addAll(t,f);
		p.play();
	}



	public void setFill(Paint fill) {
		statusName.setFill(fill);
		statusIdle.setFill(fill);
		statusLocation.setFill(fill);
		
	}
	
}
