package run;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import neo4j.Worker;
import node.Labels;
import node.TypeKeyCreator;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import relationship.RelType;
import ast.ASTCreator;
import ast.StoreVisitor;

public class StoreWorker implements Worker {

	private static Logger logger = Logger.getLogger(StoreWorker.class);

	private Map<ASTNode, Node> map = new HashMap<>();
	private List<ASTNode> types = new LinkedList<>();
	private List<ASTNode> methods = new LinkedList<>();

	private List<Node> compilationUnits = new ArrayList<>();

	@Override
	public void work(GraphDatabaseService db) {

		storeASTs(db);
		
		createProjectNode(db);

		createTypeKeys(db);
		createMethodKeys(db);

		createUmlRelationships(db);

		logger.info("Work finished");
	}

	private void storeASTs(GraphDatabaseService db) {
		
		// create and store ASTs one by one
		ASTCreator creator = new ASTCreator(Option.PROJECT_DIR);
		Iterator<ASTNode> iterator = creator.iterator();
		
		while (iterator.hasNext()) {
			ASTNode root = iterator.next();
			
			// store AST into database
			StoreVisitor visitor = new StoreVisitor(db);
			root.accept(visitor);
			
			map.putAll(visitor.getMap());
			types.addAll(visitor.getTypes());
			methods.addAll(visitor.getMethods());
			compilationUnits.add(map.get(root));
		}
	}

	private void createProjectNode(GraphDatabaseService db) {
		logger.info("Create Project node");
		
		// create virtual "Project" node and link it to all CompilationUnits
		Node projectNode = db.createNode(Labels.Project);
		for (Node node : compilationUnits) {
			Relationship rel = projectNode.createRelationshipTo(node,
					RelType.AST);
			rel.setProperty("NAME", "FILES");
		}
	}

	private void createTypeKeys(GraphDatabaseService db) {
		logger.info("Create TypeKey nodes");
		
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
	
	private void createMethodKeys(GraphDatabaseService db) {
		logger.info("Create MethodKey nodes");
		logger.debug(methods.size() + " methods in all");
	}

	private void createUmlRelationships(GraphDatabaseService db) {
		logger.info("Create UML relationships");
		
		String classExtendsUmlQuery = "match (C1:TypeDeclaration)-"
				+ "[:AST {NAME:'SUPERCLASS_TYPE'}]->"
				+ "()–[:KEY]->(:TypeKey)<-[:KEY]-(C2:TypeDeclaration) "
				+ "create (C1)-[:UML {NAME:'EXTENDS'}]->(C2)";
		
		String interfaceExtendsUmlQuery = "match (I1:TypeDeclaration {INTERFACE:{true}})-"
				+ "[:AST {NAME:'SUPER_INTERFACE_TYPES'}]->"
				+ "()–[:KEY]->(:Typekey)<-[:KEY]-(I2:TypeDeclaration {INTERFACE:{true}}) "
				+ "create (I1)-[:UML {NAME:'EXTENDS'}]->(I2)";
		
		String implementsUmlQuery = "match (C:TypeDeclaration {INTERFACE:{false}})-"
				+ "[:AST {NAME:'SUPER_INTERFACE_TYPES'}]->"
				+ "()–[:KEY]->(:TypeKey)<-[:KEY]-(I:TypeDeclaration {INTERFACE:{true}}) "
				+ "create (C)-[:UML {NAME:'IMPLEMENTS'}]->(I)";

		db.execute(classExtendsUmlQuery);
		db.execute(interfaceExtendsUmlQuery);
		db.execute(implementsUmlQuery);
	}
}
