// Agent human in project warehouse

/* Initial beliefs and rules */

weightCanCarry(100).

/* Initial goals */


/* Plans */

+!requestItem(Item,Weight): not carrying & weightCanCarry(W) & Weight <= W
	<- .send(dispatcher,achieve,reserveItem(Item));
		+waitingResponse;
	   .

+!requestItem(Item,Weight): cannotCarry(Item) | carrying | weightCanCarry(W) & Weight > W
	<- .print("Item too heavy to carry");
		+cannotCarry(Item);
	   .
	   
+!requestItem(Item,Weight): waitingResponse
	<- .print("Cannot respond");
	   .
+!requestItem(Item,Weight): carrying
	<- .wait("-carrying");
	   !requestItem(Item,Weight);
	   .
-!requestItem(Item,Weight) :true 
<- .print("Cannot Carry item");
	-waitingResponse;
	.

+!sendItem(Item): true
	<- +carrying;
	   !go(Item);
	   get(Item);
	   .send(dispatcher,achieve,notreserved(Item));
	   !go(exit);
	   drop(Item);
	   !go(restArea);
	   -carrying;
	   -waitingResponse;

	  .

-!sendItem(Item) : true 
<- .print("ERROR carrying item");
	move_to(restArea);
	-carrying;
	-waitingResponse;
	.

+!go(Place) : not staying_in(Place)
	<- move_to(Place).

+!go(Place) : staying_in(Place)
	<- true.

	
-!go(Place) : true <- .print("Error in moving").

+!newItem(Item,Weight):true <- true.
+!newItem(Item): true <- true.