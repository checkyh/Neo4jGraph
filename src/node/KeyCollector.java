package node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import relationship.RelType;

public class KeyCollector {

	private static final int TYPE = 0;
	private static final int METHOD = 1;
	private static final int VARIABLE = 2;

	@SuppressWarnings("rawtypes")
	private LinkedList[] keys;

	private LinkedList<ASTNode> types = new LinkedList<>();
	private LinkedList<ASTNode> methods = new LinkedList<>();
	private LinkedList<ASTNode> variables = new LinkedList<>();

	public KeyCollector() {
		keys = new LinkedList[3];
		keys[0] = types;
		keys[1] = methods;
		keys[2] = variables;
	}

	public void receive(ASTNode node) {
		if (node instanceof Type || node instanceof TypeDeclaration) {
			types.add(node);
		}
		if (node instanceof MethodDeclaration
				|| node instanceof MethodInvocation) {
			methods.add(node);
		}
		if (node instanceof VariableDeclaration) {
			variables.add(node);
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
		return binding.getKey();
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

	private String getVariableBinding(ASTNode node) {
		IBinding binding;
		if (node instanceof VariableDeclaration) {
			binding = ((VariableDeclaration) node).resolveBinding();
		} else {
			throw new IllegalArgumentException();
		}
		return binding.getKey();
	}

	public void createKeys(GraphDatabaseService db, Map<ASTNode, Node> map) {
		createKeys(db, map, TYPE, Labels.TypeKey);
		createKeys(db, map, METHOD, Labels.MethodKey);
		createKeys(db, map, VARIABLE, Labels.VariableKey);
	}

	private void createKeys(GraphDatabaseService db, Map<ASTNode, Node> map,
			int kind, Label label) {
		Map<String, Node> dict = new HashMap<>();
		@SuppressWarnings("unchecked")
		LinkedList<ASTNode> concreteKeys = (LinkedList<ASTNode>) keys[kind];
		for (ASTNode node : concreteKeys) {
			String bindingKey;
			if (kind == TYPE) {
				bindingKey = getTypeBinding(node);
			} else if (kind == METHOD) {
				bindingKey = getMethodBinding(node);
			} else if (kind == VARIABLE) {
				bindingKey = getVariableBinding(node);
			} else {
				throw new IllegalArgumentException();
			}

			if (dict.get(bindingKey) == null) {
				Node typeKey = db.createNode(Labels.Key, label);
				typeKey.setProperty("KEY", bindingKey);
				dict.put(bindingKey, typeKey);
			}

			Node keyNode = dict.get(bindingKey);
			map.get(node).createRelationshipTo(keyNode, RelType.KEY);
		}
	}
}
