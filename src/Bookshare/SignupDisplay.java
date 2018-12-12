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
		TextField IDinput2 = new TextField(25);
		infor.add(IDinput2);

		SD_PW = new Label("비밀번호");
		infor.add(SD_PW);
		TextField PWinput2 = new TextField(25);
		infor.add(PWinput2);

		SD_PN = new Label("전화번호");
		infor.add(SD_PN);
		TextField PNinput = new TextField(25);
		infor.add(PNinput);

		success = new Button("완료");
		infor.add(success);

		add("Center", infor);
		setSize(300, 350);
		addWindowListener(new WinListener());
	}

	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			dispose();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
}