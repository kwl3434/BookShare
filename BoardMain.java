package Bookshare;

import java.awt.*;
import java.awt.event.*;


public class BoardMain extends Frame implements ActionListener, MouseListener{

	private Label label = new Label("게시판", Label.CENTER);
	// private Vector vector = new Vector();
	private List list = new List(25);
	private Button write = new Button("게시물 작성");
	private Button look = new Button("게시물 보기");
	private Button close = new Button("닫기");

	public static ClientThread dr_thread;

	public BoardMain(ClientThread client, String title) {
		super(title);
		setLayout(new BorderLayout());
		this.setSize(800, 600);
		
		// 리스트 목록 구성
		Panel northpanel = new Panel();
		northpanel.setLayout(new BorderLayout(5, 5));
		northpanel.add("North", label);	// 게시판 라벨 
		label.setFont(new Font("Sanserif", Font.BOLD, 22));
		add("North", northpanel);	
				
		Panel centerpanel = new Panel();
		centerpanel.setLayout(new FlowLayout(FlowLayout.LEFT, 400, 5));
		this.setBackground(new Color(243,243,243));
		northpanel.add("Center", list);	// 리스트
		add("Center", centerpanel);		

		Panel southpanel = new Panel();
		southpanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		southpanel.add(write);	// 게시물 작성 버튼
		southpanel.add(look);	// 게시물 보기 버튼
		southpanel.add(close);	// 닫기 버튼
		add("South", southpanel);	
			
		this.setResizable(false);
		Dimension dS = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dF = this.getSize();
		this.setLocation((int)(dS.getWidth() / 2 - dF.getWidth() / 2), 
			(int)(dS.getHeight() / 2 - dF.getHeight() / 2));

				
		close.addActionListener(this);	// 리스트목록  닫기 버튼
		list.addMouseListener(this);	// 리스트목록 마우스 클릭 시
		look.addActionListener(this);	// 게시물 보기 버튼

				
		// this.pack();
		// this.setResizable(false);
		Dimension di = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension di1 = this.getSize();
		this.setLocation((int)(di.getWidth() / 2 - di1.getWidth() / 2), 
			(int)(di.getHeight() / 2 - di1.getHeight() / 2));
		this.setVisible(true);

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
		
		} else if (b.getLabel().equals("닫기")) {
			// 검색 처리 루틴
			dr_thread.requestQuitBoard();
		}
	}
	public void mouseClicked(MouseEvent e){
		if(e.getSource() == list){
			if(e.getClickCount() == 2){	//더블 클릭 시
				dr_thread.requestReadBoard();
			}
		}
	}
	
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
	

}