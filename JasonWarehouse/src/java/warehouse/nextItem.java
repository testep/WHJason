// Internal action code for project warehouse

package warehouse;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.*;
import jason.asSyntax.Term;
import mvc.Item;

public class nextItem extends DefaultInternalAction {
	public ArrayList<Item> queue = new ArrayList<Item>();
	public boolean more = true;
	public static Item active = null;
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
    	try{
    		if(more){
    			
    			more = false;
    			Scanner in = null; 
        		try {
        			in = new Scanner(new File("in/items.txt"));
        			int N = in.nextInt();
        			for (int i = 0; i < N ; i++) {
        				double weight = in.nextDouble();
        				String name  = in.next() ;
        				double time = in.nextDouble();
//        				name = name.substring(1,name.length());
        				Item it = new Item(name,weight);
        				it.timeafter = time;
//        				System.out.println("TIMEAFTER: "+time);
        				queue.add(it);
        				
        			}
        		} catch (FileNotFoundException e) {
        
        			e.printStackTrace();
        		}finally {
        			in.close();
        		}
//        		
        		
    		}
    		active = queue.remove(0);
//    		System.out.println("Sleeping for:" + (long)(Math.random()*5000));
//    		Thread.sleep((long)(Math.random()*5000));
    		StringTerm name = new StringTermImpl(active.name);
    		NumberTerm weight = new NumberTermImpl(active.weight);
    		NumberTerm time = new NumberTermImpl(active.timeafter);
    		ListTerm item = new ListTermImpl();
    		item.add(name);
    		item.add(weight);
    		item.add(time);
//    		System.out.println("FROM NEXTITEM:" + SupplierManager.ItemCollection.size());
        	return un.unifies(item, args[0]);
        } catch (Exception e) {
       			throw new JasonException("Error from NEXTIITEM");
       	}
    }
}
