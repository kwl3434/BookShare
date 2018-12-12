package Bookshare;

import java.awt.*;
import java.awt.event.*;

public class NoteMessageRoom extends Frame implements ActionListener {

	private Button nr_Sendbtn;
	
	private Label nr_CurNote, nr_CurLabel, nr_Receiver, nr_Title;
	private TextArea nr_ReveiveTitle, dr_taContents, nr_NoteContents, nr_CurrentContents; // ��ȭ�� ���� ����Ʈâ
	private List dr_lstMember; // ��������Ʈ
	

	private TextField dr_tfInput, nr_ReceiverField, nr_TitleField, nr_NoteField; // ��ȭ�� �Է��ʵ�

	private static ClientThread dr_thread;

	public NoteMessageRoom(ClientThread client, String title) {
		super(title);
		setLayout(new BorderLayout());

		// ��ȭ�濡�� ����ϴ� ������Ʈ�� ��ġ�Ѵ�.
		Panel northpanel = new Panel();
		northpanel.setLayout(new FlowLayout());

		Panel centerpanel = new Panel();
		centerpanel.setLayout(new BorderLayout());

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
		
		add("North", northpanel);
		add("Center", centerpanel);
		add("South", southpanel);

		dr_thread = client; // ClientThread Ŭ������ �����Ѵ�.

		// �Է� �ؽ�Ʈ �ʵ忡 ��Ŀ���� ���ߴ� �޼ҵ� �߰�
		setSize(300, 350);
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
		if (b.getLabel().equals("����������")) {
			
			// ȭ������� ó�� ��ƾ

		} else if (b.getLabel().equals("����ϱ�")) {
						// �α׾ƿ� ó�� ��ƾ
		}
	}

}