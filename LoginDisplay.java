package bookshare;

import java.awt.*;
import java.awt.event.*;

public class LoginDisplay extends Frame implements ActionListener {
	private Button LD_btlogon; // 로그인 실행 버튼
	private Button IDsearch; // ID 찾기
	private Button PWsearch; // PW 찾기
	private Button Signup; // 회원가입

	public LoginDisplay(ClientThread client, String title) {
		super(title);
		setLayout(new BorderLayout());

		Panel north = new Panel();
		north.setLayout(new FlowLayout());
		north.add(new Label("Book 자유시장"));
		add("North", north);

		Panel center = new Panel();
		center.setLayout(new FlowLayout());

		center.add(new Label("아이디    "));
		TextField IDinput = new TextField(25);
		center.add(IDinput);

		center.add(new Label("비밀번호"));
		TextField PWinput = new TextField(25);
		center.add(PWinput);

		LD_btlogon = new Button("로그인");
		center.add(LD_btlogon);
		add("Center", center);

		Panel south = new Panel();
		south.setLayout(new FlowLayout());
		IDsearch = new Button("아이디 찾기");
		south.add(IDsearch);

		PWsearch = new Button("비밀번호 찾기");
		south.add(PWsearch);

		Signup = new Button("회원가입");
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
