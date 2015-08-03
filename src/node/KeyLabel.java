package node;

import org.neo4j.graphdb.Label;

public enum KeyLabel implements Label {
	Key,
	TypeKey,
	MethodKey,
	VariableKey,
}
