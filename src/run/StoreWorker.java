package run;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import neo4j.Worker;
import node.TypeKeyCreator;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import relationship.RelType;
import ast.ASTCreator;
import ast.StoreVisitor;

public class StoreWorker implements Worker {

	private static Logger logger = Logger.getLogger(StoreWorker.class);

	private List<ASTNode> types = new LinkedList<>();
	private Map<ASTNode, Node> map = new HashMap<>();

	@Override
	public void work(GraphDatabaseService db) {

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
			Relationship rel = projectNode.createRelationshipTo(node,
					RelType.AST);
			rel.setProperty("NAME", "FILES");
		}

		// add TypeKey nodes
		createTypeKeys(db, types, map);

		createUmlRelationships(db);

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

	private void createTypeKeys(GraphDatabaseService db, List<ASTNode> types,
			Map<ASTNode, Node> map) {
		TypeKeyCreator keyNodeCreator = new TypeKeyCreator(db);
		for (ASTNode node : types) {
			IBinding binding;
			if (node instanceof Type) {
				binding = ((Type) node).resolveBinding();
			} else if (node instanceof TypeDeclaration) {
				binding = ((TypeDeclaration) node).resolveBinding();
			} else {
				throw new IllegalArgumentException();
			}
			String bindingKey = binding.getKey();
			Node keyNode = keyNodeCreator.getInstance(bindingKey);
			Node neo4jNode = map.get(node);
			neo4jNode.createRelationshipTo(keyNode, RelType.KEY);
		}
	}

	private void createUmlRelationships(GraphDatabaseService db) {
		// add 'EXTENDS' UML relationship between classes
		String classExtendsUmlQuery = "match (C1:TypeDeclaration)-"
				+ "[:AST {NAME:'SUPERCLASS_TYPE'}]->"
				+ "()–[:KEY]->(:TypeKey)<-[:KEY]-(C2:TypeDeclaration) "
				+ "create (C1)-[:UML {NAME:'EXTENDS'}]->(C2)";
		
		// add 'EXTENDS' UML relationship between interfaces
		String interfaceExtendsUmlQuery = "match (I1:TypeDeclaration {INTERFACE:{true}})-"
				+ "[:AST{NAME:'SUPER_INTERFACE_TYPES'}]->"
				+ "()–[:KEY]->(:Typekey)<-[:KEY]-(I2:TypeDeclaration {INTERFACE:{true}}) "
				+ "create (I1)-[:UML {NAME:'EXTENDS'}]->(I2)";

		db.execute(classExtendsUmlQuery);
		db.execute(interfaceExtendsUmlQuery);
	}
}
