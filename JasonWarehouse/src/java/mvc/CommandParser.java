package mvc;

import server.Server;

public class CommandParser {
	public static int[] locationParser(String ag,String location){
		int[] coo = new int[2];
//		System.out.println(location);
		if(location.contains("supplier")){
			coo[0] = Server.M.Supplier.SX;
			coo[1] = Server.M.Supplier.SY + Server.M.Supplier.DS ;

		}
		else if(location.contains("trash1")){
			coo[0] = 1;
			coo[1] = 5;
		}
		else if(location.contains("restArea")){
			coo[0] = Server.M.AgList.get(ag).x;
			coo[1] = Server.M.AgList.get(ag).y;
		}
		else if(location.contains("exit")){
			coo[0] = Server.M.Supplier.EX;
			coo[1] = Server.M.Supplier.EY + Server.M.Supplier.DE ;

		}else{
			location=filter(location);

			int dot = location.indexOf('0');	

			coo[0] = Integer.parseInt(location.substring(0,dot));
			coo[1] = Integer.parseInt(location.substring(dot+1));
			int dir = Server.M.AgMap[coo[0]][coo[1]].dir ;

			coo[0]+=dir;
//			System.out.println("Coos:" + coo[0] + " " + coo[1]);

		}
		return coo;
	}
	
	public static int[] shelfParser(String item){
		int[] coo = new int[3];
		int dot = item.indexOf('0');
//		System.out.println(dot);
		coo[0] = Integer.parseInt(item.substring(0,dot));

		coo[1] = Integer.parseInt(item.substring(dot+1));
		coo[2] = Server.M.AgMap[coo[0]][coo[1]].dir;
		return coo;
	}

	public static String filter(String item) {
		item = item.trim();
		if(item.startsWith("\"")){
			item = item.substring(1,item.length()-1);
		}
		
		return item;
	}
}
