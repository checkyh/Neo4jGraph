package ast;

import java.io.*;

import org.eclipse.jdt.core.dom.*;

public class Parser {
	
	private String filename;
	private String program;
	
	public Parser(String filename) {
		this.filename = filename;
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
	
	private void parsePathEntries(String[] classpathEntries, String[] sourcepathEntries, String filename) {
		int pos = filename.lastIndexOf('\\');
		String sourcepath = filename.substring(0, pos);
		String classpath = sourcepath.replace("src", "bin");
		sourcepathEntries[0] = sourcepath;
		classpathEntries[0] = classpath;
	}
	
	public CompilationUnit parse() {
		// JLS: Java Language Specification
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(program.toCharArray());
		parser.setResolveBindings(true);
		
		String[] classpathEntries = new String[1];
		String[] sourcepathEntries = new String[1];
		parsePathEntries(classpathEntries, sourcepathEntries, filename);
		parser.setEnvironment(classpathEntries, sourcepathEntries, null, false);
		parser.setUnitName(filename);
		
		return (CompilationUnit) parser.createAST(null);
	}
}
