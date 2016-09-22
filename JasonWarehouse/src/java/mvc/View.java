package mvc;

import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class View extends World {
	public Scene S ;
	public Group View;
	
	public Group Field = new Group();
	public Group ControlPanel = new Group();
	public VBox itemList = new VBox();
	public Model Environment;
	
	public Group Analysis = new Group();
	public Text ItemAVG = new Text();
	public Text AgentUR= new Text();
	
	public Group Supplier;

	public Rectangle[][] shelves; 
	Rectangle newDest  = new Rectangle(); 
	private int adjuster = (int) (BlockW * 0.2);
	public Font font = new Font("Arial",adjuster);
	private int dist = 50;
	public String itAvg = "Average time in supply : ";
	public String utRate = "Average idle time of Agents: ";
	
	public double sum = 0;
	public int itemCount = 0;
	
	public double totalidle = 0;
	public double total = 0;
	public double start;
	public View(Model M){
		super();
		start = System.currentTimeMillis();
		System.out.println("Creating View...");
		Environment = M;
		BlockH *= 0.7;
		BlockW *=0.7;
		HEIGHT = M.height*BlockH ;
		WIDTH = M.width*BlockW;
		shelves = new Rectangle[(int) M.width][(int) M.height];
		draw();
	}

	/* Starts the visualisation*/
	public void draw(){
		drawShelves();
		System.out.println("Created shelves");
		drawBorders();
		
		drawLines();
		drawAgents();
		System.out.println("Created Agents");
		drawControlPanel();
		System.out.println("Created Control Panel");
		Environment.Supplier.drawAllItems();
		System.out.println("Created items");
//		drawItems();
		super.setScene(getView());
	}
	
	private void drawShelves() {
		for(Shelf item : Environment.ShelfList){
			Rectangle ob = new Rectangle();
			ob.setHeight(BlockW+1);
			ob.setWidth(BlockH+1);
			ob.setX((item.x - 1)*BlockH );
			ob.setY((Environment.height - item.y)*BlockH);
			ob.setFill(Color.DARKTURQUOISE);
			ob.setStroke(Color.BLACK);
			shelves[item.x][item.y] = ob;
			
		}
		for (int i = 0; i < shelves.length; i++) {
			for (int j = 0; j < shelves[0].length; j++) {
				if(shelves[i][j] != null) Field.getChildren().addAll(shelves[i][j]);
			}
		}
	}

	private void drawItems() {
		
		for(Item item : Environment.Supplier.ItemCollection){
			item.figure.setX(((Rectangle)Supplier.getChildren().get(0)).getX());
			item.figure.setY(((Rectangle)Supplier.getChildren().get(0)).getY());
			item.agent.setX(((Rectangle)Supplier.getChildren().get(0)).getX());
			item.agent.setY(((Rectangle)Supplier.getChildren().get(0)).getY());
			item.toFront();
			Supplier.getChildren().add(item);
		}
		
	}
	


	public void drawAgents(){
		int t1 = 1,t2 = 1;
		VBox Legend = new VBox();
		for(Map.Entry<String, Agent> a : Environment.AgList.entrySet()){
				Agent agent = a.getValue();
				int exC = 0;int inC=0;
				switch(agent.getType()){
				case "exit":	
					Rectangle ob = new Rectangle();
					ob.setHeight(BlockW+1);
					ob.setWidth(BlockH+1);
					ob.setX((agent.x - 1)*BlockH );
					ob.setY((Environment.height - agent.y)*BlockH);
					ob.setFill(Color.FIREBRICK);
					Line e1 = new Line((agent.x-1) * BlockW ,(Environment.height - agent.y ) * BlockH ,(agent.x-1) * BlockW ,(Environment.height - agent.y - 1  ) * BlockH );
					Line e2 = new Line((agent.x) * BlockW ,(Environment.height - agent.y ) * BlockH ,(agent.x) * BlockW ,(Environment.height - agent.y - 1  ) * BlockH );
					Text exit = new Text("Out\n"+exC++);
					exit.setOpacity(0.5);
					exit.setX((agent.x-1) * BlockW + 10);
					exit.setY((Environment.height - agent.y ) * BlockH - 20);
					exit.setFont(new Font(10));
					Field.getChildren().addAll(ob,e1,e2,exit);
					exit.toFront();
	//				if(t1>0){
	//					Text t = new Text("Exit Area");
	//					t.setX((agent.x - 5) * BlockW + 5);
	//					t.setY((Environment.height - agent.y ) * BlockH + BlockH);
	//					Field.getChildren().addAll(t);
	//					t1--;
	//				}
					break;
				case "human":
					Group newHuman = new Group();
					Image i;
					if(agent.name.contains("Exit")) { i=new Image("humanexit.png");}
					else { i = new Image("human.png");}
					ImageView human = new ImageView(i);
					human.setFitHeight(BlockH - 5);
					human.setFitWidth(BlockW - 5);
					human.setX((agent.x - 1) * BlockW  + 2 );
					human.setY((Environment.height - agent.y) * BlockH + 2);
					newHuman.getChildren().addAll(human);
					agent.moveManager.setNode(newHuman);
					agent.moveManager.still = System.currentTimeMillis();
					Field.getChildren().addAll(newHuman);
					agent.moveManager.status = new HBox();
					agent.moveManager.statusName = new Text(agent.name+":");
					agent.moveManager.statusText = new Text("Idle");
					agent.moveManager.statusImage = new ImageView(new Image("human.png"));
					agent.moveManager.statusImage.setFitHeight(BlockH - 8);
					agent.moveManager.statusImage.setFitWidth(BlockW - 8);
					agent.moveManager.status.getChildren().addAll(agent.moveManager.statusImage,agent.moveManager.statusName,agent.moveManager.statusText,agent.moveManager.idleText);
					
					agent.moveManager.status.setSpacing(5);

					dist+=40;
					Legend.getChildren().add(agent.moveManager.status);
					break;
					
				case "forklift":
					Group newForklift = new Group();
					Image j ;
					if(agent.name.contains("Exit")) { j= new Image("forkliftexit.png");}
					else {j= new Image("forklift.png");}
					ImageView forklift = new ImageView(j);
					forklift.setFitHeight(BlockH - 5);
					forklift.setFitWidth(BlockW - 5);
					forklift.setX((agent.x - 1 ) * BlockW + 2 );
					forklift.setY((Environment.height - agent.y ) * BlockH + 2 );
					newForklift.getChildren().addAll(forklift);
					agent.moveManager.setNode(newForklift);
					agent.moveManager.still = System.currentTimeMillis();
					Field.getChildren().addAll(newForklift);
					agent.moveManager.status = new HBox();
					agent.moveManager.statusName = new Text(agent.name);
					agent.moveManager.statusText = new Text("Idle");
					agent.moveManager.statusImage = new ImageView(new Image("forklift.png"));
					agent.moveManager.statusImage.setFitHeight(BlockH - 8);
					agent.moveManager.statusImage.setFitWidth(BlockW - 8);
					agent.moveManager.status.setSpacing(5);
					agent.moveManager.status.getChildren().addAll(agent.moveManager.statusImage,agent.moveManager.statusName,agent.moveManager.statusText,agent.moveManager.idleText);
					dist+=40;
					Legend.getChildren().add(agent.moveManager.status);
					break;
				
				case "supplier":
					Supplier = new Group();
					Rectangle supplier= new Rectangle();
					supplier.setHeight(BlockH+1);
					supplier.setWidth(BlockW+1);
					supplier.setX((agent.x - 1) * BlockW);
					supplier.setY((Environment.height - agent.y ) * BlockH);
					supplier.setFill(Color.GREEN);
					Supplier.getChildren().addAll(supplier);
					Line s1 = new Line((agent.x-1) * BlockW ,(Environment.height - agent.y + 1) * BlockH ,(agent.x-1) * BlockW ,(Environment.height - agent.y +2  ) * BlockH );
					Line s2 = new Line((agent.x) * BlockW ,(Environment.height - agent.y + 1 ) * BlockH ,(agent.x) * BlockW ,(Environment.height - agent.y + 2  ) * BlockH );
					Text in = new Text("In\n "+inC++);
					in.setOpacity(0.5);
					in.setX((agent.x-1) * BlockW + 6);
					in.setY((Environment.height - agent.y + 1) * BlockH + 10);
					in.setFont(new Font(10));
					Field.getChildren().addAll(Supplier,s1,s2,in);
					in.toFront();
	//				if(t2>0){
	//					Text load = new Text("Load Area");
	//					load.setX((agent.x - 1) * BlockW + 5);
	//					load.setY((Environment.height - agent.y ) * BlockH + BlockH);
	//					
	//					Field.getChildren().addAll(load);
	//					t2--;
	//				}
					break;
				default:
					Ellipse ag = new Ellipse();
					ag.setRadiusX(BlockW/2);
					ag.setRadiusY(BlockH/2);
					ag.setCenterX(agent.x * BlockW - BlockW/2 );
					ag.setCenterY((Environment.height - agent.y) * BlockH + BlockH/2 );
					ag.setFill(Color.PINK);
					Text k = new Text(agent.getType());
					k.setX(ag.getCenterX() - adjuster);
					k.setY(ag.getCenterY() );
					k.setFont(font);
					Field.getChildren().addAll(ag,k);
					break;
				}
				Legend.getStyleClass().add("legend");
				Legend.setTranslateX(-100);
				
		}
		ControlPanel.getChildren().add(Legend);
	}
	public void drawBorders(){

		Line up    = new Line(0,0,WIDTH,   0  );
		Line down  = new Line(0,HEIGHT,WIDTH,  HEIGHT  );
		Line left  = new Line(0,0,0,HEIGHT);
		Line right = new Line(WIDTH,0,WIDTH,   HEIGHT);
		Field.getChildren().addAll(up,down,left,right);

		
	}
	
	public Group getView(){
//		Human.setManaged(false);
		if(View==null){
			View = new Group();
			View.getChildren().add(Field);
			View.setTranslateX(PADDER+100);
			View.setTranslateY(PADDER/2);

			View.getChildren().add(ControlPanel);
			
			}

		return View;
	}
	
	
	/* Draws the grid lines*/
	public void drawLines(){
		for(int i = 1 ; i < Environment.height ; i++){
			Line l = new Line(0,i*BlockH ,WIDTH,   i * BlockH  );
			Field.getChildren().add(l);
		}

		for(int i = 1 ; i < Environment.width ; i++){
			Line l = new Line(i*BlockW ,0,   i*BlockW , HEIGHT);

			Field.getChildren().add(l);
		}
		
	}
	
	public void drawControlPanel(){
		ScrollBar sc = new ScrollBar();
		//Field.getChildren().add(sc);
		 sc.setLayoutX(WIDTH+PADDER-sc.getWidth());
	        sc.setMin(0);
	        sc.setOrientation(Orientation.VERTICAL);
	        sc.setPrefHeight(180);
	        sc.setMax(360);
		if(ControlPanel == null)
			ControlPanel = new Group();
		itemList.setTranslateX(WIDTH+PADDER+20);
//		itemList.getStyleClass().add("legend");
		itemList.setMaxHeight(HEIGHT-30);
		 sc.valueProperty().addListener(new ChangeListener<Number>() {
	            public void changed(ObservableValue<? extends Number> ov,
	                Number old_val, Number new_val) {
	                    itemList.setLayoutY(-new_val.doubleValue());
	            }
	        });
//		Button button = new Button("Stop");
//		button.setOnMouseClicked(e->{
//			ControlPanel.setVisible(false);
//		});
		
//		ControlPanel.setTranslateY(HEIGHT);
//		ControlPanel.getChildren().add(button);
//		Rectangle iRect = new Rectangle();
//		iRect.heightProperty().bind(size);
//		iRect.setWidth(PADDER-10);
//		iRect.setX(WIDTH);
//		iRect.setY(PADDER);
//		iRect.setFill(Color.WHITE);
//		iRect.setStroke(Color.BLACK);	
//		Rectangle a = new Rectangle();
//		a.setHeight(Environment.AgList.size()*20);
//		a.setWidth(PADDER-10);
//		a.setX(0);
//		a.setY(PADDER);
//		a.setFill(Color.WHITE);
//		a.setStroke(Color.BLACK);
//		iRect.toBack();
//		a.toBack();
		ControlPanel.setTranslateX(-200);
		ControlPanel.getChildren().addAll(itemList);

		
		ItemAVG.setText(itAvg);
		ItemAVG.setX(WIDTH+PADDER+20);
		ItemAVG.setY(HEIGHT);
		ItemAVG.setFont(new Font("Arial",15));
		ItemAVG.setFill(Color.MEDIUMORCHID);
		
		
		AgentUR.setText("Average Idle Time of Agents : 0 s");
		AgentUR.setX(-PADDER/2);
		AgentUR.setY(HEIGHT);
		AgentUR.setFill(Color.DODGERBLUE);
		AgentUR.setFont(new Font("Arial",15));
//		Analysis.setSpacing(BlockW);
//		Analysis.setTranslateY(HEIGHT);
		Analysis.getChildren().addAll(ItemAVG, AgentUR);
		ControlPanel.getChildren().add(Analysis);
	}
	
	public void addSum(double time){
		sum += time;
		double avg = (double)sum/++itemCount;
		String a = avg+"";
		int in = a.indexOf(".");
		a = a.substring(0, in+2);
		ItemAVG.setText(itAvg + a + "s");
	}
	
	public void addTime(double time,double idle){

		total+=time/1000;
		totalidle+=idle/1000;
		double avg = totalidle/total;//Environment.AgList.size();
//		Urate = (total-totalidle)/total;
//		Urate/=100;
//		 
//		System.out.println(total + " " +idle + " " +Urate);
//		String rate =Urate +"";
//		System.out.println("RATE"+rate);
		String s = avg +"";
		int x = s.indexOf(".");
		String per = s.substring(0,x)+"."+s.substring(x+1,x+3);
		AgentUR.setText(utRate + per +" s");
	}

	
	
	
}
