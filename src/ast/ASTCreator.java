package ast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

public class ASTCreator implements Iterable<ASTNode> {
	
	private static Logger logger = Logger.getLogger(ASTCreator.class);

	private String[] classpathEntries;
	private String[] sourcepathEntries;
	private List<String> filepaths;

	public ASTCreator(String projectDirPath) {
		PathExplorer explorer = PathExplorer.startExplore(projectDirPath);
		classpathEntries = explorer.getClassPaths();
		sourcepathEntries = explorer.getSourcePaths();
		filepaths = explorer.getFilePaths();
	}

	@Override
	public Iterator<ASTNode> iterator() {
		return new ASTIterator();
	}

	private class ASTIterator implements Iterator<ASTNode> {

		private Iterator<String> iter;
		
		public ASTIterator() {
			iter = filepaths.iterator();
		}

		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public ASTNode next() {
			String filepath = iter.next();
			return createAST(filepath);
		}
	}

	public ASTNode createAST(String filepath) {

		String program;

		try {
			program = readFromFile(filepath);
		} catch (IOException e) {
			throw new IllegalStateException("Cannot read from file "
					+ filepath);
		}

		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(program.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setEnvironment(classpathEntries, sourcepathEntries, null, true);
		parser.setUnitName(filepath);
		parser.setResolveBindings(true);

		logger.info("Create AST for " + filepath);
		return parser.createAST(null);
	}

	private String readFromFile(String path) throws IOException {
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

}
