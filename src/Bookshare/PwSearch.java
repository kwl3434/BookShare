package Bookshare;

import java.awt.*;
import java.awt.event.*;

public class PwSearch extends Frame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4L;
	private Button PS_search; // ID ã�� �Ϸ�
	private Label PS_pnl;
	private TextField PS_pnf; // �ڵ�����ȣ ���� ĭ
	private Label PS_message;

	public PwSearch(String title) {
		super(title);
		setLayout(new BorderLayout());
		
		Panel north = new Panel();
		north.setLayout(new FlowLayout());
		north.add(new Label("��й�ȣ ã��"));
		add("North", north);
		
		Panel center = new Panel();
		center.setLayout(new FlowLayout());

		PS_pnl = new Label("�ڵ��� ��ȣ");
		center.add(PS_pnl);

		PS_pnf = new TextField(25);
		center.add(PS_pnf);
		
		PS_search = new Button("Ȯ��");
		PS_search.addActionListener(this);
		center.add(PS_search);
		add("Center", center);
		
		Panel south = new Panel();
		PS_message = new Label("���Է��Ͻ� �ڵ��� ��ȣ�� ���ڷ� ��й�ȣ�� ���۵˴ϴ�.��");
		south.add(PS_message);
		add("South", south);
		
		addWindowListener(new WinListener());
		setSize(350,150);
		setVisible(true);
	}
	
	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			dispose();
		}
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		Button b = (Button)ae.getSource();
		if(b.getLabel().equals("Ȯ��")) {
			if(PS_pnf.equals("")) { // 1�̸� ����â ��
				MessageBox msgBox = new MessageBox(this, "��й�ȣ ã�� ����", "��Ȯ�� �ڵ��� ��ȣ�� �Է��Ͻʽÿ�.");
				msgBox.show();
			}else {
				
			}
		}
	}
}