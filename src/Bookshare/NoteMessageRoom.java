package Bookshare;

import java.awt.*;
import java.awt.event.*;

public class NoteMessageRoom extends Frame implements ActionListener {

	private Button nr_Sendbtn;
	
	private Label nr_CurNote, nr_CurLabel, nr_Receiver, nr_Title;
	private TextArea nr_ReveiveTitle, dr_taContents, nr_NoteContents, nr_CurrentContents; // 대화말 내용 리스트창
	private List dr_lstMember; // 쪽지리스트
	

	private TextField dr_tfInput, nr_ReceiverField, nr_TitleField, nr_NoteField; // 대화말 입력필드

	private static ClientThread dr_thread;

	public NoteMessageRoom(ClientThread client, String title) {
		super(title);
		setLayout(new BorderLayout());

		// 대화방에서 사용하는 컴포넌트를 배치한다.
		Panel northpanel = new Panel();
		northpanel.setLayout(new FlowLayout());

		Panel centerpanel = new Panel();
		centerpanel.setLayout(new BorderLayout());

		nr_CurNote = new Label("보낸사람                         받은 쪽지 목록                쪽지내용");
		centerpanel.add("North",nr_CurNote);
		dr_lstMember = new List(20);
		centerpanel.add("West",dr_lstMember);
		dr_lstMember.addItemListener(null);
		nr_ReveiveTitle = new TextArea(10,15);
		nr_ReveiveTitle.setEditable(false);
		centerpanel.add("Center",nr_ReveiveTitle);
		nr_CurrentContents = new TextArea(10,30);
		centerpanel.add("East",nr_CurrentContents);
		
		
		Panel southpanel = new Panel();
		Panel Note = new Panel();
		Panel Title = new Panel();
		southpanel.setLayout(new BorderLayout());
		Note.setLayout(new BorderLayout());
		Title.setLayout(new BorderLayout());
		
		nr_Receiver = new Label("받는사람");
		southpanel.add("West", nr_Receiver);
		nr_ReceiverField = new TextField(21);
		nr_ReceiverField.setEditable(false);
		southpanel.add("Center", nr_ReceiverField);
		southpanel.add("South", Note);
		
		nr_Title = new Label("제목        ");
		Note.add("West",nr_Title);
		nr_TitleField = new TextField(21);
		Note.add("Center", nr_TitleField);
		
		Panel Note2 = new Panel();
		Note2.setLayout(new BorderLayout());
		Note.add("South", Note2);
		nr_NoteContents = new TextArea(10,21);
		Note2.add("North", nr_NoteContents);
		
		Panel Notebtn = new Panel();
		Notebtn.setLayout(new FlowLayout());
		Note2.add("South", Notebtn);
		nr_Sendbtn = new Button("쪽지 보내기");
		Notebtn.add(nr_Sendbtn);
		
		add("North", northpanel);
		add("Center", centerpanel);
		add("South", southpanel);

		dr_thread = client; // ClientThread 클래스와 연결한다.

		// 입력 텍스트 필드에 포커스를 맞추는 메소드 추가
		setSize(300, 350);
		addWindowListener(new WinListener());

	}

	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			dr_thread.requestQuitRoom();
		}
	}

	// 화면지우기, 로그아웃 이벤트를 처리한다.
	public void actionPerformed(ActionEvent ae) {
		Button b = (Button) ae.getSource();
		if (b.getLabel().equals("쪽지보내기")) {
			
			// 화면지우기 처리 루틴

		} else if (b.getLabel().equals("퇴실하기")) {
						// 로그아웃 처리 루틴
		}
	}

}