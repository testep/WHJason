// Agent coordinator in project warehouse

/* Initial beliefs and rules */

/* Initial goals */
!order.
!backup.

/* Plans */

+!order: not no_more_items 
<-	warehouse.nextItem(Item);  

	.nth(0,Item,Name);
	.nth(1,Item,Weight);
	.nth(2,Item,Time);
	.wait(Time); //Removed not needed
//	.concat("now +",Time," s",X);//not needed

	+itemWeight(Name,Weight);

	.send(dispatcher,tell,itemWeight(Weight,Name));
	.send(supplier,achieve,addItem(Weight,Name));
//	.at(X,"+!order");
	
	!order;
. 

+!order:  no_more_items 
<-	.print("No more items");
. 

-!order: true
<-	.at("now +4 s","+!order");
. 
+!backup:true
<-  .wait(20000);
	save;
	
.
+!findItemWeight(Item):true
<- 	?itemWeight(Item,W);
	.send(dispatcher,tell,itemWeight(Item,W));
	.

+!transport(Item,Weight)[source(Ag)]: location(Item,Loc)
<- 	invade(Item,Loc);
	.send(Ag,achieve,transportItem(Loc,Item,Weight));
.

+!transport(Item,Weight)[source(Ag)]: not location(Item,_)
<- !shelf_occupy(Item,Weight,Ag);
.

-!transport(Item)[source(Ag)] : true 
<-	.send(Ag,achieve,unset);
. 

+!shelf_occupy(Item,Weight,Ag) : true
<- 	
	try(Item);
	+finished;
	?location(Item,Loc);
//	!transport(Item)[source(Ag)];
	.send(Ag,achieve,transportItem(Loc,Item,Weight));	
.
-!shelf_occupy(Item,Weight,Ag) : true
<- 	!shelf_occupy(Item,Weight,Ag);
	.

+!newItem(Item,Weight):true <- true.
+!dispatch(Item,Weight):true <- .send(dispatcher,achieve,requestItem(Item,Weight)).
+!newItem(Item,Weight):true <- true.
+!requestItem(Item,Weight) : true 
	<- true;
	   .
-!requestItem(Weight,Item):true <- .print("Cannot add Item").

+!newItem(Item): true <- true.