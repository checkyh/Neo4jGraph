package ast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import node.BooleanLiteralCreator;
import node.ModifierCreator;
import node.NodeCreator;
import node.NullLiteralCreator;
import node.PrimitiveTypeCreator;
import node.TypeKeyCreator;

import org.eclipse.jdt.core.dom.*;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

/**
 * StoreVisitor stores the whole AST into Neo4j database, mapping nodes in AST
 * to nodes in Neo4j, and edges in AST to relationships in Neo4j.
 * <p>
 * StoreVisitor stores a (one-one) <code>Map</code> from ASTNode to Neo4j's node,
 * and uses this map when setting properties and adding relationships.
 * <p>
 * Working pattern and procedure:
 * <ol><li>create node in <code>preVisit()</code>
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
	
	private NodeCreator modifierCreator;
	private NodeCreator primitiveTypeCreator;
	private NodeCreator booleanLiteralCreator;
	private NodeCreator nullLiteralCreator;
	
	private TypeKeyCreator typeKeyCreator;
	
	Map<ASTNode, Node> map = new HashMap<>();
	
	public static final int MERGE_NODE = 0x1;
	
	private boolean mergeNode;
	
	private void extractFlags(int mode) {
		mergeNode = (mode & MERGE_NODE) != 0;
	}
	
	private StoreVisitor(GraphDatabaseService db, int flags) {
		
		this.db = db;
		
		extractFlags(flags);
		
		if (mergeNode) {
			this.modifierCreator = new ModifierCreator(db);
			this.primitiveTypeCreator = new PrimitiveTypeCreator(db);
			this.booleanLiteralCreator = new BooleanLiteralCreator(db);
			this.nullLiteralCreator = new NullLiteralCreator(db);
		} else {
			this.modifierCreator = null;
			this.primitiveTypeCreator = null;
			this.booleanLiteralCreator = null;
			this.nullLiteralCreator = null;
		}
		
		this.typeKeyCreator = new TypeKeyCreator(db);
	}
	
	public StoreVisitor(GraphDatabaseService db) {
//		this(db, MERGE_NODE);
		this(db, 0x0);
	}
	
	private void addRelationship(ASTNode startNode, Object endNode, String type) {
		if (endNode == null) {
			return;
		}
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
		if (value == null) {
			return;
		}
        map.get(node).setProperty(name, value);
	}

	private Node createNode(ASTNode node) {
		// use class name as Node's label
		// Note that the class name is name of subclass, not "ASTNode" 
		String[] names = node.getClass().getName().split("\\.");
		String name = names[names.length - 1];
		Label label = DynamicLabel.label(name);
		return db.createNode(label);
	}
	
	private void addLabel(ASTNode node, String labelName) {
		map.get(node).addLabel(DynamicLabel.label(labelName));
	}
	
	private void addGeneralLabel(ASTNode node) {
		if (node instanceof Expression) {
			addLabel(node, "Expression");
		} else if (node instanceof Statement) {
			addLabel(node, "Statement");
		} else if (node instanceof Type) {
			addLabel(node, "Type");
		}
		// TODO add more general labels
	}
	
	@Override
	public void preVisit(ASTNode node) {
		Node neo4jNode;
		
		if (mergeNode) {
			if (node instanceof Modifier) {
				neo4jNode = modifierCreator.getInstance(node);
			} else if (node instanceof PrimitiveType) {
				neo4jNode = primitiveTypeCreator.getInstance(node);
			} else if (node instanceof BooleanLiteral) {
				neo4jNode = booleanLiteralCreator.getInstance(node);
			} else if (node instanceof NullLiteral) {
				neo4jNode = nullLiteralCreator.getInstance(node);
			} else {
				neo4jNode = createNode(node);
			}
		} else {
			neo4jNode = createNode(node);
		}
		
		map.put(node, neo4jNode);
		
		addGeneralLabel(node);
	}
	
	@Override
	public void postVisit(ASTNode node) {
		if (node instanceof Type) {
			IBinding binding = ((Type) node).resolveBinding();
			String key;
			if (binding == null) {
				key = "null";
			} else {
				key = binding.getKey();
			}
			Node keyNode = typeKeyCreator.createTypeKey(key);
			keyNode.addLabel(DynamicLabel.label("TypeKey"));
			map.get(node).createRelationshipTo(keyNode, DynamicRelationshipType.withName("KEY"));
		}
		// TODO add MethodKey and VariableKey
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
		//blank c
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
		if (!mergeNode) {
			setProperty(node, "BOOLEAN_VALUE", node.booleanValue());
		}
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
		//blank c
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
		if (!mergeNode) {
			setProperty(node, "KERWORD", node.getKeyword().toString());
		}
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
		if (!mergeNode) {
			setProperty(node, "PRIMITIVE_TYPE_CODE", node.getPrimitiveTypeCode().toString());
		}
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
		addRelationship(node, node.getArray(), ASTProperty.ARRAY);
		addRelationship(node, node.getIndex(), ASTProperty.INDEX);
	}

	@Override
	public void endVisit(ArrayCreation node) {
		addRelationship(node, node.getType(), ASTProperty.TYPE);
		addRelationships(node, node.dimensions(), ASTProperty.DIMENSIONS);
		addRelationship(node, node.getInitializer(), ASTProperty.INITIALIZER);
	}

	@Override
	public void endVisit(ArrayInitializer node) {
		addRelationships(node, node.expressions(), ASTProperty.EXPRESSIONS);
	}

	@Override
	public void endVisit(ArrayType node) {
		addRelationship(node, node.getElementType(), ASTProperty.ELEMENT_TYPE);
	}

	@Override
	public void endVisit(AssertStatement node) {
		
	}

	@Override
	public void endVisit(Assignment node) {
		addRelationship(node, node.getLeftHandSide(), ASTProperty.LEFT_HAND_SIDE);
		addRelationship(node, node.getRightHandSide(), ASTProperty.RIGHT_HAND_SIDE);
	}

	@Override
	public void endVisit(Block node) {
		addRelationships(node, node.statements(), ASTProperty.STATEMENTS);
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
		addRelationship(node, node.getException(), ASTProperty.EXCEPTION);
		addRelationship(node, node.getBody(), ASTProperty.BODY);
		
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
		addRelationship(node, node.getExpression(), ASTProperty.EXPRESSION);
	}

	@Override
	public void endVisit(FieldAccess node) {
		
	}

	@Override
	public void endVisit(FieldDeclaration node) {
		addRelationship(node, node.getJavadoc(), ASTProperty.JAVADOC);
		addRelationships(node, node.modifiers(), ASTProperty.MODIFIERS);
		addRelationship(node, node.getType(), ASTProperty.TYPE);
		addRelationships(node, node.fragments(), ASTProperty.FRAGMENTS);
	}
	
	@Override
	public void endVisit(ForStatement node) {
		addRelationships(node, node.initializers(), ASTProperty.INITIALIZERS);
		addRelationship(node, node.getExpression(), ASTProperty.EXPRESSION);
		addRelationships(node, node.updaters(), ASTProperty.UPDATERS);
		addRelationship(node, node.getBody(), ASTProperty.BODY);
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
		addRelationship(node, node.getLeftOperand(), ASTProperty.LEFT_OPERAND);
		addRelationship(node, node.getRightOperand(), ASTProperty.RIGHT_OPERAND);
		addRelationships(node, node.extendedOperands(), ASTProperty.EXTENDED_OPERANDS);
	}
	
	@Override
	public void endVisit(Initializer node) {
		
	}
	
	@Override
	public void endVisit(InstanceofExpression node) {
		
	}
	
	@Override
	public void endVisit(Javadoc node) {
		addRelationships(node, node.tags(), ASTProperty.TAGS);
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
		addRelationship(node, node.getJavadoc(), ASTProperty.JAVADOC);
		addRelationships(node, node.modifiers(), ASTProperty.MODIFIERS);
		addRelationships(node, node.typeParameters(), ASTProperty.TYPE_PARAMETERS);
		addRelationship(node, node.getReturnType2(), ASTProperty.RETURN_TYPE);
		addRelationship(node, node.getName(), ASTProperty.NAME);
		addRelationships(node, node.parameters(), ASTProperty.PARAMETERS);
		addRelationship(node, node.getBody(), ASTProperty.BODY);
	}
	
	@Override
	public void endVisit(MethodInvocation node) {
		addRelationship(node, node.getExpression(), ASTProperty.EXPRESSION);
		addRelationships(node, node.typeArguments(), ASTProperty.TYPE_ARGUMENTS);
		addRelationship(node, node.getName(), ASTProperty.NAME);
		addRelationships(node, node.arguments(), ASTProperty.ARGUMENTS);
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
		addRelationship(node, node.getJavadoc(), ASTProperty.JAVADOC);
		addRelationships(node, node.annotations(), ASTProperty.ANNOTATIONS);
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
		addRelationship(node, node.getOperand(), ASTProperty.OPERAND);
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
		addRelationship(node, node.getQualifier(), ASTProperty.QUALIFIER);
		addRelationship(node, node.getName(), ASTProperty.NAME);
	}
	
	@Override
	public void endVisit(QualifiedType node) {
		addRelationship(node, node.getQualifier(), ASTProperty.QUALIFIER);
		addRelationship(node, node.getName(), ASTProperty.NAME);
	
	}
	
	@Override
	public void endVisit(ReturnStatement node) {
		addRelationship(node, node.getExpression(), ASTProperty.EXPRESSION);
	}
	
	@Override
	public void endVisit(SimpleName node) {
		
	}
	
	@Override
	public void endVisit(SimpleType node) {
		addRelationship(node, node.getName(), ASTProperty.NAME);
	}
	
	@Override
	public void endVisit(SingleMemberAnnotation node) {
		
	}
	
	@Override
	public void endVisit(SingleVariableDeclaration node) {
		addRelationships(node, node.modifiers(), ASTProperty.MODIFIERS);
		addRelationship(node, node.getType(), ASTProperty.TYPE);
		addRelationship(node, node.getName(), ASTProperty.NAME);
		addRelationship(node, node.getInitializer(), ASTProperty.INITIALIZER);
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
		addRelationships(node, node.fragments(), ASTProperty.FRAGMENTS);
	}
	
	@Override
	public void endVisit(TextElement node) {
		
	}
	
	@Override
	public void endVisit(ThisExpression node) {
		
	}
	
	@Override
	public void endVisit(ThrowStatement node) {
		addRelationship(node, node.getExpression(), ASTProperty.EXPRESSION);
		
	}
	
	@Override
	public void endVisit(TryStatement node) {
		//addRelationship(node, node.getResources(), ASTProperty.RESOURCES);
		addRelationship(node, node.getBody(), ASTProperty.BODY);
		addRelationships(node, node.catchClauses(), ASTProperty.CATCH_CLAUSES);
		addRelationship(node, node.getFinally(), ASTProperty.FINALLY);
	}
	
	@Override
	public void endVisit(TypeDeclaration node) {
		addRelationship(node, node.getJavadoc(), ASTProperty.JAVADOC);
		addRelationships(node, node.modifiers(), ASTProperty.MODIFIERS);
		addRelationship(node, node.getName(), ASTProperty.NAME);
		addRelationships(node, node.typeParameters(), ASTProperty.TYPE_PARAMETERS);
		addRelationship(node, node.getSuperclassType(), ASTProperty.SUPERCLASS_TYPE);
		addRelationships(node, node.superInterfaceTypes(), ASTProperty.SUPER_INTERFACE_TYPES);
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
		addRelationships(node, node.modifiers(), ASTProperty.MODIFIERS);
		addRelationship(node, node.getType(), ASTProperty.TYPE);
		addRelationships(node, node.fragments(), ASTProperty.FRAGMENTS);
	}
	
	@Override
	public void endVisit(VariableDeclarationFragment node) {
		addRelationship(node, node.getName(), ASTProperty.NAME);
		addRelationship(node, node.getInitializer(), ASTProperty.INITIALIZER);
	}
	
	@Override
	public void endVisit(VariableDeclarationStatement node) {
		addRelationships(node, node.modifiers(), ASTProperty.MODIFIERS);
		addRelationship(node, node.getType(), ASTProperty.TYPE);
		addRelationships(node, node.fragments(), ASTProperty.FRAGMENTS);
	}
	
	@Override
	public void endVisit(WhileStatement node) {
		
	}
	
	@Override
	public void endVisit(WildcardType node) {
		
	}
	
}
