package ast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import node.BooleanLiteralCreator;
import node.ModifierCreator;
import node.NodeCreator;

import org.eclipse.jdt.core.dom.*;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

public class StoreVisitor extends ASTVisitor {
	
	private GraphDatabaseService db;
	
	private Node parent = null;
	private Node current = null;
	private RelationshipType rWeak = DynamicRelationshipType.withName("Weak");
	
	private NodeCreator modifierCreator;
	private NodeCreator booleanLiteralCreator;
	
	Map<ASTNode, Node> map = new HashMap<>();
	
	public StoreVisitor(GraphDatabaseService db) {
		this.db = db;
		this.modifierCreator = new ModifierCreator(db);
		this.booleanLiteralCreator = new BooleanLiteralCreator(db);
	}
	
	private void createNode(ASTNode node) {
		Node neo4jNode;
//		if (node instanceof Modifier) {
//			neo4jNode = modifierCreator.getInstance(node);
//		} else if (node instanceof BooleanLiteral) {
//			neo4jNode = booleanLiteralCreator.getInstance(node);
//		} else {

			String[] names = node.getClass().getName().split("\\.");
			String name = names[names.length - 1];
			Label label = DynamicLabel.label(name);
			neo4jNode = db.createNode(label);
//		}
		
		map.put(node, neo4jNode);
	}
	
	private void addRelationship(ASTNode startNode, Object endNode, String type) {
		Node from = map.get(startNode);
		Node to = map.get((ASTNode) endNode);
		from.createRelationshipTo(to, DynamicRelationshipType.withName(type));
	}
	
	@SuppressWarnings("rawtypes")
	private void addRelationships(ASTNode startNode, List endNodes, String type) {
		for (Object endNode : endNodes) {
			addRelationship(startNode, endNode, type);
		}
	}
	
	private void setProperty(ASTNode node, String name, Object value) {
		if (value != null) {
			map.get(node).setProperty(name, value);
		}
	}
	
	@Override
	public void preVisit(ASTNode node) {
		
		createNode(node);
		
		this.current = map.get(node);
		ASTNode p = node.getParent();
		if (p == null) {
			this.parent = null;
		} else {
			this.parent = map.get(p);
		}

		if (parent != null) {
//			parent.createRelationshipTo(current, rWeak);
		}
		
	}
	
	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		return true;
	}
	
	@Override
	public boolean visit(AnnotationTypeMemberDeclaration node) {
		return true;
	}
	
	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		return true;
	}

	@Override
	public boolean visit(ArrayAccess node) {
		return true;
	}

	@Override
	public boolean visit(ArrayCreation node) {
		return true;
	}

	@Override
	public boolean visit(ArrayInitializer node) {
		return true;
	}

	@Override
	public boolean visit(ArrayType node) {
		return true;
	}

	@Override
	public boolean visit(AssertStatement node) {
		return true;
	}

	@Override
	public boolean visit(Assignment node) {
		return true;
	}

	@Override
	public boolean visit(Block node) {
		return true;
	}

	@Override
	public boolean visit(BlockComment node) {
		return true;
	}

	@Override
	public boolean visit(BooleanLiteral node) {
		// this method should be left blank
		return true;
	}

	@Override
	public boolean visit(BreakStatement node) {
		return true;
	}

	@Override
	public boolean visit(CastExpression node) {
		return true;
	}
	
	@Override
	public boolean visit(CatchClause node) {
		return true;
	}
	
	@Override
	public boolean visit(CharacterLiteral node) {
		return true;
	}
	
	@Override
	public boolean visit(ClassInstanceCreation node) {
		return true;
	}
	
	@Override
	public boolean visit(CompilationUnit node) {
		return true;
	}
	
	@Override
	public boolean visit(ConditionalExpression node) {
		return true;
	}
	
	@Override
	public boolean visit(ConstructorInvocation node) {
		return true;
	}
	
	@Override
	public boolean visit(ContinueStatement node) {
		return true;
	}
	
	@Override
	public boolean visit(DoStatement node) {
		return true;
	}
	
	@Override
	public boolean visit(EmptyStatement node) {
		return true;
	}
	
	@Override
	public boolean visit(EnhancedForStatement node) {
		return true;
	}
	
	@Override
	public boolean visit(EnumConstantDeclaration node) {
		return true;
	}
	
	@Override
	public boolean visit(EnumDeclaration node) {
		return true;
	}
	
	@Override
	public boolean visit(ExpressionStatement node) {
		return true;
	}

	@Override
	public boolean visit(FieldAccess node) {
		return true;
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		return true;
	}
	
	@Override
	public boolean visit(ForStatement node) {
		return true;
	}
	
	@Override
	public boolean visit(IfStatement node) {
		return true;
	}
	
	@Override
	public boolean visit(ImportDeclaration node) {
		setProperty(node, "STATIC", node.isStatic());
		setProperty(node, "ON_DEMAND", node.isOnDemand());
		return true;
	}
	
	@Override
	public boolean visit(InfixExpression node) {
		return true;
	}
	
	@Override
	public boolean visit(Initializer node) {
		return true;
	}
	
	@Override
	public boolean visit(InstanceofExpression node) {
		return true;
	}
	
	@Override
	public boolean visit(Javadoc node) {
		return true;
	}
	
	@Override
	public boolean visit(LabeledStatement node) {
		return true;
	}
	
	@Override
	public boolean visit(MarkerAnnotation node) {
		return true;
	}
	
	@Override
	public boolean visit(MemberRef node) {
		return true;
	}
	
	@Override
	public boolean visit(MemberValuePair node) {
		return true;
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		return true;
	}
	
	@Override
	public boolean visit(MethodInvocation node) {
		return true;
	}
	
	@Override
	public boolean visit(MethodRef node) {
		return true;
	}
	
	@Override
	public boolean visit(MethodRefParameter node) {
		return true;
	}
	
	@Override
	public boolean visit(Modifier node) {
		// this method should be left blank
		return true;
	}
	
	@Override
	public boolean visit(NormalAnnotation node) {
		return true;
	}
	
	@Override
	public boolean visit(NullLiteral node) {
		return true;
	}
	
	@Override
	public boolean visit(NumberLiteral node) {
		return true;
	}
	
	@Override
	public boolean visit(PackageDeclaration node) {
		return true;
	}
	
	@Override
	public boolean visit(ParameterizedType node) {
		return true;
	}
	
	@Override
	public boolean visit(ParenthesizedExpression node) {
		return true;
	}
	
	@Override
	public boolean visit(PostfixExpression node) {
		return true;
	}
	
	@Override
	public boolean visit(PrefixExpression node) {
		return true;
	}
	
	@Override
	public boolean visit(PrimitiveType node) {
		return true;
	}
	
	@Override
	public boolean visit(QualifiedName node) {
		return true;
	}
	
	@Override
	public boolean visit(QualifiedType node) {
		return true;
	}
	
	@Override
	public boolean visit(ReturnStatement node) {
		return true;
	}
	
	@Override
	public boolean visit(SimpleName node) {
		return true;
	}
	
	@Override
	public boolean visit(SimpleType node) {
		return true;
	}
	
	@Override
	public boolean visit(SingleMemberAnnotation node) {
		return true;
	}
	
	@Override
	public boolean visit(SingleVariableDeclaration node) {
		return true;
	}
	
	@Override
	public boolean visit(StringLiteral node) {
		return true;
	}
	
	@Override
	public boolean visit(SuperConstructorInvocation node) {
		return true;
	}
	
	@Override
	public boolean visit(SuperFieldAccess node) {
		return true;
	}
	
	@Override
	public boolean visit(SuperMethodInvocation node) {
		return true;
	}
	
	@Override
	public boolean visit(SwitchCase node) {
		return true;
	}
	
	@Override
	public boolean visit(SwitchStatement node) {
		return true;
	}
	
	@Override
	public boolean visit(SynchronizedStatement node) {
		return true;
	}
	
	@Override
	public boolean visit(TagElement node) {
		return true;
	}
	
	@Override
	public boolean visit(TextElement node) {
		return true;
	}
	
	@Override
	public boolean visit(ThisExpression node) {
		return true;
	}
	
	@Override
	public boolean visit(ThrowStatement node) {
		return true;
	}
	
	@Override
	public boolean visit(TryStatement node) {
		return true;
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		setProperty(node, "JAVADOC", node.getJavadoc());
		setProperty(node, "INTERFACE", node.isInterface());
		setProperty(node, "SUPERCLASS_TYPE", node.getSuperclassType());
		return true;
	}
	
	@Override
	public boolean visit(TypeDeclarationStatement node) {
		return true;
	}
	
	@Override
	public boolean visit(TypeLiteral node) {
		return true;
	}
	
	@Override
	public boolean visit(TypeParameter node) {
		return true;
	}
	
	@Override
	public boolean visit(VariableDeclarationExpression node) {
		return true;
	}
	
	@Override
	public boolean visit(VariableDeclarationFragment node) {
		return true;
	}
	
	@Override
	public boolean visit(VariableDeclarationStatement node) {
		return true;
	}
	
	@Override
	public boolean visit(WhileStatement node) {
		return true;
	}
	
	@Override
	public boolean visit(WildcardType node) {
		return true;
	}

	@Override
	public void endVisit(AnnotationTypeDeclaration node) {
		
	}
	
	@Override
	public void endVisit(AnnotationTypeMemberDeclaration node) {
		
	}
	
	@Override
	public void endVisit(AnonymousClassDeclaration node) {
		
	}

	@Override
	public void endVisit(ArrayAccess node) {
		
	}

	@Override
	public void endVisit(ArrayCreation node) {
		
	}

	@Override
	public void endVisit(ArrayInitializer node) {
		
	}

	@Override
	public void endVisit(ArrayType node) {
		
	}

	@Override
	public void endVisit(AssertStatement node) {
		
	}

	@Override
	public void endVisit(Assignment node) {
		
	}

	@Override
	public void endVisit(Block node) {
		
	}

	@Override
	public void endVisit(BlockComment node) {
		
	}

	@Override
	public void endVisit(BooleanLiteral node) {
		
	}

	@Override
	public void endVisit(BreakStatement node) {
		
	}

	@Override
	public void endVisit(CastExpression node) {
		
	}
	
	@Override
	public void endVisit(CatchClause node) {
		
	}
	
	@Override
	public void endVisit(CharacterLiteral node) {
		
	}
	
	@Override
	public void endVisit(ClassInstanceCreation node) {
		
	}
	
	@Override
	public void endVisit(CompilationUnit node) {
		addRelationship(node, node.getPackage(), ASTProperty.PACKAGE);
		addRelationships(node, node.imports(), ASTProperty.IMPORTS);
		addRelationships(node, node.types(), ASTProperty.TYPES);
	}
	
	@Override
	public void endVisit(ConditionalExpression node) {
		
	}
	
	@Override
	public void endVisit(ConstructorInvocation node) {
		
	}
	
	@Override
	public void endVisit(ContinueStatement node) {
		
	}
	
	@Override
	public void endVisit(DoStatement node) {
		
	}
	
	@Override
	public void endVisit(EmptyStatement node) {
		
	}
	
	@Override
	public void endVisit(EnhancedForStatement node) {
		
	}
	
	@Override
	public void endVisit(EnumConstantDeclaration node) {
		
	}
	
	@Override
	public void endVisit(EnumDeclaration node) {
		
	}
	
	@Override
	public void endVisit(ExpressionStatement node) {
		
	}

	@Override
	public void endVisit(FieldAccess node) {
		
	}

	@Override
	public void endVisit(FieldDeclaration node) {
		addRelationships(node, node.modifiers(), ASTProperty.MODIFIERS);
		addRelationship(node, node.getType(), ASTProperty.TYPE);
		addRelationships(node, node.fragments(), ASTProperty.FRAGMENTS);
	}
	
	@Override
	public void endVisit(ForStatement node) {
		
	}
	
	@Override
	public void endVisit(IfStatement node) {
		
	}
	
	@Override
	public void endVisit(ImportDeclaration node) {
		addRelationship(node, node.getName(), ASTProperty.NAME);
	}
	
	@Override
	public void endVisit(InfixExpression node) {
		
	}
	
	@Override
	public void endVisit(Initializer node) {
		
	}
	
	@Override
	public void endVisit(InstanceofExpression node) {
		
	}
	
	@Override
	public void endVisit(Javadoc node) {
		
	}
	
	@Override
	public void endVisit(LabeledStatement node) {
		
	}
	
	@Override
	public void endVisit(MarkerAnnotation node) {
		
	}
	
	@Override
	public void endVisit(MemberRef node) {
		
	}
	
	@Override
	public void endVisit(MemberValuePair node) {
		
	}
	
	@Override
	public void endVisit(MethodDeclaration node) {
		addRelationships(node, node.modifiers(), ASTProperty.MODIFIERS);
		addRelationship(node, node.getName(), ASTProperty.NAME);
		addRelationships(node, node.parameters(), ASTProperty.PARAMETERS);
		addRelationship(node, node.getBody(), ASTProperty.BODY);
	}
	
	@Override
	public void endVisit(MethodInvocation node) {
		
	}
	
	@Override
	public void endVisit(MethodRef node) {
		
	}
	
	@Override
	public void endVisit(MethodRefParameter node) {
		
	}
	
	@Override
	public void endVisit(Modifier node) {
		
	}
	
	@Override
	public void endVisit(NormalAnnotation node) {
		
	}
	
	@Override
	public void endVisit(NullLiteral node) {
		
	}
	
	@Override
	public void endVisit(NumberLiteral node) {
		
	}
	
	@Override
	public void endVisit(PackageDeclaration node) {
		addRelationship(node, node.getName(), ASTProperty.NAME);
	}
	
	@Override
	public void endVisit(ParameterizedType node) {
		
	}
	
	@Override
	public void endVisit(ParenthesizedExpression node) {
		
	}
	
	@Override
	public void endVisit(PostfixExpression node) {
		
	}
	
	@Override
	public void endVisit(PrefixExpression node) {
		
	}
	
	@Override
	public void endVisit(PrimitiveType node) {
		
	}
	
	@Override
	public void endVisit(QualifiedName node) {
		
	}
	
	@Override
	public void endVisit(QualifiedType node) {
		
	}
	
	@Override
	public void endVisit(ReturnStatement node) {
		
	}
	
	@Override
	public void endVisit(SimpleName node) {
		
	}
	
	@Override
	public void endVisit(SimpleType node) {
		
	}
	
	@Override
	public void endVisit(SingleMemberAnnotation node) {
		
	}
	
	@Override
	public void endVisit(SingleVariableDeclaration node) {
		
	}
	
	@Override
	public void endVisit(StringLiteral node) {
		
	}
	
	@Override
	public void endVisit(SuperConstructorInvocation node) {
		
	}
	
	@Override
	public void endVisit(SuperFieldAccess node) {
		
	}
	
	@Override
	public void endVisit(SuperMethodInvocation node) {
		
	}
	
	@Override
	public void endVisit(SwitchCase node) {
		
	}
	
	@Override
	public void endVisit(SwitchStatement node) {
		
	}
	
	@Override
	public void endVisit(SynchronizedStatement node) {
		
	}
	
	@Override
	public void endVisit(TagElement node) {
		
	}
	
	@Override
	public void endVisit(TextElement node) {
		
	}
	
	@Override
	public void endVisit(ThisExpression node) {
		
	}
	
	@Override
	public void endVisit(ThrowStatement node) {
		
	}
	
	@Override
	public void endVisit(TryStatement node) {
		
	}
	
	@Override
	public void endVisit(TypeDeclaration node) {
		addRelationships(node, node.modifiers(), ASTProperty.MODIFIERS);
		addRelationship(node, node.getName(), ASTProperty.NAME);
		addRelationships(node, node.typeParameters(), ASTProperty.TYPE_PARAMETERS);
		addRelationships(node, node.bodyDeclarations(), ASTProperty.BODY_DECLARATIONS);
	}

	@Override
	public void endVisit(TypeDeclarationStatement node) {
		
	}
	
	@Override
	public void endVisit(TypeLiteral node) {
		
	}
	
	@Override
	public void endVisit(TypeParameter node) {
		
	}
	
	@Override
	public void endVisit(VariableDeclarationExpression node) {
		
	}
	
	@Override
	public void endVisit(VariableDeclarationFragment node) {
		
	}
	
	@Override
	public void endVisit(VariableDeclarationStatement node) {
		
	}
	
	@Override
	public void endVisit(WhileStatement node) {
		
	}
	
	@Override
	public void endVisit(WildcardType node) {
		
	}
	
}
