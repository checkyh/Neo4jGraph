package run;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import neo4j.Neo4j;
import neo4j.Worker;
import node.TypeKeyCreator;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.Type;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import relationship.RelType;
import ast.ASTCreator;
import ast.StoreVisitor;

public class StoreWorker implements Worker {
	
	private static Logger logger = Logger.getLogger(StoreWorker.class);
	
	private List<Type> types = new LinkedList<>();
	private Map<ASTNode, Node> map = new HashMap<>();

	@Override
	public void workFor(Neo4j neo4j) {
		
		GraphDatabaseService db = neo4j.getDb();
		
		List<Node> compilationUnits = new ArrayList<>();

		// create and store ASTs one by one
		ASTCreator creator = new ASTCreator(Option.PROJECT_DIR);
		Iterator<ASTNode> iterator = creator.iterator();
		while (iterator.hasNext()) {
			ASTNode root = iterator.next();
			Node rootNode = storeAST(db, root);
			compilationUnits.add(rootNode);
		}
		
		// create virtual "Project" node and link it to all CompilationUnits
		Node projectNode = db.createNode(DynamicLabel.label("Project"));
		for (Node node : compilationUnits) {
			Relationship rel = projectNode.createRelationshipTo(node, RelType.AST);
			rel.setProperty("NAME", "FILES");
		}
		
		// add TypeKey nodes
		createTypeKeys(db, types, map);

		logger.info("Work finished");
	}

	private Node storeAST(GraphDatabaseService db, ASTNode root) {
		
		// store AST into database
		StoreVisitor visitor = new StoreVisitor(db);
		root.accept(visitor);
		
		types.addAll(visitor.getTypes());
		map.putAll(visitor.getMap());
		
		return map.get(root);
	}

	private void createTypeKeys(GraphDatabaseService db, List<Type> types,
			Map<ASTNode, Node> map) {
		TypeKeyCreator keyNodeCreator = new TypeKeyCreator(db);
		for (Type type : types) {
			IBinding binding = type.resolveBinding();
			String bindingKey = binding.getKey();
			Node keyNode = keyNodeCreator.getInstance(bindingKey);
			Node typeNode = map.get(type);
			typeNode.createRelationshipTo(keyNode, RelType.KEY);
		}
	}
}
