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
		
		return (CompilationUnit) parser.createAST(null);
	}
}
