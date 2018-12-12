package Bookshare;

import java.awt.*;
import java.awt.event.*;

public class DisplayRoom extends Frame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private Button dr_btMessage; // 쪽지 창
	private Button dr_btBoard; // 게시판 창
	private Button dr_btLogout; //로그아웃 창

	public static ClientThread dr_thread;

	public DisplayRoom(ClientThread client, String title) {
		super(title);
		setLayout(new BorderLayout());

		// 대화방에서 사용하는 컴포넌트를 배치한다.
		Panel centerpanel = new Panel();
		centerpanel.setLayout(new FlowLayout());
		dr_btBoard = new Button("게시판");
		dr_btBoard.addActionListener(this);
		centerpanel.add(dr_btBoard);

		dr_btMessage = new Button("쪽지함");
		dr_btMessage.addActionListener(this);
		centerpanel.add(dr_btMessage);
		
		dr_btLogout = new Button("로그아웃");
		dr_btLogout.addActionListener(this);
		centerpanel.add(dr_btLogout);

		add("South", centerpanel);

		dr_thread = client; // ClientThread 클래스와 연결한다.

		// 입력 텍스트 필드에 포커스를 맞추는 메소드 추가
		
		setSize(300, 350);
		addWindowListener(new WinListener());
		
	}

	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			//구현 필요
		}
	}

	// 화면지우기, 로그아웃 이벤트를 처리한다.
	public void actionPerformed(ActionEvent ae) {
		Button b = (Button) ae.getSource();
		if (b.getLabel().equals("게시판")) {
			// 게시판 입장 루틴
			System.out.println("게시판");
			dr_thread.requestEnterBoard();
		} else if (b.getLabel().equals("쪽지함")) {
			dr_thread.requestEnterMessage();
			// 쪽지함 입장 루틴
		}else if(b.getLabel().equals("로그아웃")) {
			// 로그아웃 처리 루틴
		}
	}
}
