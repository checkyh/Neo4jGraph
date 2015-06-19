package ast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.WildcardType;
import org.neo4j.cypher.internal.compiler.v2_1.docbuilders.internalDocBuilder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;


public class Query2 {

	private  final int pid;
	private final GraphDatabaseService db;

	private static final String setPid = ",P_ID:{proid} }) RETURN n";
	
	public Query2(GraphDatabaseService db,int pid){
		this.db=db;
		this.pid=pid;
//		int tmpid=this.getMaxPid();
//		if(tmpid==-1)
//			pid=1;
//		else
//			pid=tmpid;
	}
	

	/* PackageDeclaration */
	public long pdQuery(PackageDeclaration node) {
		ResourceIterator<Node> resultIterator = null;
		String queryString = "MERGE (n: PackageDeclaration { NAME : {pkgName}"
				+ setPid;
		Map<String, Object> params = new HashMap<>();
		if (node!= null) {
			params.put("pkgName", node.getName().getFullyQualifiedName());
			// params.put("Key", node.resolveBinding().getKey());
		} else {
			params.put("pkgName", "default");
			// params.put("Key", "default");
		}
		params.put("proid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();
		return result.getId();
	}

	/* ImportDeclaration */
	public long idQuery(ImportDeclaration node) {
				ResourceIterator<Node> resultIterator = null;

		String queryString = "MERGE (n: ImportDeclaration { NAME : {ImportName} ,STATIC : {Static}, ON_DEMAND:{onDemand}"
				+ setPid;
		Map<String, Object> params = new HashMap<>();
		params.put("ImportName", node.getName().getFullyQualifiedName());
		params.put("Static", node.isStatic());
		params.put("onDemand", node.isOnDemand());
		params.put("proid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();

		return result.getId();

	}

	/*
	 * generate query sentence for TypeDeclaration
	 */
	public long tdQuery(TypeDeclaration node) {
		ResourceIterator<Node> resultIterator = null;

		String queryString = "CREATE (n: TypeDeclaration:AbstractTypeDeclaration:BodyDeclaration { NAME : {typeName},INTERFACE : {isInterface},P_ID:{proid}}) "
				+ "MERGE (m:Tkey :Key {VALUE:{Key},P_ID:{proid}}) "
				+ "MERGE (n)-[:KEY]->(m) " + "RETURN n";
		Map<String, Object> params = new HashMap<>();
		params.put("typeName", node.getName().getFullyQualifiedName());
		params.put("isInterface", node.isInterface());
		if (node.resolveBinding() != null) {
			params.put("Key", node.resolveBinding().getKey());
		} else
			params.put("Key", "null");
		params.put("proid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();
		return result.getId();

	}

	/*
	 * generate query sentence for AnnotationTypeDeclaration
	 */
	public long adQuery(AnnotationTypeDeclaration node) {
		ResourceIterator<Node> resultIterator = null;
		String queryString = "CREATE (n: AnnotationTypeDeclaration :AbstractTypeDeclaration:BodyDeclaration{ NAME : {atypeName},P_ID:{proid}}) "
				+ "MERGE (m:Tkey :Key {VALUE:{Key},P_ID:{proid}}) "
				+ "MERGE (n)-[:KEY]->(m) " + "RETURN n";
		Map<String, Object> params = new HashMap<>();
		params.put("atypeName", node.getName().getFullyQualifiedName());
		if (node.resolveBinding() != null)
			params.put("Key", node.resolveBinding().getKey());
		else
			params.put("Key", "null");
		params.put("proid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();
		return result.getId();
	}

	/* EnumDeclaration */
	public long edQuery(EnumDeclaration node) {
		ResourceIterator<Node> resultIterator = null;
		String queryString = "CREATE (n: EnumDeclaration :AbstractTypeDeclaration:BodyDeclaration{ NAME : {etypeName},P_ID:{proid}}) "
				+ "MERGE (m:Tkey :Key {VALUE:{Key},P_ID:{proid}}) "
				+ "MERGE (n)-[:KEY]->(m) " + "RETURN n";
		Map<String, Object> params = new HashMap<>();
		params.put("etypeName", node.getName().getFullyQualifiedName());
		if (node.resolveBinding() != null)
			params.put("Key", node.resolveBinding().getKey());
		else
			params.put("Key", "null");
		params.put("proid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();
		return result.getId();

	}

	/* EnumConstantDeclaration */
	public long enumConstantDeclarationQuery(EnumConstantDeclaration node) {
		ResourceIterator<Node> resultIterator = null;

		String queryString = "CREATE (n: EnumConstantDeclaration:BodyDeclaration { NAME : {etypeName},P_ID:{proid}}) "
				+ "MERGE (m:Mkey :Key {VALUE:{mKey},P_ID:{proid}}) "
				+ "MERGE (n)-[:KEY]->(m) "
				+ "MERGE (h:Vkey :Key {VALUE:{vkey},P_ID:{proid}}) "
				+ "MERGE (n)-[:KEY]->(h) " + "RETURN n";
		Map<String, Object> params = new HashMap<>();
		params.put("etypeName", node.getName().getFullyQualifiedName());
		if (node.resolveConstructorBinding() != null)
			params.put("mKey", node.resolveConstructorBinding().getKey());
		else
			params.put("mKey", "null");
		if (node.resolveVariable() != null)
			params.put("vkey", node.resolveVariable().getKey());
		else
			params.put("vkey", "null");
		params.put("proid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();

		return result.getId();

	}

	/* CompilationUnit */
	public long cuQuery(CompilationUnit node, String fileName) {
		ResourceIterator<Node> resultIterator = null;

		String queryString = "CREATE (n: CompilationUnit { NAME : {fileName} "
				+ setPid;
		Map<String, Object> params = new HashMap<>();
		params.put("fileName", fileName);
		params.put("proid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();

		return result.getId();
	}

	/* Modifier */
	public long modifierQuery(Modifier node) {
		ResourceIterator<Node> resultIterator = null;

		String queryString = "MERGE (n: Modifier { KEYWORD : {key}" + setPid;
		Map<String, Object> params = new HashMap<>();
		params.put("key", node.getKeyword().toString());
		params.put("proid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();

		return result.getId();

	}

	/* Javadoc */
	public long javadocQuery(Javadoc node) {
		ResourceIterator<Node> resultIterator = null;

		String queryString = "CREATE (n: Javadoc {DOC : {doc} " + setPid;
		Map<String, Object> params = new HashMap<>();
		params.put("doc", node.toString());
		params.put("proid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();

		return result.getId();
	}

	/* TypeParameter */
	public long typeParameterQuery(TypeParameter node) {
		ResourceIterator<Node> resultIterator = null;

		String queryString = "CREATE (n: TypeParameter { NAME : {name},P_ID:{proid}}) "
				+ "MERGE (m:Tkey :Key {VALUE:{key},P_ID:{proid}}) "
				+ "MERGE (n)-[:KEY]->(m) " + "RETURN n";
		Map<String, Object> params = new HashMap<>();
		params.put("name", node.getName().getFullyQualifiedName());
		if (node.resolveBinding() != null)
			params.put("key", node.resolveBinding().getKey());
		else
			params.put("key", "null");
		params.put("proid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();

		return result.getId();
	}

	/* Type */
	public long typeQuery(Type node) {
		ResourceIterator<Node> resultIterator = null;

		String queryString = "";
		String common = "MERGE (m:Tkey :Key {VALUE:{key},P_ID:{proid}}) "
				+ "MERGE (n)-[:KEY]->(m) " + "RETURN n";
		Map<String, Object> params = new HashMap<>();
		switch (node.getNodeType()) {
		case ASTNode.PRIMITIVE_TYPE:
			queryString = "MERGE (n: PrimitiveType :Type{ PRIMITIVE_TYPE_CODE : {code},P_ID:{proid}}) "
					+ common;
			params.put("code", ((PrimitiveType) node).getPrimitiveTypeCode()
					.toString());
			break;
		case ASTNode.SIMPLE_TYPE:
			queryString = "MERGE (n: SimpleType:Type { NAME : {name},IS_FROMSOURCE:{isFS},T_KEY:{key},P_ID:{proid}}) "
					+ common;
			if (node.resolveBinding() != null) {
				params.put("isFS", node.resolveBinding().isFromSource());
			} else {
				params.put("isFS", "null");
			}
			break;
		case ASTNode.ARRAY_TYPE:
			queryString = "MERGE (n: ArrayType:Type { NAME : {name},DIMENTIONS:{dimentions},T_KEY:{key},P_ID:{proid}}) "
					+ common;
			params.put("dimentions", ((ArrayType) node).getDimensions());
			break;
		case ASTNode.UNION_TYPE:
			queryString = "MERGE (n: UnionType:Type { NAME : {name},T_KEY:{key},P_ID:{proid}}) "
					+ common;
			break;
		case ASTNode.QUALIFIED_TYPE:
			queryString = "MERGE (n: QualifiedType:Type { NAME : {name},T_KEY:{key},P_ID:{proid}}) "
					+ common;
			break;
		case ASTNode.PARAMETERIZED_TYPE:
			queryString = "MERGE (n: ParameterizedType:Type { NAME : {name},T_KEY:{key},P_ID:{proid}}) "
					+ common;
			break;
		case ASTNode.WILDCARD_TYPE:
			queryString = "MERGE (n: WildcardType:Type { NAME : {name},UPPER_BOUND:{ub},T_KEY:{key},P_ID:{proid}}) "
					+ common;
			params.put("ub", ((WildcardType) node).isUpperBound());
			break;
		}
		if (node.resolveBinding() != null) {
			params.put("name", node.resolveBinding().getName());
			params.put("key", node.resolveBinding().getKey());
		} else {
			params.put("name", "null");
			params.put("key", "null");
		}
		params.put("proid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();

		return result.getId();

	}

	/* Annotation */
	public long AnnotationQuery(Annotation node) {
		ResourceIterator<Node> resultIterator = null;

		String queryString = "";
		String common = "MERGE (m:Tkey :Key {VALUE:{key},P_ID:{proid}}) "
				+ "MERGE (n)-[:KEY]->(m) " + "RETURN n";
		Map<String, Object> params = new HashMap<>();
		switch (node.getNodeType()) {
		case ASTNode.MARKER_ANNOTATION:
			queryString = "CREATE (n: MarkerAnnotation:Annotation:Expression { TYPE_NAME : {tname},P_ID:{proid}}) "
					+ common;
			break;
		case ASTNode.SINGLE_MEMBER_ANNOTATION:
			queryString = "CREATE (n: SingleMemberAnnotation :Annotation:Expression{ TYPE_NAME : {tname},VALUE : {value},P_ID:{proid}}) "
					+ common;
			params.put("value", ((SingleMemberAnnotation) node).getValue()
					.toString());
			break;
		case ASTNode.NORMAL_ANNOTATION:
			queryString = "CREATE (n: NormalAnnotation:Annotation:Expression { TYPE_NAME : {tname},P_ID:{proid}}) "
					+ common;
			break;
		}
		params.put("tname", node.getTypeName().getFullyQualifiedName());
		if (node.resolveTypeBinding() != null)
			params.put("key", node.resolveTypeBinding().getKey());
		else
			params.put("key", "null");
		params.put("proid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();

		return result.getId();

	}

	/* IExtendedModifier */
	public long iExtendedModifier(IExtendedModifier node) {
		if (node instanceof Modifier)
			return modifierQuery((Modifier) node);
		if (node instanceof Annotation)
			return AnnotationQuery((Annotation) node);
		return -1L;
	}

	/* MemberValuePair */
	public long MemberValuePairQuery(MemberValuePair node) {
		ResourceIterator<Node> resultIterator = null;

		String queryString = "CREATE (n: MemberValuePair { NAME : {name}, VALUE : {value}"
				+ setPid;
		Map<String, Object> params = new HashMap<>();
		params.put("name", node.getName().getFullyQualifiedName());
		params.put("value", node.getValue().toString());
		params.put("proid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();

		return result.getId();
	}

	/* FieldDeclaration */
	public long fieldDeclarationQuery(FieldDeclaration node) {
		ResourceIterator<Node> resultIterator = null;

		String queryString = "CREATE (n: FieldDeclaration:BodyDeclaration {SP : {content} "
				+ setPid;
		Map<String, Object> params = new HashMap<>();
		params.put("content", node.getStartPosition());
		params.put("proid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();

		return result.getId();
	}

	/* VariableDeclarationFragment */
	public long variableDeclarationFragmentQuery(
			VariableDeclarationFragment node) {
		ResourceIterator<Node> resultIterator = null;

		String queryString = "CREATE (n: VariableDeclarationFragment:VariableDeclaration "
				+ "{NAME : {name},EXTRA_DIMENSIONS:{ed},P_ID:{proid}}) "
				+ "MERGE (m:Vkey :Key {VALUE:{key},P_ID:{proid}}) "
				+ "MERGE (n)-[:KEY]->(m) " + "RETURN n";
		Map<String, Object> params = new HashMap<>();
		params.put("name", node.getName().getFullyQualifiedName());
		if (node.resolveBinding() != null)
			params.put("key", node.resolveBinding().getKey());
		else
			params.put("key", "null");

		params.put("ed", node.getExtraDimensions());
		params.put("proid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();

		return result.getId();
	}

	/* Expression */
	public long expressionQuery(Expression node) {
		ResourceIterator<Node> resultIterator = null;

		Map<String, Object> params = new HashMap<>();
		String common = "P_ID:{proid} }) ";
		String squery = "";
		switch (node.getNodeType()) {
		case ASTNode.MARKER_ANNOTATION:
		case ASTNode.NORMAL_ANNOTATION:
		case ASTNode.SINGLE_MEMBER_ANNOTATION:
			return this.AnnotationQuery((Annotation) node);
		case ASTNode.ARRAY_ACCESS:

			squery = "CREATE (n: ArrayAccess :Expression{" + common;

			break;
		case ASTNode.ARRAY_CREATION:
			squery = "CREATE (n: ArrayCreation :Expression{" + common;
			break;
		case ASTNode.ARRAY_INITIALIZER:
			squery = "CREATE (n: ArrayInitializer:Expression {CONTENT:{content}," + common;
			params.put("content",((ArrayInitializer) node).toString());
			break;
		case ASTNode.ASSIGNMENT:
			squery = "CREATE (n: Assignment :Expression{OPERATOR:{operator},"
					+ common;
			params.put("operator", ((Assignment) node).getOperator().toString());

			break;

		case ASTNode.BOOLEAN_LITERAL:
			squery = "MERGE (n: BooleanLiteral :Expression{BOOLEAN_VALUE:{value},"
					+ common;
			params.put("value", ((BooleanLiteral) node).booleanValue());

			break;

		case ASTNode.CAST_EXPRESSION:
			squery = "CREATE (n: CastExpression :Expression{" + common;
			break;

		case ASTNode.CHARACTER_LITERAL:
			squery = "MERGE (n: CharacterLiteral :Expression{ESCAPED_VALUE:{value},"
					+ common;
			params.put("value", ((CharacterLiteral) node).getEscapedValue());

			break;

		case ASTNode.CLASS_INSTANCE_CREATION:
			squery = "CREATE (n: ClassInstanceCreation :Expression{P_ID:{proid} }) "
					+ "MERGE (h : Mkey :Key{VALUE:{mkey},P_ID:{proid} }) "
					+ "MERGE (n)-[:KEY]->(h) ";
			if (((ClassInstanceCreation) node).resolveConstructorBinding() != null)
				params.put("mkey", ((ClassInstanceCreation) node)
						.resolveConstructorBinding().getKey());
			else
				params.put("mkey", "null");
			break;
		case ASTNode.CONDITIONAL_EXPRESSION:
			squery = "CREATE (n: ConditionalExpression:Expression {" + common;
			break;
		case ASTNode.FIELD_ACCESS:
			squery = "CREATE (n: FieldAccess:Expression {NAME:{name},P_ID:{proid}}) "
					+ "MERGE (h : Vkey :Key{VALUE:{vkey},P_ID:{proid} }) "
					+ "MERGE (n)-[:KEY]->(h) ";
			params.put("name", ((FieldAccess) node).getName()
					.getFullyQualifiedName());
			if (((FieldAccess) node).resolveFieldBinding() != null) {
				params.put("vkey", ((FieldAccess) node).resolveFieldBinding()
						.getKey());
			} else {
				params.put("vkey", "null");
			}
			break;
		case ASTNode.INFIX_EXPRESSION:
			squery = "CREATE (n: InfixExpression :Expression{OPERATOR:{operator},"
					+ common;
			params.put("operator", ((InfixExpression) node).getOperator()
					.toString());

			break;
		case ASTNode.INSTANCEOF_EXPRESSION:
			squery = "CREATE(n: InstanceofExpression:Expression {" + common;
			break;

		case ASTNode.METHOD_INVOCATION:
			squery = "CREATE (n: MethodInvocation:Expression {NAME:{name},P_ID:{proid}}) "
					+ "MERGE (h : Mkey :Key{VALUE:{mkey},P_ID:{proid} }) "
					+ "MERGE (n)-[:KEY]->(h) ";
			if (((MethodInvocation) node).resolveMethodBinding() != null)
				params.put("mkey", ((MethodInvocation) node)
						.resolveMethodBinding().getKey());
			else
				params.put("mkey", "null");
			params.put("name", ((MethodInvocation) node).getName()
					.getFullyQualifiedName());
			break;
		case ASTNode.QUALIFIED_NAME:
		case ASTNode.SIMPLE_NAME:
			String extra = "";
			if (((Name) node).resolveBinding() != null) {
				switch (((Name) node).resolveBinding().getKind()) {
				case IBinding.VARIABLE:
					extra = "MERGE (h : Vkey :Key{VALUE:{evkey},P_ID:{proid} }) "
							+ "MERGE (n)-[:KEY]->(h) ";
					params.put("evkey", ((Name) node).resolveBinding().getKey());
					break;
				case IBinding.METHOD:
					extra = "MERGE (h : Mkey :Key{VALUE:{emkey},P_ID:{proid} }) "
							+ "MERGE (n)-[:KEY]->(h) ";
					params.put("emkey", ((Name) node).resolveBinding().getKey());
					break;
				case IBinding.TYPE:
					extra = "MERGE (h : Tkey :Key{VALUE:{etkey},P_ID:{proid} }) "
							+ "MERGE (n)-[:KEY]->(h) ";
					params.put("etkey", ((Name) node).resolveBinding().getKey());
					break;
				}
			} else {
				extra = "";
			}
			squery = "CREATE (n: Name:Expression {NAME:{name}," + common
					+ extra;
			params.put("name", ((Name) node).getFullyQualifiedName());
			break;

		case ASTNode.NULL_LITERAL:
			squery = "MERGE (n: NullLiteral:Expression{P_ID:{proid}}) ";
			params.put("proid", pid);
			break;
		// Log.debugLoger(query.toString());
		case ASTNode.NUMBER_LITERAL:
			squery = "MERGE (n: NumberLiteral:Expression {TOKEN:{token},"
					+ common;
			params.put("token", ((NumberLiteral) node).getToken());

			break;
		case ASTNode.PARENTHESIZED_EXPRESSION:
			squery = "CREATE(n: ParenthesizedExpression:Expression {" + common;
			break;
		case ASTNode.POSTFIX_EXPRESSION:
			squery = "CREATE (n: PostfixExpression:Expression {OPERATOR:{operator},"
					+ common;
			params.put("operator", ((PostfixExpression) node).getOperator()
					.toString());

			break;

		case ASTNode.PREFIX_EXPRESSION:
			squery = "CREATE (n: PrefixExpression:Expression {OPERATOR:{operator},"
					+ common;
			params.put("operator", ((PrefixExpression) node).getOperator()
					.toString());

			break;
		case ASTNode.STRING_LITERAL:
			squery = "MERGE (n: StringLiteral:Expression {ESCAPED_VALUE:{value},"
					+ common;
			params.put("value", ((StringLiteral) node).getEscapedValue());
			break;

		case ASTNode.SUPER_FIELD_ACCESS:
			squery = "CREATE (n: SuperFieldAccess:Expression {NAME:{name},P_ID:{proid}}) "
					+ "MERGE (h : Vkey :Key{VALUE:{vkey},P_ID:{proid} }) "
					+ "MERGE (n)-[:KEY]->(h) ";
			if (((SuperFieldAccess) node).getQualifier() != null)
				params.put("qualifier", ((SuperFieldAccess) node)
						.getQualifier().getFullyQualifiedName());
			else
				params.put("qualifier", "null");
			params.put("name", ((SuperFieldAccess) node).getName()
					.getFullyQualifiedName());
			if (((SuperFieldAccess) node).resolveFieldBinding() != null) {
				params.put("vkey", ((SuperFieldAccess) node).resolveFieldBinding()
						.getKey());
			} else {
				params.put("vkey", "null");
			}

			break;
		case ASTNode.SUPER_METHOD_INVOCATION:
			squery = "CREATE (n: SuperMethodInvocation:Expression {QUALIFIER:{qualifier},NAME:{name},P_ID:{proid}})"
					+ "MERGE (h : Mkey :Key :Key{VALUE:{mkey},P_ID:{proid} }) "
					+ "MERGE (n)-[:KEY]->(h) ";
			if (((SuperMethodInvocation) node).getQualifier() != null)
				params.put("qualifier", ((SuperMethodInvocation) node)
						.getQualifier().getFullyQualifiedName());
			else
				params.put("qualifier", "null");
			params.put("name", ((SuperMethodInvocation) node).getName()
					.getFullyQualifiedName());
			if (((SuperMethodInvocation) node).resolveMethodBinding() != null)
				params.put("mkey", ((SuperMethodInvocation) node)
						.resolveMethodBinding().getKey());
			else
				params.put("mkey", "null");

			break;
		case ASTNode.THIS_EXPRESSION:
			squery = "CREATE (n: ThisExpression:Expression {QUALIFIER:{qualifier},"
					+ common;
			if (((ThisExpression) node).getQualifier() != null)
				params.put("qualifier", ((ThisExpression) node).getQualifier()
						.getFullyQualifiedName());
			else
				params.put("qualifier", "null");

			break;
		case ASTNode.TYPE_LITERAL:
			squery = "CREATE (n: TypeLiteral:Expression {" + common;
			break;
		case ASTNode.VARIABLE_DECLARATION_EXPRESSION:
			squery = "CREATE (n: VariableDeclarationExpression:Expression {"
					+ common;
			break;
		}
		squery += "MERGE (m : Tkey :Key{VALUE:{key},P_ID:{proid} }) "
				+ "MERGE (n)-[:KEY]->(m) " + "RETURN n";
		if (node.resolveTypeBinding() != null)
			params.put("key", node.resolveTypeBinding().getKey());
		else
			params.put("key", "null");
		params.put("proid", pid);
		resultIterator = db.execute(squery, params).columnAs("n");
		Node result = resultIterator.next();

		return result.getId();
	}

	/* Statement */
	public long statementQuery(Statement node) {
		ResourceIterator<Node> resultIterator = null;
		String queryString = "";
		Map<String, Object> params = new HashMap<>();

		switch (node.getNodeType()) {
		case ASTNode.ASSERT_STATEMENT:
			queryString = "CREATE (n: AssertStatement:Statement {P_ID:{proid},SP:{sp}}) RETURN n";
			break;
		case ASTNode.BLOCK:
			queryString = "CREATE (n: Block:Statement {P_ID:{proid},SP:{sp}}) RETURN n";
			break;
		case ASTNode.BREAK_STATEMENT:
			queryString = "CREATE (n: BreakStatement :Statement{LABEL:{label},SP:{sp}"
					+ setPid;
			if (((BreakStatement) node).getLabel() != null)
				params.put("label", ((BreakStatement) node).getLabel()
						.getFullyQualifiedName());
			else
				params.put("label", "null");
			break;
		case ASTNode.CONSTRUCTOR_INVOCATION:
			queryString = "CREATE (n: ConstructorInvocation:Statement {SP:{sp},P_ID:{proid}}) "
					+ "MERGE (h : Mkey :Key{VALUE:{mkey},P_ID:{proid} }) "
					+ "MERGE (n)-[:KEY]->(h) " + "RETURN n";
			if (((ConstructorInvocation) node).resolveConstructorBinding() != null)
				params.put("mkey", ((ConstructorInvocation) node)
						.resolveConstructorBinding().getKey());
			else
				params.put("mkey", "null");
			break;
		case ASTNode.CONTINUE_STATEMENT:
			queryString = "CREATE (n: ContinueStatement:Statement {LABEL:{label},SP:{sp}"
					+ setPid;
			if (((ContinueStatement) node).getLabel() != null)
				params.put("label", ((ContinueStatement) node).getLabel()
						.getFullyQualifiedName());
			else
				params.put("label", "null");
			break;
		case ASTNode.DO_STATEMENT:
			queryString = "CREATE (n: DoStatement:Statement {P_ID:{proid},SP:{sp}}) RETURN n";
			break;
		case ASTNode.EMPTY_STATEMENT:
			queryString = "CREATE (n: EmptyStatement:Statement {P_ID:{proid},SP:{sp}}) RETURN n";
			break;
		case ASTNode.ENHANCED_FOR_STATEMENT:
			queryString = "CREATE (n: EnhancedForStatement:Statement {P_ID:{proid},SP:{sp}}) RETURN n";
			break;
		case ASTNode.EXPRESSION_STATEMENT:
			queryString = "CREATE (n: ExpressionStatement:Statement {P_ID:{proid},SP:{sp}}) RETURN n";
			break;
		case ASTNode.FOR_STATEMENT:
			queryString = "CREATE (n: ForStatement:Statement {P_ID:{proid},SP:{sp}}) RETURN n";
			break;
		case ASTNode.IF_STATEMENT:
			queryString = "CREATE (n: IfStatement:Statement {CONDITION:{cond},SP:{sp}"
					+ setPid;
			params.put("cond", ((IfStatement) node).getExpression().toString());
			break;
		case ASTNode.LABELED_STATEMENT:
			queryString = "CREATE (n: LabeledStatement:Statement {LABEL:{label},SP:{sp}"
					+ setPid;
			params.put("label", ((LabeledStatement) node).getLabel()
					.getFullyQualifiedName());
			break;
		case ASTNode.RETURN_STATEMENT:
			queryString = "CREATE (n: ReturnStatement:Statement{P_ID:{proid},SP:{sp}}) RETURN n";
			break;
		case ASTNode.SUPER_CONSTRUCTOR_INVOCATION:
			queryString = "CREATE (n: SuperConstructorInvocation:Statement {SP:{sp},P_ID:{proid}}) "
					+ "MERGE (h : Mkey :Key{VALUE:{mkey},P_ID:{proid} }) "
					+ "MERGE (n)-[:KEY]->(h) " + "RETURN n";
			if (((SuperConstructorInvocation) node).resolveConstructorBinding() != null)
				params.put("mkey", ((SuperConstructorInvocation) node)
						.resolveConstructorBinding().getKey());
			else
				params.put("mkey", "null");

			break;
		case ASTNode.SWITCH_CASE:
			queryString = "CREATE (n: SwitchCase:Statement {P_ID:{proid},SP:{sp}}) RETURN n";
			break;
		case ASTNode.SWITCH_STATEMENT:
			queryString = "MERGE (n: SwitchStatement:Statement {P_ID:{proid},SP:{sp}}) RETURN n";
			break;
		case ASTNode.SYNCHRONIZED_STATEMENT:
			queryString = "CREATE (n: SynchronizedStatement:Statement {P_ID:{proid},SP:{sp}}) RETURN n";
			break;
		case ASTNode.THROW_STATEMENT:
			queryString = "CREATE (n: ThrowStatement:Statement {P_ID:{proid},SP:{sp}}) RETURN n";
			break;
		case ASTNode.TRY_STATEMENT:
			queryString = "CREATE (n: TryStatement:Statement {P_ID:{proid},SP:{sp}}) RETURN n";
			break;
		case ASTNode.TYPE_DECLARATION_STATEMENT:
			queryString = "CREATE (n: TypeDeclarationStatement :Statement{P_ID:{proid},SP:{sp}}) RETURN n";
			break;
		case ASTNode.VARIABLE_DECLARATION_STATEMENT:
			queryString = "CREATE (n: VariableDeclarationStatement :Statement{P_ID:{proid},SP:{sp}} ) RETURN n";
			break;
		case ASTNode.WHILE_STATEMENT:
			queryString = "CREATE (n: WhileStatement :Statement{P_ID:{proid},SP:{sp}}) RETURN n";
			break;

		}
		params.put("proid", pid);
		params.put("sp", node.getStartPosition());
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();

		return result.getId();
	}

	/* Initializer */
	public long initializerQuery(Initializer node) {
		ResourceIterator<Node> resultIterator = null;

		String queryString = "CREATE (n: Initializer :BodyDeclaration{P_ID:{proid}"
				+ setPid;
		Map<String, Object> params = new HashMap<>();
		params.put("proid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();

		return result.getId();

	}

	/* SingleVariableDeclaration */
	public long singleVariableDeclarationQuery(
			SingleVariableDeclaration node) {
		ResourceIterator<Node> resultIterator = null;

		String queryString = "CREATE (n: SingleVariableDeclaration:VariableDeclaration "
				+ "{NAME : {name},VARARGS:{varargs},EXTRA_DIMENSIONS:{ed},P_ID:{proid}}) "
				+ "MERGE (h : Vkey :Key{VALUE:{key},P_ID:{proid} }) "
				+ "MERGE (n)-[:KEY]->(h) " + "RETURN n";
		Map<String, Object> params = new HashMap<>();
		params.put("name", node.getName().getFullyQualifiedName());
		if (node.resolveBinding() != null)
			params.put("key", node.resolveBinding().getKey());
		else
			params.put("key", "null");
		params.put("varargs", node.isVarargs());
		params.put("ed", node.getExtraDimensions());
		params.put("proid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();

		return result.getId();

	}

	/* MethodDeclaration */
	public long methodDeclarationQuery(MethodDeclaration node) {
		ResourceIterator<Node> resultIterator = null;

		String queryString = "CREATE (n: MethodDeclaration :BodyDeclaration"
				+ "{NAME : {name},CONSTRUCTOR:{constructor},EXTRA_DIMENSIONS:{ed},P_ID:{proid}}) "
				+ "MERGE (h : Mkey :Key{VALUE:{key},P_ID:{proid} }) "
				+ "MERGE (n)-[:KEY]->(h) " + "RETURN n";
		Map<String, Object> params = new HashMap<>();
		params.put("name", node.getName().getFullyQualifiedName());
		/*************************************/
		System.out.println(node.getName().getFullyQualifiedName());
		/**************************************/

		if (node.resolveBinding() != null)
			params.put("key", node.resolveBinding().getKey());
		else
			params.put("key", "null");
		params.put("constructor", node.isConstructor());
		params.put("ed", node.getExtraDimensions());
		params.put("proid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();

		return result.getId();
	}

	/* AnonymousClassDeclaration */
	public long anonymousClassDeclarationQuery(
			AnonymousClassDeclaration node) {
		ResourceIterator<Node> resultIterator = null;

		String queryString = "CREATE (n: AnonymousClassDeclaration {P_ID:{proid}})"
				+ "MERGE (h : Tkey :Key{VALUE:{key},P_ID:{proid} }) "
				+ "MERGE (n)-[:KEY]->(h) " + "RETURN n";
		Map<String, Object> params = new HashMap<>();
		params.put("key", node.resolveBinding().getKey());
		params.put("proid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();

		return result.getId();
	}

	/* AnnotationTypeMemberDeclaration */
	public long annotationTypeMemberDeclarationQuery(
			AnnotationTypeMemberDeclaration node) {
		ResourceIterator<Node> resultIterator = null;

		String queryString = "CREATE (n: AnnotationTypeMemberDeclaration :BodyDeclaration{NAME:{name},P_ID:{proid}})"
				+ "MERGE (h : Tkey :Key{VALUE:{key},P_ID:{proid} }) "
				+ "MERGE (n)-[:KEY]->(h) " + "RETURN n";
		Map<String, Object> params = new HashMap<>();
		params.put("name", node.getName().getFullyQualifiedName());
		if (node.resolveBinding() != null)
			params.put("key", node.resolveBinding().getKey());
		else
			params.put("key", "null");
		params.put("proid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();

		return result.getId();

	}

	/* CatchClause */
	public long catchClauseQuery(CatchClause node) {
		ResourceIterator<Node> resultIterator = null;

		String queryString = "CREATE (n: CatchClause{P_ID:{proid}" + setPid;
		Map<String, Object> params = new HashMap<>();
		params.put("proid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();

		return result.getId();
	}

	/* start node in cfg */
	public long startQuery(String key) {
		ResourceIterator<Node> resultIterator = null;

		String queryString = "CREATE (n: Start {P_ID:{proid}})"
				+ "MERGE (h : Mkey :Key{VALUE:{key},P_ID:{proid} }) "
				+ "MERGE (n)-[:KEY]->(h) " + "RETURN n";
		Map<String, Object> params = new HashMap<>();
		params.put("proid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();

		return result.getId();
	}

	/* end node in cfg */
	public long endQuery(String key) {
		ResourceIterator<Node> resultIterator = null;

		String queryString = "CREATE (n: End {P_ID:{proid}})"
				+ "MERGE (h : Mkey :Key{VALUE:{key},P_ID:{proid} }) "
				+ "MERGE (n)-[:KEY]->(h) " + "RETURN n";
		Map<String, Object> params = new HashMap<>();
		params.put("proid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();

		return result.getId();
	}

	public int getPid() {
		return pid;
	}

	public void addRelation(long id1, long id2, String prop) {
		String queryString = "match (f) match (t) where id(f)={from} and id(t)={to} merge (f)-[r:AST {NAME:{prop}}]->(t) ";
		Map<String, Object> params = new HashMap<>();
		params.put("from", id1);
		params.put("to", id2);
		params.put("prop", prop);
		db.execute(queryString, params).columnAs("n");
	}
	
	/**
	 * store the project node into the database and record its id in the
	 * database
	 * 
	 * @return
	 */
	public long projectQuery(String name,String version,String uploader) {
		ResourceIterator<Node> resultIterator = null;
		String queryString = "MERGE (n: Project { NAME : {pname},VERSION:{version},UPLOADER:{up},P_ID:{pid}}) RETURN n";
		Map<String, Object> params = new HashMap<>();
		params.put("pname", name);
		params.put("version", version);
		params.put("up", uploader);
		params.put("pid", pid);
		resultIterator = db.execute(queryString, params).columnAs("n");
		Node result = resultIterator.next();
		long id=result.getId();
		resultIterator.close();
		return id;
	}
	
	public static int getMaxPid(GraphDatabaseService db) {
		Result result = null;
		String queryString = "match (n) return max(n.P_ID) as mpid ";
		result = db.execute(queryString);
		//Iterator<Integer> max = result.columnAs("mpid");
		Iterator<Integer> n_column=result.columnAs("mpid");
		if(n_column.hasNext()){
			return n_column.next().intValue();
		}
		return -1; 

	}

}
