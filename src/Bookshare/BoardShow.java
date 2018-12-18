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

public class BoardShow extends Frame implements ActionListener{
	
	private Button dr_btRemove; // ����
	private Button dr_btMessage; //�����ϱ�
	private Label Title;
	private Label Contents;
	private Label Password;
	private Label Time;
	
	private TextArea dr_taContents; // �Խ��� ����
	private TextField dr_tfPassword; // ��ȭ�� �Է��ʵ�
	private TextField dr_tfTitle; //�Խ��� ����
	private TextField boardtime;
	public static ClientThread dr_thread;
	public int b_no=0;
	
	public BoardShow(ClientThread client, String title) {
		super(title);
		setLayout(new BorderLayout());

		// ��ȭ�濡�� ����ϴ� ������Ʈ�� ��ġ�Ѵ�.
		Panel southpanel = new Panel();
		southpanel.setLayout(new FlowLayout());
		dr_btRemove = new Button("�����ϱ�");
		dr_btRemove.addActionListener(this);
		southpanel.add(dr_btRemove);
		dr_btMessage = new Button("�۾��̿��� �����ϱ�");
		dr_btMessage.addActionListener(this);
		southpanel.add(dr_btMessage);

		Panel centerpanel = new Panel();
		centerpanel.setLayout(new FlowLayout());
		
		Contents = new Label("����");
		centerpanel.add(Contents);
		
		dr_taContents = new TextArea(50,50);
		dr_taContents.setEditable(false);
		centerpanel.add(dr_taContents);

		Panel northpanel = new Panel();
		northpanel.setLayout(new FlowLayout());
		Title = new Label("����");
		northpanel.add(Title);
		dr_tfTitle = new TextField(41);
		dr_tfTitle.setEditable(false);
		northpanel.add(dr_tfTitle);
		Time = new Label("�ۼ��ð�");
		boardtime = new TextField();
		boardtime.setEditable(false);
		northpanel.add(Time);
		northpanel.add(boardtime);
		
		
		Password = new Label("��й�ȣ");
		northpanel.add(Password);
		
		dr_tfPassword = new TextField(13);
		northpanel.add(dr_tfPassword);

		add("North", northpanel);
		add("Center", centerpanel);
		add("South", southpanel);

		dr_thread = client; // ClientThread Ŭ������ �����Ѵ�.

		// �Է� �ؽ�Ʈ �ʵ忡 ��Ŀ���� ���ߴ� �޼ҵ� �߰�

		addWindowListener(new WinListener());

	}

	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			dr_thread.requestQuitRBoard();
			//ä������
		}
	}

	// ȭ�������, �α׾ƿ� �̺�Ʈ�� ó���Ѵ�.
	public void actionPerformed(ActionEvent ae) {
		Button b = (Button) ae.getSource();
		if(b.getLabel().equals("�����ϱ�")) {
			dr_thread.requestremoveBoard(dr_tfPassword.getText(), b_no);
			
		}else if(b.getLabel().equals("�۾��̿��� �����ϱ�")) {
			//ä������
		}
	}
	public void setTextArea(String title, String data,String date) {
		dr_tfTitle.setText(title);
		dr_taContents.setText(data);
		boardtime.setText(date);
	}

}
