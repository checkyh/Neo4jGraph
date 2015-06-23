package ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JProgressBar;

public class ProgressFrame extends JFrame {

	private JPanel contentPane;
	private JTextArea txtrI;
	private JProgressBar progressBar;

//	/**
//	 * Launch the application.
//	 */
//	public static void main(String[] args) {
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
//	}
	
	public void appendText(String str){
		this.txtrI.append(str);
		this.txtrI.append("\n");
	}

	public void setProgress (int n) {
		this.progressBar.setValue(n);
		this.progressBar.setString(""+n+"%");
	}
	/**
	 * Create the frame.
	 */
	public ProgressFrame() {
		setTitle("Progress");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		progressBar = new JProgressBar();
		contentPane.add(progressBar, BorderLayout.SOUTH);
		progressBar.setStringPainted(true);
		
		txtrI = new JTextArea();
		contentPane.add(txtrI, BorderLayout.CENTER);
	}

}
