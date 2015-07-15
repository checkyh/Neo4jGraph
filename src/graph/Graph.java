package graph;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public class Graph implements neo4j.Worker {

	private final int V;
	private int E;
	private List<Edge>[] adj;
	
	public Graph(int V) {
		this.V = V;
		this.E = 0;
		adj = (List<Edge>[]) new ArrayList[V];
		for (int v = 0; v < V; v++) {
			adj[v] = new ArrayList<Edge>();
		}
	}
	
	public Graph(In in) {
		this(in.readInt());
		int E = in.readInt();
		for (int i = 0; i < E; i++) {
			int v = in.readInt();
			int w = in.readInt();
			char symbol = in.readString().charAt(0);
			addEdge(new Edge(v, w, symbol));
		}
	}
	
	public int V() {
		return V;
	}
	
	public int E() {
		return E;
	}
	
	public void addEdge(Edge e) {
		int v = e.from();
		adj[v].add(e);
		E++;
	}
	
	public Iterable<Edge> adj(int v) {
		return adj[v];
	}
	
	public Iterable<Edge> edges() {
		List<Edge> list = new ArrayList<Edge>();
		for (int v = 0; v < V; v++) {
			for (Edge e : adj[v]) {
				list.add(e);
			}
		}
		return list;
	}
	
	@Override
	public void work(GraphDatabaseService db) {
		Label lState = DynamicLabel.label("State");
		RelationshipType rEps = DynamicRelationshipType.withName("EPS");
		RelationshipType rCommon = DynamicRelationshipType.withName("Common");
		
		Node[] nodes = new Node[V];
		for (int v = 0; v < V; v++) {
			nodes[v] = db.createNode(lState);
			nodes[v].setProperty("number", v);
		}
		nodes[0].addLabel(DynamicLabel.label("Start"));
		nodes[5].addLabel(DynamicLabel.label("End"));
		
		for (Edge e : edges()) {
			Node from = nodes[e.from()];
			Node to = nodes[e.to()];
			if (e.symbol() == '#') {
				from.createRelationshipTo(to, rEps);
			} else {
				Relationship r = from.createRelationshipTo(to, rCommon);
				r.setProperty("symbol", e.symbol());
			}
		}
	}
}
