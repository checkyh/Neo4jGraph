package neo4j;

import java.util.ArrayList;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.tooling.GlobalGraphOperations;

public class GraphdbPrinter implements Worker {

	@Override
	public void work(GraphDatabaseService db) {
		print(db);
	}

	private void print(GraphDatabaseService db) {
		ArrayList<Node> nodes = new ArrayList<>();
		for (Node node : GlobalGraphOperations.at(db).getAllNodes()) {
			nodes.add(node);
		}
		
		int N = nodes.size();
		for (int i = 0; i < N; i++) {
			Node node = nodes.get(i);
			System.out.println(String.format("node [%d]", node.getId()));
			for (Relationship r : node.getRelationships()) {
				Node from = r.getStartNode();
				Node to = r.getEndNode();
				if (from.getId() == node.getId()) {
					System.out.println(String.format("relationship (%d)-[%d]-(%d)", 
							from.getId(), r.getId(), to.getId()));
				}
			}
		}
	}
	
	public static void main(String[] args) {
		
		GraphdbPrinter gp = new GraphdbPrinter();
		
		String dirPath = "M:\\Neo4j-Database\\neo4jgraph";
		Neo4j neo4j = new Neo4j(dirPath);
		neo4j.run(gp);
		neo4j.shutdown();
	}

}
