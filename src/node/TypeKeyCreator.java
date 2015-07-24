package node;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.Type;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

public class TypeKeyCreator {
	
	private Map<Object, Node> map = new HashMap<>();
	private Label lKey = DynamicLabel.label("Key");
	private RelationshipType rKey = DynamicRelationshipType.withName("KEY");
	
	private GraphDatabaseService db;
	
	public TypeKeyCreator(GraphDatabaseService db) {
		this.db = db;
	}
	
	private Node createNode(String key) {
		Node node = db.createNode();
		node.addLabel(lKey);
		node.setProperty("KEY", key);
		return node;
	}
	
	public void createTypeKey(Type astNode, Node startNode) {
		IBinding binding = astNode.resolveBinding();
		if (binding == null) {
			throw new NullPointerException("binding is null");
		}
		String key = binding.getKey();
		if (map.get(key) == null) {
			Node endNode = createNode(key);
			startNode.createRelationshipTo(endNode, rKey);
			map.put(key, endNode);
		}
	}
}
