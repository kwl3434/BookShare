package Bookshare;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBconnect {
	private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private final String DB_URL = "jdbc:mysql://localhost/bookshare?characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false";
	private static final String SEPARATOR = "|";
	private final String USER_NAME = "root";
	private final String PASSWORD = "thrhd";

	DBconnect() {
		
	}
	public int logincheck(String id,String pass)
	{
		/*
		 * �α��� ���� ����
		 * 0 ����ó��
		 * 1 ����
		 */
		Connection conn = null;
		Statement state = null;
		boolean check = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			System.out.println("\n- MySQL Connection");
			state = conn.createStatement();

			String sql;
			sql = "SELECT * FROM user";
			ResultSet rs = state.executeQuery(sql);

			while (rs.next()) {
				String ID = rs.getString("id");
				String password = rs.getString("password");
				System.out.println(ID);
				System.out.println(password);
				if(ID.compareTo(id)==0&&password.compareTo(pass)==0) {
					check = true;
					break;
				}
			}
			rs.close();
			state.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} /*finally {
			try {
				if (state != null)
					state.close();
			} catch (SQLException ex1) {
				ex1.printStackTrace();
			}
		}*/
		System.out.println("\n\n- MySQL Connection Close");
		if(check) return 0;
		else return 1;
	}
	
	public int createid(String id,String pw,String phone) 
	{
		/*
		 * ȸ������ ����
		 * 0  ���� ȸ������
		 * 1 id ����
		 * 2 password ����
		 * 3 ��ȭ��ȣ ����
		 * 4 �̸��� ����
		*/
		Connection conn = null;
		Statement state = null;
		int rs=0;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			System.out.println("\n- MySQL Connection");
			state = conn.createStatement();

			String sql;
			sql = "INSERT INTO user(id,password,phonenumber)";
			sql+= "VALUES('"+id+"','"+pw+"','"+phone+"')";
			rs = state.executeUpdate(sql);
			
			state.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\n\n- MySQL Connection Close");
		if(rs>0) return 0;
		else return 1;
	}
	
	public String[] getmsg(String id) 
	{
		/*
		 * Ŭ���̾�Ʈ �޼������ ���� ����
		 * ���� �޼��� ����
		 * ������ null ����
		 */
		String [] buffer= new String[1000];
		Connection conn = null;
		Statement state = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			System.out.println("\n- MySQL Connection");
			state = conn.createStatement();

			String sql;
			sql = "SELECT msg_no,text,source,date FROM msg where id='"+id+"'";
			ResultSet rs = state.executeQuery(sql);
			int i=0;
			while (rs.next()) {
				String msg_No = rs.getString("msg_no");
				String text = rs.getString("text");
				String source = rs.getString("source");
				String date = rs.getString("date");
				System.out.println(text);
				System.out.println(source);
				System.out.println(date);
				buffer[i]=msg_No+SEPARATOR+text+SEPARATOR+source+SEPARATOR+date;
				i++;
			}
			rs.close();
			state.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\n\n- MySQL Connection Close");
		return buffer;
	}
	public int setmsg(String msg,String source, String dest, String date) 
	{
		/*
		 * �����޼��� ������� ����
		 * 0 ����
		 * 1 id����
		 * 2 msg����
		 */
		Connection conn = null;
		Statement state = null;
		String msg_No="0";
		int rs=0;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			System.out.println("\n- MySQL Connection");
			state = conn.createStatement();

			String sql;
			sql = "INSERT INTO msg(msg_no,text,source,dest,date)";
			sql+= "VALUES('"+msg_No+"','"+msg+"','"+source+"','"+dest+"',"+date+"')";
			rs = state.executeUpdate(sql);
			
			state.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\n\n- MySQL Connection Close");
		if(rs>0) return 0;
		else return 1;
	}
	public int removemsg(int msgindex) 
	{
		/*
		 * �޼��� ���� ����
		 * 0 ����
		 * 1 ����
		 */
		Connection conn = null;
		Statement state = null;
		int rs=0;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			System.out.println("\n- MySQL Connection");
			state = conn.createStatement();

			String sql;
			sql = "DELETE FROM msg WHERE msg_no='"+msgindex+"'";
			rs = state.executeUpdate(sql);
			
			
			state.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\n\n- MySQL Connection Close");
		if(rs>0)return 0;
		else return 1;
	}
	public int setboard(String title,String text, String id, String password,String date) 
	{
		/*
		 * �Խ��� �ۼ� ����
		 * 0 ����
		 * 1 �Խ��� ���� ����
		 * 2 �Խ��� �н����� ����
		 * 3 �Խ��� ���� ����
		 */
		Connection conn = null;
		Statement state = null;
		String board_No="0";
		int rs=0;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			System.out.println("\n- MySQL Connection");
			state = conn.createStatement();

			String sql;
			sql = "INSERT INTO board(board_no,title,text,id,password,date)";
			sql+= "VALUES('"+board_No+"','"+title+"','"+text+"','"+id+"',"+password+"'"+date+"')";
			rs = state.executeUpdate(sql);
			
			state.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\n\n- MySQL Connection Close");
		if(rs>0) return 0;
		else return 1;
	}
	public String[] getboard() 
	{
		/*
		 * �Խñ� �ε� ����
		 * �Խñ۸��� ����
		 * ""���� ����
		 */
		String [] buffer= new String[1000];
		Connection conn = null;
		Statement state = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			System.out.println("\n- MySQL Connection");
			state = conn.createStatement();

			String sql;
			sql = "SELECT board_no,title,text,id,date FROM board";
			ResultSet rs = state.executeQuery(sql);
			int i=0;
			while (rs.next()) {
				String board_No = rs.getString("board_no");
				String title = rs.getString("title");
				String text = rs.getString("text");
				String ID = rs.getString("id");
				String date = rs.getString("date");
				buffer[i]=board_No+SEPARATOR+title+SEPARATOR+text+SEPARATOR+ID+SEPARATOR+date;
				i++;
			}
			rs.close();
			state.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\n\n- MySQL Connection Close");
		return buffer;
	}
	public int removeboard(int boardindex) 
	{
		/*
		 * �Խñ� ���� ����
		 * 0 ����
		 * 1 �н����� ����
		 * 2 ����������� ����
		 */
		Connection conn = null;
		Statement state = null;
		int rs=0;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			System.out.println("\n- MySQL Connection");
			state = conn.createStatement();

			String sql;
			sql = "DELETE FROM board WHERE board_no='"+boardindex+"'";
			rs = state.executeUpdate(sql);
			
			
			state.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\n\n- MySQL Connection Close");
		if(rs>0)return 0;
		else return 1;
	}
	public int updateboard(int boardindex, String board,String title, String date, String id) 
	{
		/*
		 * �Խñ� ������Ʈ ����
		 * 0 ����
		 * 1 �н����� ����
		 * 2 ����������� ����
		 */
		Connection conn = null;
		Statement state = null;
		int rs=0;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			System.out.println("\n- MySQL Connection");
			state = conn.createStatement();

			String sql;
			sql = "UPDATE board SET title='";
			sql+=title+"',"+"board='"+board+"',"+"id='"+id+"', date='"+date+"' WHERE board_no='"+boardindex+"' LIMIT 1";
			rs = state.executeUpdate(sql);
			
			state.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\n\n- MySQL Connection Close");
		if(rs>0)return 0;
		else return 1;
	}
}	
