package Bookshare;

import java.awt.*;
import java.awt.event.*;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

public class MessageRoom extends Frame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private Button nr_Sendbtn;
	private Label nr_CurNote, nr_Receiver, nr_Title;
	private TextArea nr_NoteContents, nr_CurrentContents; // ��ȭ�� ���� ����Ʈâ
	private List nr_lstMember, nr_lstTitle; // ��������Ʈ
	private Toolkit toolkit;
	private TextField  nr_ReceiverField, nr_TitleField; // ��ȭ�� �Է��ʵ�

	public static ClientThread dr_thread;

	public MessageRoom(ClientThread client, String title) {
		super(title);
		setLayout(new BorderLayout());

		// ��ȭ�濡�� ����ϴ� ������Ʈ�� ��ġ�Ѵ�.
		Panel northpanel = new Panel();
		northpanel.setLayout(new FlowLayout());

		Panel centerpanel = new Panel();
		centerpanel.setLayout(new BorderLayout());
		ImageIcon img = new ImageIcon("C:\\Users\\owner\\Desktop\\BOOK\\MR_Top.png");
		ImageIcon img2 = new ImageIcon("C:\\Users\\owner\\Desktop\\BOOK\\MR_Sender.png");
		ImageIcon img3 = new ImageIcon("C:\\Users\\owner\\Desktop\\BOOK\\MR_Title.png");
		ImageIcon img_content = new ImageIcon("C:\\Users\\owner\\Desktop\\BOOK\\MR_Content.png");
		ImageIcon img_bike = new ImageIcon("C:\\Users\\owner\\Desktop\\BOOK\\MR_Bike.png");
		
		nr_CurNote = new Label("�������                         ���� ���� ���                ��������");
		JLabel nr1_CurNote = new JLabel(img);
		centerpanel.add("North",nr_CurNote);
		centerpanel.add("North",nr1_CurNote);
		nr_lstMember = new List(20);
		centerpanel.add("West",nr_lstMember);
		nr_lstTitle = new List(20);
		centerpanel.add("Center",nr_lstTitle);
		
		nr_CurrentContents = new TextArea(10,30);
		centerpanel.add("East",nr_CurrentContents);
		
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
		
		
		southpanel.add("Center",Under_centerpanel);
		Under_centerpanel.add("North", Under_Northpanel);
		Under_centerpanel.add("South", Under_Southpanel);
		
		JLabel nr1_Receiver = new JLabel(img2);//�޴»��
		nr1_Receiver.setPreferredSize(new Dimension(80, 20));
		Under_Northpanel.add("West", nr1_Receiver);
		nr_ReceiverField = new TextField(20);
		nr_ReceiverField.setEditable(false);
		Under_Northpanel.add("Center", nr_ReceiverField);
		
		JLabel nr1_Title = new JLabel(img3);//����
		nr1_Title.setPreferredSize(new Dimension(80, 20));
		Under_Southpanel.add("West",nr1_Title);
		nr_TitleField = new TextField(21);
		Under_Southpanel.add("Center", nr_TitleField);
		
		southpanel.add("East", Sendpanel);
		JButton nr1_Sendbtn = new JButton("");//����������
		nr1_Sendbtn.setIcon(img_bike);
		nr1_Sendbtn.addActionListener(this);
		nr1_Sendbtn.setPreferredSize(new Dimension(135, 15));
		Sendpanel.add("Center",nr1_Sendbtn);
		
		southpanel.add("South", contentpanel);
		JLabel nr1_Content = new JLabel(img_content);
		nr1_Content.setPreferredSize(new Dimension(80, 176));
		contentpanel.add("West", nr1_Content);
		nr_NoteContents = new TextArea(10,20);
		contentpanel.add("Center", nr_NoteContents);
		
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
			dr_thread.requestQuitMSG();
		}
	}

	// ȭ�������, �α׾ƿ� �̺�Ʈ�� ó���Ѵ�.
	public void actionPerformed(ActionEvent ae) {
		Button b = (Button) ae.getSource();
		if (b.getLabel().equals("")) {
			
			// ȭ������� ó�� ��ƾ

		} else if (b.getLabel().equals("����ϱ�")) {
						// �α׾ƿ� ó�� ��ƾ
		}
	}

}