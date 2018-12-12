package Bookshare;

import java.io.*;
import java.net.*;
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
	private static Date starttime; // 로그온 시각

	public String st_ID; // ID 저장

	// 메시지 패킷 코드 및 데이터 정의
	private static final int WAITROOM = 100;
	private static final int CHATROOM = 101;
	private static final int MDY_WAITUSERS = 200;
	private static final int MDY_ROOMUSERS = 201;
	// 클라이언트로부터 전달되는 메시지 코드
	private static final int REQ_LOGON = 1001;
	private static final int REQ_ENTERROOM = 1011;
	private static final int REQ_ENTERBOARD =1012;
	private static final int REQ_ENTERMSG =1013;
	private static final int REQ_SENDWORDS = 1021;
	private static final int REQ_WISPERSEND = 1022;
	private static final int REQ_LOGOUT = 1031;
	private static final int REQ_QUITROOM = 1041;
	private static final int REQ_IDSEARCH = 1051;

	// 클라이언트에 전송하는 메시지 코드
	private static final int YES_LOGON = 2001;
	private static final int NO_LOGON = 2002;
	private static final int YES_ENTERROOM = 2011;
	private static final int NO_ENTERROOM = 2012;
	private static final int MDY_USERIDS = 2013;
	private static final int YES_SENDWORDS = 2021;
	private static final int NO_SENDWORDS = 2022;
	private static final int YES_WISPERWORDS = 2023;
	private static final int NO_WISPERWORDS = 2024;
	private static final int YES_LOGOUT = 2031;
	private static final int NO_LOGOUT = 2032;
	private static final int YES_QUITROOM = 2041;
	private static final int C_LOGOUT = 2033;
	private static final int C_QUITROOM = 2042;
	private static final int YES_ENTERBOARD = 2050;
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
			DB = new DBconnect();
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
			while (true) {
				String recvData = st_in.readUTF();
				StringTokenizer st = new StringTokenizer(recvData, SEPARATOR);
				int command = Integer.parseInt(st.nextToken());
				switch (command) {
				
				// 로그온 시도 메시지 PACKET : REQ_LOGON|ID
				case REQ_LOGON: {
					int result;
					String id = st.nextToken(); // 클라이언트의 ID를 얻는다.
					result = addUser(id, this);
					String pw = st.nextToken();
					
					st_buffer.setLength(0);
					/*
					 * 디비처리부분
					 */
					if (result == 0) { // 접속을 허용한 상태
						st_buffer.append(YES_LOGON);
						// YES_LOGON|개설시각|ID1`ID2`..
						send(st_buffer.toString());
						System.out.println(id+" "+pw);
					} else { // 접속불가 상태
						st_buffer.append(NO_LOGON); // NO_LOGON|errCode
						st_buffer.append(SEPARATOR);
						st_buffer.append(result); // 접속불가 원인코드 전송
						send(st_buffer.toString());
						System.out.println("false");
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
						starttime = new Date();
					}

					// YES_ENTERROOM PACKET : YES_ENTERROOM
					st_buffer.append(YES_ENTERROOM);
					send(st_buffer.toString()); // YES_ENTERROOM 패킷을 전송한다.

					// MDY_USERIDS PACKET : MDY_USERIDS|id1'id2' ....
					st_buffer.setLength(0);
					st_buffer.append(MDY_USERIDS);
					st_buffer.append(SEPARATOR);
					String userIDs = getRoomUsers(); // 대화방 참여 사용자 ID를 구한다
					st_buffer.append(userIDs);
					broadcast(st_buffer.toString(), CHATROOM); // MDY_USERIDS 패킷을 전송한다.
					break;
				}

				// 대화말 전송 시도 메시지 PACKET : REQ_SENDWORDS|ID|대화말
				case REQ_SENDWORDS: {
					st_buffer.setLength(0);
					st_buffer.append(YES_SENDWORDS);
					st_buffer.append(SEPARATOR);
					String id = st.nextToken(); // 전송한 사용자의 ID를 구한다.
					st_buffer.append(id);
					st_buffer.append(SEPARATOR);
					try {
						String data = st.nextToken(); // 대화말을 구한다.
						st_buffer.append(data);
					} catch (NoSuchElementException e) {
					}
					broadcast(st_buffer.toString(), CHATROOM); // YES_SENDWORDS 패킷 전송
					break;
				}
				case REQ_WISPERSEND: {
					String id = st.nextToken();
					String data = st.nextToken();
					String rid = st.nextToken();
					if (id.compareTo(rid) == 0) {
						st_buffer.setLength(0);
						st_buffer.append(NO_WISPERWORDS);
						send(st_buffer.toString());
					} else {
						st_buffer.setLength(0);
						st_buffer.append(YES_WISPERWORDS);
						st_buffer.append(SEPARATOR);
						// 전송한 사용자의 ID를 구한다.
						st_buffer.append(id);
						st_buffer.append(SEPARATOR);
						// 대화말을 구한다.

						st_buffer.append(data);
						st_buffer.append(SEPARATOR);
						st_buffer.append(rid);
						multicast(st_buffer.toString(), id, rid);
					}
					break;
				}
				// LOGOUT 전송 시도 메시지
				// PACKET : YES_LOGOUT|탈퇴자ID|탈퇴자 이외의 ids
				case REQ_LOGOUT: {
					String ids = "";
					st_buffer.setLength(0);
					st_buffer.append(YES_LOGOUT);
					st_buffer.append(SEPARATOR);
					st_buffer.append(st_ID);
					logonVector.remove(st_ID);
					logonHash.remove(st_ID);
					st_buffer.append(SEPARATOR);
					ids = getUsers();
					if (ids.compareTo("") == 0)
						ids = " ";
					st_buffer.append(ids);
					broadcast(st_buffer.toString(), WAITROOM);
					st_buffer.setLength(0);
					st_buffer.append(C_LOGOUT);
					send(st_buffer.toString());
					break;
				}

				// 방 입장전의 LOGOUT 전송 시도 메시지 PACKET : YES_QUITROOM
				case REQ_QUITROOM: {
					String ids = "";
					st_buffer.setLength(0);
					st_buffer.append(YES_QUITROOM);
					st_buffer.append(SEPARATOR);
					roomVector.removeElement(st_ID);
					roomHash.remove(st_ID);
					ids = getRoomUsers();
					if (ids.compareTo("") == 0)
						ids = " ";
					st_buffer.append(ids);
					broadcast(st_buffer.toString(), CHATROOM);
					st_buffer.setLength(0);
					st_buffer.append(C_QUITROOM);
					send(st_buffer.toString());
					break;
				}
				//게시판 입장 전송 메세지 PACKET : YES_ENTERBOARD|게시판내용
				case REQ_ENTERBOARD:{ 
					st_buffer.setLength(0);
					st_buffer.append(YES_ENTERBOARD);
					/*
					 * 디비접속 후 게시판 내용 읽어서 보내주기
					 */
					send(st_buffer.toString());
				}
				} // switch 종료

				Thread.sleep(100);
			} // while 종료

		} catch (NullPointerException e) { // 로그아웃시 st_in이 이 예외를 발생하므로
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

	// 로그온에 참여한 사용자 ID를 구한다.
	private String getUsers() {
		StringBuffer id = new StringBuffer();
		String ids;
		Enumeration<String> enu = logonVector.elements();
		while (enu.hasMoreElements()) {
			id.append(enu.nextElement());
			id.append(DELIMETER);
		}
		try {
			ids = new String(id); // 문자열로 변환한다.
			ids = ids.substring(0, ids.length() - 1); // 마지막 "`"를 삭제한다.
		} catch (StringIndexOutOfBoundsException e) {
			return "";
		}
		return ids;
	}

	// 대화방에 참여한 사용자 ID를 구한다.

	private String getRoomUsers() {
		StringBuffer id = new StringBuffer();
		String ids;
		Enumeration<String> enu = roomVector.elements();
		while (enu.hasMoreElements()) {
			id.append(enu.nextElement());
			id.append(DELIMETER);
		}
		try {
			ids = new String(id);
			ids = ids.substring(0, ids.length() - 1); // 마지막 "`"를 삭제한다.
		} catch (StringIndexOutOfBoundsException e) {
			return "";
		}
		return ids;
	}

	private void modifyWaitUsers() throws IOException {
		String ids = getUsers();
		st_buffer.setLength(0);
		st_buffer.append(MDY_WAITUSERS);
		st_buffer.append(SEPARATOR);
	}

	private void modifyRoomUsers() throws IOException {
		String ids = getRoomUsers();
		st_buffer.setLength(0);
		st_buffer.append(MDY_ROOMUSERS);
		st_buffer.append(SEPARATOR);
		st_buffer.append(ids);
		broadcast(st_buffer.toString(), CHATROOM);
	}

	// 대화방에 참여한 모든 사용자(브로드케스팅)에게 데이터를 전송한다.
	public synchronized void broadcast(String sendData, int room) throws IOException {
		ServerThread client;
		Enumeration<String> enu;
		if (room == WAITROOM) {
			enu = logonVector.elements();
			while (enu.hasMoreElements()) {
				client = (ServerThread) logonHash.get(enu.nextElement());
				client.send(sendData);
			}

		} else {
			enu = roomVector.elements();
			while (enu.hasMoreElements()) {
				client = (ServerThread) roomHash.get(enu.nextElement());
				client.send(sendData);
			}
		}
	}

	public synchronized void multicast(String sendData, String id, String rid) throws IOException {
		ServerThread client;
		Enumeration<String> enu;
		enu = roomVector.elements();
		while (enu.hasMoreElements()) {
			String next = enu.nextElement();
			System.out.println(next);
			if (next.compareTo(id) == 0 || next.compareTo(rid) == 0) {
				client = (ServerThread) roomHash.get(next);
				client.send(sendData);
			}
		}
	}

	// 데이터를 전송한다.
	public void send(String sendData) throws IOException {
		synchronized (st_out) {
			st_out.writeUTF(sendData);
			st_out.flush();
		}
	}
}
