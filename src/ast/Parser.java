package ast;

import java.io.*;

import org.eclipse.jdt.core.dom.*;

public class Parser {
	
	private String program;
	
	public Parser(String filename) {
		byte[] input = null;
		try {
			FileInputStream fis = new FileInputStream(filename);
			BufferedInputStream bis = new BufferedInputStream(fis);
			input = new byte[bis.available()];
			bis.read(input);
			bis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.program = new String(input);
	}
	
	public CompilationUnit parse() {
		// JLS: Java Language Specification
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(program.toCharArray());
		parser.setResolveBindings(true);
		String[] classpathEntries = new String[1];
		classpathEntries[0] = "D:\\Java-Projects\\Git\\Neo4jGraph\\bin\\testcase\\";
		String[] sourcepathEntries = new String[1];
		sourcepathEntries[0] = "D:\\Java-Projects\\Git\\Neo4jGraph\\src\\testcase\\";
		parser.setEnvironment(classpathEntries, sourcepathEntries, null, false);
		parser.setUnitName("D:\\Java-Projects\\Git\\Neo4jGraph\\src\\testcase\\SwapArrayElements.java");
		
		return (CompilationUnit) parser.createAST(null);
	}
}
