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
	/* �α׿µ� ����� ���� */
	private static Hashtable<String, ServerThread> logonHash;
	private static Vector<String> logonVector;
	/* ��ȭ�� ������ ���� */
	private static Hashtable<String, ServerThread> roomHash;
	private static Vector<String> roomVector;

	private static int isOpenRoom = 0; // ��ȭ���� �����ȵ�(�ʱⰪ)

	private static final String SEPARATOR = "|"; // �޽����� ������
	private static final String DELIMETER = "`"; // �Ҹ޽����� ������

	public String st_ID; // ID ����

	// �޽��� ��Ŷ �ڵ� �� ������ ����
	// Ŭ���̾�Ʈ�κ��� ���޵Ǵ� �޽��� �ڵ�
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

	// Ŭ���̾�Ʈ�� �����ϴ� �޽��� �ڵ�
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

	// ���� �޽��� �ڵ�
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

				// �α׿� �õ� �޽��� PACKET : REQ_LOGON|ID
				case REQ_LOGON: {
					int result, check;
					String id = st.nextToken(); // Ŭ���̾�Ʈ�� ID�� ��´�.
					result = addUser(id, this);

					String pw = st.nextToken();

					st_buffer.setLength(0);
					/*
					 * ���ó���κ�
					 */
					check = DB.logincheck(id, pw);
					System.out.println(check);
					if (result == 0 && check == 0) { // ������ ����� ����

						st_buffer.append(YES_LOGON);
						st_buffer.append(SEPARATOR);
						st_buffer.append(id);
						// YES_LOGON|�����ð�|ID1`ID2`..
						send(st_buffer.toString());
						System.out.println(id + " " + pw);
					} else { // ���ӺҰ� ����
						st_buffer.append(NO_LOGON); // NO_LOGON|errCode
						st_buffer.append(SEPARATOR);
						st_buffer.append(result); // ���ӺҰ� �����ڵ� ����
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
					 * ��� Insert �κ�
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
				// ��ȭ�� ���� �õ� �޽��� PACKET : REQ_ENTERROOM|ID
				case REQ_ENTERROOM: {
					st_buffer.setLength(0);
					String id = st.nextToken(); // Ŭ���̾�Ʈ�� ID�� ��´�.
					if (checkUserID(id) == null) {

						// NO_ENTERROOM PACKET : NO_ENTERROOM|errCode
						st_buffer.append(NO_ENTERROOM);
						st_buffer.append(SEPARATOR);
						st_buffer.append(MSG_CANNOTOPEN);
						send(st_buffer.toString()); // NO_ENTERROOM ��Ŷ�� �����Ѵ�.
						break;
					}

					roomVector.addElement(id); // ����� ID �߰�
					roomHash.put(id, this); // ����� ID �� Ŭ���̾�Ʈ�� ����� ������ ����

					if (isOpenRoom == 0) { // ��ȭ�� �����ð� ����
						isOpenRoom = 1;
					}

					// YES_ENTERROOM PACKET : YES_ENTERROOM
					st_buffer.append(YES_ENTERROOM);
					send(st_buffer.toString()); // YES_ENTERROOM ��Ŷ�� �����Ѵ�.
					break;
				}

				// ��ȭ�� ���� �õ� �޽��� PACKET : REQ_SENDWORDS|ID|��ȭ��
				case REQ_ENTERMSG: {
					st_buffer.setLength(0);
					st_buffer.append(YES_ENTERMSG);
					send(st_buffer.toString()); // YES_SENDWORDS ��Ŷ ����
					break;
				}
				// LOGOUT ���� �õ� �޽���
				// PACKET : YES_LOGOUT|Ż����ID|Ż���� �̿��� ids
				case REQ_LOGOUT: {
					st_buffer.setLength(0);
					st_buffer.append(YES_LOGOUT);
					logonVector.remove(st_ID);
					logonHash.remove(st_ID);
					send(st_buffer.toString());
					break;
				}

				// �� �������� LOGOUT ���� �õ� �޽��� PACKET : YES_QUITROOM
				case REQ_QUITROOM: {
					roomVector.removeElement(st_ID);
					roomHash.remove(st_ID);
					st_buffer.setLength(0);
					st_buffer.append(C_QUITROOM);
					send(st_buffer.toString());
					break;
				}
				// �Խ��� ���� ���� �޼��� PACKET : YES_ENTERBOARD|�Խ��ǳ���
				case REQ_ENTERBOARD: {
					String[] buffer = new String[1000];
					/*
					 * ������� �� �Խ��� ���� �о �����ֱ�
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
					 * ������� �� �Խ��� ���� �о �����ֱ�
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
					 * ������� �� �Խ��� ���� �о �����ֱ�
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
				} // switch ����

				Thread.sleep(100);
			} // while ����

		} catch (

		NullPointerException e) { // �α׾ƿ��� st_in�� �� ���ܸ� �߻��ϹǷ�
		} catch (InterruptedException e) {
		} catch (IOException e) {
		}
	}

	// �ڿ��� �����Ѵ�.

	public void release() {
	}

	/*
	 * �ؽ� ���̺� ������ ��û�� Ŭ���̾�Ʈ�� ID �� ������ ����ϴ� �����带 ���. ��, �ؽ� ���̺��� ��ȭ�� �ϴ� Ŭ���̾�Ʈ�� ����Ʈ��
	 * ����.
	 */
	private static synchronized int addUser(String id, ServerThread client) {
		if (checkUserID(id) != null) {
			return MSG_ALREADYUSER;
		}
		if (logonHash.size() >= ChatServer.cs_maxclient) {
			return MSG_SERVERFULL;
		}
		logonVector.addElement(id); // ����� ID �߰�
		logonHash.put(id, client); // ����� ID �� Ŭ���̾�Ʈ�� ����� �����带 �����Ѵ�.
		client.st_ID = id;
		return 0; // Ŭ���̾�Ʈ�� ���������� �����ϰ�, ��ȭ���� �̹� ������ ����.
	}

	/*
	 * ������ ��û�� ������� ID�� ��ġ�ϴ� ID�� �̹� ���Ǵ� ���� �����Ѵ�. ��ȯ���� null�̶�� �䱸�� ID�� ��ȭ�� ������ ������.
	 */
	private static ServerThread checkUserID(String id) {
		ServerThread alreadyClient = null;
		alreadyClient = (ServerThread) logonHash.get(id);
		return alreadyClient;
	}

	// �����͸� �����Ѵ�.
	public void send(String sendData) throws IOException {
		synchronized (st_out) {
			st_out.writeUTF(sendData);
			st_out.flush();
		}
	}
}
