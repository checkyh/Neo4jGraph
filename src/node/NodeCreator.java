package node;

import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 * Singleton Pattern
 *
 */
public abstract class NodeCreator {
	
	Map<Object, Node> map;
	GraphDatabaseService db;

	public abstract Node getInstance(ASTNode astNode);
}
