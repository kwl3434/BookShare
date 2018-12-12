package Bookshare;

import java.awt.*;
import java.awt.event.*;

public class PwSearch extends Frame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4L;
	private Button PS_search; // ID 찾기 완료
	private Label PS_pnl;
	private TextField PS_pnf; // 핸드폰번호 쓰는 칸
	private Label PS_message;

	public PwSearch(String title) {
		super(title);
		setLayout(new BorderLayout());
		
		Panel north = new Panel();
		north.setLayout(new FlowLayout());
		north.add(new Label("비밀번호 찾기"));
		add("North", north);
		
		Panel center = new Panel();
		center.setLayout(new FlowLayout());

		PS_pnl = new Label("핸드폰 번호");
		center.add(PS_pnl);

		PS_pnf = new TextField(25);
		center.add(PS_pnf);
		
		PS_search = new Button("확인");
		PS_search.addActionListener(this);
		center.add(PS_search);
		add("Center", center);
		
		Panel south = new Panel();
		PS_message = new Label("※입력하신 핸드폰 번호의 문자로 비밀번호가 전송됩니다.※");
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
		if(b.getLabel().equals("확인")) {
			if(PS_pnf.equals("")) { // 1이면 에러창 뜸
				MessageBox msgBox = new MessageBox(this, "비밀번호 찾기 실패", "정확한 핸드폰 번호를 입력하십시오.");
				msgBox.show();
			}else {
				
			}
		}
	}
}