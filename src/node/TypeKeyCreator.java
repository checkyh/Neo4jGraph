package node;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

public class TypeKeyCreator {
	
	private Map<String, Node> map = new HashMap<>();
	private Label lKey = DynamicLabel.label("Key");
	
	private GraphDatabaseService db;
	
	public TypeKeyCreator(GraphDatabaseService db) {
		this.db = db;
	}
	
	public Node getInstance(String bindingKey) {
		if (map.get(bindingKey) == null) {
			Node node = db.createNode(lKey);
			node.setProperty("KEY", bindingKey);
			map.put(bindingKey, node);
		}
		return map.get(bindingKey);
	}
}
