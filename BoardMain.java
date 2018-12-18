package Bookshare;

import java.awt.*;
import java.awt.event.*;


public class BoardMain extends Frame implements ActionListener, MouseListener{

	private Label label = new Label("�Խ���", Label.CENTER);
	// private Vector vector = new Vector();
	private List list = new List(25);
	private Button write = new Button("�Խù� �ۼ�");
	private Button look = new Button("�Խù� ����");
	private Button close = new Button("�ݱ�");

	public static ClientThread dr_thread;

	public BoardMain(ClientThread client, String title) {
		super(title);
		setLayout(new BorderLayout());
		this.setSize(800, 600);
		
		// ����Ʈ ��� ����
		Panel northpanel = new Panel();
		northpanel.setLayout(new BorderLayout(5, 5));
		northpanel.add("North", label);	// �Խ��� �� 
		label.setFont(new Font("Sanserif", Font.BOLD, 22));
		add("North", northpanel);	
				
		Panel centerpanel = new Panel();
		centerpanel.setLayout(new FlowLayout(FlowLayout.LEFT, 400, 5));
		this.setBackground(new Color(243,243,243));
		northpanel.add("Center", list);	// ����Ʈ
		add("Center", centerpanel);		

		Panel southpanel = new Panel();
		southpanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		southpanel.add(write);	// �Խù� �ۼ� ��ư
		southpanel.add(look);	// �Խù� ���� ��ư
		southpanel.add(close);	// �ݱ� ��ư
		add("South", southpanel);	
			
		this.setResizable(false);
		Dimension dS = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dF = this.getSize();
		this.setLocation((int)(dS.getWidth() / 2 - dF.getWidth() / 2), 
			(int)(dS.getHeight() / 2 - dF.getHeight() / 2));

				
		close.addActionListener(this);	// ����Ʈ���  �ݱ� ��ư
		list.addMouseListener(this);	// ����Ʈ��� ���콺 Ŭ�� ��
		look.addActionListener(this);	// �Խù� ���� ��ư

				
		// this.pack();
		// this.setResizable(false);
		Dimension di = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension di1 = this.getSize();
		this.setLocation((int)(di.getWidth() / 2 - di1.getWidth() / 2), 
			(int)(di.getHeight() / 2 - di1.getHeight() / 2));
		this.setVisible(true);

		dr_thread = client; // ClientThread Ŭ������ �����Ѵ�.

		// �Է� �ؽ�Ʈ �ʵ忡 ��Ŀ���� ���ߴ� �޼ҵ� �߰�

		addWindowListener(new WinListener());
	}

	
	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			dr_thread.requestQuitBoard();
		}
	}

	
	// ȭ�������, �α׾ƿ� �̺�Ʈ�� ó���Ѵ�.
	public void actionPerformed(ActionEvent ae) {
		Button b = (Button) ae.getSource();
		if (b.getLabel().equals("�Խù� �ۼ�")) {
			// �� �ۼ� ó�� ��ƾ
			dr_thread.requestWriteBoard();
			
		} else if (b.getLabel().equals("�Խù� ����")) {
			// ���� ó�� ��ƾ
			dr_thread.requestReadBoard();
		
		} else if (b.getLabel().equals("�ݱ�")) {
			// �˻� ó�� ��ƾ
			dr_thread.requestQuitBoard();
		}
	}
	public void mouseClicked(MouseEvent e){
		if(e.getSource() == list){
			if(e.getClickCount() == 2){	//���� Ŭ�� ��
				dr_thread.requestReadBoard();
			}
		}
	}
	
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
	

}