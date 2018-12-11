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
	private NoteMessageRoom NoteMRoom;
	private DisplayRoom room;
	private NoteMessageRoom Noteroom;

	private static final String SEPARATOR = "|";
	private static final String DELIMETER = "`";

	// 메시지 패킷 코드 및 데이터 정의

	// 서버에 전송하는 메시지 코드
	private static final int REQ_LOGON = 1001;
	private static final int REQ_ENTERROOM = 1011;
	private static final int REQ_SENDWORDS = 1021;
	private static final int REQ_WISPERSEND = 1022;
	private static final int REQ_LOGOUT = 1031;
	private static final int REQ_QUITROOM = 1041;
	
	private static final int REQ_SENDNOTE = 1090;

	// 서버로부터 전송되는 메시지 코드
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
	private static final int C_LOGOUT = 2033;
	private static final int YES_QUITROOM = 2041;
	private static final int C_QUITROOM = 2042;
	
	private static final int YES_SENDNOTE = 2090;
	private static final int NO_SENDNOTE = 2091;

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

	public void run() {

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
					ct_client.cc_tfLogon.setEditable(false);
					ct_client.cc_tfStatus.setText("로그온이 성공했습니다.");
					String date = st.nextToken(); // 대화방 개설시간
					ct_client.cc_tfDate.setText(date);
					String ids = st.nextToken(); // 대화방 참여자 리스트
					StringTokenizer users = new StringTokenizer(ids, DELIMETER);
					ct_client.cc_lstMember.removeAll();
					while (users.hasMoreTokens()) {
						ct_client.cc_lstMember.add(users.nextToken());
					}
					break;
				}

				// 로그온 실패 또는 로그온하고 대화방이 개설되지 않은 상태
				// PACKET : NO_LOGON|errCode
				case NO_LOGON: {
					int errcode = Integer.parseInt(st.nextToken());
					if (errcode == MSG_ALREADYUSER) {
						logonbox.dispose();
						msgBox = new MessageBox(ct_client, "로그온", "이미 다른 사용자가 있습니다.");
						msgBox.show();
					} else if (errcode == MSG_SERVERFULL) {
						logonbox.dispose();
						msgBox = new MessageBox(ct_client, "로그온", "대화방이 만원입니다.");
						msgBox.show();
					}
					break;
				}

				// 대화방 개설 및 입장 성공 메시지 PACKET : YES_ENTERROOM
				case YES_ENTERROOM: {
					ct_client.dispose(); // 로그온 창을 지운다.
					NoteMRoom = new NoteMessageRoom(this, "쪽지함");
					NoteMRoom.pack();
					NoteMRoom.show(); // 대화방 창을 출력한다.
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
					NoteMRoom.dr_lstMember.clear(); // 모든 ID를 삭제한다.
					String ids = st.nextToken(); // 대화방 참여자 리스트
					StringTokenizer roomusers = new StringTokenizer(ids, DELIMETER);
					NoteMRoom.dr_lstMember.removeAll();
					while (roomusers.hasMoreTokens()) {
						NoteMRoom.dr_lstMember.add(roomusers.nextToken());
					}
					break;
				}

				// 수신 메시지 출력 PACKET : YES_SENDWORDS|ID|대화말
				case YES_SENDWORDS: {
					String id = st.nextToken(); // 대화말 전송자의 ID를 구한다.
					try {
						String data = st.nextToken();
						NoteMRoom.dr_taContents.append(id + " : " + data + "\n");
					} catch (NoSuchElementException e) {
					}
					NoteMRoom.dr_tfInput.setText(""); // 대화말 입력 필드를 지운다.
					break;
				}
				case YES_WISPERWORDS: {
					String id = st.nextToken();
					try {
						String data = st.nextToken();
						String rid = st.nextToken();
						NoteMRoom.dr_taContents.append(id + "->" + rid + " : " + data + "\n");
					} catch (NoSuchElementException e) {
					}
					NoteMRoom.dr_tfInput.setText("");
					break;
				}
				
				case YES_SENDNOTE: {
					String id = st.nextToken();
					try {
						String data = st.nextToken();
						String uid = st.nextToken();
						String title = st.nextToken();
						System.out.println("아이디"+id+"\n데이터 "+data+"\nuid "+uid+"\ntitle "+title);
						
						//StringTokenizer user = new StringTokenizer(uid, DELIMETER);
						//ct_client.cc_lstMember.removeAll();
						//while (user.hasMoreTokens()) {
							this.Noteroom.nr_lstSender.add(uid);
						//}
						
						StringTokenizer usertitle = new StringTokenizer(title, DELIMETER);
						while (usertitle.hasMoreTokens()) {
							Noteroom.nr_lstTitle.add(usertitle.nextToken());
						}
						NoteMRoom.Data = data;//임시데이터..수정요함
					} catch (NoSuchElementException e) {
					}
					NoteMRoom.dr_tfInput.setText("");
					break;
				}
				
				case NO_WISPERWORDS: {
					NoteMRoom.dr_taContents.append("자신에게는 귓속말 할수 없습니다.\n");
					NoteMRoom.dr_tfInput.setText("");
					break;
				}
				// LOGOUT 메시지 처리
				// PACKET : YES_LOGOUT|탈퇴자id|탈퇴자 제외 id1, id2,....
				case YES_LOGOUT: {
					String outid = st.nextToken();
					ct_client.cc_tfStatus.setText(outid + "님이 로그아웃 되셨습니다.");
					String ids = st.nextToken();
					if (ids.compareTo(" ") == 0)
						ct_client.cc_lstMember.removeAll();
					else {
						StringTokenizer users = new StringTokenizer(ids, DELIMETER);
						ct_client.cc_lstMember.removeAll();
						while (users.hasMoreTokens()) {
							ct_client.cc_lstMember.add(users.nextToken());
						}
					}
					break;
				}
				case C_LOGOUT: {
					ct_client.cc_tfStatus.setText("로그아웃 되셨습니다.");
					ct_client.cc_tfDate.setText("로그아웃상태");
					ct_client.cc_tfLogon.setEditable(true);
					ct_client.cc_lstMember.removeAll();
					break;
				}
				// 퇴실 메시지(YES_QUITROOM) 처리 PACKET : YES_QUITROOM|ids
				case YES_QUITROOM: {
					String ids = st.nextToken(); // 대화방 참여자 리스트
					if (ids.compareTo(" ") == 0) {
						NoteMRoom.dr_lstMember.removeAll();
					} else {
						StringTokenizer roomusers = new StringTokenizer(ids, DELIMETER);
						NoteMRoom.dr_lstMember.removeAll();
						while (roomusers.hasMoreTokens()) {
							NoteMRoom.dr_lstMember.add(roomusers.nextToken());
						}

					}
					break;
				}
				case C_QUITROOM: {
					NoteMRoom.dispose();
					ct_client.show();
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
	public void requestLogon(String id) {
		try {
			logonbox = new MessageBox(ct_client, "로그온", "서버에 로그온 중입니다.");
			logonbox.show();
			ct_buffer.setLength(0); // Logon 패킷을 생성한다.
			ct_buffer.append(REQ_LOGON);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(id);
			send(ct_buffer.toString()); // Logon 패킷을 전송한다.
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

	// EnterRoom 패킷(REQ_ENTERROOM|ID)을 생성하고 전송한다.
	public void requestEnterRoom(String id) {
		try {
			ct_buffer.setLength(0); // EnterRoom 패킷을 생성한다.
			ct_buffer.append(REQ_ENTERROOM);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(id);
			send(ct_buffer.toString()); // EnterRoom 패킷을 전송한다.
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void requestQuitRoom() {
		try {
			ct_buffer.setLength(0); // EnterRoom 패킷을 생성한다.
			ct_buffer.append(REQ_QUITROOM);
			send(ct_buffer.toString()); // EnterRoom 패킷을 전송한다.
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	// SendWords 패킷(REQ_SENDWORDS|ID|대화말)을 생성하고 전송한다.
	public void requestSendWords(String words) {
		try {
			ct_buffer.setLength(0); // SendWords 패킷을 생성한다.
			ct_buffer.append(REQ_SENDWORDS);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(ct_client.msg_logon);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(words);
			send(ct_buffer.toString()); // SendWords 패킷을 전송한다.
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void requestWisperSend(String words, String ID) {
		try {
			ct_buffer.setLength(0); // SendWords 패킷을 생성한다.
			ct_buffer.append(REQ_WISPERSEND);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(ct_client.msg_logon);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(words);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(ID);
			send(ct_buffer.toString()); // SendWords 패킷을 전송한다.
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	public void requestSendNote(String words, String ID, String title) {
		try {
			ct_buffer.setLength(0);
			ct_buffer.append(REQ_SENDNOTE);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(ct_client.msg_logon);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(words);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(ID);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(title);
			send(ct_buffer.toString());
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	// 클라이언트에서 메시지를 전송한다.
	private void send(String sendData) throws IOException {
		ct_out.writeUTF(sendData);
		ct_out.flush();
	}
}