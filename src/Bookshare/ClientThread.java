package Bookshare;

import java.io.*;
import java.util.*;
import java.net.*;

public class ClientThread extends Thread {

	private ChatClient ct_client; // ChatClient 객체
	private Socket ct_sock; // 클라이언트 소켓
	private DataInputStream ct_in; // 입력 스트림
	private DataOutputStream ct_out; // 출력 스트림
	private StringBuffer ct_buffer; // 버퍼
	private Thread thisThread;
	private DisplayRoom room;
	private BoardContent bcontent;
	private BoardShow bshow;
	private BoardMain bmain;
	private MessageRoom MR; 
	private String ct_ID;

	private static final String SEPARATOR = "|";
	private static final String DELIMETER = "`";
	// 메시지 패킷 코드 및 데이터 정의

	// 서버에 전송하는 메시지 코드
	private static final int REQ_LOGON = 1001;
	private static final int REQ_SIGNUP = 1002;
	private static final int REQ_ENTERROOM = 1011;
	private static final int REQ_ENTERBOARD =1012;
	private static final int REQ_ENTERMSG =1013;
	private static final int REQ_WRITEBOARD =1014;
	private static final int REQ_READBOARD =1015;
	private static final int REQ_SENDWORDS = 1021;
	private static final int REQ_WISPERSEND = 1022;
	private static final int REQ_LOGOUT = 1031;
	private static final int REQ_QUITROOM = 1041;
	

	// 서버로부터 전송되는 메시지 코드
	private static final int YES_LOGON = 2001;
	private static final int NO_LOGON = 2002;
	private static final int YES_SIGNUP = 2003;
	private static final int NO_SIGNUP = 2004;
	private static final int YES_ENTERROOM = 2011;
	private static final int NO_ENTERROOM = 2012;
	private static final int MDY_USERIDS = 2013;
	private static final int YES_ENTERMSG = 2021;
	private static final int NO_SENDWORDS = 2022;
	private static final int YES_WISPERWORDS = 2023;
	private static final int NO_WISPERWORDS = 2024;
	private static final int YES_LOGOUT = 2031;
	private static final int NO_LOGOUT = 2032;
	private static final int YES_QUITROOM = 2041;
	private static final int C_QUITROOM = 2042;
	private static final int YES_ENTERBOARD = 2050;
	private static final int YES_ENTERWRITEBOARD = 2051;
	private static final int YES_ENTERREADBOARD = 2052;
	private static final int YES_WRITEBOARD = 2060;
	private static final int NO_WRITEBOARD = 2061;
	private static final int YES_READBOARD = 2062;

	// 에러 메시지 코드
	private static final int MSG_ALREADYUSER = 3001;
	private static final int MSG_SERVERFULL = 3002;
	private static final int MSG_CANNOTOPEN = 3011;
	

	private static MessageBox msgBox, logonbox;
	
	/*
	 * 원격호스트와 연결을 위한 생성자 실행 : java ChatClient 호스트이름 포트번호 To DO .....
	 */

	// 로컬호스트에서 사용하기 위하여 만든 생성자
	// 서버와 클라이언트가 같은 시스템을 사용한다.
	public ClientThread(ChatClient client) {
		try {
			ct_sock = new Socket(InetAddress.getLocalHost(), 2777);
			ct_in = new DataInputStream(ct_sock.getInputStream());
			ct_out = new DataOutputStream(ct_sock.getOutputStream());
			ct_buffer = new StringBuffer(4096);
			thisThread = this;
			ct_client = client; // 객체변수에 할당
		} catch (IOException e) {
			MessageBoxLess msgout = new MessageBoxLess(client, "연결에러", "서버에 접속할 수 없습니다.");
			msgout.show();
		}
	}

	@SuppressWarnings("deprecation")
	public void run() {
		room = new DisplayRoom(this, "메인");
		bmain = new BoardMain(this,"게시판 메인");
		room = new DisplayRoom(this, "대화방");
		bshow = new BoardShow(this,"게시판 보기");
		bcontent = new BoardContent(this, "게시판 작성");
		
		try {
			Thread currThread = Thread.currentThread();
			while (currThread == thisThread) { // 종료는 LOG_OFF에서 thisThread=null;에 의하여
				String recvData = ct_in.readUTF();
				StringTokenizer st = new StringTokenizer(recvData, SEPARATOR);
				int command = Integer.parseInt(st.nextToken());
				switch (command) {

				// 로그온 성공 메시지 PACKET : YES_LOGON|개설시각|ID1`ID2`ID3...
				case YES_LOGON: {
					logonbox.dispose();
					ct_client.dispose(); // 로그온 창을 지운다.
					room.show(); // 대화방 창을 출력한다.
					System.out.println("성공");
					ct_ID=st.nextToken();
					break;
				}

				// 로그온 실패 또는 로그온하고 대화방이 개설되지 않은 상태
				// PACKET : NO_LOGON|errCode
				case NO_LOGON: {
					logonbox.dispose();
					msgBox = new MessageBox(ct_client, "로그온", "옳은 ID 혹은 PW를 입력해 주세요.");
					msgBox.show();
					break;
				}
				//  PACKET : YES_SIGNUP
				case YES_SIGNUP:{ 
					MessageBox msgBox = new MessageBox(null, "회원가입 완료", "회원가입이 완료되었습니다.");
					msgBox.show();
					ct_client.SignD.dispose();
					ct_client.pack();
					ct_client.show();
					break;
				}
				//  PACKET : NO_SIGNUP|check(1~3)
				case NO_SIGNUP:{
					String check = st.nextToken();
					if(check.equals("1")) {
						System.out.println(check);
						MessageBox msgBox = new MessageBox(null, "ID에러", "중복된 아이디입니다.");
						msgBox.show();
					}else if(check.equals("2")) {
						System.out.println(check);
						MessageBox msgBox = new MessageBox(null, "휴대폰 번호에러", "중복된 휴대폰번호입니다.");
					    msgBox.show();
					}else {
						MessageBox msgBox = new MessageBox(null, "ID, 휴대폰 번호에러", "중복된 ID,휴대폰번호입니다.");
						msgBox.show();
						System.out.println(check);
					}
					break;
				}
				// 대화방 개설 및 입장 성공 메시지 PACKET : YES_ENTERROOM
				case YES_ENTERROOM: {
					ct_client.dispose(); // 로그온 창을 지운다.
					room.pack();
					room.show(); // 대화방 창을 출력한다.
					bcontent.pack();
					bcontent.show();
					bshow.pack();
					bshow.show();
					break;
				}

				// 대화방 개설 및 입장 실패 메시지 PACKET : NO_ENTERROOM|errCode
				case NO_ENTERROOM: {
					int roomerrcode = Integer.parseInt(st.nextToken());
					if (roomerrcode == MSG_CANNOTOPEN) {
						msgBox = new MessageBox(ct_client, "대화방입장", "로그온된 사용자가 아닙니다.");
						msgBox.show();
					}
					break;
				}

				// 대화방에 참여한 사용자 리스트를 업그레이드 한다.
				// PACKET : MDY_USERIDS|id1'id2'id3.....
				case MDY_USERIDS: {
					/*room.dr_lstMember.clear(); // 모든 ID를 삭제한다.
					String ids = st.nextToken(); // 대화방 참여자 리스트
					StringTokenizer roomusers = new StringTokenizer(ids, DELIMETER);
					room.dr_lstMember.removeAll();
					while (roomusers.hasMoreTokens()) {
						room.dr_lstMember.add(roomusers.nextToken());
					}*/
					break;
				}
				case YES_WRITEBOARD:{
					MessageBox msgBox = new MessageBox(null, "게시물", "게시물이 작성되었습니다.");
					msgBox.show();
					//
					break;
				}
				// 수신 메시지 출력 PACKET : YES_SENDWORDS|ID|대화말
				case YES_ENTERMSG: {
					room.dispose();
					MR = new MessageRoom(this,"쪽지창");
					MR.pack();
					MR.show();
					break;
				}
				case YES_WISPERWORDS: {
					/*String id = st.nextToken();
					try {
						String data = st.nextToken();
						String rid = st.nextToken();
						room.dr_taContents.append(id + "->" + rid + " : " + data + "\n");
					} catch (NoSuchElementException e) {
					}
					room.dr_tfInput.setText("");
					*/
					break;
				}
				case NO_WISPERWORDS: {
					/*room.dr_taContents.append("자신에게는 귓속말 할수 없습니다.\n");
					room.dr_tfInput.setText("");
					*/
					break;
				}
				// LOGOUT 메시지 처리
				// PACKET : YES_LOGOUT|탈퇴자id|탈퇴자 제외 id1, id2,....
				case YES_LOGOUT: {
					room.dispose();
					ct_client.show();
					ct_client.cc_tfID.setText("");
					ct_client.cc_tfPW.setText("");
					break;
				}
				// 퇴실 메시지(YES_QUITROOM) 처리 PACKET : YES_QUITROOM|ids
				case YES_QUITROOM: {
					/*Sring ids = st.nextToken(); // 대화방 참여자 리스트
					if (ids.compareTo(" ") == 0) {
						room.dr_lstMember.removeAll();
					} else {
						StringTokenizer roomusers = new StringTokenizer(ids, DELIMETER);
						room.dr_lstMember.removeAll();
						while (roomusers.hasMoreTokens()) {
							room.dr_lstMember.add(roomusers.nextToken());
						}

					}
					*/
					break;
				}
				case C_QUITROOM: {
					room.dispose();
					ct_client.show();
					break;
				}
				case YES_ENTERBOARD:{
					String board_No = st.nextToken();
					String title = st.nextToken();
					st.nextToken();
					String ID = st.nextToken();
					String date = st.nextToken();
					bmain.pack();
					bmain.show();
					room.dispose();
					bmain.lr_writelist.add(board_No+"| 제목 : "+title+"  "+"  작성자 : "+ID+"  작성일 : "+date);
					break;
				}
				case YES_ENTERREADBOARD:{
					String board_No = st.nextToken();
					String title = st.nextToken();
					String text = st.nextToken();
					String ID = st.nextToken();
					String date = st.nextToken();
					System.out.println(board_No+title+text+ID+date);
					bshow.setTextArea(title, text, date);
					bmain.dispose();
					bshow.pack();
					bshow.show();
					break;
				}
				
				} // switch 종료

				Thread.sleep(200);

			} // while 종료(스레드 종료)

		} catch (InterruptedException e) {
			System.out.println(e);
			release();

		} catch (IOException e) {
			System.out.println(e);
			release();
		}
	}

	// 네트워크 자원을 해제한다.
	public void release() {
	};

	// Logon 패킷(REQ_LOGON|ID)을 생성하고 전송한다.
	public void requestLogon(String id,String pw) {
		try {
			logonbox = new MessageBox(ct_client, "로그온", "서버에 로그온 중입니다.");
			logonbox.show();
			ct_buffer.setLength(0); // Logon 패킷을 생성한다.
			ct_buffer.append(REQ_LOGON);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(id);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(pw);
			send(ct_buffer.toString()); // Logon 패킷을 전송한다.
			System.out.println(id+" "+pw);
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void requestLogout() {
		try {
			ct_buffer.setLength(0); // EnterRoom 패킷을 생성한다.
			ct_buffer.append(REQ_LOGOUT);
			ct_buffer.append(SEPARATOR);
			send(ct_buffer.toString()); // EnterRoom 패킷을 전송한다.
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void requestEnterMessage() {
		try {
			ct_buffer.setLength(0); 
			ct_buffer.append(REQ_ENTERMSG);
			send(ct_buffer.toString()); 
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void requestEnterBoard() {
		try {
			ct_buffer.setLength(0); // enterboard 패킷을 생성한다.
			ct_buffer.append(REQ_ENTERBOARD);
			send(ct_buffer.toString()); // enterboard 패킷을 전송한다.
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	public void requestWriteBoard(String title, String text, String id, String password) {
		try {
			ct_buffer.setLength(0); // enterWriteboard 패킷을 생성한다.
			ct_buffer.append(REQ_WRITEBOARD);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(title);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(text);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(id);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(password);
			send(ct_buffer.toString()); // enterWriteboard 패킷을 전송한다.
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	public void requestReadBoard(String no) {
		try {
			ct_buffer.setLength(0); // SendWords 패킷을 생성한다.
			ct_buffer.append(REQ_READBOARD);
			ct_buffer.append(SEPARATOR);
			new String();
			ct_buffer.append(no);
			send(ct_buffer.toString()); // SendWords 패킷을 전송한다.
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	public void requestSignup(String id, String pw, String pn) {
		try {
			ct_buffer.setLength(0); // SendWords 패킷을 생성한다.
			ct_buffer.append(REQ_SIGNUP);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(id);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(pw);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(pn);
			send(ct_buffer.toString()); // SendWords 패킷을 전송한다.
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	public void requestEnterWriteBoard() {
		bmain.dispose();
		bcontent.pack();
		bcontent.show();
	}
	public void requestQuitRBoard() {
		bshow.dispose();
		bmain.pack();
		bmain.show();
	}
	public void requestQuitWBoard() {
		bcontent.dispose();
		bmain.pack();
		bmain.show();
	}
	public void requestQuitBoard() {
		bmain.dispose();
		room.pack();
		room.show();
	}
	public void requestQuitMSG() {
		MR.dispose();
		room.pack();
		room.show();
	}
	public String getID() {
		return ct_ID;
	}
	// 클라이언트에서 메시지를 전송한다.
	private void send(String sendData) throws IOException {
		ct_out.writeUTF(sendData);
		ct_out.flush();
	}
}