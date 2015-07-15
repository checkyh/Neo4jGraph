package node;

import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

/**
 * Factory Pattern
 * 
 * @author William
 *
 */
public abstract class NodeCreator {
	
	Map<Object, Node> map;
	GraphDatabaseService db;
	Label label;

	public abstract Node getInstance(ASTNode astNode);
}
