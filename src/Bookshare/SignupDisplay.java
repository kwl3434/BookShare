package Bookshare;

import java.awt.*;
import java.awt.event.*;

//  아이디 비번 전화번호

public class SignupDisplay extends Frame implements ActionListener {
	private Button SD_Signup; // 회원가입 완료
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
		head = new Label("회원가입");
		head2.add(head);
		add("North", head2);

		Panel infor = new Panel(new FlowLayout());

		SD_ID = new Label("아이디    ");
		infor.add(SD_ID);
		IDinput = new TextField(25);
		infor.add(IDinput);

		SD_PW = new Label("비밀번호");
		infor.add(SD_PW);
		PWinput = new TextField(25);
		infor.add(PWinput);

		SD_PN = new Label("전화번호");
		infor.add(SD_PN);
		PNinput = new TextField(25);
		infor.add(PNinput);

		success = new Button("완료");
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
		if (b.getLabel().equals("완료")) {
			String id = IDinput.getText();
			String pw = PWinput.getText();
			String pn = PNinput.getText();
			if(id.length()<5) {
				MessageBox msgBox = new MessageBox(this, "ID", "ID를 5글자 이상 입력해주세요.");
				msgBox.show();
				return ;
			}else if(pw.length()<5) {
				MessageBox msgBox = new MessageBox(this, "PW", "PW를 5글자 이상 입력해주세요.");
				msgBox.show();
				return ;
			}else if(pn.length()!=11) {
				MessageBox msgBox = new MessageBox(this, "PHONENUMBER", "11자의 휴대폰 번호를 입력해주세요.");
				msgBox.show();
				return ;
			}else {
				System.out.println(id+pw+pn);
				dr_thread.requestSignup(id, pw, pn);
			}
		}
	}
}