// Internal action code for project warehouse

package warehouse;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;
import mvc.SupplierManager;

public class reserve extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
        try{
        	int pointer = SupplierManager.pointer++;
        	System.out.println(pointer);
        	StringTerm agent = (StringTerm)args[0];
        	String ag = agent.getString();
        	switch(ag){
        	case "human": SupplierManager.space[0] = pointer; break; 
        	case "forklift": SupplierManager.space[1] = pointer; break; 
        	}
        	SupplierManager.pointer++;
        	
        	return true;
        }catch (ArrayIndexOutOfBoundsException e) {
        	throw new JasonException("The internal action ÅfdistanceÅf"+
        			"has not received five arguments!");
       	} catch (ClassCastException e) {
       		throw new JasonException("The internal action ÅfdistanceÅf"+
       			"has received arguments that are not numbers!");
       	} catch (Exception e) {
       			throw new JasonException("Error in ÅfdistanceÅf");
       	}
    }
}
