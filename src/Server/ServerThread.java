package Server;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.*;

class ServerThread extends Thread {
	Socket sock;
	BufferedWriter output;
	BufferedReader input;
	TextArea display;
	Label info;
	TextField text;
	String clientdata;
	String serverdata = "";
	BookShareServer cs;
	public DBconnect BDB;
	String[] arrid = new String[100];
	boolean check = false;

	private static final String SEPARATOR = "|";
	private static final int REQ_LOGON = 1001;
	private static final int REQ_CREATEID = 1000;
	private static final int REQ_SENDWORDS = 1021;
	private static final int REQ_WISPERSEND = 1022;
	private static final int REQ_LOGOUT = 1002;
	private static final int REQ_QUIT = 1003;
	private Set<String> allid;

	public ServerThread(BookShareServer c, Socket s, TextArea ta, Label l) {
		sock = s;
		display = ta;
		info = l;
		cs = c;
		BDB = new DBconnect();

		try {
			input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			output = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void run() {
		try {
			cs.list.add(new AbstractMap.SimpleEntry<ServerThread, Boolean>(this, false));
			int r=0;
			while ((clientdata = input.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(clientdata, SEPARATOR);
				int command = Integer.parseInt(st.nextToken());
				switch (command) {
				case REQ_LOGON: {
					String ID = st.nextToken();
					//String PASSWORD = st.nextToken();
					r=BDB.logincheck("test","test");
					allid = cs.hash.keySet();
					allid.toArray(arrid);
					for (int i = 0; i < arrid.length; i++) {
						if (arrid[i] != null) {
							if (arrid[i].compareTo(ID) == 0)
								check = true;
						}
					}
					cs.hash.put(this.toString(), this);
					if (!check&&r==0) {
						cs.hash.remove(this.toString());
						cs.hash.put(ID, this); // 해쉬 테이블에 아이디와 스레드를 저장한다
						for (int i = 0; i < cs.list.size(); i++) {
							if (cs.list.get(i).getKey().getName().compareTo(cs.hash.get(ID).getName()) == 0) {
								cs.list.get(i).setValue(true);
								ServerThread SThread1 = (ServerThread) cs.list.get(i).getKey();
								SThread1.output.write("로그인 완료.\r\n");
								SThread1.output.flush();
								display.append("클라이언트가 " + ID + "(으)로 로그인 하였습니다.\r\n");
							}
						}
					} else {
						for (int i = 0; i < cs.list.size(); i++) {
							if (cs.list.get(i).getKey().getName()
									.compareTo(cs.hash.get(this.toString()).getName()) == 0) {
								ServerThread SThread1 = (ServerThread) cs.list.get(i).getKey();
								SThread1.output.write("(에러)중복된 ID\r\n");
								SThread1.output.flush();
								display.append("중복된 " + ID + "입니다.\r\n");
							}
						}
					}
					check = false;
					break;

				}
				case REQ_CREATEID:{
					String ID=st.nextToken();
					String PASSWORD=st.nextToken();
					String PHONE=st.nextToken();
					r = BDB.createid(ID, PASSWORD, PHONE);
				}
				case REQ_SENDWORDS: {
					String ID = st.nextToken();
					String message = st.nextToken();
					display.append(ID + " : " + message + "\r\n");
					for (int i = 0; i < cs.list.size(); i++) {
						if (cs.list.get(i).getValue()) {
							ServerThread SThread = (ServerThread) cs.list.get(i).getKey();
							SThread.output.write(ID + " : " + message + "\r\n");
							SThread.output.flush();
						}
					}
					break;
				}
				case REQ_WISPERSEND: {
					String ID = st.nextToken();
					String WID = st.nextToken();
					String message = st.nextToken();
					display.append(ID + " -> " + WID + " : " + message + "\r\n");
					ServerThread SThread = (ServerThread) cs.hash.get(ID);
					// 해쉬테이블에서 귓속말 메시지를 전송한 클라이언트의 스레드를 구함
					SThread.output.write(ID + " -> " + WID + " : " + message + "\r\n");
					// 귓속말 메시지를 전송한 클라이언트에 전송함
					SThread.output.flush();
					SThread = (ServerThread) cs.hash.get(WID);
					// 해쉬테이블에서 귓속말 메시지를 수신할 클라이언트의 스레드를 구함
					SThread.output.write(ID + " : " + message + "\r\n");
					// 귓속말 메시지를 수신할 클라이언트에 전송함
					SThread.output.flush();
					break;
				}
				case REQ_LOGOUT: {
					String ID = st.nextToken();
					display.append(ID + "가 로그아웃 되었습니다.\r\n");
					cs.hash.remove(ID);
					for (int i = 0; i < cs.list.size(); i++) {
						if (cs.list.get(i).getValue()) {
							ServerThread SThread = (ServerThread) cs.list.get(i).getKey();
							SThread.output.write(ID + "가 로그아웃되었습니다.\r\n");
							SThread.output.flush();
						}
					}
					break;
				}
				case REQ_QUIT: {
					String ID = st.nextToken();
					if (ID != null) {
						for (int i = 0; i < cs.list.size(); i++) {
							if (cs.list.get(i).getKey().getName().compareTo(cs.hash.get(ID).getName()) == 0) {
								display.append(ID + "가 로그아웃 되었습니다.\r\n");
								cs.hash.remove(ID);
								cs.list.remove(i);
								break;
							}
						}
					} else {
						for (int i = 0; i < cs.list.size(); i++) {
							if (cs.list.get(i).getKey().getName()
									.compareTo(cs.hash.get(this.toString()).getName()) == 0) {
								display.append(cs.hash.get(this.toString()).getName());
								cs.hash.remove(cs.hash.get(this.toString()).getName());
								cs.list.remove(i);
								break;
							}
						}
					}
				}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			sock.close();
		} catch (IOException ea) {
			ea.printStackTrace();
		}
	}
}