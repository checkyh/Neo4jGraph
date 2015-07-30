package run;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import neo4j.Worker;
import node.TypeKeyCreator;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.Type;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import relationship.Rels;
import ast.ASTCreator;
import ast.StoreVisitor;

public class StoreWorker implements Worker {
	
	private static Logger logger = Logger.getLogger(StoreWorker.class);

	@Override
	public void work(GraphDatabaseService db) {
		
		logger.info("working for " + Option.FILEPATH);

		// parse java file to AST
		ASTCreator creator = new ASTCreator(Option.PROJECT_DIR);

		Iterator<ASTNode> iterator = creator.iterator();
		while (iterator.hasNext()) {
			ASTNode root = iterator.next();
			storeAST(db, root);
		}
		
		logger.info("work finished");
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
