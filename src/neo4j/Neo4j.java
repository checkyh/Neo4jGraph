package neo4j;

import java.io.File;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class Neo4j {
	
	private static Logger logger = Logger.getLogger(Neo4j.class);
	
	private GraphDatabaseService db;
	
	public static final int WRITE = 0;
	public static final int APPEND = 1;
	
	public static Neo4j open(String databaseDirectory, int mode) {
		if (mode == WRITE) {
			deleteDirectory(new File(databaseDirectory));
		}
		GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(databaseDirectory);
		logger.info("database opened, directory path: " + databaseDirectory);
		return new Neo4j(db);
	}

	private Neo4j(GraphDatabaseService db) {
		this.db = db;
	}
	
	private static boolean deleteDirectory(File dir) {
		if (dir.isDirectory()) {
			for (String children : dir.list()) {
				boolean success = deleteDirectory(new File(dir, children));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	// Here must be a design pattern.
	public void run(Worker worker) {
		
		Transaction transaction = db.beginTx();
		
		try {
			worker.work(db);
			transaction.success();
		} finally {
			transaction.close();
		}
	}
	
	public void shutdown() {
		db.shutdown();
		logger.info("database closed");
	}

}
