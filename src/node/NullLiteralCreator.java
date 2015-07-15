package node;

import org.eclipse.jdt.core.dom.ASTNode;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

public class NullLiteralCreator extends NodeCreator {
	
	private Node node = null;
	
	public NullLiteralCreator(GraphDatabaseService db) {
		this.map = null;  // not used
		this.db = db;
		this.label = DynamicLabel.label("NullLiteral");
	}

	@Override
	public Node getInstance(ASTNode astNode) {
		if (node == null) {
			node = db.createNode(label);
		}
		return node;
	}

}
