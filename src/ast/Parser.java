package ast;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;


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
	private String projectDirPath;
	
	public Parser(String projectDirPath) {
		this.projectDirPath = projectDirPath;
	}
	
	public void readFromFile(String filename) {
		this.filename = filename;
		FileReader fileReader = new FileReader(filename);
		this.program = fileReader.getProgram();
	}
	
	public CompilationUnit parse() {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(program.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		JavaFiles javaFiles = new JavaFiles(projectDirPath);
		String[] sourcepathEntries = javaFiles.getSources();
		String[] classpathEntries = javaFiles.getTargets();
		parser.setEnvironment(classpathEntries, sourcepathEntries, null, false);
		parser.setUnitName(filename);
		parser.setResolveBindings(true);
		
		return (CompilationUnit) parser.createAST(null);
	}
}
