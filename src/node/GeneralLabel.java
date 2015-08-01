package node;

import org.neo4j.graphdb.Label;

public enum GeneralLabel implements Label {
	BodyDeclaration,
	AbstractTypeDeclaration,
	Comment,
	Expression,
	Annatation,
	Name,
	Statement,
	Type,
	VariableDeclaration,
}
