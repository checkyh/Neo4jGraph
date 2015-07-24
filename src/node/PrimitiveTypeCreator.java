package node;

import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

public class PrimitiveTypeCreator extends NodeCreator {
	
	public PrimitiveTypeCreator(GraphDatabaseService db) {
		this.map = new HashMap<>();
		this.db = db;
		this.label = DynamicLabel.label("PrimitiveType");
	}

	@Override
	public Node getInstance(ASTNode astNode) {
		String typeCode = ((PrimitiveType) astNode).getPrimitiveTypeCode().toString();
		if (map.get(typeCode) == null) {
			Node node = db.createNode(label);
			node.setProperty("PRIMITIVE_TYPE_CODE", typeCode);
			map.put(typeCode, node);
		}
		
		return map.get(typeCode);
	}
}
