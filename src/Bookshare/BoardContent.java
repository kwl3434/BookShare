package Bookshare;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

public class BoardContent extends Frame implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3L;

	//private Button dr_btRevise; // 수정
	private Button dr_btUpload; // 업로드하기
	
	private Label Title;
	private Label Contents;
	private Label Password;
	
	private TextArea dr_taContents; // 게시판 내용
	private TextField dr_tfPassword; // 대화말 입력필드
	private TextField dr_tfTitle; //게시판 제목
	private Date boardtime;
	public static ClientThread dr_thread;

	public BoardContent(ClientThread client, String title) {
		super(title);
		setLayout(new BorderLayout());

		// 대화방에서 사용하는 컴포넌트를 배치한다.
		Panel southpanel = new Panel();
		southpanel.setLayout(new FlowLayout());
		//dr_btRevise = new Button("수정하기");
		//dr_btRevise.addActionListener(this);
		//southpanel.add(dr_btRevise);

		dr_btUpload = new Button("업로드하기");
		dr_btUpload.addActionListener(this);
		southpanel.add(dr_btUpload);

		Panel centerpanel = new Panel();
		centerpanel.setLayout(new FlowLayout());
		
		Contents = new Label("내용");
		centerpanel.add(Contents);
		
		dr_taContents = new TextArea(50,50);
		dr_taContents.setEditable(true);
		centerpanel.add(dr_taContents);

		Panel northpanel = new Panel();
		northpanel.setLayout(new FlowLayout());
		Title = new Label("제목");
		northpanel.add(Title);
		
		dr_tfTitle = new TextField(41);
		northpanel.add(dr_tfTitle);
		
		Password = new Label("비밀번호");
		northpanel.add(Password);
		
		dr_tfPassword = new TextField(13);
		northpanel.add(dr_tfPassword);

		add("North", northpanel);
		add("Center", centerpanel);
		add("South", southpanel);

		dr_thread = client; // ClientThread 클래스와 연결한다.

		// 입력 텍스트 필드에 포커스를 맞추는 메소드 추가

		addWindowListener(new WinListener());

	}

	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			dr_thread.requestQuitWBoard();
		}
	}

	// 화면지우기, 로그아웃 이벤트를 처리한다.
	public void actionPerformed(ActionEvent ae) {
		Button b = (Button) ae.getSource();
		if (b.getLabel().equals("업로드하기")) {
			//채워야함
		}
	}

}
