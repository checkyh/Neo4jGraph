package ast;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import run.Config;


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
	
	private String program;
	
	public Parser() {
		readFromFile();
	}
	
	private void readFromFile() {
		FileReader fileReader = new FileReader(Config.FILEPATH);
		this.program = fileReader.getProgram();
	}
	
	public CompilationUnit parse() {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(program.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		PathExplorer explorer = new PathExplorer(Config.PROJECT_DIR);
		String[] classpathEntries = explorer.getClassPaths();
		String[] sourcepathEntries = explorer.getSourcePaths();
		parser.setEnvironment(classpathEntries, sourcepathEntries, null, false);
		parser.setUnitName(Config.FILEPATH);
		parser.setResolveBindings(true);
		
		return (CompilationUnit) parser.createAST(null);
	}
}
