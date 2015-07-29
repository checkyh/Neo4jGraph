package ast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

import run.Config;

public class Parser {
	
	private String program;
	
	public Parser() {
		try {
			this.program = readProgram(Config.FILEPATH);
		} catch (IOException e) {
			throw new IllegalStateException("Cannot read from file " + Config.FILEPATH);
		}
	}
	
	private String readProgram(String path) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(path));
		StringBuilder sb = new StringBuilder();
		String ls = System.getProperty("line.separator");
		while (true) {
			String line = in.readLine();
			if (line == null) {
				break;
			}
			sb.append(line);
			sb.append(ls);
		}
		in.close();
		return sb.toString();
	}
	
	public ASTNode parse() {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(program.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		PathExplorer explorer = new PathExplorer(Config.PROJECT_DIR);
		String[] classpathEntries = explorer.getClassPaths();
		String[] sourcepathEntries = explorer.getSourcePaths();
		parser.setEnvironment(classpathEntries, sourcepathEntries, null, false);
		parser.setUnitName(Config.FILEPATH);
		parser.setResolveBindings(true);
		
		return parser.createAST(null);
	}
}
