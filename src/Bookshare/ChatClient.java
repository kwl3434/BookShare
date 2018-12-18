package Bookshare;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ChatClient extends JFrame implements ActionListener {

	private Button cc_btLogon; // �α׿� ���� ��ư
	private Button cc_btIDsearch; // idã��
	private Button cc_btPWsearch; // pwã��
	private Button cc_btSignup; // ȸ������
	private IdSearch IdSc;
	private PwSearch PwSc;
	
	protected SignupDisplay SignD;
	protected JTextField cc_tfID; // �α׿� �Է� �ؽ�Ʈ �ʵ�
	
	public JPasswordField cc_tfPW; // �α׿� ���� �ȳ�
	
	
	public static ClientThread cc_thread;
	public static ChatClient client;
	public String msg_logon = "";
	public String msg_pw = "";

	public ChatClient(String title) {
		super(title);
		setLayout(new BorderLayout());

		JPanel north = new JPanel();
		north.setLayout(new FlowLayout());
		north.add(new JLabel("Book ��������"));
		add("North", north);

		JPanel center = new JPanel();
		center.setLayout(new FlowLayout());

		center.add(new JLabel("���̵�    "));
		cc_tfID = new JTextField(14);
		center.add(cc_tfID);

		center.add(new JLabel("��й�ȣ"));
		cc_tfPW = new JPasswordField(14);
		center.add(cc_tfPW);

		cc_btLogon = new Button("�α���");
		center.add(cc_btLogon);
		cc_btLogon.addActionListener(this);
		add("Center", center);

		JPanel south = new JPanel();
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
		addWindowListener(new WinListener());
		//setSize(100,100);
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
		}else if (b.getLabel().equals("���̵� ã��")) {
			IdSc = new IdSearch("���̵� ã��");
			IdSc.show();
			// idã�� ��ƾ
		} else if (b.getLabel().equals("��й�ȣ ã��")) {
			PwSc = new PwSearch("��й�ȣ ã��"); 
			PwSc.show();
			// ��й�ȣ ã�� ��ƾ
		} else if (b.getLabel().equals("ȸ������")) {
			// ȸ������ ��ƾ
			SignD = new SignupDisplay(cc_thread,"ȸ������");
			SignD.show();
		}
	}

	public static void main(String args[]) {
		client = new ChatClient("Book market");
		client.setSize(270, 300);
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
