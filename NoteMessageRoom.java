package Bookshare;

import java.awt.*;
import java.awt.event.*;

public class NoteMessageRoom extends Frame implements ActionListener, KeyListener {

	private Button dr_btClear; // ��ȭ�� â ȭ�� �����
	private Button dr_btLogout; // �α׾ƿ� ���� ��ư
	private Button nr_Sendbtn;
	
	public Label nr_CurNote, nr_CurLabel, nr_Receiver, nr_Title;
	public TextArea nr_ReveiveTitle, dr_taContents, nr_NoteContents, nr_CurrentContents; // ��ȭ�� ���� ����Ʈâ
	public List dr_lstMember; // ��������Ʈ
	

	public TextField dr_tfInput, nr_ReceiverField, nr_TitleField, nr_NoteField; // ��ȭ�� �Է��ʵ�

	public static ClientThread dr_thread;

	public NoteMessageRoom(ClientThread client, String title) {
		super(title);
		setLayout(new BorderLayout());

		// ��ȭ�濡�� ����ϴ� ������Ʈ�� ��ġ�Ѵ�.
		Panel northpanel = new Panel();
		northpanel.setLayout(new FlowLayout());
		
		/*dr_btClear = new Button("ȭ�������");
		dr_btClear.addActionListener(this);
		northpanel.add(dr_btClear);

		dr_btLogout = new Button("����ϱ�");
		dr_btLogout.addActionListener(this);
		northpanel.add(dr_btLogout);*/
		

		Panel centerpanel = new Panel();
		centerpanel.setLayout(new BorderLayout());
		//dr_taContents = new TextArea(10, 27);
		//dr_taContents.setEditable(false);
		//centerpanel.add(dr_taContents);

		nr_CurNote = new Label("�������                         ���� ���� ���                ��������");
		centerpanel.add("North",nr_CurNote);
		dr_lstMember = new List(20);
		centerpanel.add("West",dr_lstMember);
		dr_lstMember.addItemListener(null);
		nr_ReveiveTitle = new TextArea(10,15);
		nr_ReveiveTitle.setEditable(false);
		centerpanel.add("Center",nr_ReveiveTitle);
		nr_CurrentContents = new TextArea(10,30);
		centerpanel.add("East",nr_CurrentContents);
		
		//Panel Currentpanel = new Panel();
		//Currentpanel.setLayout(new BorderLayout());
		//centerpanel.add("Center", Currentpanel);
		//nr_CurLabel = new Label("��������");
		//Currentpanel.add("North", nr_CurLabel);
		//nr_CurrentContents = new TextArea(10,30);
		//Currentpanel.add("South", nr_CurrentContents);
		
		
		Panel southpanel = new Panel();
		Panel Note = new Panel();
		Panel Title = new Panel();
		southpanel.setLayout(new BorderLayout());
		Note.setLayout(new BorderLayout());
		Title.setLayout(new BorderLayout());
		
		nr_Receiver = new Label("�޴»��");
		southpanel.add("West", nr_Receiver);
		nr_ReceiverField = new TextField(21);
		nr_ReceiverField.setEditable(false);
		southpanel.add("Center", nr_ReceiverField);
		southpanel.add("South", Note);
		
		nr_Title = new Label("����        ");
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
		nr_Sendbtn = new Button("���� ������");
		Notebtn.add(nr_Sendbtn);
		
		
		
		//dr_tfInput = new TextField(41);
		//dr_tfInput.addKeyListener(this);
		//southpanel.add(dr_tfInput);

		add("North", northpanel);
		add("Center", centerpanel);
		add("South", southpanel);

		dr_thread = client; // ClientThread Ŭ������ �����Ѵ�.

		// �Է� �ؽ�Ʈ �ʵ忡 ��Ŀ���� ���ߴ� �޼ҵ� �߰�

		addWindowListener(new WinListener());

	}

	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			dr_thread.requestQuitRoom();
		}
	}

	// ȭ�������, �α׾ƿ� �̺�Ʈ�� ó���Ѵ�.
	public void actionPerformed(ActionEvent ae) {
		Button b = (Button) ae.getSource();
		if (b.getLabel().equals("ȭ�������")) {
			dr_taContents.setText("");
			// ȭ������� ó�� ��ƾ

		} else if (b.getLabel().equals("����ϱ�")) {
			dr_thread.requestQuitRoom();
			// �α׾ƿ� ó�� ��ƾ
		}
	}

	// �Է��ʵ忡 �Է��� ��ȭ���� ������ �����Ѵ�.
	public void keyPressed(KeyEvent ke) {
		if (ke.getKeyChar() == KeyEvent.VK_ENTER) {
			String item = dr_lstMember.getSelectedItem();
			String words = dr_tfInput.getText();
			if (item != null) {
				dr_lstMember.deselect(dr_lstMember.getSelectedIndex());
				dr_thread.requestWisperSend(words, item);
			} else {
				dr_thread.requestSendWords(words); // ��ȭ���� ������ ����� �����Ѵ�.
			}
		}
	}

	public void keyReleased(KeyEvent ke) {
	}

	public void keyTyped(KeyEvent ke) {
	}

}