package ui;

import java.util.ArrayList;

import javax.swing.JFrame;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import ast.JFileVisitor2;
import ast.Query2;
import java.awt.BorderLayout;
import javax.swing.JTextField;
import javax.swing.JProgressBar;

public class Import extends JFrame implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IProject project;
	String version;
	String uploader;
	private String D_PATH;
	private int pid;
	private int javanum;
	private int i;

	private long projectId;
	private ArrayList<Long> cIds = new ArrayList<Long>();
	private JTextField textField;

	public Import(String dpath, IProject project, String version,
			String uploader) {
		this.D_PATH = dpath;
		this.project = project;
		this.version = version;
		this.uploader = uploader;
		this.javanum = 0;
		this.i = 0;
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		textField = new JTextField();
		getContentPane().add(textField, BorderLayout.CENTER);
		textField.setColumns(10);
		
		JProgressBar progressBar = new JProgressBar();
		getContentPane().add(progressBar, BorderLayout.SOUTH);
	}

	// save content of the Text fields because they get disposed
	// as soon as the Dialog closes
	// private void importIntoNeo() {
	// try {
	// this.D_PATH = txtNeoFolder.getText();
	// this.analyse(project);
	// } catch (JavaModelException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }

	private void analyse(IProject project) throws JavaModelException {
		IPackageFragment[] packages = JavaCore.create(project)
				.getPackageFragments();
		// parse(JavaCore.create(project));
		this.setPid();
		for (IPackageFragment mypackage : packages) {
			this.javanum += mypackage.getCompilationUnits().length;
		}
		for (IPackageFragment mypackage : packages) {
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				// System.out.println("the package name is: "
				// + mypackage.getElementName() + ",path: "
				// + mypackage.getPath());
				createAST(mypackage);
			}
		}
		this.store();

	}

	/**
	 * 1 store the project info into the database 2 add edges from project to
	 * java files
	 */
	private void store() {
		GraphDatabaseService db = new GraphDatabaseFactory()
				.newEmbeddedDatabase(D_PATH);
		Query2 query2 = new Query2(db, pid);
		org.neo4j.graphdb.Transaction tx = db.beginTx();
		try {
			this.projectId = query2.projectQuery(project.getName(), version,
					uploader);
			for (long cid : cIds) {
				query2.addRelation(projectId, cid, "FILES");
			}
			tx.success();
		} finally {
			tx.close();
		}
		db.shutdown();
	}

	private void createAST(IPackageFragment mypackage)
			throws JavaModelException {
		for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
			// now create the AST for the ICompilationUnits
			CompilationUnit parse = parse(unit);
			System.out.println("the name of java file is: "
					+ unit.getElementName());
			JFileVisitor2 visitor = new JFileVisitor2(D_PATH,
					unit.getElementName(), pid);
			parse.accept(visitor);
			System.out.println("cid: " + visitor.getCuid());
			this.cIds.add(visitor.getCuid());
			i++;
			int value = (int) (i / (javanum + 0.1) * 100);
			System.out.println("i is:" + i + ", size is:" + javanum
					+ " ,value is:" + value);
			// operation.setValue(value);
		}
	}

	public void setPid() {
		GraphDatabaseService db = new GraphDatabaseFactory()
				.newEmbeddedDatabase(D_PATH);
		int id = Query2.getMaxPid(db);
		System.out.println(id);
		db.shutdown();
		this.pid = id == -1 ? 1 : id + 1;
		System.out.println("this pid is: " + this.pid);
	}

	private CompilationUnit parse(ICompilationUnit unit) {
		@SuppressWarnings("deprecation")
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}

	@Override
	public void run() {
		try {
			this.setVisible(true);
			this.analyse(project);
			this.dispose();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
