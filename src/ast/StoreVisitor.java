package ast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import node.BooleanLiteralCreator;
import node.Labels;
import node.ModifierCreator;
import node.NodeCreator;
import node.NullLiteralCreator;
import node.KeyCollector;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import relationship.RelName;
import relationship.RelType;

/**
 * StoreVisitor stores the whole AST into Neo4j database, mapping nodes in AST
 * to nodes in Neo4j, and edges in AST to relationships in Neo4j.
 * <p>
 * StoreVisitor stores a (one-one) <code>Map</code> from ASTNode to Neo4j's
 * node, and uses this map when setting properties and adding relationships.
 * <p>
 * Working pattern and procedure:
 * <ol>
 * <li>create node in <code>preVisit()</code>
 * <li>set property for node in <code>visit(node)</code>
 * <li>add relationship between nodes in <code>endVisit(node)</code>
 * </ol>
 * <p>
 * Note that <code>addRelationship</code> and <code>addRelationships</code>
 * should be invoked in <code>endVisit</code> (but not in <code>visit</code>),
 * because a relationship can only be created when both nodes are created.
 *
 */
public class StoreVisitor extends ASTVisitor {

	private GraphDatabaseService db;
	private KeyCollector collector;

	private Map<ASTNode, Node> map = new HashMap<>();

	private NodeCreator modifierCreator;
	private NodeCreator booleanLiteralCreator;
	private NodeCreator nullLiteralCreator;

	public StoreVisitor(GraphDatabaseService db, KeyCollector collector) {
		this.db = db;
		this.collector = collector;
		this.modifierCreator = new ModifierCreator(db);
		this.booleanLiteralCreator = new BooleanLiteralCreator(db);
		this.nullLiteralCreator = new NullLiteralCreator(db);
	}
	
	public Map<ASTNode, Node> getMap() {
		return map;
	}

	private void addRelationship(ASTNode startNode, ASTNode endNode,
			String relName) {
		if (endNode == null) {
			return;
		}
		Node from = map.get(startNode);
		Node to = map.get(endNode);

		Relationship rel = from.createRelationshipTo(to, RelType.AST);
		rel.setProperty("NAME", relName);
	}

	@SuppressWarnings("rawtypes")
	private void addRelationships(ASTNode startNode, List endNodes, String type) {
		for (Object obj : endNodes) {
			ASTNode endNode = (ASTNode) obj;
			addRelationship(startNode, endNode, type);
		}
	}

	private void setProperty(ASTNode node, String name, Object value) {
		if (value == null) {
			return;
		}
		map.get(node).setProperty(name, value);
	}

	private void addLabel(ASTNode node, String labelName) {
		map.get(node).addLabel(DynamicLabel.label(labelName));
	}

	private void addLabel(ASTNode node, Label label) {
		map.get(node).addLabel(label);
	}

	private void addRawLabel(ASTNode node) {
		// use class name as Node's label
		// Note that the class name is name of subclass, not "ASTNode"
		String[] names = node.getClass().getName().split("\\.");
		String name = names[names.length - 1];
		addLabel(node, name);
	}

	private void addGeneralLabel(ASTNode node) {
		if (node instanceof BodyDeclaration) {
			addLabel(node, Labels.BodyDeclaration);
		} else if (node instanceof AbstractTypeDeclaration) {
			addLabel(node, Labels.AbstractTypeDeclaration);
		} else if (node instanceof Comment) {
			addLabel(node, Labels.Comment);
		} else if (node instanceof Expression) {
			addLabel(node, Labels.Expression);
		} else if (node instanceof Annotation) {
			addLabel(node, Labels.Annatation);
		} else if (node instanceof Name) {
			addLabel(node, Labels.Name);
		} else if (node instanceof Statement) {
			addLabel(node, Labels.Statement);
		} else if (node instanceof Type) {
			addLabel(node, Labels.Type);
		} else if (node instanceof VariableDeclaration) {
			addLabel(node, Labels.VariableDeclaration);
		}
	}

	@Override
	public void preVisit(ASTNode node) {
		Node neo4jNode;

		if (node instanceof Modifier) {
			neo4jNode = modifierCreator.getInstance(node);
		} else if (node instanceof BooleanLiteral) {
			neo4jNode = booleanLiteralCreator.getInstance(node);
		} else if (node instanceof NullLiteral) {
			neo4jNode = nullLiteralCreator.getInstance(node);
		} else {
			neo4jNode = db.createNode();
		}

		map.put(node, neo4jNode);

		addRawLabel(node);
		addGeneralLabel(node);

		collector.receive(node);
	}

	@Override
	public void postVisit(ASTNode node) {

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
		// blank c
		return true;
	}

	@Override
	public boolean visit(ArrayType node) {
		setProperty(node, "DIMENTIONS", node.getDimensions());
		return true;
	}

	@Override
	public boolean visit(AssertStatement node) {
		return true;
	}

	@Override
	public boolean visit(Assignment node) {
		setProperty(node, "OPERATOR", node.getOperator().toString());
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
		// property set in NodeCreator
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
		// blank c
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
		setProperty(node, "JAVADOC", node.getJavadoc());
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
		setProperty(node, "OPERATOR", node.getOperator().toString());
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
		setProperty(node, "CONSTRUCTOR", node.isConstructor());
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
		// property set in NodeCreator
		return true;
	}

	@Override
	public boolean visit(NormalAnnotation node) {
		return true;
	}

	@Override
	public boolean visit(NullLiteral node) {
		// this method should be left blank
		return true;
	}

	@Override
	public boolean visit(NumberLiteral node) {
		setProperty(node, "TOKEN", node.getToken());
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
		setProperty(node, "OPERATOR", node.getOperator().toString());
		return true;
	}

	@Override
	public boolean visit(PrefixExpression node) {
		return true;
	}

	@Override
	public boolean visit(PrimitiveType node) {
		setProperty(node, "PRIMITIVE_TYPE_CODE", node.getPrimitiveTypeCode()
				.toString());
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
		setProperty(node, "IDENTIFIER", node.getIdentifier());
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
		setProperty(node, "VARARGS", node.isVarargs());
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
		setProperty(node, "TAG_NAME", node.getTagName());
		return true;
	}

	@Override
	public boolean visit(TextElement node) {
		setProperty(node, "TEXT", node.getText());
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
		setProperty(node, "INTERFACE", node.isInterface());
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
		addRelationship(node, node.getArray(), RelName.ARRAY);
		addRelationship(node, node.getIndex(), RelName.INDEX);
	}

	@Override
	public void endVisit(ArrayCreation node) {
		addRelationship(node, node.getType(), RelName.TYPE);
		addRelationships(node, node.dimensions(), RelName.DIMENSIONS);
		addRelationship(node, node.getInitializer(), RelName.INITIALIZER);
	}

	@Override
	public void endVisit(ArrayInitializer node) {
		addRelationships(node, node.expressions(), RelName.EXPRESSIONS);
	}

	@Override
	public void endVisit(ArrayType node) {
		addRelationship(node, node.getElementType(), RelName.ELEMENT_TYPE);
	}

	@Override
	public void endVisit(AssertStatement node) {

	}

	@Override
	public void endVisit(Assignment node) {
		addRelationship(node, node.getLeftHandSide(), RelName.LEFT_HAND_SIDE);
		addRelationship(node, node.getRightHandSide(), RelName.RIGHT_HAND_SIDE);
	}

	@Override
	public void endVisit(Block node) {
		addRelationships(node, node.statements(), RelName.STATEMENTS);
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
		addRelationship(node, node.getException(), RelName.EXCEPTION);
		addRelationship(node, node.getBody(), RelName.BODY);

	}

	@Override
	public void endVisit(CharacterLiteral node) {

	}

	@Override
	public void endVisit(ClassInstanceCreation node) {

	}

	@Override
	public void endVisit(CompilationUnit node) {
		addRelationship(node, node.getPackage(), RelName.PACKAGE);
		addRelationships(node, node.imports(), RelName.IMPORTS);
		addRelationships(node, node.types(), RelName.TYPES);
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
		addRelationship(node, node.getExpression(), RelName.EXPRESSION);
	}

	@Override
	public void endVisit(FieldAccess node) {
		addRelationship(node, node.getExpression(), RelName.EXPRESSION);
		addRelationship(node, node.getName(), RelName.NAME);
	}

	@Override
	public void endVisit(FieldDeclaration node) {
		addRelationship(node, node.getJavadoc(), RelName.JAVADOC);
		addRelationships(node, node.modifiers(), RelName.MODIFIERS);
		addRelationship(node, node.getType(), RelName.TYPE);
		addRelationships(node, node.fragments(), RelName.FRAGMENTS);
	}

	@Override
	public void endVisit(ForStatement node) {
		addRelationships(node, node.initializers(), RelName.INITIALIZERS);
		addRelationship(node, node.getExpression(), RelName.EXPRESSION);
		addRelationships(node, node.updaters(), RelName.UPDATERS);
		addRelationship(node, node.getBody(), RelName.BODY);
	}

	@Override
	public void endVisit(IfStatement node) {

	}

	@Override
	public void endVisit(ImportDeclaration node) {
		addRelationship(node, node.getName(), RelName.NAME);
	}

	@Override
	public void endVisit(InfixExpression node) {
		addRelationship(node, node.getLeftOperand(), RelName.LEFT_OPERAND);
		addRelationship(node, node.getRightOperand(), RelName.RIGHT_OPERAND);
		addRelationships(node, node.extendedOperands(),
				RelName.EXTENDED_OPERANDS);
	}

	@Override
	public void endVisit(Initializer node) {

	}

	@Override
	public void endVisit(InstanceofExpression node) {

	}

	@Override
	public void endVisit(Javadoc node) {
		addRelationships(node, node.tags(), RelName.TAGS);
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
		addRelationship(node, node.getJavadoc(), RelName.JAVADOC);
		addRelationships(node, node.modifiers(), RelName.MODIFIERS);
		addRelationships(node, node.typeParameters(), RelName.TYPE_PARAMETERS);
		addRelationship(node, node.getReturnType2(), RelName.RETURN_TYPE);
		addRelationship(node, node.getName(), RelName.NAME);
		addRelationships(node, node.parameters(), RelName.PARAMETERS);
		addRelationship(node, node.getBody(), RelName.BODY);
	}

	@Override
	public void endVisit(MethodInvocation node) {
		addRelationship(node, node.getExpression(), RelName.EXPRESSION);
		addRelationships(node, node.typeArguments(), RelName.TYPE_ARGUMENTS);
		addRelationship(node, node.getName(), RelName.NAME);
		addRelationships(node, node.arguments(), RelName.ARGUMENTS);
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
		// blank
	}

	@Override
	public void endVisit(PackageDeclaration node) {
		addRelationship(node, node.getJavadoc(), RelName.JAVADOC);
		addRelationships(node, node.annotations(), RelName.ANNOTATIONS);
		addRelationship(node, node.getName(), RelName.NAME);
	}

	@Override
	public void endVisit(ParameterizedType node) {

	}

	@Override
	public void endVisit(ParenthesizedExpression node) {

	}

	@Override
	public void endVisit(PostfixExpression node) {
		addRelationship(node, node.getOperand(), RelName.OPERAND);
	}

	@Override
	public void endVisit(PrefixExpression node) {

	}

	@Override
	public void endVisit(PrimitiveType node) {
		// blank
	}

	@Override
	public void endVisit(QualifiedName node) {
		addRelationship(node, node.getQualifier(), RelName.QUALIFIER);
		addRelationship(node, node.getName(), RelName.NAME);
	}

	@Override
	public void endVisit(QualifiedType node) {
		addRelationship(node, node.getQualifier(), RelName.QUALIFIER);
		addRelationship(node, node.getName(), RelName.NAME);

	}

	@Override
	public void endVisit(ReturnStatement node) {
		addRelationship(node, node.getExpression(), RelName.EXPRESSION);
	}

	@Override
	public void endVisit(SimpleName node) {

	}

	@Override
	public void endVisit(SimpleType node) {
		addRelationship(node, node.getName(), RelName.NAME);
	}

	@Override
	public void endVisit(SingleMemberAnnotation node) {

	}

	@Override
	public void endVisit(SingleVariableDeclaration node) {
		addRelationships(node, node.modifiers(), RelName.MODIFIERS);
		addRelationship(node, node.getType(), RelName.TYPE);
		addRelationship(node, node.getName(), RelName.NAME);
		addRelationship(node, node.getInitializer(), RelName.INITIALIZER);
	}

	@Override
	public void endVisit(StringLiteral node) {

	}

	@Override
	public void endVisit(SuperConstructorInvocation node) {
		addRelationship(node, node.getExpression(), RelName.EXPRESSION);
		addRelationships(node, node.typeArguments(), RelName.TYPE_ARGUMENTS);
		addRelationships(node, node.arguments(), RelName.ARGUMENTS);
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
		addRelationships(node, node.fragments(), RelName.FRAGMENTS);
	}

	@Override
	public void endVisit(TextElement node) {

	}

	@Override
	public void endVisit(ThisExpression node) {
		addRelationship(node, node.getQualifier(), RelName.QUALIFIER);
	}

	@Override
	public void endVisit(ThrowStatement node) {
		addRelationship(node, node.getExpression(), RelName.EXPRESSION);

	}

	@Override
	public void endVisit(TryStatement node) {
		// addRelationship(node, node.getResources(), RelName.RESOURCES);
		addRelationship(node, node.getBody(), RelName.BODY);
		addRelationships(node, node.catchClauses(), RelName.CATCH_CLAUSES);
		addRelationship(node, node.getFinally(), RelName.FINALLY);
	}

	@Override
	public void endVisit(TypeDeclaration node) {
		addRelationship(node, node.getJavadoc(), RelName.JAVADOC);
		addRelationships(node, node.modifiers(), RelName.MODIFIERS);
		addRelationship(node, node.getName(), RelName.NAME);
		addRelationships(node, node.typeParameters(), RelName.TYPE_PARAMETERS);
		addRelationship(node, node.getSuperclassType(), RelName.SUPERCLASS_TYPE);
		addRelationships(node, node.superInterfaceTypes(),
				RelName.SUPER_INTERFACE_TYPES);
		addRelationships(node, node.bodyDeclarations(),
				RelName.BODY_DECLARATIONS);
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
		addRelationships(node, node.modifiers(), RelName.MODIFIERS);
		addRelationship(node, node.getType(), RelName.TYPE);
		addRelationships(node, node.fragments(), RelName.FRAGMENTS);
	}

	@Override
	public void endVisit(VariableDeclarationFragment node) {
		addRelationship(node, node.getName(), RelName.NAME);
		addRelationship(node, node.getInitializer(), RelName.INITIALIZER);
	}

	@Override
	public void endVisit(VariableDeclarationStatement node) {
		addRelationships(node, node.modifiers(), RelName.MODIFIERS);
		addRelationship(node, node.getType(), RelName.TYPE);
		addRelationships(node, node.fragments(), RelName.FRAGMENTS);
	}

	@Override
	public void endVisit(WhileStatement node) {

	}

	@Override
	public void endVisit(WildcardType node) {

	}

}
