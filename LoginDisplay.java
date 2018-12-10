package bookshare;

import java.awt.*;
import java.awt.event.*;

public class LoginDisplay extends Frame implements ActionListener {
	private Button LD_btlogon; // �α��� ���� ��ư
	private Button IDsearch; // ID ã��
	private Button PWsearch; // PW ã��
	private Button Signup; // ȸ������

	public LoginDisplay(ClientThread client, String title) {
		super(title);
		setLayout(new BorderLayout());

		Panel north = new Panel();
		north.setLayout(new FlowLayout());
		north.add(new Label("Book ��������"));
		add("North", north);

		Panel center = new Panel();
		center.setLayout(new FlowLayout());

		center.add(new Label("���̵�    "));
		TextField IDinput = new TextField(25);
		center.add(IDinput);

		center.add(new Label("��й�ȣ"));
		TextField PWinput = new TextField(25);
		center.add(PWinput);

		LD_btlogon = new Button("�α���");
		center.add(LD_btlogon);
		add("Center", center);

		Panel south = new Panel();
		south.setLayout(new FlowLayout());
		IDsearch = new Button("���̵� ã��");
		south.add(IDsearch);

		PWsearch = new Button("��й�ȣ ã��");
		south.add(PWsearch);

		Signup = new Button("ȸ������");
		south.add(Signup);
		center.add("South", south);
		setSize(300, 350);
		addWindowListener(new WinListener());
	}

	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}
}
