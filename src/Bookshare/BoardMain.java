package Bookshare;

import java.awt.*;
import java.awt.event.*;

public class BoardMain extends Frame implements ActionListener{

	private Label lr_Label1;
	private Label lr_Label2;
	private Button lr_btwrite;	 // �� �ۼ� ��ư
	private Button lr_btLook;	// �ڷΰ��� ��ư
	private Button lr_btfind;	// �˻� ��ư
	private List lr_writelist; // �� ��� ����Ʈ
	private TextField lr_tfInput; // �� �˻� �ʵ�
	

	public static ClientThread dr_thread;

	public BoardMain(ClientThread client, String title) {
		super(title);
		setLayout(new BorderLayout());
		this.setSize(800, 600);
		
		Dimension dS = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dF = this.getSize();
		int x = (int)((dS.getWidth()-dF.getWidth())/2);
		int y = (int)((dS.getHeight()-dF.getHeight())/2);
		this.setLocation(x, y);
		
		// ����ϴ� ������Ʈ�� ��ġ
		Panel northpanel = new Panel();
		northpanel.setLayout(new FlowLayout());
		lr_writelist = new List(20);
	
		northpanel.add(lr_writelist);	// ����Ʈ �г�
		lr_writelist.addItemListener(null);
		

		Panel centerpanel = new Panel();
		centerpanel.setLayout(new FlowLayout());
		lr_btwrite = new Button("�Խù� �ۼ�");
		lr_btwrite.addActionListener(this);
		centerpanel.add(lr_btwrite);
		
		lr_btLook = new Button("�Խù� ����");
		lr_btLook.addActionListener(this);
		centerpanel.add(lr_btLook);	

		
		Panel southpanel = new Panel();
		southpanel.setLayout(new FlowLayout());
		lr_tfInput = new TextField(20);
		southpanel.add(lr_tfInput);
		lr_btfind = new Button("�˻�");
		southpanel.add(lr_btfind);


		add("North", northpanel);
		add("Center", centerpanel);
		add("South", southpanel);

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
			String item = lr_writelist.getSelectedItem();
			if(item != null) {
				
			}
			
		} else if (b.getLabel().equals("�˻�")) {
			// �˻� ó�� ��ƾ
		}
	}




}