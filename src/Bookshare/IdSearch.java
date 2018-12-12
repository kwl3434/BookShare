package Bookshare;

import java.awt.*;
import java.awt.event.*;

public class IdSearch extends Frame implements ActionListener {
	private Button IS_search; // ID ã�� �Ϸ�
	private Label IS_pnl;
	private TextField IS_pnf; // �ڵ�����ȣ ���� ĭ
	private Label IS_message;

	public String msg_pn="";
	
	public IdSearch(String title) {
		super(title);
		setLayout(new BorderLayout());
		
		Panel north = new Panel();
		north.setLayout(new FlowLayout());
		north.add(new Label("���̵� ã��"));
		add("North", north);
		
		Panel center = new Panel();
		center.setLayout(new FlowLayout());

		IS_pnl = new Label("�ڵ��� ��ȣ");
		center.add(IS_pnl);

		IS_pnf = new TextField(25);
		center.add(IS_pnf);
		
		IS_search = new Button("Ȯ��");
		IS_search.addActionListener(this);
		center.add(IS_search);
		add("Center", center);
		
		Panel south = new Panel();
		IS_message = new Label("���Է��Ͻ� �ڵ��� ��ȣ�� ���ڷ� ���̵� ���۵˴ϴ�.��");
		south.add(IS_message);
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
			msg_pn = IS_pnf.getText();
			if(!msg_pn.equals("")) { // 1�̸� ����â ��
				
			}else {
				System.out.println("�ȳ��ϼ�33333��");
				MessageBox msgBox = new MessageBox(this, "���̵� ã�� ����", "��Ȯ�� �ڵ��� ��ȣ�� �Է��Ͻʽÿ�.");
				msgBox.show();
			}
		}
	}
}