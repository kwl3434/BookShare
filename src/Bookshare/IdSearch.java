package Bookshare;

import java.awt.*;
import java.awt.event.*;

public class IdSearch extends Frame implements ActionListener {
	private Button IS_search; // ID 찾기 완료
	private Label IS_pnl;
	private TextField IS_pnf; // 핸드폰번호 쓰는 칸
	private Label IS_message;

	public String msg_pn="";
	
	public IdSearch(String title) {
		super(title);
		setLayout(new BorderLayout());
		
		Panel north = new Panel();
		north.setLayout(new FlowLayout());
		north.add(new Label("아이디 찾기"));
		add("North", north);
		
		Panel center = new Panel();
		center.setLayout(new FlowLayout());

		IS_pnl = new Label("핸드폰 번호");
		center.add(IS_pnl);

		IS_pnf = new TextField(25);
		center.add(IS_pnf);
		
		IS_search = new Button("확인");
		IS_search.addActionListener(this);
		center.add(IS_search);
		add("Center", center);
		
		Panel south = new Panel();
		IS_message = new Label("※입력하신 핸드폰 번호의 문자로 아이디가 전송됩니다.※");
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
		if(b.getLabel().equals("확인")) {
			msg_pn = IS_pnf.getText();
			if(!msg_pn.equals("")) { // 1이면 에러창 뜸
				
			}else {
				System.out.println("안녕하세33333요");
				MessageBox msgBox = new MessageBox(this, "아이디 찾기 실패", "정확한 핸드폰 번호를 입력하십시오.");
				msgBox.show();
			}
		}
	}
}