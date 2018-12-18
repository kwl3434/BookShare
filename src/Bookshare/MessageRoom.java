package Bookshare;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

public class MessageRoom extends Frame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// private Button nr_Sendbtn;
	private Label nr_CurNote, nr_Receiver, nr_Title;
	private TextArea nr_NoteContents, nr_CurrentContents; // 대화말 내용 리스트창
	private Button nr1_Sendbtn;
	public List nr_lstMember, nr_lstTitle; // 쪽지리스트
	public Vector<String> VecText;
	private Toolkit toolkit;
	private TextField nr_ReceiverField, nr_TitleField; // 대화말 입력필드

	public static ClientThread dr_thread;

	public MessageRoom(ClientThread client, String title) {
		super(title);
		setLayout(new BorderLayout());
		VecText = new Vector<String>();
		// 대화방에서 사용하는 컴포넌트를 배치한다.
		Panel northpanel = new Panel();
		northpanel.setLayout(new FlowLayout());

		Panel centerpanel = new Panel();
		centerpanel.setLayout(new BorderLayout());
		ImageIcon img = new ImageIcon("MR_Top.png");
		ImageIcon img2 = new ImageIcon("MR_Sender.png");
		ImageIcon img3 = new ImageIcon("MR_Title.png");
		ImageIcon img_content = new ImageIcon("MR_Content.png");
		ImageIcon img_bike = new ImageIcon("MR_Bike.png");

		nr_CurNote = new Label("보낸사람                         받은 쪽지 목록                쪽지내용");
		JLabel nr1_CurNote = new JLabel(img);
		centerpanel.add("North", nr_CurNote);
		centerpanel.add("North", nr1_CurNote);
		nr_lstMember = new List(20);
		centerpanel.add("West", nr_lstMember);

		nr_lstTitle = new List(20);
		centerpanel.add("Center", nr_lstTitle);

		nr_CurrentContents = new TextArea(10, 30);
		nr_CurrentContents.setEditable(false);
		centerpanel.add("East", nr_CurrentContents);

		Panel southpanel = new Panel();
		Panel Under_Southpanel = new Panel();
		Panel Under_centerpanel = new Panel();
		Panel Under_Northpanel = new Panel();
		Panel Note = new Panel();
		Panel Title = new Panel();
		Panel Sendpanel = new Panel();
		Panel contentpanel = new Panel();

		southpanel.setLayout(new BorderLayout());
		Under_Southpanel.setLayout(new BorderLayout());
		Under_centerpanel.setLayout(new BorderLayout());
		Under_Northpanel.setLayout(new BorderLayout());
		Note.setLayout(new BorderLayout());
		Title.setLayout(new BorderLayout());
		Sendpanel.setLayout(new BorderLayout());
		contentpanel.setLayout(new BorderLayout());

		southpanel.add("Center", Under_centerpanel);
		Under_centerpanel.add("North", Under_Northpanel);
		Under_centerpanel.add("South", Under_Southpanel);

		JLabel nr1_Receiver = new JLabel(img2);// 받는사람
		nr1_Receiver.setPreferredSize(new Dimension(80, 20));
		Under_Northpanel.add("West", nr1_Receiver);
		nr_ReceiverField = new TextField(20);
		nr_ReceiverField.setEditable(false);
		Under_Northpanel.add("Center", nr_ReceiverField);

		/*
		 * JLabel nr1_Title = new JLabel(img3);//제목 nr1_Title.setPreferredSize(new
		 * Dimension(80, 20)); Under_Southpanel.add("West",nr1_Title); nr_TitleField =
		 * new TextField(21); Under_Southpanel.add("Center", nr_TitleField);
		 */

		southpanel.add("East", Sendpanel);
		nr1_Sendbtn = new Button("메세지보내기");// 쪽지보내기
		nr1_Sendbtn.addActionListener(this);
		Sendpanel.add("Center", nr1_Sendbtn);
		Button nr1_Readbtn = new Button("메세지읽기");
		nr1_Readbtn.addActionListener(this);
		Sendpanel.add("East", nr1_Readbtn);

		Button nr1_IDbtn = new Button("수신자선택");
		nr1_IDbtn.addActionListener(this);
		Sendpanel.add("West", nr1_IDbtn);

		southpanel.add("South", contentpanel);
		JLabel nr1_Content = new JLabel(img_content);
		nr1_Content.setPreferredSize(new Dimension(80, 176));
		contentpanel.add("West", nr1_Content);
		nr_NoteContents = new TextArea(10, 20);
		contentpanel.add("Center", nr_NoteContents);

		add("North", northpanel);
		add("Center", centerpanel);
		add("South", southpanel);

		dr_thread = client; // ClientThread 클래스와 연결한다.

		// 입력 텍스트 필드에 포커스를 맞추는 메소드 추가
		setSize(300, 350);
		addWindowListener(new WinListener());

	}

	public void SETrcvtext(String dest) {
		nr_ReceiverField.setText(dest);
	}

	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			dr_thread.requestQuitMSG();
			nr_lstMember.removeAll();
			nr_lstTitle.removeAll();
			VecText.removeAllElements();
		}
	}

	// 화면지우기, 로그아웃 이벤트를 처리한다.
	public void actionPerformed(ActionEvent ae) {
		Button b = (Button) ae.getSource();
		if (b.getLabel().equals("메세지읽기")) {
			nr_CurrentContents.setText(VecText.get(nr_lstTitle.getSelectedIndex()));
		} else if (b.getLabel().equals("수신자선택")) {
			nr_ReceiverField.setText(nr_lstMember.getItem(nr_lstMember.getSelectedIndex()));
		}else if(b.getLabel().equals("메세지보내기")) {
			if(!(nr_NoteContents.getText().equals(""))&&!(nr_ReceiverField.getText().equals(""))) {
			dr_thread.requestSendMessage(nr_ReceiverField.getText(),nr_NoteContents.getText());
			}
		}
	}

}