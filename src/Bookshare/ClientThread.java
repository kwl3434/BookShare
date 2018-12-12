package Bookshare;

import java.io.*;
import java.util.*;
import java.net.*;

public class ClientThread extends Thread {

	private ChatClient ct_client; // ChatClient ��ü
	private Socket ct_sock; // Ŭ���̾�Ʈ ����
	private DataInputStream ct_in; // �Է� ��Ʈ��
	private DataOutputStream ct_out; // ��� ��Ʈ��
	private StringBuffer ct_buffer; // ����
	private Thread thisThread;
	private DisplayRoom room;
	private BoardContent bcontent;
	private BoardShow bshow;
	private BoardMain bmain;
	private NoteMessageRoom MR; 

	private static final String SEPARATOR = "|";
	private static final String DELIMETER = "`";

	// �޽��� ��Ŷ �ڵ� �� ������ ����

	// ������ �����ϴ� �޽��� �ڵ�
	private static final int REQ_LOGON = 1001;
	private static final int REQ_ENTERROOM = 1011;
	private static final int REQ_ENTERBOARD =1012;
	private static final int REQ_ENTERMSG =1013;
	private static final int REQ_WRITEBOARD =1014;
	private static final int REQ_READBOARD =1015;
	private static final int REQ_SENDWORDS = 1021;
	private static final int REQ_WISPERSEND = 1022;
	private static final int REQ_LOGOUT = 1031;
	private static final int REQ_QUITROOM = 1041;
	

	// �����κ��� ���۵Ǵ� �޽��� �ڵ�
	private static final int YES_LOGON = 2001;
	private static final int NO_LOGON = 2002;
	private static final int YES_ENTERROOM = 2011;
	private static final int NO_ENTERROOM = 2012;
	private static final int MDY_USERIDS = 2013;
	private static final int YES_ENTERMSG = 2021;
	private static final int NO_SENDWORDS = 2022;
	private static final int YES_WISPERWORDS = 2023;
	private static final int NO_WISPERWORDS = 2024;
	private static final int YES_LOGOUT = 2031;
	private static final int NO_LOGOUT = 2032;
	private static final int C_LOGOUT = 2033;
	private static final int YES_QUITROOM = 2041;
	private static final int C_QUITROOM = 2042;
	private static final int YES_ENTERBOARD = 2050;
	private static final int YES_ENTERWRITEBOARD = 2051;
	private static final int YES_ENTERREADBOARD = 2052;

	// ���� �޽��� �ڵ�
	private static final int MSG_ALREADYUSER = 3001;
	private static final int MSG_SERVERFULL = 3002;
	private static final int MSG_CANNOTOPEN = 3011;

	private static MessageBox msgBox, logonbox;

	/*
	 * ����ȣ��Ʈ�� ������ ���� ������ ���� : java ChatClient ȣ��Ʈ�̸� ��Ʈ��ȣ To DO .....
	 */

	// ����ȣ��Ʈ���� ����ϱ� ���Ͽ� ���� ������
	// ������ Ŭ���̾�Ʈ�� ���� �ý����� ����Ѵ�.
	public ClientThread(ChatClient client) {
		try {
			ct_sock = new Socket(InetAddress.getLocalHost(), 2777);
			ct_in = new DataInputStream(ct_sock.getInputStream());
			ct_out = new DataOutputStream(ct_sock.getOutputStream());
			ct_buffer = new StringBuffer(4096);
			thisThread = this;
			ct_client = client; // ��ü������ �Ҵ�
		} catch (IOException e) {
			MessageBoxLess msgout = new MessageBoxLess(client, "���ῡ��", "������ ������ �� �����ϴ�.");
			msgout.show();
		}
	}

	public void run() {

		try {
			Thread currThread = Thread.currentThread();
			while (currThread == thisThread) { // ����� LOG_OFF���� thisThread=null;�� ���Ͽ�
				String recvData = ct_in.readUTF();
				StringTokenizer st = new StringTokenizer(recvData, SEPARATOR);
				int command = Integer.parseInt(st.nextToken());
				switch (command) {

				// �α׿� ���� �޽��� PACKET : YES_LOGON|�����ð�|ID1`ID2`ID3...
				case YES_LOGON: {
					logonbox.dispose();
					ct_client.dispose(); // �α׿� â�� �����.
					room = new DisplayRoom(this, "����");
					room.pack();
					room.show(); // ��ȭ�� â�� ����Ѵ�.
					System.out.println("����");
					break;
				}

				// �α׿� ���� �Ǵ� �α׿��ϰ� ��ȭ���� �������� ���� ����
				// PACKET : NO_LOGON|errCode
				case NO_LOGON: {
					int errcode = Integer.parseInt(st.nextToken());
					if (errcode == MSG_ALREADYUSER) {
						logonbox.dispose();
						msgBox = new MessageBox(ct_client, "�α׿�", "�̹� �ٸ� ����ڰ� �ֽ��ϴ�.");
						msgBox.show();
					} else if (errcode == MSG_SERVERFULL) {
						logonbox.dispose();
						msgBox = new MessageBox(ct_client, "�α׿�", "��ȭ���� �����Դϴ�.");
						msgBox.show();
					}
					break;
				}

				// ��ȭ�� ���� �� ���� ���� �޽��� PACKET : YES_ENTERROOM
				case YES_ENTERROOM: {
					ct_client.dispose(); // �α׿� â�� �����.
					room = new DisplayRoom(this, "��ȭ��");
					room.pack();
					room.show(); // ��ȭ�� â�� ����Ѵ�.
					bcontent = new BoardContent(this, "�Խ��� �ۼ�");
					bcontent.pack();
					bcontent.show();
					bshow = new BoardShow(this,"�Խ��� ����");
					bshow.pack();
					bshow.show();
					break;
				}

				// ��ȭ�� ���� �� ���� ���� �޽��� PACKET : NO_ENTERROOM|errCode
				case NO_ENTERROOM: {
					int roomerrcode = Integer.parseInt(st.nextToken());
					if (roomerrcode == MSG_CANNOTOPEN) {
						msgBox = new MessageBox(ct_client, "��ȭ������", "�α׿µ� ����ڰ� �ƴմϴ�.");
						msgBox.show();
					}
					break;
				}

				// ��ȭ�濡 ������ ����� ����Ʈ�� ���׷��̵� �Ѵ�.
				// PACKET : MDY_USERIDS|id1'id2'id3.....
				case MDY_USERIDS: {
					/*room.dr_lstMember.clear(); // ��� ID�� �����Ѵ�.
					String ids = st.nextToken(); // ��ȭ�� ������ ����Ʈ
					StringTokenizer roomusers = new StringTokenizer(ids, DELIMETER);
					room.dr_lstMember.removeAll();
					while (roomusers.hasMoreTokens()) {
						room.dr_lstMember.add(roomusers.nextToken());
					}*/
					break;
				}

				// ���� �޽��� ��� PACKET : YES_SENDWORDS|ID|��ȭ��
				case YES_ENTERMSG: {
					room.dispose();
					MR = new NoteMessageRoom(this,"����â");
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
					/*room.dr_taContents.append("�ڽſ��Դ� �ӼӸ� �Ҽ� �����ϴ�.\n");
					room.dr_tfInput.setText("");
					*/
					break;
				}
				// LOGOUT �޽��� ó��
				// PACKET : YES_LOGOUT|Ż����id|Ż���� ���� id1, id2,....
				case YES_LOGOUT: {
					/*String outid = st.nextToken();
					ct_client.cc_tfStatus.setText(outid + "���� �α׾ƿ� �Ǽ̽��ϴ�.");
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
					*/
					break;
				}
				case C_LOGOUT: {
					/*
					ct_client.cc_tfStatus.setText("�α׾ƿ� �Ǽ̽��ϴ�.");
					ct_client.cc_tfDate.setText("�α׾ƿ�����");
					ct_client.cc_tfLogon.setEditable(true);
					ct_client.cc_lstMember.removeAll();
					*/
					break;
				}
				// ��� �޽���(YES_QUITROOM) ó�� PACKET : YES_QUITROOM|ids
				case YES_QUITROOM: {
					/*Sring ids = st.nextToken(); // ��ȭ�� ������ ����Ʈ
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
					room.dispose();
					bmain = new BoardMain(this,"�Խ��� ����");
					bmain.pack();
					bmain.show();
					break;
				}
				case YES_ENTERWRITEBOARD:{
					bmain.dispose();
					bcontent = new BoardContent(this,"�Խ��� �ۼ�");
					bcontent.pack();
					bcontent.show();
					break;
				}
				case YES_ENTERREADBOARD:{
					bmain.dispose();
					bshow = new BoardShow(this,"�Խ��� ����");
					bshow.pack();
					bshow.show();
					break;
				}
				
				} // switch ����

				Thread.sleep(200);

			} // while ����(������ ����)

		} catch (InterruptedException e) {
			System.out.println(e);
			release();

		} catch (IOException e) {
			System.out.println(e);
			release();
		}
	}

	// ��Ʈ��ũ �ڿ��� �����Ѵ�.
	public void release() {
	};

	// Logon ��Ŷ(REQ_LOGON|ID)�� �����ϰ� �����Ѵ�.
	public void requestLogon(String id,String pw) {
		try {
			logonbox = new MessageBox(ct_client, "�α׿�", "������ �α׿� ���Դϴ�.");
			logonbox.show();
			ct_buffer.setLength(0); // Logon ��Ŷ�� �����Ѵ�.
			ct_buffer.append(REQ_LOGON);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(id);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(pw);
			send(ct_buffer.toString()); // Logon ��Ŷ�� �����Ѵ�.
			System.out.println(id+" "+pw);
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void requestLogout() {
		try {
			ct_buffer.setLength(0); // EnterRoom ��Ŷ�� �����Ѵ�.
			ct_buffer.append(REQ_LOGOUT);
			ct_buffer.append(SEPARATOR);
			send(ct_buffer.toString()); // EnterRoom ��Ŷ�� �����Ѵ�.
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	// EnterRoom ��Ŷ(REQ_ENTERROOM|ID)�� �����ϰ� �����Ѵ�.
	public void requestEnterRoom(String id) {
		try {
			ct_buffer.setLength(0); // EnterRoom ��Ŷ�� �����Ѵ�.
			ct_buffer.append(REQ_ENTERROOM);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(id);
			send(ct_buffer.toString()); // EnterRoom ��Ŷ�� �����Ѵ�.
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void requestQuitRoom() {
		try {
			ct_buffer.setLength(0); // EnterRoom ��Ŷ�� �����Ѵ�.
			ct_buffer.append(REQ_QUITROOM);
			send(ct_buffer.toString()); // EnterRoom ��Ŷ�� �����Ѵ�.
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	// SendWords ��Ŷ(REQ_SENDWORDS|ID|��ȭ��)�� �����ϰ� �����Ѵ�.
	public void requestEnterMessage() {
		try {
			ct_buffer.setLength(0); // SendWords ��Ŷ�� �����Ѵ�.
			ct_buffer.append(REQ_ENTERMSG);
			send(ct_buffer.toString()); // SendWords ��Ŷ�� �����Ѵ�.
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void requestEnterBoard() {
		try {
			ct_buffer.setLength(0); // enterboard ��Ŷ�� �����Ѵ�.
			ct_buffer.append(REQ_ENTERBOARD);
			send(ct_buffer.toString()); // enterboard ��Ŷ�� �����Ѵ�.
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	public void requestWriteBoard() {
		try {
			ct_buffer.setLength(0); // enterWriteboard ��Ŷ�� �����Ѵ�.
			ct_buffer.append(REQ_WRITEBOARD);
			send(ct_buffer.toString()); // enterWriteboard ��Ŷ�� �����Ѵ�.
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	public void requestReadBoard() {
		try {
			ct_buffer.setLength(0); // SendWords ��Ŷ�� �����Ѵ�.
			ct_buffer.append(REQ_READBOARD);
			send(ct_buffer.toString()); // SendWords ��Ŷ�� �����Ѵ�.
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	// Ŭ���̾�Ʈ���� �޽����� �����Ѵ�.
	private void send(String sendData) throws IOException {
		ct_out.writeUTF(sendData);
		ct_out.flush();
	}
}