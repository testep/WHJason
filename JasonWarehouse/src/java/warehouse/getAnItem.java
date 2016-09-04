// Internal action code for project warehouseV2

package warehouse;

import java.util.ArrayList;

import jason.asSemantics.*;
import jason.asSyntax.*;
import mvc.Item;
import server.DB;
import server.Server;

public class getAnItem extends DefaultInternalAction {
	public ArrayList<String> requests = new ArrayList<String>();

	boolean more=true;
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
    	
    	
    	try{
    		DB db = DB.getDB();
    		Item i = db.getRandomItem();
    		if(i == null)return false;
    		NumberTerm id  = new NumberTermImpl(i.id);
    		StringTerm name  = new StringTermImpl(i.name);
    		NumberTerm weight  = new NumberTermImpl(i.weight);
    		StringTerm loc  = new StringTermImpl(i.location);
    		System.out.println( "ID = " + i.id );
	        System.out.println( "NAME = " + i.name );
	        System.out.println( "Weight = " + i.weight );
	        System.out.println( "Location = " + i.location );
	        System.out.println();
        	System.out.println("FROM GETANITEM:" + i.name);
        	ListTerm str = new ListTermImpl();
        	str.add(id);
        	str.add(name);
        	str.add(weight);
        	str.add(loc);
        	return un.unifies(str, args[0]);
        } catch (Exception e) {
       			e.printStackTrace();
       	}
		return false;
    	
//    	
    }

}
