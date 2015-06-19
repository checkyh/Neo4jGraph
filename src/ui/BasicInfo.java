package ui;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Group;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import ast.JFileVisitor2;
import ast.Query2;

import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.wb.swt.SWTResourceManager;

public class BasicInfo extends TitleAreaDialog {

	private String firstName;
	private String lastName;
	private Text txtNeoFolder;
	private Text txtVersion;
	private Text txtUploader;
	private Shell pShell;
	private IProject project;
	private String D_PATH;
	private int pid;
	
	private int javanum;
	private int i;

	// private final Query2 query2;
	// private GraphDatabaseService db;
	// private ExecutionEngine engine;

	private long projectId;
	private ArrayList<Long> cIds = new ArrayList<Long>();
	private Label lbProName;
	private ProgressBar progressBar;
//	private LongRunningOperation operation;

	public BasicInfo(Shell parentShell, IProject project) {
		super(parentShell);
		this.pShell = parentShell;
		this.project = project;
		this.javanum=0;
		this.i=0;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Basic information of the project to be imported");
		setMessage("You have to fill all the blanks",
				IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(11, false));
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		lblNewLabel.setText("Work with:");

		txtNeoFolder = new Text(container, SWT.BORDER);
		txtNeoFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 9, 1));

		Button btChoose = new Button(container, SWT.NONE);
		btChoose.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false,
				false, 1, 1));
		btChoose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog neofolderDialog = new DirectoryDialog(pShell);
				neofolderDialog.open();
				txtNeoFolder.setText(neofolderDialog.getFilterPath());
			}
		});
		btChoose.setText("Choose");

		Group grpProject = new Group(container, SWT.NONE);
		grpProject.setText("Project");
		grpProject.setLayout(new GridLayout(2, false));
		GridData gd_grpProject = new GridData(SWT.FILL, SWT.CENTER, true, true,
				11, 1);
		gd_grpProject.widthHint = 441;
		grpProject.setLayoutData(gd_grpProject);

		Label lblNewLabel_1 = new Label(grpProject, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNewLabel_1.setText("Name:");

		lbProName = new Label(grpProject, SWT.NONE);
		lbProName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false,
				1, 1));
		lbProName.setText(this.project.getName());

		Label lbVersion = new Label(grpProject, SWT.NONE);
		lbVersion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lbVersion.setText("Version:");

		txtVersion = new Text(grpProject, SWT.BORDER);
		txtVersion.setText("1.0.0");
		txtVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Label lbUploader = new Label(grpProject, SWT.NONE);
		lbUploader.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lbUploader.setText("Uploader:");

		txtUploader = new Text(grpProject, SWT.BORDER);
		txtUploader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		new Label(grpProject, SWT.NONE);

		progressBar = new ProgressBar(grpProject, SWT.NONE);
		progressBar.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		progressBar.setVisible(false);
//		operation=new LongRunningOperation(progressBar.getDisplay(), progressBar);

		return area;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	// save content of the Text fields because they get disposed
	// as soon as the Dialog closes
	private void importIntoNeo() {
		try {
			this.D_PATH = txtNeoFolder.getText();
			this.analyse(project);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void okPressed() {
		progressBar.setVisible(true);
//		operation.start();
		System.out.println("start.....");
		importIntoNeo();
		super.okPressed();
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	private void analyse(IProject project) throws JavaModelException {
		IPackageFragment[] packages = JavaCore.create(project)
				.getPackageFragments();
		// parse(JavaCore.create(project));
		this.setPid();
		for (IPackageFragment mypackage : packages) {
			this.javanum+=mypackage.getCompilationUnits().length;
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
			this.projectId = query2.projectQuery(lbProName.getText(),
					txtVersion.getText(), txtUploader.getText());
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
			int value=(int) (i/(javanum+0.1)*100);
			System.out.println("i is:"+i+", size is:"+javanum+" ,value is:"+value);
			progressBar.setSelection(value);
//			operation.setValue(value);
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
	
//	class LongRunningOperation extends Thread {
//		private int value;
//		private Display display;
//
//		private ProgressBar progressBar;
//
//		public LongRunningOperation(Display display, ProgressBar progressBar) {
//			this.display = display;
//			this.progressBar = progressBar;
//			this.value=0;
//		}
//
//		public void run() {
//			while (value<=100) {
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//				}
//				display.asyncExec(new Runnable() {
//					public void run() {
//						if (progressBar.isDisposed())
//							return;
//						progressBar.setSelection(value);
//					}
//				});
//			}
//		}
//
//		public int getValue() {
//			return value;
//		}
//
//		public void setValue(int value) {
//			this.value = value;
//		}
//	}

}

