package run;

import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.Type;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

import ast.Parser;
import ast.StoreVisitor;
import neo4j.Worker;
import node.TypeKeyCreator;

public class StoreWorker implements Worker {
	
	private String filename;
	
	public StoreWorker(String filename) {
		this.filename = filename;
	}

	@Override
	public void work(GraphDatabaseService db) {
		System.out.println("[StoreWorker] working for " + filename);
		
		// parse java file to AST
		Parser parser = new Parser(filename);
		CompilationUnit unit = parser.parse();
		
		// store AST into database
		StoreVisitor visitor = new StoreVisitor(db);
		unit.accept(visitor);
		
		List<Type> types = visitor.getTypes();
		Map<ASTNode, Node> map = visitor.getMap();
		
		// add TypeKey nodes
		TypeKeyCreator keyNodeCreator = new TypeKeyCreator(db);
		RelationshipType rKey = DynamicRelationshipType.withName("KEY");
		
		for (Type type : types) {
			IBinding binding = type.resolveBinding();
			String bindingKey = binding.getKey();
			Node keyNode = keyNodeCreator.getInstance(bindingKey);
			Node typeNode = map.get(type);
			typeNode.createRelationshipTo(keyNode, rKey);
		}
		
		System.out.println("[StoreWorker] work finished");
	}
}
