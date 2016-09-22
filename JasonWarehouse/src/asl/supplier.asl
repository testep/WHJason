// Agent supplier in project warehouse

/* Initial beliefs and rules */

/* Initial goals */

/* Plans */

+!addItem(Weight,Item) : true
	<- 
		addItem(Weight,Item);
	   .broadcast(achieve,newItem(Weight,Item));
   .
-!addItem(Weight,Item):true <- .print("Cannot add Item").

+!reserveItem(Item,Weight)[source(Ag)] : not reserved(Item)
	<- 
//		 warehouse.reserve(Ag);
		+reserved(Item);
		.send(Ag,achieve,carryItem(Item,Weight));
		
	.
	
+!reserveItem(Item,Weight)[source(Ag)]: reserved(Item) 
 <-	.print("Item reserved.Cannot reserve for ",Ag);
	.send(Ag,achieve,deny);
	
	.
	
+!notreserved(Item): reserved(Item) 
 <-	-reserved(Item);
 	.print("Not reserved");
	.
+!notreserved(Item): not reserved(Item) 
 <-	true;
	.

+!failed(Item): reserved(Item)
<- -reserved(Item);
	.broadcast(achieve,newItem(Item));
.
-!failed(Item)[source(Ag)]:true <- .print("Failed from",Ag," for ",Item). 
-!reserveItem(Item): true <- .print("ERROR reserving").

+!newOrder : true <- .print("Order is in").
-!newOrder : true <- .print("Cannot order").


+!requestItem(Item,Weight) : true 
	<- true;  .	
