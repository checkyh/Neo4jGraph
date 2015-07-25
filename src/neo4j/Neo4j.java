package neo4j;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 * Example:
 * <pre><code> Neo4j neo4j = new Neo4j(dirPath);
 * neo4j.clear();
 * neo4j.run(worker);
 * neo4j.shutdown();
 * </code></pre>
 *
 */
public class Neo4j {
	
	private GraphDatabaseService db;
	
	public Neo4j(String dirPath) {
		db = new GraphDatabaseFactory().newEmbeddedDatabase(dirPath);
		System.out.println("[Neo4j] opened, directory path: " + dirPath);
	}
	
	public void clear() {
		db.execute("MATCH ()-[r]-() DELETE r");
		db.execute("MATCH (n) DELETE n");
		System.out.println("[Neo4j] cleared");
	}
	
	// Here must be a design pattern.
	public void run(Worker worker) {
		
		Transaction tx = db.beginTx();
		
		try {
			worker.work(db);
			tx.success();
		} finally {
			tx.close();
		}
	}
	
	public void shutdown() {
		db.shutdown();
		System.out.println("[Neo4j] closed");
	}

}
