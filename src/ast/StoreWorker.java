package ast;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.neo4j.graphdb.GraphDatabaseService;

import neo4j.Worker;

public class StoreWorker implements Worker {
	
	private String filename;
	
	public StoreWorker(String filename) {
		this.filename = filename;
	}

	@Override
	public void work(GraphDatabaseService db) {
		Parser parser = new Parser(filename);
		CompilationUnit unit = parser.parse();
		StoreVisitor visitor = new StoreVisitor(db);
		unit.accept(visitor);
	}

}
