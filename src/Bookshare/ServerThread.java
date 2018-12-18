package Bookshare;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.*;

public class ServerThread extends Thread {
	private Socket st_sock;
	private DataInputStream st_in;
	private DataOutputStream st_out;
	private StringBuffer st_buffer;
	private DBconnect DB;
	/* 로그온된 사용자 저장 */
	private static Hashtable<String, ServerThread> logonHash;
	private static Vector<String> logonVector;
	/* 대화방 참여자 저장 */
	private static Hashtable<String, ServerThread> roomHash;
	private static Vector<String> roomVector;

	private static int isOpenRoom = 0; // 대화방이 개설안됨(초기값)

	private static final String SEPARATOR = "|"; // 메시지간 구분자
	private static final String DELIMETER = "`"; // 소메시지간 구분자

	public String st_ID; // ID 저장

	// 메시지 패킷 코드 및 데이터 정의
	// 클라이언트로부터 전달되는 메시지 코드
	private static final int REQ_LOGON = 1001;
	private static final int REQ_SIGNUP = 1002;
	private static final int REQ_ENTERROOM = 1011;
	private static final int REQ_ENTERBOARD = 1012;
	private static final int REQ_ENTERMSG = 1013;
	private static final int REQ_WRITEBOARD = 1014;
	private static final int REQ_READBOARD = 1015;
	private static final int REQ_REMOVEBOARD =1016;
	private static final int REQ_SENDWORDS = 1021;
	private static final int REQ_WISPERSEND = 1022;
	private static final int REQ_LOGOUT = 1031;
	private static final int REQ_QUITROOM = 1041;

	// 클라이언트에 전송하는 메시지 코드
	private static final int YES_LOGON = 2001;
	private static final int NO_LOGON = 2002;
	private static final int YES_SIGNUP = 2003;
	private static final int NO_SIGNUP = 2004;
	private static final int YES_ENTERROOM = 2011;
	private static final int NO_ENTERROOM = 2012;
	private static final int MDY_USERIDS = 2013;
	private static final int YES_ENTERMSG = 2021;
	private static final int YES_WISPERWORDS = 2023;
	private static final int NO_WISPERWORDS = 2024;
	private static final int YES_LOGOUT = 2031;
	private static final int NO_LOGOUT = 2032;
	private static final int YES_QUITROOM = 2041;
	private static final int C_LOGOUT = 2033;
	private static final int C_QUITROOM = 2042;
	private static final int YES_ENTERBOARD = 2050;
	private static final int YES_WRITEBOARD = 2060;
	private static final int NO_WRITEBOARD = 2061;
	private static final int YES_READBOARD = 2062;
	private static final int YES_REMOVEBOARD =2063;
	private static final int NO_REMOVEBOARD =2064;

	// 에러 메시지 코드
	private static final int MSG_ALREADYUSER = 3001;
	private static final int MSG_SERVERFULL = 3002;
	private static final int MSG_CANNOTOPEN = 3011;

	static {
		logonHash = new Hashtable<String, ServerThread>(ChatServer.cs_maxclient);
		logonVector = new Vector<String>(ChatServer.cs_maxclient);
		roomHash = new Hashtable<String, ServerThread>(ChatServer.cs_maxclient);
		roomVector = new Vector<String>(ChatServer.cs_maxclient);
	}

	public ServerThread(Socket sock) {
		try {
			st_sock = sock;
			st_in = new DataInputStream(sock.getInputStream());
			st_out = new DataOutputStream(sock.getOutputStream());
			st_buffer = new StringBuffer(2048);
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void run() {
		try {
			DB = new DBconnect();
			while (true) {
				String recvData = st_in.readUTF();
				StringTokenizer st = new StringTokenizer(recvData, SEPARATOR);
				int command = Integer.parseInt(st.nextToken());
				switch (command) {

				// 로그온 시도 메시지 PACKET : REQ_LOGON|ID
				case REQ_LOGON: {
					int result, check;
					String id = st.nextToken(); // 클라이언트의 ID를 얻는다.
					result = addUser(id, this);

					String pw = st.nextToken();

					st_buffer.setLength(0);
					/*
					 * 디비처리부분
					 */
					check = DB.logincheck(id, pw);
					System.out.println(check);
					if (result == 0 && check == 0) { // 접속을 허용한 상태

						st_buffer.append(YES_LOGON);
						st_buffer.append(SEPARATOR);
						st_buffer.append(id);
						// YES_LOGON|개설시각|ID1`ID2`..
						send(st_buffer.toString());
						System.out.println(id + " " + pw);
					} else { // 접속불가 상태
						st_buffer.append(NO_LOGON); // NO_LOGON|errCode
						st_buffer.append(SEPARATOR);
						st_buffer.append(result); // 접속불가 원인코드 전송
						send(st_buffer.toString());
						System.out.println("false");
					}
					break;
				}
				case REQ_SIGNUP: {
					int check;
					String id = st.nextToken();
					String pw = st.nextToken();
					String pn = st.nextToken();
					/*
					 * 디비 Insert 부분
					 */
					check = DB.checksignup(id, pn);
					if (check == 0) {
						st_buffer.setLength(0);
						st_buffer.append(YES_SIGNUP);
						send(st_buffer.toString());
						DB.createid(id, pw, pn);
					} else {
						st_buffer.setLength(0);
						st_buffer.append(NO_SIGNUP);
						st_buffer.append(SEPARATOR);
						st_buffer.append(String.valueOf(check));
						send(st_buffer.toString());
					}
					break;
				}
				// 대화방 개설 시도 메시지 PACKET : REQ_ENTERROOM|ID
				case REQ_ENTERROOM: {
					st_buffer.setLength(0);
					String id = st.nextToken(); // 클라이언트의 ID를 얻는다.
					if (checkUserID(id) == null) {

						// NO_ENTERROOM PACKET : NO_ENTERROOM|errCode
						st_buffer.append(NO_ENTERROOM);
						st_buffer.append(SEPARATOR);
						st_buffer.append(MSG_CANNOTOPEN);
						send(st_buffer.toString()); // NO_ENTERROOM 패킷을 전송한다.
						break;
					}

					roomVector.addElement(id); // 사용자 ID 추가
					roomHash.put(id, this); // 사용자 ID 및 클라이언트와 통신할 스레드 저장

					if (isOpenRoom == 0) { // 대화방 개설시간 설정
						isOpenRoom = 1;
					}

					// YES_ENTERROOM PACKET : YES_ENTERROOM
					st_buffer.append(YES_ENTERROOM);
					send(st_buffer.toString()); // YES_ENTERROOM 패킷을 전송한다.
					break;
				}

				// 대화말 전송 시도 메시지 PACKET : REQ_SENDWORDS|ID|대화말
				case REQ_ENTERMSG: {
					st_buffer.setLength(0);
					st_buffer.append(YES_ENTERMSG);
					send(st_buffer.toString()); // YES_SENDWORDS 패킷 전송
					break;
				}
				// LOGOUT 전송 시도 메시지
				// PACKET : YES_LOGOUT|탈퇴자ID|탈퇴자 이외의 ids
				case REQ_LOGOUT: {
					st_buffer.setLength(0);
					st_buffer.append(YES_LOGOUT);
					logonVector.remove(st_ID);
					logonHash.remove(st_ID);
					send(st_buffer.toString());
					break;
				}

				// 방 입장전의 LOGOUT 전송 시도 메시지 PACKET : YES_QUITROOM
				case REQ_QUITROOM: {
					roomVector.removeElement(st_ID);
					roomHash.remove(st_ID);
					st_buffer.setLength(0);
					st_buffer.append(C_QUITROOM);
					send(st_buffer.toString());
					break;
				}
				// 게시판 입장 전송 메세지 PACKET : YES_ENTERBOARD|게시판내용
				case REQ_ENTERBOARD: {
					String[] buffer = new String[1000];
					/*
					 * 디비접속 후 게시판 내용 읽어서 보내주기
					 */
					buffer = DB.getboard();
					for (int i = 0; i < buffer.length; i++) {
						if (buffer[i].equals("")) {
							System.out.println("hello");
							break;
						} else {
							StringTokenizer stb = new StringTokenizer(buffer[i], SEPARATOR);
							String board_No = stb.nextToken();
							String title = stb.nextToken();
							String text = stb.nextToken();
							String ID = stb.nextToken();
							String date = stb.nextToken();
							st_buffer.setLength(0);
							st_buffer.append(YES_ENTERBOARD);
							st_buffer.append(SEPARATOR);
							st_buffer.append(board_No);
							st_buffer.append(SEPARATOR);
							st_buffer.append(title);
							st_buffer.append(SEPARATOR);
							st_buffer.append(text);
							st_buffer.append(SEPARATOR);
							st_buffer.append(ID);
							st_buffer.append(SEPARATOR);
							st_buffer.append(date);
							System.out.println(st_buffer.toString());
							send(st_buffer.toString());
						}
					}
					break;
				}
				case REQ_WRITEBOARD: {
					st_buffer.setLength(0);
					String title = st.nextToken();
					String text = st.nextToken();
					String id = st.nextToken();
					String pw = st.nextToken();
					int result;
					/*
					 * 디비접속 후 게시판 내용 읽어서 보내주기
					 */
					result = DB.setboard(title, text, id, pw);
					if (result == 0) {
						st_buffer.append(YES_WRITEBOARD);
						send(st_buffer.toString());
					} else {
						st_buffer.append(NO_WRITEBOARD);
						st_buffer.append(SEPARATOR);
						new String();
						st_buffer.append(String.valueOf(result));
						send(st_buffer.toString());
					}
					break;
				}
				case REQ_READBOARD: {
					String no = st.nextToken();
					String buffer = new String();

					st_buffer.setLength(0);
					/*
					 * 디비접속 후 게시판 내용 읽어서 보내주기
					 */
					new String();
					buffer = DB.selectboard(Integer.parseInt(no));
					StringTokenizer stb = new StringTokenizer(buffer, SEPARATOR);
					String board_No = stb.nextToken();
					String title = stb.nextToken();
					String text = stb.nextToken();
					String ID = stb.nextToken();
					String date = stb.nextToken();
					st_buffer.setLength(0);
					st_buffer.append(YES_READBOARD);
					st_buffer.append(SEPARATOR);
					st_buffer.append(board_No);
					st_buffer.append(SEPARATOR);
					st_buffer.append(title);
					st_buffer.append(SEPARATOR);
					st_buffer.append(text);
					st_buffer.append(SEPARATOR);
					st_buffer.append(ID);
					st_buffer.append(SEPARATOR);
					st_buffer.append(date);
					send(st_buffer.toString());
					break;
				}
				case REQ_REMOVEBOARD:{
					String pw = st.nextToken();
					String b_no = st.nextToken();
					int result;
					result = DB.removeboard(pw, b_no);
					st_buffer.setLength(0);
					if(result==0) 
						st_buffer.append(YES_REMOVEBOARD);
					else
						st_buffer.append(NO_REMOVEBOARD);
					send(st_buffer.toString());
					break;
				}
				} // switch 종료

				Thread.sleep(100);
			} // while 종료

		} catch (

		NullPointerException e) { // 로그아웃시 st_in이 이 예외를 발생하므로
		} catch (InterruptedException e) {
		} catch (IOException e) {
		}
	}

	// 자원을 해제한다.

	public void release() {
	}

	/*
	 * 해쉬 테이블에 접속을 요청한 클라이언트의 ID 및 전송을 담당하는 스레드를 등록. 즉, 해쉬 테이블은 대화를 하는 클라이언트의 리스트를
	 * 포함.
	 */
	private static synchronized int addUser(String id, ServerThread client) {
		if (checkUserID(id) != null) {
			return MSG_ALREADYUSER;
		}
		if (logonHash.size() >= ChatServer.cs_maxclient) {
			return MSG_SERVERFULL;
		}
		logonVector.addElement(id); // 사용자 ID 추가
		logonHash.put(id, client); // 사용자 ID 및 클라이언트와 통신할 스레드를 저장한다.
		client.st_ID = id;
		return 0; // 클라이언트와 성공적으로 접속하고, 대화방이 이미 개설된 상태.
	}

	/*
	 * 접속을 요청한 사용자의 ID와 일치하는 ID가 이미 사용되는 지를 조사한다. 반환값이 null이라면 요구한 ID로 대화방 입장이 가능함.
	 */
	private static ServerThread checkUserID(String id) {
		ServerThread alreadyClient = null;
		alreadyClient = (ServerThread) logonHash.get(id);
		return alreadyClient;
	}

	// 데이터를 전송한다.
	public void send(String sendData) throws IOException {
		synchronized (st_out) {
			st_out.writeUTF(sendData);
			st_out.flush();
		}
	}
}
