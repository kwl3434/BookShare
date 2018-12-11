package ex;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import ex.BoardMain.WinListener;

public class PostView extends Frame implements ActionListener{

	private BoardMain board;
	
	// ���õ� �Խù� ���� ����
	private Label ttitle = new Label("���� : ", Label.RIGHT);
	private Label writer = new Label("�ۼ��� : ", Label.RIGHT);
	private Label inTitle = new Label();
	private Label inName = new Label();
	private TextArea content = new TextArea();
	private Button enter = new Button("Ȯ��");
	
	public PostView(String title) {
		super(title);
		setLayout(new BorderLayout());

				
		// �Խù� ���� ���� 
		Panel panel1 = new Panel(new BorderLayout());
		
		Panel panel2 = new Panel(new GridLayout(2, 1));
		panel2.add(ttitle);
		panel2.add(writer);
		panel1.add("West", panel2);
		
		Panel panel3 = new Panel(new GridLayout(2, 1));
		panel3.add(inTitle);
		panel3.add(inName);
		panel1.add("Center", panel3);	
		add("North", panel1);
		add("Center", content);
		
		Panel panel4 = new Panel(new FlowLayout());
		panel4.add(enter);
		add("South", panel4);
		content.setEnabled(false);
		this.setSize(300, 200);
		this.setResizable(false);
		Dimension Ds = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension Df = this.getSize();
		this.setLocation((int)(Ds.getWidth() / 2 - Df.getWidth() / 2), 
		(int)(Ds.getHeight() / 2 - Df.getHeight() / 2));
				
		enter.addActionListener(this);	// �ۺ��� Ȯ�� ��ư
		
		this.pack();
		this.setVisible(true);
		
		
		// lr_thread = client; // ClientThread Ŭ������ �����Ѵ�.

		// �Է� �ؽ�Ʈ �ʵ忡 ��Ŀ���� ���� �� �޼ҵ� �߰�

		addWindowListener(new WinListener());

	}
	

	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			// lr_thread.requestQuitRoom();
			System.exit(0);
			
		}
	}

	// �Խù� �ۼ�, �Խù� ����, �ݱ� �̺�Ʈ�� ó���Ѵ�.
	public void actionPerformed(ActionEvent ae) {
		Button b = (Button) ae.getSource();
		if (b.getLabel().equals("Ȯ��")) {
			this.dispose();
			board = new BoardMain("�Խ���");
			board.pack();
			board.show();
	}

	
	}
	public static class PostView_main{
		public static void main(String[] arg){
			PostView es = new PostView("�Խ���");
		}
	}


}		
