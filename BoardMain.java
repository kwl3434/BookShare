package ex;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;


public class BoardMain extends Frame implements ActionListener{	
		
	private PostView view;
	
	// 게시물 리스트
	private Label label = new Label("게시판", Label.CENTER);
	private Vector vector = new Vector();
	private List list = new List(25);
	private Button write = new Button("게시물 작성");
	private Button look = new Button("게시물 보기");
	private Button close = new Button("닫기");
	

	// public static ClientThread lr_thread;

	public BoardMain(String title) {
		super(title);
		setLayout(new BorderLayout());
		
		// 리스트 목록 구성
		Panel northpanel = new Panel();
		northpanel.setLayout(new BorderLayout(5, 5));
		northpanel.add("North", label);	// 리스트목록 라벨 
		add("North", northpanel);	
		
		Panel centerpanel = new Panel();
		centerpanel.setLayout(new FlowLayout(FlowLayout.LEFT, 400, 5));
		northpanel.add("Center", list);	// 리스트
		add("Center", centerpanel);		

		Panel southpanel = new Panel();
		southpanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		southpanel.add(write);	// 게시물 작성 버튼
		southpanel.add(look);	// 게시물 보기 버튼
		southpanel.add(close);	// 닫기 버튼
		add("South", southpanel);	
	
		centerpanel.setSize(400, 400);
		this.setSize(400, 400);
		this.setResizable(false);
		Dimension dS = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dF = this.getSize();
		this.setLocation((int)(dS.getWidth() / 2 - dF.getWidth() / 2), 
			(int)(dS.getHeight() / 2 - dF.getHeight() / 2));

		
		
		close.addActionListener(this);	// 리스트목록  닫기 버튼
		// list.addMouseListener(this);	// 리스트목록 마우스 클릭 시
		look.addActionListener(this);	// 게시물 보기 버튼

		
		this.pack();
		this.setResizable(false);
		Dimension di = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension di1 = this.getSize();
		this.setLocation((int)(di.getWidth() / 2 - di1.getWidth() / 2), 
			(int)(di.getHeight() / 2 - di1.getHeight() / 2));
		this.setVisible(true);
		
		
		// lr_thread = client; // ClientThread 클래스와 연결한다.

		// 입력 텍스트 필드에 포커스를 맞추 는 메소드 추가

		addWindowListener(new WinListener());

	}
	

	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			// lr_thread.requestQuitRoom();
			System.exit(0);
		}
	}

	// 게시물 작성, 게시물 보기, 닫기 이벤트를 처리한다.
	public void actionPerformed(ActionEvent ae) {
		Button b = (Button) ae.getSource();
		if (b.getLabel().equals("게시물 작성")) {
			// 글 작성 처리 루틴
			

		} else if (b.getLabel().equals("게시물 보기")) {
			// 보기 처리 루틴
			this.dispose();
			view = new PostView("게시물 보기");
			view.pack();
			view.show();
		}
			
		else if(b.getLabel().equals("닫기")) {
			// 닫기 처리 루틴
			this.dispose();
			
		}
	}

	public void mouseClicked(MouseEvent e){
		if(e.getSource() == list){
			if(e.getClickCount() == 2){	//더블클릭
				// view();
			}
		}
	
	}
	public static class BoardMain_main{
		public static void main(String[] arg){
			BoardMain es = new BoardMain("게시판");
		}
	}


}		
