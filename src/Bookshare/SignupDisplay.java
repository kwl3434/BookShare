package Bookshare;

import java.awt.*;
import java.awt.event.*;

//  ���̵� ��� ��ȭ��ȣ

public class SignupDisplay extends Frame implements ActionListener {
	private Button SD_Signup; // ȸ������ �Ϸ�
	private Label SD_ID;
	private Label SD_PW;
	private Label SD_PN;
	private Label head;
	private Button success;
	private TextField PNinput,PWinput,IDinput;
	
	public static ClientThread dr_thread;
	
	public SignupDisplay(ClientThread client, String title) {
		super(title);
		setLayout(new BorderLayout());

		Panel head2 = new Panel(new FlowLayout());
		head = new Label("ȸ������");
		head2.add(head);
		add("North", head2);

		Panel infor = new Panel(new FlowLayout());

		SD_ID = new Label("���̵�    ");
		infor.add(SD_ID);
		IDinput = new TextField(25);
		infor.add(IDinput);

		SD_PW = new Label("��й�ȣ");
		infor.add(SD_PW);
		PWinput = new TextField(25);
		infor.add(PWinput);

		SD_PN = new Label("��ȭ��ȣ");
		infor.add(SD_PN);
		PNinput = new TextField(25);
		infor.add(PNinput);

		success = new Button("�Ϸ�");
		success.addActionListener(this);
		infor.add(success);

		add("Center", infor);
		setSize(300, 350);
		addWindowListener(new WinListener());
		dr_thread = client;
	}

	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			dispose();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Button b = (Button) e.getSource();
		if (b.getLabel().equals("�Ϸ�")) {
			String id = IDinput.getText();
			String pw = PWinput.getText();
			String pn = PNinput.getText();
			if(id.length()<5) {
				MessageBox msgBox = new MessageBox(this, "ID", "ID�� 5���� �̻� �Է����ּ���.");
				msgBox.show();
				return ;
			}else if(pw.length()<5) {
				MessageBox msgBox = new MessageBox(this, "PW", "PW�� 5���� �̻� �Է����ּ���.");
				msgBox.show();
				return ;
			}else if(pn.length()!=11) {
				MessageBox msgBox = new MessageBox(this, "PHONENUMBER", "11���� �޴��� ��ȣ�� �Է����ּ���.");
				msgBox.show();
				return ;
			}else {
				System.out.println(id+pw+pn);
				dr_thread.requestSignup(id, pw, pn);
			}
		}
	}
}