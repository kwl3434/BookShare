package Bookshare;

import java.awt.*;
import java.awt.event.*;

public class DisplayRoom extends Frame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private Button dr_btMessage; // ���� â
	private Button dr_btBoard; // �Խ��� â
	private Button dr_btLogout; //�α׾ƿ� â

	public static ClientThread dr_thread;

	public DisplayRoom(ClientThread client, String title) {
		super(title);
		setLayout(new BorderLayout());

		// ��ȭ�濡�� ����ϴ� ������Ʈ�� ��ġ�Ѵ�.
		Panel centerpanel = new Panel();
		centerpanel.setLayout(new FlowLayout());
		dr_btBoard = new Button("�Խ���");
		dr_btBoard.addActionListener(this);
		centerpanel.add(dr_btBoard);

		dr_btMessage = new Button("������");
		dr_btMessage.addActionListener(this);
		centerpanel.add(dr_btMessage);
		
		dr_btLogout = new Button("�α׾ƿ�");
		dr_btLogout.addActionListener(this);
		centerpanel.add(dr_btLogout);

		add("South", centerpanel);

		dr_thread = client; // ClientThread Ŭ������ �����Ѵ�.

		// �Է� �ؽ�Ʈ �ʵ忡 ��Ŀ���� ���ߴ� �޼ҵ� �߰�
		
		setSize(300, 350);
		addWindowListener(new WinListener());
		
	}

	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			//���� �ʿ�
		}
	}

	// ȭ�������, �α׾ƿ� �̺�Ʈ�� ó���Ѵ�.
	public void actionPerformed(ActionEvent ae) {
		Button b = (Button) ae.getSource();
		if (b.getLabel().equals("�Խ���")) {
			// �Խ��� ���� ��ƾ
			System.out.println("�Խ���");
			dr_thread.requestEnterBoard();
		} else if (b.getLabel().equals("������")) {
			dr_thread.requestEnterMessage();
			// ������ ���� ��ƾ
		}else if(b.getLabel().equals("�α׾ƿ�")) {
			// �α׾ƿ� ó�� ��ƾ
		}
	}
}
