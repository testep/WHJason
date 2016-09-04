package mvc;

import server.*;

public class AStar{
	
	MinPQ<Node> listHuman ;
	
	public String path(String ag, int startX, int startY, int DestX, int DestY){
		Node start = new Node("",0,startX,startY,DestX,DestY);
		Agent active = Server.M.AgList.get(ag);
		listHuman.insert(start);

		
		while(!listHuman.isEmpty() ){

			Node current = listHuman.getMin();
			int x = current.x ; 
			int y = current.y ; 
			int moves = current.moves;
			active.moveManager.visited[x][y] = true;
			String path = current.path;
			
			if(x == DestX && y == DestY){
				listHuman.clear();
				active.moveManager.updateLocation(x, y);
				active.moveManager.AtDest = true;
				active.moveManager.resetVisited();
				return path;
			}
			
			else{
				if( y+1 <= Server.M.height){

					if(!active.moveManager.visited[x][y+1] && Server.M.AgMap[x][y+1] == null){
						active.moveManager.visited[x][y+1] = true;
						Node up = new Node(path+"u",moves + 1, x , y+1 ,DestX,DestY);
						listHuman.insert(up);
					}
				}
				if(x+1 <= Server.M.width){

					if(!active.moveManager.visited[x+1][y] && Server.M.AgMap[x+1][y] == null){
						active.moveManager.visited[x+1][y] = true;
						Node right = new Node(path+"r",moves + 1,x+1,y,DestX,DestY);
						listHuman.insert(right);
					}
				}
				if(x-1 > 0){

					if(!active.moveManager.visited[x-1][y] && Server.M.AgMap[x-1][y] == null){
						active.moveManager.visited[x-1][y] = true;
						Node left = new Node(path+"l",moves + 1, x-1 , y ,DestX,DestY);
						listHuman.insert(left);
					}
				}
				if(y-1 > 0){

					if(!active.moveManager.visited[x][y-1] && Server.M.AgMap[x][y - 1] == null){
						active.moveManager.visited[x][y-1] = true;
						Node down = new Node(path+"d",moves + 1,x,y-1,DestX,DestY);
						listHuman.insert(down);
					}
				}
			}

		}
		return null;
	}

	public AStar(int capacity) {

		listHuman = new MinPQ<Node>(capacity);

	}
	
	
}


