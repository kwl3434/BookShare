package Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBconnect {
	private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private final String DB_URL = "jdbc:mysql://localhost/bookshare?characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false";

	private final String USER_NAME = "root";
	private final String PASSWORD = "thrhd";

	DBconnect() {
		
	}
	public int logincheck(String id,String password)
	{
		/*
		 * �α��� ���� ����
		 * 0 ����ó��
		 * 1 ���̵𿡷�
		 * 2 �н����忡�� 
		 */
		Connection conn = null;
		Statement state = null;
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
				String phonenumber = rs.getString("phonenumber");
				System.out.println("ID : " + ID);
				System.out.println("PASSWORD : " + password);
				System.out.println("PHONENUMBER : " + phonenumber);
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
		return 0;
	}
	
	public int createid(String info) 
	{
		/*
		 * ȸ������ ����
		 * 0  ���� ȸ������
		 * 1 id ����
		 * 2 password ����
		 * 3 ��ȭ��ȣ ����
		 * 4 �̸��� ����
		*/
		return 0;
	}
	
	public String getmsg() 
	{
		/*
		 * Ŭ���̾�Ʈ �޼������ ���� ����
		 * ���� �޼��� ����
		 * ������ null ����
		 */
		return "";
	}
	public int setmsg(String msg) 
	{
		/*
		 * �����޼��� ������� ����
		 * 0 ����
		 * 1 id����
		 * 2 msg����
		 */
		return 0;
	}
	public int removemsg(int msgindex) 
	{
		/*
		 * �޼��� ���� ����
		 * 0 ����
		 * 1 ����
		 */
		return 0;
	}
	public int setboard(String board) 
	{
		/*
		 * �Խ��� �ۼ� ����
		 * 0 ����
		 * 1 �Խ��� ���� ����
		 * 2 �Խ��� �н����� ����
		 * 3 �Խ��� ���� ����
		 */
		return 0;
	}
	public String getboard() 
	{
		/*
		 * �Խñ� �ε� ����
		 * �Խñ۸��� ����
		 * ""���� ����
		 */
		return "";
	}
	public int removeboard(int boardindex) 
	{
		/*
		 * �Խñ� ���� ����
		 * 0 ����
		 * 1 �н����� ����
		 * 2 ����������� ����
		 */
		return 0;
	}
	public int updateboard(int boardindex, String board) 
	{
		/*
		 * �Խñ� ������Ʈ ����
		 * 0 ����
		 * 1 �н����� ����
		 * 2 ����������� ����
		 */
		return 0;
	}
}	
