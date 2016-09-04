package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javafx.scene.control.Button;
import javafx.stage.Stage;
import mvc.Model;

public interface FXServer extends Remote {
	
	public boolean handshake() throws RemoteException;
	
	public boolean showStage() throws RemoteException;
	
	public boolean save() throws RemoteException;
	
	public boolean move(String ag,String location) throws RemoteException;
	
	public void sendButton() throws RemoteException;

	public boolean getItem(String ag, String string)throws RemoteException;

	public boolean setModel(int workers,int forks,int workerexit,int forkexit) throws RemoteException;

	public int addItem(String weight, String item) throws RemoteException;
	
	public String tryShelves(String item) throws RemoteException;

	public boolean dropItem(String ag, String item) throws RemoteException;
	
	public String invade(String item,String shelf) throws RemoteException;

	public boolean putBack(String ag)  throws RemoteException;

	public boolean insert(String name,String weight, String loc) throws RemoteException;
	
	
	
}
