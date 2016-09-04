package mvc;

public class Node implements Comparable<Node>{
	int heuristic;
	int x , y , moves;
	String path;
	
	public Node(String path, int moves, int x , int y , int DestX,int DestY) {
		super();
		this.path = path;
		this.x = x;
		this.y = y;
		this.moves = moves;
		this.heuristic = moves + calculateHeu(x,y,DestX,DestY);
	}

	private int calculateHeu( int x,int y , int DestX,int DestY ) {
		return Math.abs(x - DestX )+ Math.abs(y - DestY);
		
	}
	@Override
	public String toString() {
		return x + " " + y + " Heu:" +heuristic+" " + path ;
	}
	@Override
	public int compareTo(Node o) {
		if(o.heuristic < this.heuristic) return 1;
		return 0;
	}
}