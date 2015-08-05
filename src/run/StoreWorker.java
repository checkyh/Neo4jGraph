package run;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import ast.ASTCreator;
import ast.StoreVisitor;
import neo4j.Worker;
import node.KeyCollector;
import node.Labels;
import relationship.RelType;

public class StoreWorker implements Worker {

	private static Logger logger = Logger.getLogger(StoreWorker.class);

	private KeyCollector collector = new KeyCollector();

	private Map<ASTNode, Node> map = new HashMap<>();

	private List<Node> compilationUnits = new ArrayList<>();

	@Override
	public void work(GraphDatabaseService db) {

		storeASTs(db);

		createProjectNode(db);

		createKeys(db);

		createHighLevelRelationships(db);

		logger.info("Work finished");
	}

	private void storeASTs(GraphDatabaseService db) {

		// create and store ASTs one by one
		ASTCreator creator = new ASTCreator(Option.PROJECT_DIR);

		while (creator.hasNext()) {
			ASTNode root = creator.next();

			// store AST into database
			StoreVisitor visitor = new StoreVisitor(db, collector);
			root.accept(visitor);

			map.putAll(visitor.getMap());
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

	private void createKeys(GraphDatabaseService db) {
		logger.info("Create TypeKey nodes");
		collector.createKeys(db, map);
	}

	private void createHighLevelRelationships(GraphDatabaseService db) {
		logger.info("Create high-level relationships");

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
		
		String callUmlQuery = "match (M1:MethodDeclaration)-[:AST*]->(:MethodInvocation)- "
				+ "[:KEY]->(:MethodKey)<-[:KEY]-(M2:MethodDeclaration) "
				+ "merge (M1)-[:CALL]->(M2)";

		db.execute(classExtendsUmlQuery);
		db.execute(interfaceExtendsUmlQuery);
		db.execute(implementsUmlQuery);
		db.execute(callUmlQuery);
	}
}
