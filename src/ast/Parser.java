package ast;

import java.io.*;

import org.eclipse.jdt.core.dom.*;

class FileReader {
	
	private String program;
	
	public FileReader(String filename) {
		byte[] input = null;
		try {
			FileInputStream fis = new FileInputStream(filename);
			BufferedInputStream bis = new BufferedInputStream(fis);
			input = new byte[bis.available()];
			bis.read(input);
			bis.close();
		} catch (IOException e) {
			throw new IllegalStateException("Cannot read file: " + filename);
		}
		this.program = new String(input);
	}
	
	public String getProgram() {
		return program;
	}
}

public class Parser {
	
	private String filename;
	private String program;
	
	public Parser(String filename) {
		this.filename = filename;
		FileReader fileReader = new FileReader(filename);
		this.program = fileReader.getProgram();
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
		parser.setSource(program.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		parser.setResolveBindings(true);
		
		String[] classpathEntries = new String[1];
		String[] sourcepathEntries = new String[1];
		parsePathEntries(classpathEntries, sourcepathEntries, filename);
		parser.setEnvironment(classpathEntries, sourcepathEntries, null, false);
		parser.setUnitName(filename);
		
		return (CompilationUnit) parser.createAST(null);
	}
}
