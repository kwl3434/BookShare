package Bookshare;

import java.awt.*;
import java.awt.event.*;

public class ChatClient extends Frame implements ActionListener {

	private Button cc_btLogon; // �α׿� ���� ��ư
	private Button cc_btIDsearch; // idã��
	private Button cc_btPWsearch; // pwã��
	private Button cc_btSignup; // ȸ������
	protected TextField cc_tfID; // �α׿� �Է� �ؽ�Ʈ �ʵ�
	public TextField cc_tfPW; // �α׿� ���� �ȳ�

	public static ClientThread cc_thread;
	public static ChatClient client;
	public String msg_logon = "";
	public String msg_pw = "";

	public ChatClient(String title) {
		super(title);
		setLayout(new BorderLayout());

		Panel north = new Panel();
		north.setLayout(new FlowLayout());
		north.add(new Label("Book ��������"));
		add("North", north);

		Panel center = new Panel();
		center.setLayout(new FlowLayout());

		center.add(new Label("���̵�    "));
		cc_tfID = new TextField(25);
		center.add(cc_tfID);

		center.add(new Label("��й�ȣ"));
		cc_tfPW = new TextField(25);
		center.add(cc_tfPW);

		cc_btLogon = new Button("�α���");
		center.add(cc_btLogon);
		cc_btLogon.addActionListener(this);
		add("Center", center);

		Panel south = new Panel();
		south.setLayout(new FlowLayout());
		cc_btIDsearch = new Button("���̵� ã��");
		cc_btIDsearch.addActionListener(this);
		south.add(cc_btIDsearch);

		cc_btPWsearch = new Button("��й�ȣ ã��");
		cc_btPWsearch.addActionListener(this);
		south.add(cc_btPWsearch);

		cc_btSignup = new Button("ȸ������");
		south.add(cc_btSignup);
		cc_btSignup.addActionListener(this);
		center.add("South", south);
		setSize(300, 350);
		addWindowListener(new WinListener());
	}

	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			cc_thread.requestLogout();
			System.exit(0); // ���߿� �α׾ƿ���ƾ���� ����
		}
	}

	// �α׿�, ��ȭ�� ���� �� ���� ��ư ���� �̺�Ʈ�� ó���Ѵ�.
	public void actionPerformed(ActionEvent ae) {
		Button b = (Button) ae.getSource();
		if (b.getLabel().equals("�α���")) {
			// �α׿� ó�� ��ƾ
			msg_logon = cc_tfID.getText(); // �α׿� ID�� �д´�.
			msg_pw = cc_tfPW.getText();
			if (msg_logon.compareTo("")!=0 && msg_pw.compareTo("")!=0) {
				cc_thread.requestLogon(msg_logon,msg_pw); // ClientThread�� �޼ҵ带 ȣ��
				System.out.println(cc_tfID+" "+cc_tfPW);
			} else {
				MessageBox msgBox = new MessageBox(this, "�α׿�", "�α׿� id,password�� �Է��ϼ���.");
				msgBox.show();
			}
		} else if (b.getLabel().equals("���̵� ã��")) {
			IdSearch IdSc = new IdSearch("���̵� ã��");
			IdSc.show();
			// idã�� ��ƾ

		} else if (b.getLabel().equals("��й�ȣ ã��")) {
			PwSearch PwSc = new PwSearch("��й�ȣ ã��");
			PwSc.show();
			// ��й�ȣ ã�� ��ƾ
		} else if (b.getLabel().equals("ȸ������")) {
			// ȸ������ ��ƾ
		}
	}

	public static void main(String args[]) {
		client = new ChatClient("��ȭ�� ���� �� ����");
		client.setSize(300, 350);
		client.show();

		// ������ �����ϰ� ������ ����� �����带 ȣ���Ѵ�.

		// ������ Ŭ���̾�Ʈ�� �ٸ� �ý������� ����ϴ� ���
		// ���� : java ChatClient [ȣ��Ʈ�̸��� ��Ʈ��ȣ�� �ʿ��ϴ�.]
		// To DO

		// ������ Ŭ���̾�Ʈ�� ���� �ý������� ����ϴ� ���
		// ���� : java ChatClient [ȣ��Ʈ�̸��� ��Ʈ��ȣ�� �ʿ����.]
		try {
			cc_thread = new ClientThread(client); // ���� ȣ��Ʈ�� ������
			cc_thread.start(); // Ŭ���̾�Ʈ�� �����带 �����Ѵ�.
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
