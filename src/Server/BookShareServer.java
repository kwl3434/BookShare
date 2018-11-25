package Server;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;


public class BookShareServer extends Frame{
	TextArea display;
	Label info;
	List<Map.Entry<ServerThread, Boolean>> list;
	Hashtable<String, ServerThread> hash;
	public ServerThread SThread;
	
	public BookShareServer() {
		super("서버");
		info = new Label();
		add(info, BorderLayout.CENTER);
		display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
		display.setEditable(false);
		add(display, BorderLayout.SOUTH);
		addWindowListener(new WinListener());
		setSize(350, 250);
		setVisible(true);
	}
	public void runServer() {
		ServerSocket server;
		Socket sock;
		ServerThread SThread;
		try {
			server = new ServerSocket(5000, 100);
			hash = new Hashtable<String, ServerThread>();
			list = new ArrayList<Map.Entry<ServerThread, Boolean>>();
			try {
				while (true) {
					sock = server.accept();
					SThread = new ServerThread(this, sock, display, info);
					SThread.start();
					info.setText(sock.getInetAddress().getHostName() + " 서버는 클라이언트와 연결됨");
				}
			} catch (IOException ioe) {
				server.close();
				ioe.printStackTrace();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	public static void main(String args[]) {
		BookShareServer s = new BookShareServer();
		s.runServer();
	}
	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}
}


