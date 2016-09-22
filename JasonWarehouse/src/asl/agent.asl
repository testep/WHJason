
+!newItem(Weight,Item)[source(A)]: not carrying & weightCanCarry(W) & Weight <= W
	<-  +waitingResponse;
		+weightOf(Item,Weight);
		.send(A,achieve,reserveItem(Item,Weight));
	   .

+!newItem(Weight,Item)[source(A)]: carrying 
	<- .wait("-carrying");
		!newItem(Weight,Item)[source(A)];
	   .
//new ones	   
+!newItem(Item)[source(A)]: weightOf(Item,Weight) 
	<- 
		.send(A,achieve,reserveItem(Item,Weight));
	   .
	   
+!newItem(Item)[source(A)]: not weightOf(Item,Weight) 
	<- 
		true;
	   .
//till here	   
+!newItem(Weight,Item): weightCanCarry(W) & Weight > W
	<- .print("Item too heavy to carry");
	   .   
+!newItem(Weight,Item): waitingResponse
	<- .print("Cannot respond");
	   .
-!newItem(Weight,Item) :true 
<- .print("Cannot Carry item");
	-waitingResponse.


+!deny : true 
	<- -waitingResponse;
		.print("Did not reserve item");
	.
+!itemLocation(Item,Weight): not stop
<-	.send(coordinator,achieve,transport(Item,Weight));
.

@trans[atomic]
+!transportItem(Loc,Item,Weight):not stop
<- 	!go(Loc);
	drop(Loc);
	-waitingResponse;
	-carrying;
	
	.send(supplier,achieve,notreserved(Item));
//	.send(coordinator,achieve,dispatch(Loc,50));
	!insert(Item,Weight,Loc);
//	!go(restArea);
	.
-!transportItem(Loc,Item,Weight):true
<- .print("Error during transport");
	putBack;
	move_to(restArea);
	.send(supplier,achieve,failed(Item));
	-waitingResponse;
	-carrying;
.

+!insert(Item,Loc,Weight):true
<- insert(Item,Loc,Weight);
.

+!carryItem(Item,Weight): not carrying
	<- +carrying;
	   !go(supplier);
	   get(Item);
	   
	   !itemLocation(Item,Weight);
	.
	
-!carryItem(Item) : true 
<- 	.print("ERROR carrying Item: ", Item);
	move_to(restArea);
	.send(supplier,achieve,notreserved(Item));
	-carrying;
	-waitingResponse;
	.
-!carryItem(Item,Weight):true <- .print("ERROR carrying Item: ", Item, " with weight: ",Weight);
	move_to(restArea);
	.send(supplier,achieve,notreserved(Item));
	-carrying;
	-waitingResponse;
	.


+!unset : true 
<-  go(trash1);
 	dumpItem;
 	.print("item dumped");
 	.
 


+!go(Place) : not staying_in(Place)
	<- move_to(Place).

+!go(Place) : staying_in(Place)
	<- true.

	
-!go(Place) : true <- .print("Error in moving").

+!unset : carrying <- destroy.
+!unset : not carrying <- true.
-!unset : true <- .print("Error destroying item").

+!requestItem(Item,Weight) : true 
	<- true;  .	
	
-!requestItem(Weight,Item):true <- true.
