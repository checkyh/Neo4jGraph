package run;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.neo4j.graphdb.GraphDatabaseService;

import ast.Parser;
import ast.StoreVisitor;
import neo4j.Worker;

public class StoreWorker implements Worker {
	
	private String filename;
	
	public StoreWorker(String filename) {
		this.filename = filename;
	}

	@Override
	public void work(GraphDatabaseService db) {
		System.out.println("[StoreWorker] working for " + filename);
		
		store(db);
		
		System.out.println("[StoreWorker] work finished");
	}

	private void store(GraphDatabaseService db) {
		Parser parser = new Parser(filename);
		CompilationUnit unit = parser.parse();
		StoreVisitor visitor = new StoreVisitor(db);
		unit.accept(visitor);
	}
}
