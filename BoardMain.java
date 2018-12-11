package ex;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;


public class BoardMain extends Frame implements ActionListener{	
		
	private PostView view;
	
	// �Խù� ����Ʈ
	private Label label = new Label("�Խ���", Label.CENTER);
	private Vector vector = new Vector();
	private List list = new List(25);
	private Button write = new Button("�Խù� �ۼ�");
	private Button look = new Button("�Խù� ����");
	private Button close = new Button("�ݱ�");
	

	// public static ClientThread lr_thread;

	public BoardMain(String title) {
		super(title);
		setLayout(new BorderLayout());
		
		// ����Ʈ ��� ����
		Panel northpanel = new Panel();
		northpanel.setLayout(new BorderLayout(5, 5));
		northpanel.add("North", label);	// ����Ʈ��� �� 
		add("North", northpanel);	
		
		Panel centerpanel = new Panel();
		centerpanel.setLayout(new FlowLayout(FlowLayout.LEFT, 400, 5));
		northpanel.add("Center", list);	// ����Ʈ
		add("Center", centerpanel);		

		Panel southpanel = new Panel();
		southpanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		southpanel.add(write);	// �Խù� �ۼ� ��ư
		southpanel.add(look);	// �Խù� ���� ��ư
		southpanel.add(close);	// �ݱ� ��ư
		add("South", southpanel);	
	
		centerpanel.setSize(400, 400);
		this.setSize(400, 400);
		this.setResizable(false);
		Dimension dS = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dF = this.getSize();
		this.setLocation((int)(dS.getWidth() / 2 - dF.getWidth() / 2), 
			(int)(dS.getHeight() / 2 - dF.getHeight() / 2));

		
		
		close.addActionListener(this);	// ����Ʈ���  �ݱ� ��ư
		// list.addMouseListener(this);	// ����Ʈ��� ���콺 Ŭ�� ��
		look.addActionListener(this);	// �Խù� ���� ��ư

		
		this.pack();
		this.setResizable(false);
		Dimension di = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension di1 = this.getSize();
		this.setLocation((int)(di.getWidth() / 2 - di1.getWidth() / 2), 
			(int)(di.getHeight() / 2 - di1.getHeight() / 2));
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
		if (b.getLabel().equals("�Խù� �ۼ�")) {
			// �� �ۼ� ó�� ��ƾ
			

		} else if (b.getLabel().equals("�Խù� ����")) {
			// ���� ó�� ��ƾ
			this.dispose();
			view = new PostView("�Խù� ����");
			view.pack();
			view.show();
		}
			
		else if(b.getLabel().equals("�ݱ�")) {
			// �ݱ� ó�� ��ƾ
			this.dispose();
			
		}
	}

	public void mouseClicked(MouseEvent e){
		if(e.getSource() == list){
			if(e.getClickCount() == 2){	//����Ŭ��
				// view();
			}
		}
	
	}
	public static class BoardMain_main{
		public static void main(String[] arg){
			BoardMain es = new BoardMain("�Խ���");
		}
	}


}		
