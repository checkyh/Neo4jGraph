package graph;

public class Edge {
	
	private final int v;
	private final int w;
	private final char symbol;
	
	public Edge(int v, int w, char symbol) {
		this.v = v;
		this.w = w;
		this.symbol = symbol;
	}
	
	public char symbol() {
		return symbol;
	}
	
	public int from() {
		return v;
	}
	
	public int to() {
		return w;
	}
	
	public String toString() {
		return String.format("%d-%c->%d", v, symbol, w);
	}

}
