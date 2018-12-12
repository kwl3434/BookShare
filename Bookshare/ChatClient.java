package Bookshare;

import java.awt.*;
import java.awt.event.*;

public class ChatClient extends Frame implements ActionListener {

	private Button cc_btLogon; // 로그온 실행 버튼
	private Button cc_btIDsearch; // id찾기
	private Button cc_btPWsearch; // pw찾기
	private Button cc_btSignup; // 회원가입
	protected TextField cc_tfID; // 로그온 입력 텍스트 필드
	public TextField cc_tfPW; // 로그온 개설 안내

	public static ClientThread cc_thread;
	public static ChatClient client;
	public String msg_logon = "";
	public String msg_pw = "";

	public ChatClient(String title) {
		super(title);
		setLayout(new BorderLayout());

		Panel north = new Panel();
		north.setLayout(new FlowLayout());
		north.add(new Label("Book 자유시장"));
		add("North", north);

		Panel center = new Panel();
		center.setLayout(new FlowLayout());

		center.add(new Label("아이디    "));
		cc_tfID = new TextField(25);
		center.add(cc_tfID);

		center.add(new Label("비밀번호"));
		cc_tfPW = new TextField(25);
		center.add(cc_tfPW);

		cc_btLogon = new Button("로그인");
		center.add(cc_btLogon);
		cc_btLogon.addActionListener(this);
		add("Center", center);

		Panel south = new Panel();
		south.setLayout(new FlowLayout());
		cc_btIDsearch = new Button("아이디 찾기");
		cc_btIDsearch.addActionListener(this);
		south.add(cc_btIDsearch);

		cc_btPWsearch = new Button("비밀번호 찾기");
		cc_btPWsearch.addActionListener(this);
		south.add(cc_btPWsearch);

		cc_btSignup = new Button("회원가입");
		south.add(cc_btSignup);
		cc_btSignup.addActionListener(this);
		center.add("South", south);
		setSize(300, 350);
		addWindowListener(new WinListener());
	}

	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			cc_thread.requestLogout();
			System.exit(0); // 나중에 로그아웃루틴으로 변경
		}
	}

	// 로그온, 대화방 개설 및 입장 버튼 눌림 이벤트를 처리한다.
	public void actionPerformed(ActionEvent ae) {
		Button b = (Button) ae.getSource();
		if (b.getLabel().equals("로그인")) {
			// 로그온 처리 루틴
			msg_logon = cc_tfID.getText(); // 로그온 ID를 읽는다.
			msg_pw = cc_tfPW.getText();
			if (msg_logon.compareTo("")!=0 && msg_pw.compareTo("")!=0) {
				cc_thread.requestLogon(msg_logon,msg_pw); // ClientThread의 메소드를 호출
				System.out.println(cc_tfID+" "+cc_tfPW);
			} else {
				MessageBox msgBox = new MessageBox(this, "로그온", "로그온 id,password를 입력하세요.");
				msgBox.show();
			}
		} else if (b.getLabel().equals("아이디 찾기")) {
			IdSearch IdSc = new IdSearch("아이디 찾기");
			IdSc.show();
			// id찾기 루틴

		} else if (b.getLabel().equals("비밀번호 찾기")) {
			PwSearch PwSc = new PwSearch("비밀번호 찾기");
			PwSc.show();
			// 비밀번호 찾기 루틴
		} else if (b.getLabel().equals("회원가입")) {
			// 회원가입 루틴
		}
	}

	public static void main(String args[]) {
		client = new ChatClient("대화방 개설 및 입장");
		client.setSize(300, 350);
		client.show();

		// 소켓을 생성하고 서버와 통신할 스레드를 호출한다.

		// 서버와 클라이언트를 다른 시스템으로 사용하는 경우
		// 실행 : java ChatClient [호스트이름과 포트번호가 필요하다.]
		// To DO

		// 서버와 클라이언트를 같은 시스템으로 사용하는 경우
		// 실행 : java ChatClient [호스트이름과 포트번호가 필요없다.]
		try {
			cc_thread = new ClientThread(client); // 로컬 호스트용 생성자
			cc_thread.start(); // 클라이언트의 스레드를 시작한다.
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
