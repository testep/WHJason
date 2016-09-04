// Agent human in project warehouse

/* Initial beliefs and rules */
{include("agent.asl")}
weightCanCarry(100).

/* Initial goals */




+!requestItem(Item,Weight) : true 
	<- true
	   .
-!requestItem(Weight,Item):true <- true.
