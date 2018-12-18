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
	private TextArea nr_NoteContents, nr_CurrentContents; // ��ȭ�� ���� ����Ʈâ
	private Button nr1_Sendbtn;
	public List nr_lstMember, nr_lstTitle; // ��������Ʈ
	public Vector<String> VecText;
	private Toolkit toolkit;
	private TextField nr_ReceiverField, nr_TitleField; // ��ȭ�� �Է��ʵ�

	public static ClientThread dr_thread;

	public MessageRoom(ClientThread client, String title) {
		super(title);
		setLayout(new BorderLayout());
		VecText = new Vector<String>();
		// ��ȭ�濡�� ����ϴ� ������Ʈ�� ��ġ�Ѵ�.
		Panel northpanel = new Panel();
		northpanel.setLayout(new FlowLayout());

		Panel centerpanel = new Panel();
		centerpanel.setLayout(new BorderLayout());
		ImageIcon img = new ImageIcon("MR_Top.png");
		ImageIcon img2 = new ImageIcon("MR_Sender.png");
		ImageIcon img3 = new ImageIcon("MR_Title.png");
		ImageIcon img_content = new ImageIcon("MR_Content.png");
		ImageIcon img_bike = new ImageIcon("MR_Bike.png");

		nr_CurNote = new Label("�������                         ���� ���� ���                ��������");
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

		JLabel nr1_Receiver = new JLabel(img2);// �޴»��
		nr1_Receiver.setPreferredSize(new Dimension(80, 20));
		Under_Northpanel.add("West", nr1_Receiver);
		nr_ReceiverField = new TextField(20);
		nr_ReceiverField.setEditable(false);
		Under_Northpanel.add("Center", nr_ReceiverField);

		/*
		 * JLabel nr1_Title = new JLabel(img3);//���� nr1_Title.setPreferredSize(new
		 * Dimension(80, 20)); Under_Southpanel.add("West",nr1_Title); nr_TitleField =
		 * new TextField(21); Under_Southpanel.add("Center", nr_TitleField);
		 */

		southpanel.add("East", Sendpanel);
		nr1_Sendbtn = new Button("�޼���������");// ����������
		nr1_Sendbtn.addActionListener(this);
		Sendpanel.add("Center", nr1_Sendbtn);
		Button nr1_Readbtn = new Button("�޼����б�");
		nr1_Readbtn.addActionListener(this);
		Sendpanel.add("East", nr1_Readbtn);

		Button nr1_IDbtn = new Button("�����ڼ���");
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

		dr_thread = client; // ClientThread Ŭ������ �����Ѵ�.

		// �Է� �ؽ�Ʈ �ʵ忡 ��Ŀ���� ���ߴ� �޼ҵ� �߰�
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

	// ȭ�������, �α׾ƿ� �̺�Ʈ�� ó���Ѵ�.
	public void actionPerformed(ActionEvent ae) {
		Button b = (Button) ae.getSource();
		if (b.getLabel().equals("�޼����б�")) {
			nr_CurrentContents.setText(VecText.get(nr_lstTitle.getSelectedIndex()));
		} else if (b.getLabel().equals("�����ڼ���")) {
			nr_ReceiverField.setText(nr_lstMember.getItem(nr_lstMember.getSelectedIndex()));
		}else if(b.getLabel().equals("�޼���������")) {
			if(!(nr_NoteContents.getText().equals(""))&&!(nr_ReceiverField.getText().equals(""))) {
			dr_thread.requestSendMessage(nr_ReceiverField.getText(),nr_NoteContents.getText());
			}
		}
	}

}