package neo4j;

import java.io.File;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import run.Config;

public class Neo4j {
	
	private GraphDatabaseService db;
	
	public static final int WRITE = 0;
	public static final int APPEND = 1;
	
	public static Neo4j open(int mode) {
		if (mode == WRITE) {
			deleteDirectory(new File(Config.DATABASE_DIR));
		}
		GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(Config.DATABASE_DIR);
		System.out.println("[Neo4j] opened, directory path: " + Config.DATABASE_DIR);
		return new Neo4j(db);
	}

	private Neo4j(GraphDatabaseService db) {
		this.db = db;
	}
	
	private static boolean deleteDirectory(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDirectory(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
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
