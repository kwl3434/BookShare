package Bookshare;

import java.awt.*;
import java.awt.event.*;

public class BoardMain extends Frame implements ActionListener{

	private Label lr_Label1;
	private Label lr_Label2;
	private Button lr_btwrite;	 // 글 작성 버튼
	private Button lr_btLook;	// 뒤로가기 버튼
	private Button lr_btfind;	// 검색 버튼
	private List lr_writelist; // 글 목록 리스트
	private TextField lr_tfInput; // 글 검색 필드
	

	public static ClientThread dr_thread;

	public BoardMain(ClientThread client, String title) {
		super(title);
		setLayout(new BorderLayout());
		this.setSize(800, 600);
		
		Dimension dS = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dF = this.getSize();
		int x = (int)((dS.getWidth()-dF.getWidth())/2);
		int y = (int)((dS.getHeight()-dF.getHeight())/2);
		this.setLocation(x, y);
		
		// 사용하는 컴포넌트를 배치
		Panel northpanel = new Panel();
		northpanel.setLayout(new FlowLayout());
		lr_writelist = new List(20);
	
		northpanel.add(lr_writelist);	// 리스트 패널
		lr_writelist.addItemListener(null);
		

		Panel centerpanel = new Panel();
		centerpanel.setLayout(new FlowLayout());
		lr_btwrite = new Button("게시물 작성");
		lr_btwrite.addActionListener(this);
		centerpanel.add(lr_btwrite);
		
		lr_btLook = new Button("게시물 보기");
		lr_btLook.addActionListener(this);
		centerpanel.add(lr_btLook);	

		
		Panel southpanel = new Panel();
		southpanel.setLayout(new FlowLayout());
		lr_tfInput = new TextField(20);
		southpanel.add(lr_tfInput);
		lr_btfind = new Button("검색");
		southpanel.add(lr_btfind);


		add("North", northpanel);
		add("Center", centerpanel);
		add("South", southpanel);

		dr_thread = client; // ClientThread 클래스와 연결한다.

		// 입력 텍스트 필드에 포커스를 맞추는 메소드 추가

		addWindowListener(new WinListener());

	}

	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			dr_thread.requestQuitBoard();
		}
	}

	// 화면지우기, 로그아웃 이벤트를 처리한다.
	public void actionPerformed(ActionEvent ae) {
		Button b = (Button) ae.getSource();
		if (b.getLabel().equals("게시물 작성")) {
			// 글 작성 처리 루틴
			dr_thread.requestWriteBoard();
		} else if (b.getLabel().equals("게시물 보기")) {
			// 보기 처리 루틴
			dr_thread.requestReadBoard();
			String item = lr_writelist.getSelectedItem();
			if(item != null) {
				
			}
			
		} else if (b.getLabel().equals("검색")) {
			// 검색 처리 루틴
		}
	}




}