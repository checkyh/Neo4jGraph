package node;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

public class TypeKeyCreator {
	
	private Map<Object, Node> map = new HashMap<>();
	private Label lKey = DynamicLabel.label("Key");
	
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
	
	public Node createTypeKey(String key) {
		if (map.get(key) == null) {
			Node node = createNode(key);
			map.put(key, node);
		}
		return map.get(key);
	}

	
}
