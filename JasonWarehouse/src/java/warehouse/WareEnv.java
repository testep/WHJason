package warehouse;

import java.io.File;
import java.io.FileInputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;
import jason.mas2j.AgentParameters;
import jason.mas2j.MAS2JProject;
import mvc.Model;
import server.FXServer;
import server.Server;

public class WareEnv extends Environment {
	public static final Literal at_supplier = Literal.parseLiteral("staying_in(supplier)");
	public static final Literal at_rest = Literal.parseLiteral("staying_in(restArea)");
	public static final Literal at_req = Literal.parseLiteral("staying_in(exit)");
	public static final Literal carrying = Literal.parseLiteral("carrying");
	public static final Literal no_more = (Literal) Literal.parseLiteral("no_more_items");
	public static final Literal stop = (Literal) Literal.parseLiteral("stop");
	

	FXServer server;
	Model M;
	public static int itemCounter ;

	public void init(String[] Rez){
		int forks=0,forksexit=0,workers=0,workersexit=0;
		 try {
		      // parse that file
		      jason.mas2j.parser.mas2j parser =
		          new jason.mas2j.parser.mas2j(new FileInputStream(Rez[0]));
		      MAS2JProject project = parser.mas();
		      List<String> names = new ArrayList<String>();
		      // get the names from the project
		      for (AgentParameters ap : project.getAgents()) {
		         String agName = ap.name;
		         if(agName.equals("human"))workers=ap.getNbInstances();
		         else if(agName.equals("forklift"))forks=ap.getNbInstances();
		         else if(agName.equals("humanExit"))workersexit=ap.getNbInstances();
		         else if(agName.equals("forkExit"))forksexit=ap.getNbInstances();
		        
		         for (int cAg = 0; cAg < ap.getNbInstances(); cAg++) {
		            String numberedAg = agName;
		            
		            if (ap.getNbInstances() > 1) {
		               numberedAg += (cAg + 1);
		              
		            }
		         names.add(numberedAg);
		      }
		      }
		 }
		      catch (Exception e) {
		    	  e.printStackTrace();			
		   }
//		 boolean connected = false;
//		while(!connected){
			try {
				server = (FXServer) Naming.lookup("rmi://localhost:5099/fx");
				Scanner in = new Scanner(new File("in/items.txt"));
				itemCounter = in.nextInt();
				in.close();
				System.out.println("Establishing connection..");
				if(server.handshake()){
					System.out.println("Connection established, selecting the layout file..");
					if(! server.setModel(workers,forks,workersexit,forksexit))
						throw new RemoteException();
				
				}else System.exit(1);

				} 
				catch (Exception e) {
					e.printStackTrace();
				}
//		}
		

		try {
			Thread.sleep(1000);
			updatePercepts();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	private void updatePercepts() throws RemoteException {
//		if(!Server.M.runAnimations){
//			addPercept(stop);
//		} else removePercept(stop);

	}

	public boolean executeAction(String ag, Structure action) {

		boolean result = false;
		try {
			if (action.getFunctor().equals("get")) {

				System.out.println("[" + ag + "] getting: " + action.getTerm(0).toString());
				
					result = server.getItem(ag , action.getTerm(0).toString());

			}  else if (action.getFunctor().equals("move_to")) {

				System.out.println("[" + ag + "] going to: " + action.getTerm(0).toString());
				result = server.move(ag , action.getTerm(0).toString());

			}


			else if (action.getFunctor().equals("drop")) {
					result = server.dropItem(ag , action.getTerm(0).toString());			
			}
			else if (action.getFunctor().equals("putBack")) {
				result = server.putBack(ag );			
			} 
			else if (action.getFunctor().equals("insert")) {
				result = server.insert(action.getTerm(0).toString(),action.getTerm(2).toString(),action.getTerm(1).toString());			
			}
			else if (action.getFunctor().equals("save")) {
				result = server.save();			
			}
			else if (action.getFunctor().equals("addItem")) {

				System.out.println("[" + ag + "] adding: " + action.getTerm(1).toString());
				int queue = server.addItem(action.getTerm(0).toString(), action.getTerm(1).toString());
				addPercept("supplier", no_more);
				itemCounter--;
				if (itemCounter <= 0) {

					addPercept("coordinator", Literal.parseLiteral("itemQueue("+action.getTerm(1).toString()+","+queue+")"));
				}
				result = true;

			} else if (action.getFunctor().equals("invade")) {

				System.out.println("[" + ag + "] new order.");
				String inv = server.invade(action.getTerm(0).toString(), action.getTerm(1).toString());
				if (inv.length() > 2) {
					String[] coo = inv.split(",");
					// for (int i = 0; i < coo.length; i++) {
					// System.out.print(coo[i]);
					//
					// }
					// System.out.println();

					for (int i = 1; i < coo.length; i++) {
						Literal loc = (Literal) Literal.parseLiteral(
								"location(" + action.getTerm(0).toString() + "," + coo[0] + "0" + coo[i] + ")");
						// System.out.println(loc.toString());
						addPercept("coordinator", loc);
					}
				}
				result = true;

			} else if (action.getFunctor().equals("try")) {

				System.out.println("[" + ag + "] occupying a shelf.");
				String shelves = server.tryShelves(action.getTerm(0).toString());
				if (shelves.equals(""))
					result = false;
				else {
					System.out.println("Loc " + shelves);
					String[] coo = shelves.split(",");
					// for (int i = 0; i < coo.length; i++) {
					// System.out.print(coo[i]);
					//
					// }
					// System.out.println();

					for (int i = 1; i < coo.length; i++) {
						Literal loc = (Literal) Literal.parseLiteral(
								"location(" + action.getTerm(0).toString() + "," + coo[0] + "0" + coo[i] + ")");
						// System.out.println(loc.toString());
						addPercept("coordinator", loc);
					}
					result = true;
				}

			}

			if (result) {
				updatePercepts();
				Thread.sleep(100);
			}

		} catch (Exception e) {
			//e.printStackTrace();
		}
		return result;
	}

}
