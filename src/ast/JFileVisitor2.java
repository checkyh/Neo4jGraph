package ast;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
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
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LineComment;
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
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;

import relationship.ASTProperty;

/**
 * methods of visiting different kinds of nodes
 * 
 * @author xiaoq_zhu
 *
 */
public class JFileVisitor2 extends ASTVisitor {
	// private Map<Integer,Long> nodes=new HashMap<Integer,Long>();
	private List<ASTNode> nodes = new ArrayList<ASTNode>();
	private List<Long> ids = new ArrayList<Long>();
	private long cuid;
//	private int pid;
	private final String filename;
	// private final Query2 query2;
	private final GraphDatabaseService db;
	private Query2 query2;

	public JFileVisitor2(String dpath, String filename, int pid) {
//		this.pid = pid;
		this.db = new GraphDatabaseFactory().newEmbeddedDatabase(dpath);
		this.query2=new Query2(db, pid);
		this.filename = filename;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 * @since 3.1
	 */
	public boolean visit(AnnotationTypeDeclaration node) {
		/* Modifier */
		@SuppressWarnings("unchecked")
		List<IExtendedModifier> modifiers = node.modifiers();
		for (int i = 0; i < modifiers.size(); i++) {
			IExtendedModifier modifier = modifiers.get(i);
			this.addModifier(node, modifier);
		}
		/* java doc */
		Javadoc javadoc = node.getJavadoc();
		if (javadoc != null) {
			this.addJavadoc(node, javadoc);
		}
		/* BODY DECLARATIONS */
		@SuppressWarnings("unchecked")
		List<BodyDeclaration> bodyDeclarations = node.bodyDeclarations();
		this.addBodyDeclarations(node, bodyDeclarations);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 * @since 3.1
	 */
	public boolean visit(AnnotationTypeMemberDeclaration node) {
		/* Modifier */
		@SuppressWarnings("unchecked")
		List<IExtendedModifier> modifiers = node.modifiers();
		for (int i = 0; i < modifiers.size(); i++) {
			IExtendedModifier modifier = modifiers.get(i);
			this.addModifier(node, modifier);
		}
		/* java doc */
		Javadoc javadoc = node.getJavadoc();
		if (javadoc != null) {
			this.addJavadoc(node, javadoc);
		}
		/* Type */
		Type type = node.getType();
		this.addType(node, type, ASTProperty.TYPE);
		/* Default */
		Expression defau = node.getDefault();
		if (defau != null)
			this.addExpression(node, defau, ASTProperty.DEFAULT);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(AnonymousClassDeclaration node) {
		/* BODY DECLARATIONS */
		@SuppressWarnings("unchecked")
		List<BodyDeclaration> bodyDeclarations = node.bodyDeclarations();
		this.addBodyDeclarations(node, bodyDeclarations);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(ArrayAccess node) {
		this.addExpression(node, node.getArray(), ASTProperty.ARRAY);
		this.addExpression(node, node.getIndex(), ASTProperty.INDEX);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(ArrayCreation node) {
		/* type */
		this.addType(node, node.getType(), ASTProperty.TYPE);
		/* dimensions */
		@SuppressWarnings("unchecked")
		List<Expression> dimensions = node.dimensions();
		for (int i = 0; i < dimensions.size(); i++) {
			this.addExpression(node, dimensions.get(i), ASTProperty.DIMENSIONS);
		}
		/* initializer */
		ArrayInitializer ini = node.getInitializer();
		if (ini != null) {
			this.addExpression(node, ini, ASTProperty.INITIALIZER);
		}
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(ArrayInitializer node) {
		@SuppressWarnings("unchecked")
		List<Expression> expressions = node.expressions();
		for (int i = 0; i < expressions.size(); i++) {
			this.addExpression(node, expressions.get(i),
					ASTProperty.EXPRESSIONS);
		}
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	@SuppressWarnings("deprecation")
	public boolean visit(ArrayType node) {
		this.addType(node, node.getComponentType(), ASTProperty.COMPONENT_TYPE);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(AssertStatement node) {
		this.addExpression(node, node.getExpression(), ASTProperty.EXPRESSION);
		this.addExpression(node, node.getMessage(), ASTProperty.MESSAGE);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(Assignment node) {
		this.addExpression(node, node.getLeftHandSide(),
				ASTProperty.LEFT_HAND_SIDE);
		this.addExpression(node, node.getRightHandSide(),
				ASTProperty.RIGHT_HAND_SIDE);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * relations The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(Block node) {
		@SuppressWarnings("unchecked")
		List<Statement> statements = node.statements();
		for (int i = 0; i < statements.size(); i++)
			this.addStatement(node, statements.get(i), ASTProperty.STATEMENTS);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 * <p>
	 * Note: {@link LineComment} and {@link BlockComment} nodes are not
	 * considered part of main structure of the AST. This method will only be
	 * called if a client goes out of their way to visit this kind of node
	 * explicitly.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 * @since 3.0
	 */
	public boolean visit(BlockComment node) {
		return false;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(BooleanLiteral node) {
		return false;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(BreakStatement node) {
		return false;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(CastExpression node) {
		this.addType(node, node.getType(), ASTProperty.TYPE);
		this.addExpression(node, node.getExpression(), ASTProperty.EXPRESSION);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(CatchClause node) {
		this.addSingleVariableDeclaration(node, node.getException(),
				ASTProperty.EXCEPTION);
		this.addStatement(node, node.getBody(), ASTProperty.BODY);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(CharacterLiteral node) {
		return false;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(ClassInstanceCreation node) {
		/* Expression */
		Expression exp = node.getExpression();
		if (exp != null)
			this.addExpression(node, node.getExpression(),
					ASTProperty.EXPRESSION);
		/* TYPE ARGUMENTS */
		@SuppressWarnings("unchecked")
		List<Type> tas = node.typeArguments();
		for (int i = 0; i < tas.size(); i++) {
			this.addType(node, tas.get(i), ASTProperty.TYPE_ARGUMENTS);
		}

		/* Type */
		Type type = node.getType();
		this.addType(node, type, ASTProperty.TYPE);

		/* Arguments */
		@SuppressWarnings("unchecked")
		List<Expression> arguments = node.arguments();
		for (int i = 0; i < arguments.size(); i++)
			this.addExpression(node, arguments.get(i), ASTProperty.ARGUMENTS);

		/* ANONYMOUS CLASS DECLARATION */
		AnonymousClassDeclaration acd = node.getAnonymousClassDeclaration();
		if (acd != null) {
			this.addAnonymousClassDeclaration(node, acd);
		}
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(CompilationUnit node) {
		org.neo4j.graphdb.Transaction tx = db.beginTx();
		try {
			/* create the root node of the file */
			/* construct the query sentence by which to store the node */
			long id1 = query2.cuQuery(node, filename);
			// nodes.put(node.getStartPosition(), id1);
			this.ids.add(id1);
			this.setCuid(id1);
			this.nodes.add(node);

			/* add child nodes and relationships between <node> and its children */
			long id2 = -1;
			/* PackageDeclaration */
			PackageDeclaration packageNode = node.getPackage();
			id2 = query2.pdQuery(packageNode);
			this.nodes.add(packageNode);
			this.ids.add(id2);
			// this.nodes.put(packageNode.getStartPosition(), id2);
			query2.addRelation(id1, id2, ASTProperty.PACKAGE);

			/* ImportDeclaration */
			@SuppressWarnings("unchecked")
			List<ImportDeclaration> imports = node.imports();
			if (imports != null) {
				for (int i = 0; i < imports.size(); i++) {
					ImportDeclaration importNode = imports.get(i);
					id2 = query2.idQuery(importNode);
					// this.nodes.put(importNode.getStartPosition(), id2);
					this.nodes.add(importNode);
					this.ids.add(id2);
					query2.addRelation(id1, id2, ASTProperty.IMPORTS);
				}
			}

			/* TypeDeclaration */
			@SuppressWarnings("unchecked")
			List<AbstractTypeDeclaration> types = node.types();
			// System.out.println("the number of types is: "+types.size());
			if (types != null) {
				for (int i = 0; i < types.size(); i++) {
					switch (types.get(i).getNodeType()) {
					case ASTNode.TYPE_DECLARATION:
						id2 = query2.tdQuery((TypeDeclaration) types.get(i));
						break;
					case ASTNode.ANNOTATION_TYPE_DECLARATION:
						id2 = query2.adQuery((AnnotationTypeDeclaration) types
								.get(i));
						break;
					case ASTNode.ENUM_DECLARATION:
						id2 = query2.edQuery((EnumDeclaration) types.get(i));
						break;
					}
					// this.nodes.put(types.get(i).getStartPosition(), id2);
					this.nodes.add(types.get(i));
					this.ids.add(id2);

					query2.addRelation(id1, id2, ASTProperty.TYPES);
				}
			}
			tx.success();
		} finally {
			tx.close();
		}

		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(ConditionalExpression node) {
		this.addExpression(node, node.getExpression(), ASTProperty.EXPRESSION);
		this.addExpression(node, node.getThenExpression(),
				ASTProperty.THEN_EXPRESSION);
		if (node.getElseExpression() != null)
			this.addExpression(node, node.getElseExpression(),
					ASTProperty.ELSE_EXPRESSION);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(ConstructorInvocation node) {
		/* TYPE ARGUMENTS */
		@SuppressWarnings("unchecked")
		List<Type> tas = node.typeArguments();
		for (int i = 0; i < tas.size(); i++) {
			this.addType(node, tas.get(i), ASTProperty.TYPE_ARGUMENTS);
		}
		/* arguments */
		@SuppressWarnings("unchecked")
		List<Expression> arguments = node.arguments();
		for (int i = 0; i < arguments.size(); i++) {
			this.addExpression(node, arguments.get(i), ASTProperty.ARGUMENTS);
		}
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(ContinueStatement node) {
		return false;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(DoStatement node) {
		this.addStatement(node, node.getBody(), ASTProperty.BODY);
		this.addExpression(node, node.getExpression(), ASTProperty.EXPRESSION);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(EmptyStatement node) {
		return false;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 * @since 3.1
	 */
	public boolean visit(EnhancedForStatement node) {
		this.addExpression(node, node.getExpression(), ASTProperty.EXPRESSION);
		this.addStatement(node, node.getBody(), ASTProperty.BODY);
		this.addSingleVariableDeclaration(node, node.getParameter(),
				ASTProperty.PARAMETER);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 * @since 3.1
	 */
	public boolean visit(EnumConstantDeclaration node) {
		/* Modifier */
		@SuppressWarnings("unchecked")
		List<IExtendedModifier> modifiers = node.modifiers();
		for (int i = 0; i < modifiers.size(); i++) {
			IExtendedModifier modifier = modifiers.get(i);
			this.addModifier(node, modifier);
		}
		/* java doc */
		Javadoc javadoc = node.getJavadoc();
		if (javadoc != null) {
			this.addJavadoc(node, javadoc);
		}
		/* arguments */
		@SuppressWarnings("unchecked")
		List<Expression> arguments = node.arguments();
		for (int i = 0; i < arguments.size(); i++) {
			this.addExpression(node, arguments.get(i), ASTProperty.ARGUMENTS);
		}
		/* ANONYMOUS_CLASS_DECLARATION */
		AnonymousClassDeclaration acd = node.getAnonymousClassDeclaration();
		if (acd != null) {
			this.addAnonymousClassDeclaration(node, acd);
		}
		return true;
	}

	public void addAnonymousClassDeclaration(ASTNode node,
			AnonymousClassDeclaration acd) {
		long id2 = query2.anonymousClassDeclarationQuery(acd);
		// this.nodes.put(acd.getStartPosition(), id2);
		this.nodes.add(acd);
		this.ids.add(id2);
		long id1 = this.ids.get(nodes.indexOf(node));
		// long id1=nodes.get(node.getStartPosition());
		query2.addRelation(id1, id2, ASTProperty.ANONYMOUS_CLASS_DECLARATION);
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 * @since 3.1
	 */
	public boolean visit(EnumDeclaration node) {
		/* Modifier */
		@SuppressWarnings("unchecked")
		List<Modifier> modifiers = node.modifiers();
		for (int i = 0; i < modifiers.size(); i++) {
			Modifier modifier = modifiers.get(i);
			this.addModifier(node, modifier);
		}
		/* java doc */
		Javadoc javadoc = node.getJavadoc();
		if (javadoc != null) {
			this.addJavadoc(node, javadoc);
		}
		/* SUPER INTERFACE TYPES */
		@SuppressWarnings("unchecked")
		List<Type> sInterfaces = node.superInterfaceTypes();
		for (int i = 0; i < sInterfaces.size(); i++) {
			this.addType(node, sInterfaces.get(i),
					ASTProperty.SUPER_INTERFACE_TYPES);
		}
		/* ENUM CONSTANTS */
		@SuppressWarnings("unchecked")
		long id2 = -1;
		long id1 = ids.get(nodes.indexOf(node));
		// long id1=nodes.get(node.getStartPosition());
		List<EnumConstantDeclaration> ecds = node.enumConstants();
		for (int i = 0; i < ecds.size(); i++) {
			this.nodes.add(ecds.get(i));
			id2 = query2.enumConstantDeclarationQuery(ecds.get(i));
			this.ids.add(id2);
			// this.nodes.put(ecds.get(i).getStartPosition(), id2);
			query2.addRelation(id1, id2, ASTProperty.ENUM_CONSTANTS);
		}
		/* BODY DECLARATIONS */
		@SuppressWarnings("unchecked")
		List<BodyDeclaration> bodyDeclarations = node.bodyDeclarations();
		this.addBodyDeclarations(node, bodyDeclarations);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(ExpressionStatement node) {
		this.addExpression(node, node.getExpression(), ASTProperty.EXPRESSION);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(FieldAccess node) {
		this.addExpression(node, node.getExpression(), ASTProperty.EXPRESSION);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(FieldDeclaration node) {
		/* Modifier */
		@SuppressWarnings("unchecked")
		List<IExtendedModifier> modifiers = node.modifiers();
		for (int i = 0; i < modifiers.size(); i++) {
			IExtendedModifier modifier = modifiers.get(i);
			this.addModifier(node, modifier);
		}
		/* java doc */
		Javadoc javadoc = node.getJavadoc();
		if (javadoc != null) {
			this.addJavadoc(node, javadoc);
		}
		/* Type */
		Type type = node.getType();
		this.addType(node, type, ASTProperty.TYPE);
		/* VariableDeclarationFragment */
		@SuppressWarnings("unchecked")
		List<VariableDeclarationFragment> fragments = node.fragments();
		for (int i = 0; i < fragments.size(); i++) {
			this.addVariableDeclarationFragment(node, fragments.get(i),
					ASTProperty.FRAGMENTS);
		}

		return true;
	}

	public void addVariableDeclarationFragment(ASTNode node,
			VariableDeclarationFragment vf, String prop) {
		this.nodes.add(vf);
		long id2 = query2.variableDeclarationFragmentQuery(vf);
		// nodes.put(vf.getStartPosition(), id2);
		this.ids.add(id2);
		long id1 = ids.get(nodes.indexOf(node));
		// long id1=nodes.get(node.getStartPosition());
		query2.addRelation(id1, id2, prop);
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(ForStatement node) {
		/* INITIALIZERS */
		@SuppressWarnings("unchecked")
		List<Expression> initializers = node.initializers();
		for (int i = 0; i < initializers.size(); i++)
			this.addExpression(node, initializers.get(i),
					ASTProperty.INITIALIZERS);
		/* EXPRESSION */
		this.addExpression(node, node.getExpression(), ASTProperty.EXPRESSION);
		/* UPDATERS */
		@SuppressWarnings("unchecked")
		List<Expression> updates = node.updaters();
		for (int i = 0; i < updates.size(); i++)
			this.addExpression(node, updates.get(i), ASTProperty.UPDATES);
		/* body */
		this.addStatement(node, node.getBody(), ASTProperty.BODY);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(IfStatement node) {
		this.addExpression(node, node.getExpression(), ASTProperty.EXPRESSION);
		this.addStatement(node, node.getThenStatement(),
				ASTProperty.THEN_STATEMENT);
		this.addStatement(node, node.getElseStatement(),
				ASTProperty.ELSE_STATEMENT);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	// public boolean visit(ImportDeclaration node) {
	// return true;
	// }

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(InfixExpression node) {
		this.addExpression(node, node.getLeftOperand(),
				ASTProperty.LEFT_OPERAND);
		this.addExpression(node, node.getRightOperand(),
				ASTProperty.RIGHT_OPERAND);
		@SuppressWarnings("unchecked")
		List<Expression> edos = node.extendedOperands();
		for (int i = 0; i < edos.size(); i++)
			this.addExpression(node, edos.get(i), ASTProperty.EXTENDED_OPERANDS);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(InstanceofExpression node) {
		this.addExpression(node, node.getLeftOperand(),
				ASTProperty.LEFT_OPERAND);
		this.addType(node, node.getRightOperand(), ASTProperty.RIGHT_OPERAND);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(Initializer node) {
		/* Modifier */
		@SuppressWarnings("unchecked")
		List<IExtendedModifier> modifiers = node.modifiers();
		for (int i = 0; i < modifiers.size(); i++) {
			IExtendedModifier modifier = modifiers.get(i);
			this.addModifier(node, modifier);
		}
		/* java doc */
		Javadoc javadoc = node.getJavadoc();
		if (javadoc != null) {
			this.addJavadoc(node, javadoc);
		}
		/* Block */
		Block block = node.getBody();
		this.addStatement(node, block, ASTProperty.BODY);
		return true;
	}

	public void addStatement(ASTNode node, Statement statement, String prop) {
		if (statement != null) {
			this.nodes.add(statement);
			long id2 = query2.statementQuery(statement);
			// this.nodes.put(statement.getStartPosition(), id2);
			this.ids.add(id2);
			long id1 = ids.get(nodes.indexOf(node));
			// long id1=nodes.get(node.getStartPosition());
			query2.addRelation(id1, id2, prop);
		}
	}

	/**
	 * Visits the given AST node.
	 * <p>
	 * Unlike other node types, the boolean returned by the default
	 * implementation is controlled by a constructor-supplied parameter
	 * {@link #ASTVisitor(boolean) ASTVisitor(boolean)} which is
	 * <code>false</code> by default. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 * @see #ASTVisitor()
	 * @see #ASTVisitor(boolean)
	 */
	public boolean visit(Javadoc node) {
		/* tags */
		// List<TagElement> tags=node.tags();
		// for(int i=0;i<tags.size();i++){
		// TagElement tag=tags.get(i);
		// String query=Query.tagElementQuery(tag);
		// this.nodes.put(tag, new NodeInfo(query));
		// this.relations.add(new Relation(node,tag,ASTProperty.TAGS));
		// }
		return false;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(LabeledStatement node) {
		/* body */
		this.addStatement(node, node.getBody(), ASTProperty.BODY);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 * <p>
	 * Note: {@link LineComment} and {@link BlockComment} nodes are not
	 * considered part of main structure of the AST. This method will only be
	 * called if a client goes out of their way to visit this kind of node
	 * explicitly.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 * @since 3.0
	 */
	public boolean visit(LineComment node) {
		return false;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 * @since 3.1
	 */
	public boolean visit(MarkerAnnotation node) {
		return false;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 * @since 3.0
	 */
	public boolean visit(MemberRef node) {
		return false;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 * @since 3.1
	 */
	public boolean visit(MemberValuePair node) {
		return false;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 * @since 3.0
	 */
	public boolean visit(MethodRef node) {
		return false;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 * @since 3.0
	 */
	public boolean visit(MethodRefParameter node) {
		return false;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(MethodDeclaration node) {
		/* Modifier */
		@SuppressWarnings("unchecked")
		List<IExtendedModifier> modifiers = node.modifiers();
		for (int i = 0; i < modifiers.size(); i++) {
			IExtendedModifier modifier = modifiers.get(i);
			this.addModifier(node, modifier);
		}
		/* java doc */
		Javadoc javadoc = node.getJavadoc();
		if (javadoc != null) {
			this.addJavadoc(node, javadoc);
		}
		/* TypeParameter */
		@SuppressWarnings("unchecked")
		List<TypeParameter> typeParameters = node.typeParameters();
		for (int i = 0; i < typeParameters.size(); i++) {
			TypeParameter tpara = typeParameters.get(i);
			this.addTypeParameter(node, tpara);
		}
		/* return type */
		Type rtype = node.getReturnType2();
		if (rtype != null)
			this.addType(node, rtype, ASTProperty.RETURN_TYPE);
		/* PARAMETERS */
		@SuppressWarnings("unchecked")
		List<SingleVariableDeclaration> svds = node.parameters();
		for (int i = 0; i < svds.size(); i++)
			this.addSingleVariableDeclaration(node, svds.get(i),
					ASTProperty.PARAMETERS);
		/* THROWN EXCEPTIONS */
		@SuppressWarnings("unchecked")
		List<Name> t_exceptions = node.thrownExceptions();
		for (int i = 0; i < t_exceptions.size(); i++) {
			this.addExpression(node, t_exceptions.get(i),
					ASTProperty.THROWN_EXCEPTIONS);
		}
		/* BODY */
		Block block = node.getBody();
		if (block != null) {
			this.addStatement(node, block, ASTProperty.BODY);
		}
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(MethodInvocation node) {
		this.addExpression(node, node.getExpression(), ASTProperty.EXPRESSION);
		/* TYPE ARGUMENTS */
		@SuppressWarnings("unchecked")
		List<Type> tas = node.typeArguments();
		for (int i = 0; i < tas.size(); i++) {
			this.addType(node, tas.get(i), ASTProperty.TYPE_ARGUMENTS);
		}
		/* arguments */
		@SuppressWarnings("unchecked")
		List<Expression> arguments = node.arguments();
		for (int i = 0; i < arguments.size(); i++) {
			this.addExpression(node, arguments.get(i), ASTProperty.ARGUMENTS);
		}
		return true;
	}

	/**
	 * example
	 * 
	 * @ClassPreamble ( author = "John Doe", date = "3/17/2002", currentRevision
	 *                = 6, lastModified = "4/12/2004", lastModifiedBy =
	 *                "Jane Doe", // Note array notation reviewers = {"Alice",
	 *                "Bob", "Cindy"} )
	 */
	public boolean visit(NormalAnnotation node) {
		@SuppressWarnings("unchecked")
		List<MemberValuePair> mvPairs = node.values();
		long id2 = -1;
		long id1 = ids.get(nodes.indexOf(node));
		// long id1=nodes.get(node.getStartPosition());
		for (int i = 0; i < mvPairs.size(); i++) {
			MemberValuePair mvPair = mvPairs.get(i);
			this.nodes.add(mvPair);
			id2 = query2.MemberValuePairQuery(mvPair);
			this.ids.add(id2);
			// nodes.put(mvPair.getStartPosition(), id2);
			query2.addRelation(id1, id2, ASTProperty.VALUES);
		}
		return false;
	}

	public boolean visit(PackageDeclaration node) {
		/*
		 * child node Javadoc
		 */
		Javadoc javadoc = node.getJavadoc();
		if (javadoc != null) {
			this.addJavadoc(node, javadoc);
		}

		/*
		 * child node ANNOTATIONS
		 */
		@SuppressWarnings("unchecked")
		List<Annotation> annotations = node.annotations();
		for (int i = 0; i < annotations.size(); i++) {
			Annotation annotation = annotations.get(i);
			this.addAnnotation(node, annotation, ASTProperty.ANNOTATIONS);
		}

		return true;
	}

	/**
	 * add annotation to <nodes> and <node,annotation,>
	 * 
	 * @param node
	 * @param annotation
	 */
	public void addAnnotation(ASTNode node, Annotation annotation, String prop) {
		this.nodes.add(annotation);
		long id2 = query2.AnnotationQuery(annotation);
		this.ids.add(id2);
		// this.nodes.put(annotation.getStartPosition(),id2);
		// long id1=nodes.get(node.getStartPosition());
		long id1 = this.ids.get(nodes.indexOf(node));
		query2.addRelation(id1, id2, prop);

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 * @since 3.1
	 */
	public boolean visit(ParameterizedType node) {
		@SuppressWarnings("unchecked")
		List<Type> types = node.typeArguments();
		for (int i = 0; i < types.size(); i++) {
			this.addType(node, types.get(i), ASTProperty.TYPE_ARGUMENTS);
		}
		this.addType(node, node.getType(), ASTProperty.TYPE);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(ParenthesizedExpression node) {
		this.addExpression(node, node.getExpression(), ASTProperty.EXPRESSION);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(PostfixExpression node) {
		this.addExpression(node, node.getOperand(), ASTProperty.OPERAND);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(PrefixExpression node) {
		this.addExpression(node, node.getOperand(), ASTProperty.OPERAND);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(PrimitiveType node) {
		return false;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(QualifiedName node) {
		return false;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 * @since 3.1
	 */
	public boolean visit(QualifiedType node) {
		this.addType(node, node.getQualifier(), ASTProperty.QUALIFIER);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(ReturnStatement node) {
		this.addExpression(node, node.getExpression(), ASTProperty.EXPRESSION);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(SimpleName node) {
		return false;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(SimpleType node) {
		return false;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 * @since 3.1
	 */
	public boolean visit(SingleMemberAnnotation node) {
		return false;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(SingleVariableDeclaration node) {
		/* Modifier */
		@SuppressWarnings("unchecked")
		List<IExtendedModifier> modifiers = node.modifiers();
		for (int i = 0; i < modifiers.size(); i++) {
			IExtendedModifier modifier = modifiers.get(i);
			this.addModifier(node, modifier);
		}
		/* type */
		Type type = node.getType();
		this.addType(node, type, ASTProperty.TYPE);
		/* INITILIZER */
		Expression initializer = node.getInitializer();
		if (initializer != null)
			this.addExpression(node, initializer, ASTProperty.INITIALIZER);
		return true;
	}

	public void addSingleVariableDeclaration(ASTNode node,
			SingleVariableDeclaration svd, String prop) {
		this.nodes.add(svd);
		long id2 = query2.singleVariableDeclarationQuery(svd);
		// this.nodes.put(svd.getStartPosition(), id2);
		this.ids.add(id2);
		long id1 = ids.get(nodes.indexOf(node));
		// long id1=nodes.get(node.getStartPosition());
		query2.addRelation(id1, id2, prop);
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(StringLiteral node) {
		return false;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(SuperConstructorInvocation node) {
		this.addExpression(node, node.getExpression(), ASTProperty.EXPRESSION);
		/* TYPE ARGUMENTS */
		@SuppressWarnings("unchecked")
		List<Type> tas = node.typeArguments();
		for (int i = 0; i < tas.size(); i++) {
			this.addType(node, tas.get(i), ASTProperty.TYPE_ARGUMENTS);
		}
		/* arguments */
		@SuppressWarnings("unchecked")
		List<Expression> arguments = node.arguments();
		for (int i = 0; i < arguments.size(); i++) {
			this.addExpression(node, arguments.get(i), ASTProperty.ARGUMENTS);
		}
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(SuperFieldAccess node) {

		return false;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(SuperMethodInvocation node) {
		/* TYPE ARGUMENTS */
		@SuppressWarnings("unchecked")
		List<Type> tas = node.typeArguments();
		for (int i = 0; i < tas.size(); i++) {
			this.addType(node, tas.get(i), ASTProperty.TYPE_ARGUMENTS);
		}
		/* arguments */
		@SuppressWarnings("unchecked")
		List<Expression> arguments = node.arguments();
		for (int i = 0; i < arguments.size(); i++) {
			this.addExpression(node, arguments.get(i), ASTProperty.ARGUMENTS);
		}
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(SwitchCase node) {
		this.addExpression(node, node.getExpression(), ASTProperty.EXPRESSION);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(SwitchStatement node) {
		this.addExpression(node, node.getExpression(), ASTProperty.EXPRESSION);
		@SuppressWarnings("unchecked")
		List<Statement> statements = node.statements();
		for (int i = 0; i < statements.size(); i++)
			this.addStatement(node, statements.get(i), ASTProperty.STATEMENTS);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(SynchronizedStatement node) {
		this.addExpression(node, node.getExpression(), ASTProperty.EXPRESSION);
		this.addStatement(node, node.getBody(), ASTProperty.BODY);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 * @since 3.0
	 */
	public boolean visit(TagElement node) {
		// List<ASTNode> fragments=node.fragments();
		// for(int i=0;i<fragments.size();i++){
		// ASTNode fragment=fragments.get(i);
		// if(fragment instanceof TextElement){
		//
		// }else if(fragment instanceof TagElement){
		//
		// }else if(fragment instanceof Name){
		//
		// }else if(fragment instanceof MemberRef){
		//
		// }else{//MethodRef
		//
		// }
		// }
		return false;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 * @since 3.0
	 */
	public boolean visit(TextElement node) {
		return false;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(ThisExpression node) {
		return false;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(ThrowStatement node) {
		this.addExpression(node, node.getExpression(), ASTProperty.EXPRESSION);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(TryStatement node) {
		/* RESOURCES */
		@SuppressWarnings("unchecked")
		List<VariableDeclarationExpression> vdes = node.resources();
		long id2 = -1;
		long id1 = ids.get(nodes.indexOf(node));
		// long id1=nodes.get(node.getStartPosition());
		for (int i = 0; i < vdes.size(); i++) {
			this.nodes.add(vdes.get(i));
			id2 = query2.expressionQuery(vdes.get(i));
			this.ids.add(id2);
			query2.addRelation(id1, id2, ASTProperty.RESOURCES);
		}
		/* Body */
		this.addStatement(node, node.getBody(), ASTProperty.BODY);
		/* CATCH CLAUSES */
		@SuppressWarnings("unchecked")
		List<CatchClause> clauses = node.catchClauses();
		for (int i = 0; i < clauses.size(); i++) {
			this.nodes.add(clauses.get(i));
			id2 = query2.catchClauseQuery(clauses.get(i));
			this.ids.add(id2);
			query2.addRelation(id1, id2, ASTProperty.CATCH_CLAUSES);
		}
		/* FINALLY */
		this.addStatement(node, node.getFinally(), ASTProperty.FINALLY);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(TypeDeclaration node) {
		/* Modifier */
		@SuppressWarnings("unchecked")
		List<IExtendedModifier> modifiers = node.modifiers();
		for (int i = 0; i < modifiers.size(); i++) {
			IExtendedModifier modifier = modifiers.get(i);
			this.addModifier(node, modifier);
		}
		/* java doc */
		Javadoc javadoc = node.getJavadoc();
		if (javadoc != null) {
			this.addJavadoc(node, javadoc);
		}
		/* TypeParameter */
		@SuppressWarnings("unchecked")
		List<TypeParameter> typeParameters = node.typeParameters();
		for (int i = 0; i < typeParameters.size(); i++) {
			TypeParameter tpara = typeParameters.get(i);
			this.addTypeParameter(node, tpara);
		}
		/* SUPERCLASS TYPE */
		if (node.getSuperclassType() != null) {
			this.addType(node, node.getSuperclassType(),
					ASTProperty.SUPERCLASS_TYPE);
		}

		/* SUPER INTERFACE TYPES */
		@SuppressWarnings("unchecked")
		List<Type> sInterfaces = node.superInterfaceTypes();
		for (int i = 0; i < sInterfaces.size(); i++) {
			this.addType(node, sInterfaces.get(i),
					ASTProperty.SUPER_INTERFACE_TYPES);
		}

		/* BODY DECLARATIONS */
		@SuppressWarnings("unchecked")
		List<BodyDeclaration> bodyDeclarations = node.bodyDeclarations();
		this.addBodyDeclarations(node, bodyDeclarations);

		return true;
	}

	/**
	 * if the node is the instance of <TypeDeclaration>,
	 * <AnnotationTypeDeclaration>, <AnonymousClassDeclaration>
	 * 
	 * @param node
	 */
	public void addBodyDeclarations(ASTNode node,
			List<BodyDeclaration> bodyDeclarations) {
		for (int i = 0; i < bodyDeclarations.size(); i++) {
			BodyDeclaration bd = bodyDeclarations.get(i);
			this.nodes.add(bd);
			long id2 = -1;
			switch (bd.getNodeType()) {
			case ASTNode.ANNOTATION_TYPE_DECLARATION:
				id2 = query2.adQuery((AnnotationTypeDeclaration) bd);
				break;
			case ASTNode.ENUM_DECLARATION:
				id2 = query2.edQuery((EnumDeclaration) bd);
				break;
			case ASTNode.TYPE_DECLARATION:
				id2 = query2.tdQuery((TypeDeclaration) bd);
				break;
			case ASTNode.FIELD_DECLARATION:
				id2 = query2.fieldDeclarationQuery((FieldDeclaration) bd);
				break;
			case ASTNode.INITIALIZER:
				id2 = query2.initializerQuery((Initializer) bd);
				break;
			case ASTNode.METHOD_DECLARATION:
				id2 = query2.methodDeclarationQuery((MethodDeclaration) bd);
				break;
			case ASTNode.ANNOTATION_TYPE_MEMBER_DECLARATION:
				id2 = query2
						.annotationTypeMemberDeclarationQuery((AnnotationTypeMemberDeclaration) bd);
				break;
			case ASTNode.ENUM_CONSTANT_DECLARATION:
				id2 = query2
						.enumConstantDeclarationQuery((EnumConstantDeclaration) bd);
				break;
			}
			this.ids.add(id2);
			// this.nodes.put(bd.getStartPosition(), id2);
			// long id1=nodes.get(node.getStartPosition());
			long id1 = ids.get(nodes.indexOf(node));
			query2.addRelation(id1, id2, ASTProperty.BODY_DECLARATIONS);
		}
	}

	/**
	 * add tpara to <nodes> and the relation <node,TYPE_PARAMETERS,tpara> to
	 * <relations>
	 * 
	 * @param node
	 * @param tpara
	 */
	public void addTypeParameter(ASTNode node, TypeParameter tpara) {
		this.nodes.add(tpara);
		long id2 = query2.typeParameterQuery(tpara);
		// this.nodes.put(tpara.getStartPosition(), id2);
		this.ids.add(id2);
		long id1 = ids.get(nodes.indexOf(node));
		// long id1=nodes.get(node.getStartPosition());
		query2.addRelation(id1, id2, ASTProperty.TYPE_PARAMETERS);
	}

	/**
	 * add javadoc to <nodes> and the relation <node,JAVA_DOC,javadoc> to
	 * <relations>
	 * 
	 * @param node
	 * @param javadoc
	 */
	public void addJavadoc(ASTNode node, Javadoc javadoc) {
		this.nodes.add(javadoc);
		long id2 = query2.javadocQuery(javadoc);
		// nodes.put(javadoc.getStartPosition(), id2);
		this.ids.add(id2);
		long id1 = ids.get(nodes.indexOf(node));
		// long id1=nodes.get(node.getStartPosition());
		query2.addRelation(id1, id2, ASTProperty.JAVA_DOC);
	}

	/**
	 * add modifier to <nodes> and the relation <node,MODIFIERS,modifier> to
	 * <relations>
	 * 
	 * @param node
	 * @param modifier
	 */
	public void addModifier(ASTNode node, IExtendedModifier modifier) {
		long id2 = query2.iExtendedModifier(modifier);
		this.ids.add(id2);
		this.nodes.add((ASTNode) modifier);
		// if(modifier instanceof Modifier){
		// nodes.put(((Modifier)modifier).getStartPosition(),id2);
		// }
		// else{
		// nodes.put(((Annotation)modifier).getStartPosition(),id2);
		// }
		long id1 = ids.get(nodes.indexOf(node));
		// long id1=nodes.get(node.getStartPosition());
		query2.addRelation(id1, id2, ASTProperty.MODIFIERS);
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(TypeDeclarationStatement node) {
		AbstractTypeDeclaration atd = node.getDeclaration();
		this.nodes.add(atd);
		long id2 = -1;
		switch (atd.getNodeType()) {
		case ASTNode.TYPE_DECLARATION:
			id2 = query2.tdQuery((TypeDeclaration) atd);
			break;
		case ASTNode.ENUM_DECLARATION:
			id2 = query2.edQuery((EnumDeclaration) atd);
			break;
		case ASTNode.ANNOTATION_TYPE_DECLARATION:
			id2 = query2.adQuery((AnnotationTypeDeclaration) atd);
			break;
		}
		this.ids.add(id2);
		long id1 = ids.get(nodes.indexOf(node));
		query2.addRelation(id1, id2, ASTProperty.DECLARATION);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(TypeLiteral node) {
		this.addType(node, node.getType(), ASTProperty.TYPE);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 * @since 3.1
	 */
	public boolean visit(TypeParameter node) {
		@SuppressWarnings("unchecked")
		List<Type> types = node.typeBounds();
		for (int i = 0; i < types.size(); i++) {
			this.addType(node, types.get(i), ASTProperty.TYPE_BOUNDS);
		}
		return true;
	}

	/**
	 * add type to <nodes> and the relation <node,TYPE_PARAMETERS,tpara> to
	 * <relations>
	 * 
	 * @param node
	 * @param type
	 */
	public void addType(ASTNode node, Type type, String prop) {
		this.nodes.add(type);
		long id2 = query2.typeQuery(type);
		// this.nodes.put(type.getStartPosition(), id2);
		this.ids.add(id2);
		// long id1=nodes.get(node.getStartPosition());
		long id1 = ids.get(nodes.indexOf(node));
		query2.addRelation(id1, id2, prop);
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 * @since 3.7.1
	 */
	public boolean visit(UnionType node) {
		@SuppressWarnings("unchecked")
		List<Type> types = node.types();
		for (int i = 0; i < types.size(); i++) {
			this.addType(node, types.get(i), ASTProperty.TYPES);
		}

		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(VariableDeclarationExpression node) {
		this.addType(node, node.getType(), ASTProperty.TYPE);
		@SuppressWarnings("unchecked")
		List<VariableDeclarationFragment> fragments = node.fragments();
		for (int i = 0; i < fragments.size(); i++)
			this.addVariableDeclarationFragment(node, fragments.get(i),
					ASTProperty.FRAGMENTS);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(VariableDeclarationStatement node) {
		/* Modifier */
		@SuppressWarnings("unchecked")
		List<IExtendedModifier> modifiers = node.modifiers();
		for (int i = 0; i < modifiers.size(); i++) {
			IExtendedModifier modifier = modifiers.get(i);
			this.addModifier(node, modifier);
		}
		/* type */
		this.addType(node, node.getType(), ASTProperty.TYPE);
		/* fragments */
		@SuppressWarnings("unchecked")
		List<VariableDeclarationFragment> vdfs = node.fragments();
		for (int i = 0; i < vdfs.size(); i++) {
			this.addVariableDeclarationFragment(node, vdfs.get(i),
					ASTProperty.FRAGMENTS);
		}
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(VariableDeclarationFragment node) {
		this.addExpression(node, node.getInitializer(), ASTProperty.INITIALIZER);
		return true;
	}

	public void addExpression(ASTNode node, Expression expression, String prop) {
		if (expression != null) {
			long id2 = query2.expressionQuery(expression);
			this.ids.add(id2);
			this.nodes.add(expression);
			// this.nodes.put(expression.getStartPosition(), id2);
			long id1 = ids.get(nodes.indexOf(node));
			// long id1=this.nodes.get(node.getStartPosition());
			query2.addRelation(id1, id2, ASTProperty.BODY_DECLARATIONS);
		}
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 */
	public boolean visit(WhileStatement node) {
		this.addExpression(node, node.getExpression(), ASTProperty.EXPRESSION);
		this.addStatement(node, node.getBody(), ASTProperty.BODY);
		return true;
	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true. Subclasses may
	 * reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @return <code>true</code> if the children of this node should be visited,
	 *         and <code>false</code> if the children of this node should be
	 *         skipped
	 * @since 3.1
	 */
	public boolean visit(WildcardType node) {
		if (node.getBound() != null) {
			this.addType(node, node.getBound(), ASTProperty.BOUND);
		}
		return true;
	}

	public String getFileName() {
		return filename;
	}

	// public List<ASTNode> getNodes(){
	// return this.nodes;
	// }

	public long getCuid() {
		return cuid;
	}

	public void setCuid(long cuid) {
		this.cuid = cuid;
	}

	public void endVisit(AnnotationTypeDeclaration node) {
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @since 3.1
	 */
	public void endVisit(AnnotationTypeMemberDeclaration node) {
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(AnonymousClassDeclaration node) {
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(ArrayAccess node) {
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(ArrayCreation node) {
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(ArrayInitializer node) {
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(ArrayType node) {
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(AssertStatement node) {
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(Assignment node) {
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(Block node) {
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * <p>
	 * Note: {@link LineComment} and {@link BlockComment} nodes are not
	 * considered part of main structure of the AST. This method will only be
	 * called if a client goes out of their way to visit this kind of node
	 * explicitly.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @since 3.0
	 */
	public void endVisit(BlockComment node) {
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(BooleanLiteral node) {
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(BreakStatement node) {
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(CastExpression node) {
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(CatchClause node) {
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(CharacterLiteral node) {
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(ClassInstanceCreation node) {
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(CompilationUnit node) {
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
		this.db.shutdown();
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(ConditionalExpression node) {
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(ConstructorInvocation node) {
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(ContinueStatement node) {
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(DoStatement node) {
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(EmptyStatement node) {
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @since 3.1
	 */
	public void endVisit(EnhancedForStatement node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @since 3.1
	 */
	public void endVisit(EnumConstantDeclaration node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @since 3.1
	 */
	public void endVisit(EnumDeclaration node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(ExpressionStatement node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(FieldAccess node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(FieldDeclaration node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(ForStatement node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(IfStatement node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(ImportDeclaration node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(InfixExpression node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(InstanceofExpression node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(Initializer node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(Javadoc node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(LabeledStatement node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * <p>
	 * Note: {@link LineComment} and {@link BlockComment} nodes are not
	 * considered part of main structure of the AST. This method will only be
	 * called if a client goes out of their way to visit this kind of node
	 * explicitly.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @since 3.0
	 */
	public void endVisit(LineComment node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @since 3.1
	 */
	public void endVisit(MarkerAnnotation node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @since 3.0
	 */
	public void endVisit(MemberRef node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @since 3.1
	 */
	public void endVisit(MemberValuePair node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @since 3.0
	 */
	public void endVisit(MethodRefParameter node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(MethodDeclaration node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(MethodInvocation node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @since 3.1
	 */
	public void endVisit(Modifier node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @since 3.1
	 */
	public void endVisit(NormalAnnotation node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(NullLiteral node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(NumberLiteral node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(PackageDeclaration node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @since 3.1
	 */
	public void endVisit(ParameterizedType node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(ParenthesizedExpression node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(PostfixExpression node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(PrefixExpression node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(PrimitiveType node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(QualifiedName node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @since 3.1
	 */
	public void endVisit(QualifiedType node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(ReturnStatement node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(SimpleName node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(SimpleType node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @since 3.1
	 */
	public void endVisit(SingleMemberAnnotation node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(SingleVariableDeclaration node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(StringLiteral node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(SuperConstructorInvocation node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(SuperFieldAccess node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(SuperMethodInvocation node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(SwitchCase node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(SwitchStatement node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(SynchronizedStatement node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @since 3.0
	 */
	public void endVisit(TagElement node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @since 3.0
	 */
	public void endVisit(TextElement node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(ThisExpression node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(ThrowStatement node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(TryStatement node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(TypeDeclaration node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(TypeDeclarationStatement node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(TypeLiteral node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @since 3.1
	 */
	public void endVisit(TypeParameter node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @since 3.7.1
	 */
	public void endVisit(UnionType node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(VariableDeclarationExpression node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(VariableDeclarationStatement node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(VariableDeclarationFragment node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 */
	public void endVisit(WhileStatement node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 *
	 * @param node
	 *            the node to visit
	 * @since 3.1
	 */
	public void endVisit(WildcardType node) {
		// default implementation: do nothing
		int index = this.nodes.indexOf(node);
		if (index != -1) {
			nodes.remove(index);
			ids.remove(index);
		}
	}

}
