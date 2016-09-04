package server;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AppServer extends Application{
	public static void main(String[] rez) throws RemoteException {
		launch(rez);
	}

	public void showStage(Stage S) throws RemoteException{
		try {
			start(S);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void start(Stage S) throws Exception {
		Registry reg = LocateRegistry.createRegistry(5099);
		reg.rebind("fx", new Server());
		FileChooser filechooser = new FileChooser();
		filechooser.setInitialDirectory(new File("layouts"));
		filechooser.setTitle("Select the layout file");
		File f = filechooser.showOpenDialog(new Stage());
		if(!f.getAbsolutePath().endsWith(".jwlf")){
			System.out.println("Wrong File Selected");
			f = filechooser.showOpenDialog(new Stage());
		}
//		File f = filechooser.showOpenDialog(new Stage());
		Server.layoutFile = f.getAbsolutePath();
		System.out.println("Path:" + Server.layoutFile);
//		System.out.println("FROM APP"+SupplierManager.Queue.size());
		while(true)
		{
			if(Server.M!=null){
				S.setScene(Server.V.getScene());
				S.show();
//				Thread.sleep(5000);
				break;
				}
			else{ 
				System.out.println("Waiting for client");
				Thread.sleep(5000);
			}
		}
		
		
	}
//	public static String selectFile() {
//		
//		return f.getAbsolutePath();
//	}
}
