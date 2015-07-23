package node;

import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

public class BooleanLiteralCreator extends NodeCreator {
	
	public BooleanLiteralCreator(GraphDatabaseService db) {
		this.map = new HashMap<>();
		this.db = db;
		this.label = DynamicLabel.label("BooleanLiteral");
	}
	
	@Override
	public Node getInstance(ASTNode astNode) {
		boolean booleanValue = ((BooleanLiteral) astNode).booleanValue();
		if (map.get(booleanValue) == null) {
			Node node = db.createNode(label);
			node.setProperty("BOOLEAN_VALUE", booleanValue);
			map.put(booleanValue, node);
		}
		return map.get(booleanValue);
	}
}
