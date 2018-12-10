package Bookshare;

import java.awt.*;
import java.awt.event.*;

public class DisplayRoom extends Frame implements ActionListener, KeyListener {

	private Button dr_btClear; // ��ȭ�� â ȭ�� �����
	private Button dr_btLogout; // �α׾ƿ� ���� ��ư

	public TextArea dr_taContents; // ��ȭ�� ���� ����Ʈâ
	public List dr_lstMember; // ��ȭ�� ������

	public TextField dr_tfInput; // ��ȭ�� �Է��ʵ�

	public static ClientThread dr_thread;

	public DisplayRoom(ClientThread client, String title) {
		super(title);
		setLayout(new BorderLayout());

		// ��ȭ�濡�� ����ϴ� ������Ʈ�� ��ġ�Ѵ�.
		Panel northpanel = new Panel();
		northpanel.setLayout(new FlowLayout());
		dr_btClear = new Button("ȭ�������");
		dr_btClear.addActionListener(this);
		northpanel.add(dr_btClear);

		dr_btLogout = new Button("����ϱ�");
		dr_btLogout.addActionListener(this);
		northpanel.add(dr_btLogout);

		Panel centerpanel = new Panel();
		centerpanel.setLayout(new FlowLayout());
		dr_taContents = new TextArea(10, 27);
		dr_taContents.setEditable(false);
		centerpanel.add(dr_taContents);

		dr_lstMember = new List(10);
		centerpanel.add(dr_lstMember);

		dr_lstMember.addItemListener(null);

		Panel southpanel = new Panel();
		southpanel.setLayout(new FlowLayout());
		dr_tfInput = new TextField(41);
		dr_tfInput.addKeyListener(this);
		southpanel.add(dr_tfInput);

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
