package node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import relationship.RelType;

public class KeyCollector {

	private List<ASTNode> types = new LinkedList<>();
	private List<ASTNode> methods = new LinkedList<>();

	public void receiveType(ASTNode node) {
		if (node instanceof Type || node instanceof TypeDeclaration) {
			types.add(node);
		}
	}

	private String getTypeBinding(ASTNode node) {
		IBinding binding;
		if (node instanceof Type) {
			binding = ((Type) node).resolveBinding();
		} else if (node instanceof TypeDeclaration) {
			binding = ((TypeDeclaration) node).resolveBinding();
		} else {
			throw new IllegalArgumentException();
		}
		String bindingKey = binding.getKey();
		return bindingKey;
	}

	public void receiveMethod(ASTNode node) {
		if (node instanceof MethodDeclaration
				|| node instanceof MethodInvocation) {
			methods.add(node);
		}
	}

	private String getMethodBinding(ASTNode node) {
		IBinding binding;
		if (node instanceof MethodDeclaration) {
			binding = ((MethodDeclaration) node).resolveBinding();
		} else if (node instanceof MethodInvocation) {
			binding = ((MethodInvocation) node).resolveMethodBinding();
		} else {
			throw new IllegalArgumentException();
		}
		return binding.getKey();
	}

	public void createTypeKeys(GraphDatabaseService db, Map<ASTNode, Node> map) {
		Map<String, Node> dict = new HashMap<>();
		for (ASTNode node : types) {
			String bindingKey = getTypeBinding(node);

			if (dict.get(bindingKey) == null) {
				Node typeKey = db.createNode(Labels.Key, Labels.TypeKey);
				typeKey.setProperty("KEY", bindingKey);
				dict.put(bindingKey, typeKey);
			}
			Node typeKey = dict.get(bindingKey);
			map.get(node).createRelationshipTo(typeKey, RelType.KEY);
		}
	}

	public void createMethodKeys(GraphDatabaseService db, Map<ASTNode, Node> map) {
		Map<String, Node> dict = new HashMap<>();
		for (ASTNode node : methods) {
			String bindingKey = getMethodBinding(node);

			if (dict.get(bindingKey) == null) {
				Node typeKey = db.createNode(Labels.Key, Labels.MethodKey);
				typeKey.setProperty("KEY", bindingKey);
				dict.put(bindingKey, typeKey);
			}

			Node typeKey = dict.get(bindingKey);
			map.get(node).createRelationshipTo(typeKey, RelType.KEY);
		}
	}
}
