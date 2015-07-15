package graph;

import neo4j.Neo4j;

public class Test {

	public static void main(String[] args) {
		
		String filename = "D:\\Java-Projects\\Neo4jGraph\\input\\G.txt";
		In in = new In(filename);
		Graph G = new Graph(in);
		for (Edge e : G.edges()) {
			System.out.println(e);
		}		
		
		String dirPath = "M:\\Neo4j-Database\\neo4jgraph";
		Neo4j neo4j = new Neo4j(dirPath);
		neo4j.clear();
		neo4j.run(G);
		neo4j.shutdown();
	}

}
