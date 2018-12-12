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
	/* �α׿µ� ����� ���� */
	private static Hashtable<String, ServerThread> logonHash;
	private static Vector<String> logonVector;
	/* ��ȭ�� ������ ���� */
	private static Hashtable<String, ServerThread> roomHash;
	private static Vector<String> roomVector;

	private static int isOpenRoom = 0; // ��ȭ���� �����ȵ�(�ʱⰪ)

	private static final String SEPARATOR = "|"; // �޽����� ������
	private static final String DELIMETER = "`"; // �Ҹ޽����� ������
	private static Date starttime; // �α׿� �ð�

	public String st_ID; // ID ����

	// �޽��� ��Ŷ �ڵ� �� ������ ����
	private static final int WAITROOM = 100;
	private static final int CHATROOM = 101;
	private static final int MDY_WAITUSERS = 200;
	private static final int MDY_ROOMUSERS = 201;
	// Ŭ���̾�Ʈ�κ��� ���޵Ǵ� �޽��� �ڵ�
	private static final int REQ_LOGON = 1001;
	private static final int REQ_ENTERROOM = 1011;
	private static final int REQ_ENTERBOARD =1012;
	private static final int REQ_ENTERMSG =1013;
	private static final int REQ_SENDWORDS = 1021;
	private static final int REQ_WISPERSEND = 1022;
	private static final int REQ_LOGOUT = 1031;
	private static final int REQ_QUITROOM = 1041;
	private static final int REQ_IDSEARCH = 1051;

	// Ŭ���̾�Ʈ�� �����ϴ� �޽��� �ڵ�
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
				
				// �α׿� �õ� �޽��� PACKET : REQ_LOGON|ID
				case REQ_LOGON: {
					int result;
					String id = st.nextToken(); // Ŭ���̾�Ʈ�� ID�� ��´�.
					result = addUser(id, this);
					String pw = st.nextToken();
					
					st_buffer.setLength(0);
					/*
					 * ���ó���κ�
					 */
					if (result == 0) { // ������ ����� ����
						st_buffer.append(YES_LOGON);
						// YES_LOGON|�����ð�|ID1`ID2`..
						send(st_buffer.toString());
						System.out.println(id+" "+pw);
					} else { // ���ӺҰ� ����
						st_buffer.append(NO_LOGON); // NO_LOGON|errCode
						st_buffer.append(SEPARATOR);
						st_buffer.append(result); // ���ӺҰ� �����ڵ� ����
						send(st_buffer.toString());
						System.out.println("false");
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
						starttime = new Date();
					}

					// YES_ENTERROOM PACKET : YES_ENTERROOM
					st_buffer.append(YES_ENTERROOM);
					send(st_buffer.toString()); // YES_ENTERROOM ��Ŷ�� �����Ѵ�.

					// MDY_USERIDS PACKET : MDY_USERIDS|id1'id2' ....
					st_buffer.setLength(0);
					st_buffer.append(MDY_USERIDS);
					st_buffer.append(SEPARATOR);
					String userIDs = getRoomUsers(); // ��ȭ�� ���� ����� ID�� ���Ѵ�
					st_buffer.append(userIDs);
					broadcast(st_buffer.toString(), CHATROOM); // MDY_USERIDS ��Ŷ�� �����Ѵ�.
					break;
				}

				// ��ȭ�� ���� �õ� �޽��� PACKET : REQ_SENDWORDS|ID|��ȭ��
				case REQ_SENDWORDS: {
					st_buffer.setLength(0);
					st_buffer.append(YES_SENDWORDS);
					st_buffer.append(SEPARATOR);
					String id = st.nextToken(); // ������ ������� ID�� ���Ѵ�.
					st_buffer.append(id);
					st_buffer.append(SEPARATOR);
					try {
						String data = st.nextToken(); // ��ȭ���� ���Ѵ�.
						st_buffer.append(data);
					} catch (NoSuchElementException e) {
					}
					broadcast(st_buffer.toString(), CHATROOM); // YES_SENDWORDS ��Ŷ ����
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
						// ������ ������� ID�� ���Ѵ�.
						st_buffer.append(id);
						st_buffer.append(SEPARATOR);
						// ��ȭ���� ���Ѵ�.

						st_buffer.append(data);
						st_buffer.append(SEPARATOR);
						st_buffer.append(rid);
						multicast(st_buffer.toString(), id, rid);
					}
					break;
				}
				// LOGOUT ���� �õ� �޽���
				// PACKET : YES_LOGOUT|Ż����ID|Ż���� �̿��� ids
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

				// �� �������� LOGOUT ���� �õ� �޽��� PACKET : YES_QUITROOM
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
				//�Խ��� ���� ���� �޼��� PACKET : YES_ENTERBOARD|�Խ��ǳ���
				case REQ_ENTERBOARD:{ 
					st_buffer.setLength(0);
					st_buffer.append(YES_ENTERBOARD);
					/*
					 * ������� �� �Խ��� ���� �о �����ֱ�
					 */
					send(st_buffer.toString());
				}
				} // switch ����

				Thread.sleep(100);
			} // while ����

		} catch (NullPointerException e) { // �α׾ƿ��� st_in�� �� ���ܸ� �߻��ϹǷ�
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

	// �α׿¿� ������ ����� ID�� ���Ѵ�.
	private String getUsers() {
		StringBuffer id = new StringBuffer();
		String ids;
		Enumeration<String> enu = logonVector.elements();
		while (enu.hasMoreElements()) {
			id.append(enu.nextElement());
			id.append(DELIMETER);
		}
		try {
			ids = new String(id); // ���ڿ��� ��ȯ�Ѵ�.
			ids = ids.substring(0, ids.length() - 1); // ������ "`"�� �����Ѵ�.
		} catch (StringIndexOutOfBoundsException e) {
			return "";
		}
		return ids;
	}

	// ��ȭ�濡 ������ ����� ID�� ���Ѵ�.

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
			ids = ids.substring(0, ids.length() - 1); // ������ "`"�� �����Ѵ�.
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

	// ��ȭ�濡 ������ ��� �����(��ε��ɽ���)���� �����͸� �����Ѵ�.
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

	// �����͸� �����Ѵ�.
	public void send(String sendData) throws IOException {
		synchronized (st_out) {
			st_out.writeUTF(sendData);
			st_out.flush();
		}
	}
}
