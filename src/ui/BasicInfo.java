package ui;

import java.awt.EventQueue;

import org.eclipse.core.resources.IProject;
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
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.wb.swt.SWTResourceManager;

public class BasicInfo extends TitleAreaDialog {

	private Text txtNeoFolder;
	private Text txtVersion;
	private Text txtUploader;
	private Shell pShell;
	private IProject project;
	private Label lbProName;
//	private ProgressBar progressBar;

	// private LongRunningOperation operation;

	public BasicInfo(Shell parentShell, IProject project) {
		super(parentShell);
		this.pShell = parentShell;
		this.project = project;
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

//		progressBar = new ProgressBar(grpProject, SWT.NONE);
//		progressBar.setBackground(SWTResourceManager
//				.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
//		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
//				false, 1, 1));
//		progressBar.setVisible(false);
		// operation=new LongRunningOperation(progressBar.getDisplay(),
		// progressBar);

		return area;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
//		progressBar.setVisible(true);
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					ProgressFrame frame = new ProgressFrame();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
		
		
		new Thread(
				new Import(txtNeoFolder.getText(), project, txtVersion
						.getText(), txtUploader.getText())).start();
		super.okPressed();
	}

}
