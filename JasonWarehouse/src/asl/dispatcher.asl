// Agent supplier in project warehouse

/* Initial beliefs and rules */

/* Initial goals */

//!removeItem.

!dispatch.
//
//
///* Plans */
//+itemWeight(Item,Weight) : not working
//<-  +working;
//	.at("now +5 s","+!removeItem");
//	
//	.
//+itemWeight(Item,Weight) : working
//<-  true;
//	.
//+!removeItem : true
//<-	.print("FINDING AN ITEM");
//	warehouse.getAnItem(Item);
//	
//	?itemWeight(Item,Weight);
//	!requestItem(Item,Weight);
//	.random(R);
//	R = R * 3000 mod 1000;  
//	.print("Waiting ",R);
//	.wait(R);
//	!removeItem;
//	.
//-!removeItem :true 
//<- .print("Item not encountered"); 
//	 
//	.wait(3000);
//	!removeItem.

+!dispatch: true 
<- 	.random(T);
	Time = T * 4000;
	.wait(Time);
	warehouse.getAnItem(Item);
	.nth(0,Item,Id);
	.nth(1,Item,Name);
	.nth(2,Item,Weight);
	.nth(3,Item,Loc);
	.print("Requesting " , Name);
	!requestItem(Loc,Weight);
	!dispatch;
.

-!dispatch : true
<-	.print("No items stored yet");
	.random(T);
	Time = T * 20000;
	.wait(Time);
	!dispatch;
	.
+!requestItem(Item,Weight) : true 
	<- .broadcast(achieve,requestItem(Item,Weight));
	   .	
	
-!requestItem(Weight,Item):true <- .print("Cannot add Item").

+!reserveItem(Item)[source(Ag)] : not reserved(Item)
	<- +reserved(Item);
		.send(Ag,achieve,sendItem(Item));
	.
	
+!reserveItem(Item)[source(Ag)]: reserved(Item) <- .print("Item reserved, ",Ag).

-!reserveItem(Item): true <- .print("ERROR reserving").

+!notreserved(Item): reserved(Item) 
 <-	-reserved(Item);
 	.print("Not reserved")
.
	

+!newItem(Item,Weight):true <- true.
+!newItem(Item): true <- true.