package node;

import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Modifier;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

public class ModifierCreator extends NodeCreator {

	public ModifierCreator(GraphDatabaseService db) {
		this.map = new HashMap<>();
		this.db = db;
		this.label = DynamicLabel.label("Modifier");
	}
	
	@Override
	public Node getInstance(ASTNode astNode) {
		String keyword = ((Modifier) astNode).getKeyword().toString();
		if (map.get(keyword) == null) {
			Node node = db.createNode(label);
			node.setProperty("KEYWORD", keyword);
			map.put(keyword, node);
		}
		
		return map.get(keyword);
	}
}
