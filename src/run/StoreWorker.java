package run;

import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.Type;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import relationship.Rels;
import ast.ASTCreator;
import ast.StoreVisitor;
import neo4j.Worker;
import node.TypeKeyCreator;

public class StoreWorker implements Worker {

	@Override
	public void work(GraphDatabaseService db) {
		System.out.println("[StoreWorker] working for " + Option.FILEPATH);

		// parse java file to AST
		ASTCreator creator = new ASTCreator(Option.PROJECT_DIR);
		List<String> filepaths = creator.getFilepaths();
//		ASTNode root = creator.createAST(Option.FILEPATH);

		for (String filepath : filepaths) {
			ASTNode root = creator.createAST(filepath);
			storeAST(db, root);
		}
		
		System.out.println("[StoreWorker] work finished");
	}

	public void storeAST(GraphDatabaseService db, ASTNode root) {
		
		// store AST into database
		StoreVisitor visitor = new StoreVisitor(db);
		root.accept(visitor);
		List<Type> types = visitor.getTypes();
		Map<ASTNode, Node> map = visitor.getMap();
		
		// add TypeKey nodes
		TypeKeyCreator keyNodeCreator = new TypeKeyCreator(db);
		for (Type type : types) {
			IBinding binding = type.resolveBinding();
			String bindingKey = binding.getKey();
			Node keyNode = keyNodeCreator.getInstance(bindingKey);
			Node typeNode = map.get(type);
			typeNode.createRelationshipTo(keyNode, Rels.KEY);
		}
		
	}
}
