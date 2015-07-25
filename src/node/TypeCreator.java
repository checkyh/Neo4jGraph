package node;

import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

// TODO unused class
public class TypeCreator extends NodeCreator {
	
	public TypeCreator(GraphDatabaseService db) {
		this.map = new HashMap<>();
		this.db = db;
	}

	@Override
	public Node getInstance(ASTNode astNode) {
		String typeCode = ((PrimitiveType) astNode).getPrimitiveTypeCode().toString();
		if (map.get(typeCode) == null) {
			Node node = db.createNode();
			node.setProperty("PRIMITIVE_TYPE_CODE", typeCode);
			map.put(typeCode, node);
		}
		
		return map.get(typeCode);
	}
}
